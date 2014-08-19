package com.greekadonis.sst

import com.greekadonis.gandas.DataFrame
import com.greekadonis.sst.services.DataLoaderService

class SSTDayController {
    static scaffold = true

    DataLoaderService dataLoaderService
    
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
}
