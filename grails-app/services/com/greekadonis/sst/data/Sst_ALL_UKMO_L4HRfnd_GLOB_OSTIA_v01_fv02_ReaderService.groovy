package com.greekadonis.sst.data

import com.greekadonis.sst.SSTDay
import com.greekadonis.sst.SSTDayLatitude
import com.greekadonis.sst.SSTDayLongitudeValue
import org.apache.commons.lang3.time.StopWatch
import org.joda.time.format.ISODateTimeFormat

//@GrailsCompileStatic
class Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService {

/* @param rawResult
- Example - src:
http://thredds.jpl.nasa.gov/thredds/dodsC/sea_surface_temperature/ALL_UKMO-L4HRfnd-GLOB-OSTIA_v01-fv02.nc.ascii?analysed_sst[0:1:0][0:1:1][0:1:1]
--------------------------------------------------

Dataset {
    Grid {
     ARRAY:
        Int16 analysed_sst[time = 1][lat = 2][lon = 2];
     MAPS:
        String time[time = 1];
        Float32 lat[lat = 2];
        Float32 lon[lon = 2];
    } analysed_sst;
} sea_surface_temperature%2fALL_UKMO-L4HRfnd-GLOB-OSTIA_v01-fv02%2enc;
---------------------------------------------
analysed_sst.analysed_sst[1][2][2]
[0][0], -32768, -32768
[0][1], -32768, -32768

analysed_sst.time[1]
"2006-04-01T00:00:00Z"

analysed_sst.lat[2]
-89.975, -89.925

analysed_sst.lon[2]
-179.975, -179.925
 */

   static transactional = false

   def saveWithSimpleJdbcService

