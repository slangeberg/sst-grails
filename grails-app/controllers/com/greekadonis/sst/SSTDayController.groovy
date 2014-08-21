package com.greekadonis.sst

import com.greekadonis.gandas.DataFrame
import com.greekadonis.sst.data.SstDataHelper
import grails.converters.JSON
import org.joda.time.LocalDate

class SSTDayController {

    static scaffold = true

    def sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService
    
    def day = {
        if( params.id ){
            render "id: ${params.id}, day: ${SSTDay.get(params.id)}"
        } else {
            render "days: <p>${SSTDay.findAll()}</p>"
        }
    }

    def testGandas = {
      DataFrame df = new DataFrame([
          "5": [25, 35, 45, 22, 43, 44, 45],
          "10": [22, 32, 45, 27, 43, 44, 43]
      ])
      render """
df: $df<br/>
df - mean values: ${df.apply(DataFrame.mean)}
"""
    }


   def testRead = {
      render sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService.getModel(new SstDataHelper().createResult()).toString()
   }

   def testWrite = {
      SSTDay day = new SSTDay(time: new LocalDate(2006, 04, 03))
         .addToLatitudes(new SSTDayLatitude(lat: 0.5))
         .addToLatitudes(new SSTDayLatitude(lat: 10.0))
         .addToLatitudes(new SSTDayLatitude(lat: 15.0))
         .addToLatitudes(new SSTDayLatitude(lat: 25.0))
         .save(flush: true, failOnError: true)
      render "saved day: ${SSTDay.get(day.id)}"
   }
}
