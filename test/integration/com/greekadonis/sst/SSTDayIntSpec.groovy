package com.greekadonis.sst

import org.joda.time.LocalDate
import spock.lang.*

class SSTDayIntSpec extends Specification {
    void "addTo* methods are populated"() {
       SSTDay day = new SSTDay(
             dataset: "dataset",
             time: LocalDate.now())
          .addToLatitudes(new SSTDayLatitude(lat: 0.0))
          .save(flush: true, failOnError: true)

       expect:
       !day.latitudes.empty
    }
}
