package com.greekadonis.sst

import org.joda.time.LocalDate

//import grails.compiler.GrailsCompileStatic

/* Example - src:
http://thredds.jpl.nasa.gov/thredds/dodsC/sea_surface_temperature/ALL_UKMO-L4HRfnd-GLOB-OSTIA_v01-fv02.nc.html
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
//@GrailsCompileStatic
class SSTDay {

    static constraints = {
        time unique: true
        sstIndex unique: true
    }

    static hasMany = [latitudes: SSTDayLatitude]

    LocalDate time

    int sstIndex //index for day in JPL labs datasets

//    List<Double> lat
//    List<Double> lon
//
//    public String toString() {
//        "[SSTDay - id: $id]"
//    }

   public SSTDay() {
      latitudes = []
   }

    @Override
    String toString() {
        "[SSTDay - id: $id, sstIndex: $sstIndex, time: $time, latitudes.size(): ${latitudes ? latitudes.size() : 'NULL'}]"
    }
}
