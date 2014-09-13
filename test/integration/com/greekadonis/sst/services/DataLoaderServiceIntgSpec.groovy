package com.greekadonis.sst.services

import com.greekadonis.sst.test.TestData
import spock.lang.*

class DataLoaderServiceIntgSpec extends Specification {

   TestData testData = new TestData()

   def dataLoaderService

   def setup() {
      dataLoaderService.fileBasePath = testData.filePath
   }

   def cleanup() {    }

   void "Can find file"() {
      def file = testData.sstTextFile

      println "file: $file"

      expect:
         file.isFile()
   }

   void "Can load sst from test file"(){
      expect:
      dataLoaderService.loadDayFromLocalFile('[0][0:20:3599][0:20:7199]')
   }
}
