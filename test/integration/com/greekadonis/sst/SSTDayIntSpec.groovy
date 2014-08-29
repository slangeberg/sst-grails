package com.greekadonis.sst

import org.joda.time.LocalDate
import spock.lang.*

class SSTDayIntSpec extends Specification {
    void "addTo* methods are populated"() {
       SSTDay day = new SSTDay( dataset: "Dataset {}", time: LocalDate.now())
          .addToLatitudes(new SSTDayLatitude(lat: 0.0))
          .save(flush: true, failOnError: true)

       expect:
       !day.latitudes.empty
    }


   void "Can query latitude by day"() {

      SSTDay day = new SSTDay( dataset: "Dataset {}", time: new LocalDate(2006, 04, 03))
         .addToLatitudes(new SSTDayLatitude(lat: 20.5))
         .save(flush: true, failOnError: true)

      SSTDayLatitude found = SSTDayLatitude.where {
         day.time > new LocalDate(2006, 04, 02)
      }.find()

      expect:
      day.time == found.day.time
   }

   void "Can query latitudes"() {

      SSTDay day = new SSTDay( dataset: "Dataset {}", time: new LocalDate(2006, 04, 03))
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
}
