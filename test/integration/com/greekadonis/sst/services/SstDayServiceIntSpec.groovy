package com.greekadonis.sst.services

import com.greekadonis.sst.SSTDay
import com.greekadonis.sst.SSTDayLatitude
import org.joda.time.LocalDate
import spock.lang.Specification

class SstDayServiceIntSpec extends Specification {

   def sstDayService

   void "First loaded day is null, if no days populated"() {

      SSTDay first = sstDayService.findFirstLoadedDay()

      expect:
      !first
   }

   void "Can find first loaded day"() {

      [ new SSTDay(sstIndex: 0, dataset: 'dataset', time:LocalDate.now().toDate()),
        new SSTDay(sstIndex: 1, dataset: 'dataset', time:LocalDate.now().plusDays(1).toDate())
      ]*.save(flush: true, failOnError: true)

      SSTDay first = sstDayService.findFirstLoadedDay()

//--> TODO: NO logging from Integ tests??

      println "find first: $first"

      expect:
      first.sstIndex == 0
   }
}