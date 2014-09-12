package com.greekadonis.sst.services

import com.greekadonis.sst.test.TestData
import spock.lang.*

/**
 *
 */
class DataLoaderServiceIntgSpec extends Specification {

   TestData testData = new TestData()

   def dataLoaderService

   def setup() { }

   def cleanup() {    }

   void "Can find file"() {
      def file = testData.sstTextFile

      println "file: $file"

      expect:
         file.isFile()
   }
}
