package com.greekadonis.sst.services

import com.greekadonis.sst.SSTDay
import org.joda.time.LocalDate
import spock.lang.Specification

class SstDayServiceIntSpec extends Specification {

   def sstDayService

   /*
       void "Can query latitude by day"() {

        SSTDay day = new SSTDay(time: new LocalDate(2006, 04, 03))
            .addToLatitudes(new SSTDayLatitude(lat: 20.5))
            .save(flush: true, failOnError: true)

        SSTDayLatitude found = SSTDayLatitude.where {
            day.time > new Date(2006, 04, 02)
        }.find()

        expect:
        day.time == found.day.time
    }

    void "Can query latitudes"() {

        SSTDay day = new SSTDay(time: new LocalDate(2006, 04, 03))
            .addToLatitudes(new SSTDayLatitude(lat: 0.5))
            .addToLatitudes(new SSTDayLatitude(lat: 10.0))
            .addToLatitudes(new SSTDayLatitude(lat: 15.0))
            .addToLatitudes(new SSTDayLatitude(lat: 25.0))
            .save(flush: true, failOnError: true)

        def query = SSTDayLatitude.where {
            lat > 10 && lat < 25
        }

        expect:
        Set all = query.findAll()
        all.size() == 1
        all[0].lat == 15
    }
    */

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