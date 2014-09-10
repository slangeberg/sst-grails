package com.greekadonis.sst.data

import com.greekadonis.sst.SSTDay
import com.greekadonis.sst.SSTDayLatitude
import com.greekadonis.sst.SSTDayLongitude
import com.greekadonis.sst.SSTDayLongitudeValue
import org.joda.time.LocalDate
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

   public SSTDay getDay(String rawResult){

      log.info "getDay(rawResult.size(): ${rawResult?.size()})"

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

      List<List<Integer>> sstVals = new ArrayList<List<Integer>>()

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
            log.debug "isLat: TRUE"
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

            line = line.replaceAll(" ", "")
            List split = line.split(",") as List
            split.remove(0) //should be coordinates, like: [lat][lon]

            //JSONArray lon = new JSONArray()
            List<Integer> lon = []
            split.each {
               lon << Integer.valueOf((it as String).trim())
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

      //log.debug "getModel() - sstVals: $sstVals"

      SSTDay day = new SSTDay(dataset: model.dataset, time: model.time)

      sstVals.eachWithIndex { List<Integer> lonValues, int latIndex ->
         SSTDayLatitude latitude = new SSTDayLatitude(day: day, lat: model.latValues[latIndex])
         List<SSTDayLongitude> longitudes = new ArrayList<SSTDayLongitude>()
         lonValues.eachWithIndex { Integer value, int lonIndex ->
            SSTDayLongitude longitude = new SSTDayLongitude(
               day: day,
               latitude: latitude,
               lon: model.lonValues[lonIndex])
            longitude.value = new SSTDayLongitudeValue(analysed_sst: value, longitude: longitude)
            longitudes << longitude
         }
         //println "latitude: $latitude"
         latitude.longitudes = longitudes

//         log.debug "latitude.longitudes: ${latitude.longitudes}"

         model.latitudes << latitude
      }

      day.latitudes = new ArrayList(model.latitudes)
      day
   }
}
