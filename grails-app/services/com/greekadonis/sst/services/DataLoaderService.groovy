package com.greekadonis.sst.services

import com.greekadonis.sst.SSTDay
import com.greekadonis.sst.data.SST_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_Reader
import grails.transaction.Transactional
import org.apache.commons.lang3.time.StopWatch
import org.springframework.transaction.annotation.Propagation

/**
 * Tries to load SSTDay related data in following order:
 * 1. DB
 * 2. File extract
 * 3. Remote dataset
 */

@Transactional
class DataLoaderService {
/*
* See: http://thredds.jpl.nasa.gov/thredds/dodsC/sea_surface_temperature/ALL_UKMO-L4HRfnd-GLOB-OSTIA_v01-fv02.nc.html
* DDS:
Dataset {
    Float32 lat[lat = 3600];
    Float32 lon[lon = 7200];
    Grid {
     ARRAY:
        Int16 analysed_sst[time = 2951][lat = 3600][lon = 7200];
     MAPS:
        String time[time = 2951];
        Float32 lat[lat = 3600];
        Float32 lon[lon = 7200];
    } analysed_sst;
    Grid {
     ARRAY:
        Int16 analysis_error[time = 2951][lat = 3600][lon = 7200];
     MAPS:
        String time[time = 2951];
        Float32 lat[lat = 3600];
        Float32 lon[lon = 7200];
    } analysis_error;
    Grid {
     ARRAY:
        Byte sea_ice_fraction[time = 2951][lat = 3600][lon = 7200];
     MAPS:
        String time[time = 2951];
        Float32 lat[lat = 3600];
        Float32 lon[lon = 7200];
    } sea_ice_fraction;
    Grid {
     ARRAY:
        Byte mask[time = 2951][lat = 3600][lon = 7200];
     MAPS:
        String time[time = 2951];
        Float32 lat[lat = 3600];
        Float32 lon[lon = 7200];
    } mask;
    String time[time = 2951];
} sea_surface_temperature%2fALL_UKMO-L4HRfnd-GLOB-OSTIA_v01-fv02%2enc;
 */
   // @see: DDS
   public static final int MAX_LAT = 3600 - 1;
   public static final int MAX_LON = 7200 - 1;

   final String DATA_FILE_NAME = 'ALL_UKMO-L4HRfnd-GLOB-OSTIA_v01-fv02.nc.ascii'

   int stepSize = 1800 //how many to skip: 1 = every step, 2 = every other, etc.
   String latParams = "[0:$stepSize:$MAX_LAT]"
   String lonParams = "[0:$stepSize:$MAX_LON]"

   def sstDayService

   String testFileContents

   /**
    * @return Day 0 - the first day
    */
   SSTDay loadDay() {
      loadDay(0)
   }

   /**
    * @param sstIndex - 0-based index for 'time' component. eg: sstIndex == 0 is the first day
    * @return
    */
   SSTDay loadDay(int sstIndex) {

      StopWatch timer = createAndStartStopWatch()

      SSTDay day = sstDayService.findBySstIndex(sstIndex)
      if ( day ) {
         log.info("loadDay($sstIndex) - DB HIT")
      } else {
         log.info("loadDay($sstIndex) - DB MISS")
         day = loadDayFromRemoteSource(getAnalysedSstParams(sstIndex))
      }
      log.info "loadDay($sstIndex) - time: ${timer.time}ms"
      day
   }

   String getAnalysedSstParams(int sstIndex){
      "[$sstIndex]$latParams$lonParams"
   }

   /**
    * @param analysed_sst - String in form of: [time][lat][lon]
    * @return
    */
   SSTDay loadDayFromRemoteSource(String analysed_sst) {

      StopWatch timer = createAndStartStopWatch()

      SSTDay day = loadDayFromLocalFile(analysed_sst)

      if ( day ) {
         log.info('loadDayFromRemoteSource() - cache HIT')

      } else {
         //Check remote source

         log.info('loadDayFromRemoteSource() - cache MISS')

         String baseUrl = "http://thredds.jpl.nasa.gov/thredds/dodsC/sea_surface_temperature/$DATA_FILE_NAME"
         String analysedSSTUrl = "$baseUrl?analysed_sst"
         String url = "$analysedSSTUrl$analysed_sst"

         log.info("loadDayFromRemoteSource() - url: $url")

         String content = url.toURL().text

         //Write to disk

         writeFile(analysed_sst, content)

         log.info("loadDayFromRemoteSource() - analysed_sst: $analysed_sst, response time: ${timer.getTime()}ms")

         SST_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_Reader reader =
            new SST_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_Reader(content)
         day = reader.getDay() //analysed_sst)
      }

      log.info("loadDayFromRemoteSource() - analysed_sst: $analysed_sst, time: ${timer.getTime()}ms")

      day
   }

   SSTDay loadDayFromLocalFile(String analysed_sst) {

      StopWatch timer = createAndStartStopWatch()

      String contents = getFileContents(analysed_sst)

//      println "loadDayFromLocalFile() - file.text - time: ${timer.time}ms"

      SST_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_Reader reader =
         new SST_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_Reader(contents)
      SSTDay day = reader.day

      log.info( "loadDayFromLocalFile($analysed_sst) - day: $day, in time: ${timer.time}ms")
      day
   }

   String getFileContents(String analysed_sst) {
      String contents = testFileContents ?: null
      if( !contents ){
         File file = getFile(analysed_sst)

         log.info( "getFileContents($analysed_sst) - file: $file")

         if( file.isFile() ) {
            contents = file.text
         }
      }
      contents
   }

   String getFilePath(String analysed_sst){
      String path = testFilePath ?: null
      if( !path ) {
         String base = "${System.getProperty("user.dir")}/data"
         String name = "${DATA_FILE_NAME}_analysed_sst${analysed_sst.replace(":", ".")}.txt"
         path = "$base/$name"
      }
      assert !path.contains("null")

      path
   }

   private File getFile(String analysed_sst) {

      StopWatch timer = createAndStartStopWatch()

      String path = getFilePath(analysed_sst)
      File file = new File(path)

      log.info "getFile() - file: $file, file.isFile(): ${file.isFile()}, file.text.size(): ${file.isFile() ? file?.text?.size() : 0}, time: ${timer.time}ms"

      file
   }

   private File writeFile(String analysed_sst, String contents) {

      log.info "writeFile($analysed_sst, contents.size(): ${contents?.size()})"

      StopWatch timer = createAndStartStopWatch()

      String path = getFilePath(analysed_sst)

      File file = new File(path)
      file.write(contents)

      log.info "writeFile(): path: $path, file: $file, time: ${timer.time}ms"

      file
   }

   StopWatch createAndStartStopWatch() {
      StopWatch timer = new StopWatch()
      timer.start()
      timer
   }
}
