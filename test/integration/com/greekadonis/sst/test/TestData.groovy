package com.greekadonis.sst.test

class TestData {

   String getFilePath(){
      this.class.classLoader.getResource('resources/data').path
   }

   File getSstTextFile() {
      String path = getFilePath() + '/ALL_UKMO-L4HRfnd-GLOB-OSTIA_v01-fv02.nc.ascii_analysed_sst[0][0.20.3599][0.20.7199].txt'
      File file = new File(path)
      file
   }
}
