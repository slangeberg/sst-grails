package com.greekadonis.sst.test

class TestData {
   File getSstTextFile() {
      def path = 'resources/data/ALL_UKMO-L4HRfnd-GLOB-OSTIA_v01-fv02.nc.ascii_analysed_sst[0][0.20.3599][0.20.7199].txt'

      def resource = this.class.classLoader.getResource(path)

      def file = new File(resource.toURI())

//      println "file: $file"

      file
   }
}
