package com.greekadonis.sst.services

import com.greekadonis.sst.SSTDay
import com.greekadonis.sst.data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(DataLoaderService)
class DataLoaderServiceSpec extends Specification {

   def sstDayService = Mock(SstDayService)
   def readerService = Mock(Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService)
   def systemConfigService = Mock(SystemConfigService)

   def mockDay = Mock(SSTDay)

   def setup() {
      service.sstDayService = sstDayService
      service.systemConfigService = systemConfigService
      service.sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService = readerService

      readerService.getDay(_ as String) >> mockDay
      systemConfigService.getLatitudeParameters() >> "[0:1]"
      systemConfigService.getLongitudeParameters() >> "[0:1]"
   }

   def cleanup() { }

   @Ignore("Stop loading real stuff!")
   void "Can load day when file content available"() {
      when:
      SSTDay day = service.loadDay(0)

      then:
      day == mockDay
   }

   void "Write more tests!!"() {

   }
}