package com.greekadonis.sst.services

import com.greekadonis.sst.SSTDay
import com.greekadonis.sst.data.SstDataHelper
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(DataLoaderService)

class DataLoaderServiceSpec extends Specification {

   SstDayService sstDayService = Mock(SstDayService)

   def setup() {
      service.sstDayService = sstDayService
      service.testFileContents = new SstDataHelper().createResult()
   }

   def cleanup() {
   }

   void "Can load day when file content available"() {
      when:
      SSTDay day = service.loadDay(0)

      then:
      day.time != null
      !day.latitudes.empty
      !day.latitudes[0].longitudes.empty
   }
}