   public SSTDay getDay(int sstIndex, String rawResult){

      log.info "getDay(rawResult.size(): ${rawResult?.size()})"

      StopWatch timer = new StopWatch()
      timer.start()

      if( rawResult?.empty ) {
         log.warn '"rawResult" param cannot be null/empty'
         return null
      }

      Map<String, Object> model = [
         dataset: '',
         latitudes: new ArrayList<SSTDayLatitude>(),
         latValues: new ArrayList<Double>(), // Numeric latitude in degrees from feed
         lonValues: new ArrayList<Double>(), // Numeric longitude from feed
         time: null
      ]

      //
      // Takes raw results as returned from SST JPL remote service
      //

      List<List<Short>> sstVals = new ArrayList<List<Short>>()

      boolean isDataSet = true
      boolean isAnalysedSst = false
      boolean isTime = false
      boolean isLat = false
      boolean isLon = false

      rawResult.eachLine { String line ->

         // log.info line

         //Where?
         if( isDataSet && line.startsWith("------") ) {
            isDataSet = false

         } else if ( line.startsWith("analysed_sst.analysed_sst") ){
            isDataSet = false
            isAnalysedSst = true
            return; //skipToNext

         } else if( line.startsWith("analysed_sst.time") ) {
            log.debug "isTime: TRUE"
            isAnalysedSst = false
            isTime = true
            return; //skip line

         } else if( line.startsWith("analysed_sst.lat") ) {
            log.debug "isLat: TRUE"
            isAnalysedSst = false
            isTime = false
            isLon = false
            isLat = true
            return; //skip line

         } else if( line.startsWith("analysed_sst.lon") ) {
            log.debug "isLon: TRUE"
            isAnalysedSst = false
            isTime = false
            isLat = false
            isLon = true
            return; //skip line
         }

         //What?
         if( isDataSet ) {
            //keep in data set
            model.dataset += "$line\n"

         } else if ( isAnalysedSst ){
            isAnalysedSst = true

//--> Todo: Minimize operations at line level - simply split file into groups of lines
//          - which can be processed in parallel, after?

            line = line.replaceAll(" ", "")
            List split = line.split(",") as List
            split.remove(0) //should be coordinates, like: [lat][lon]

            //JSONArray lon = new JSONArray()
            List<Short> lon = []
            split.each {
               lon << Short.valueOf((it as String).trim())
            }
            if( !lon.empty ) {
               sstVals << lon
            }

         } else if( isTime ){
            log.debug "isTime - line: $line"
            model.time = ISODateTimeFormat.localDateParser().parseLocalDate(line.replace("\"", "").split("T")[0])
            isTime = false

         } else if ( isLat ){
            line.split(",").collect(model.latValues) {
               Double.valueOf(it.trim())
            }
            isLat = false

         } else if ( isLon ){
            line.split(",").collect(model.lonValues) {
               Double.valueOf(it.trim())
            }
            isLon = false
         }
      }

      log.info "getDay() - Text parsing completed at ${timer.time}ms"

      timer.split()

//--> Todo: Move persistence to calling class, as this class should only be reading (?)

      SSTDay day = new SSTDay(dataset: model.dataset, sstIndex: sstIndex, time: model.time)
      day.save(flush: true, failOnError: true)

      log.info "Saved day and flushed(): $day"

//--> Todo: Again - can be split into parallel tasks?

//      final List<SSTDayLongitudeValue> longitudeValues = []

//      sstVals.eachWithIndex { List<Integer> lonValues, int latIndex ->
//        // SSTDayLatitude latitude = new SSTDayLatitude(lat: model.latValues[latIndex])
//         //List<SSTDayLongitude> longitudes = new ArrayList<SSTDayLongitude>()
//         lonValues.eachWithIndex { Integer value, int lonIndex ->
////            SSTDayLongitude longitude = new SSTDayLongitude(
////               latitude: latitude,
////               lon: model.lonValues[lonIndex])
////            longitude.value = new SSTDayLongitudeValue(analysed_sst: value, longitudeId: longitudeId)
////            longitudes << longitude
//
//            longitudeValues << new SSTDayLongitudeValue(day: day, analysed_sst: value)//, longitudeId: longitudeId)
//         }
//         //println "latitude: $latitude"
////         latitude.longitudes = longitudes
////         latitude.day = day
//
////         log.debug "latitude.longitudes: ${latitude.longitudes}"
//
//      //   model.latitudes << latitude
//      }
      // day.latitudes = new ArrayList(model.latitudes)

//      SSTDayLongitudeValue longitudeValue


      int count = 0
      int dayId = day.id

      int INSERT_TYPE = 7

      if( INSERT_TYPE == 1 ) {
         List<Short> values = []
         sstVals.each { List<Integer> lonValues  ->
            lonValues.each { Short value ->
               values << value
            }
         }
         saveWithSimpleJdbcService.insertLongitudeValuesJdbcBatch(dayId, values)

      } else if ( INSERT_TYPE == 2) {
         sstVals.eachWithIndex { List<Short> lonValues, int latIndex ->
            lonValues.eachWithIndex { Short value, int lonIndex ->
               //if( count < 10000 ) {
               count++
               saveWithSimpleJdbcService.insertSstDayLongitudeValue(value, dayId)

               if (count % 10000 == 0) {
                  log.info "getDay() - 10000 @ $count LV records inserted (no batching) in: ${timer.time - timer.splitTime}ms"

                  timer.split()
               }
            }
         }
      }  else if ( INSERT_TYPE == 3) {
         List<Short> values = []
         sstVals.each { List<Integer> lonValues  ->
            lonValues.each { Short value ->
               values << value
            }
         }
         saveWithSimpleJdbcService.insertLongitudeValuesGroovySql(dayId, values)

      }  else if ( INSERT_TYPE == 4) {
         List<Short> values = []
         sstVals.each { List<Integer> lonValues  ->
            lonValues.each { Short value ->
               values << value
            }
         }
         saveWithSimpleJdbcService.insertLongitudeValuesGroovySqlBatch(dayId, values)

      }   else if ( INSERT_TYPE == 5) {
         List<Short> values = []
         sstVals.each { List<Integer> lonValues  ->
            lonValues.each { Short value ->
               values << value
            }
         }
         saveWithSimpleJdbcService.insertLongitudeValuesViaCSV(dayId, values)

      } else if (INSERT_TYPE == 6) {
         List<SSTDayLongitudeValue> values = []
         sstVals.each { List<Integer> lonValues  ->
            lonValues.each { Short value ->
               SSTDayLongitudeValue longitudeValue = new SSTDayLongitudeValue()
               longitudeValue.day = day
               longitudeValue.analysed_sst = value
               values << longitudeValue
            }
         }
         log.info "getDay() - going to batch with Gorm..."
         values*.save()

      } else if (INSERT_TYPE == 7) {
         List<SSTDayLongitudeValue> values = []
         sstVals.each { List<Integer> lonValues  ->
            lonValues.each { Short value ->
               SSTDayLongitudeValue longitudeValue = new SSTDayLongitudeValue()
               longitudeValue.day = day
               longitudeValue.analysed_sst = value
               values << longitudeValue
            }
         }
         saveWithSimpleJdbcService.insertLongitudeValuesWithStatelessSession(values)
      }

//      GParsPool.withPool {
//         sstVals.parallel{ List<Integer> lonValues ->
//               lonValues.each { Integer value ->
//                  longitudeValues << new SSTDayLongitudeValue(day: day, analysed_sst: value)
//               }
//            }
//      }

     // log.info "getDay() - ${count} LongitudeValues inserted (jdbc) in: ${timer.time-timer.splitTime}ms"

      log.info "getDay() - Total time: $timer"

      day
   }

   def sessionFactory
   def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

   def cleanUpGorm(int index) {
     // log.info "cleanUpGorm($index)"
      
      def session = sessionFactory.currentSession
      session.flush()
      session.clear()
      propertyInstanceMap.get().clear()
   }
}