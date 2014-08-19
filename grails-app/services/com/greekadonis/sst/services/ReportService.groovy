package com.greekadonis.sst.services

import com.greekadonis.sst.SSTDay
import com.greekadonis.sst.SSTDayLatitude
import com.greekadonis.sst.SSTDayLongitude
import com.greekadonis.sst.SSTDayLongitudeValue
import grails.transaction.Transactional
import org.apache.commons.lang.math.RandomUtils
import org.apache.commons.lang3.time.StopWatch

@Transactional
class ReportService {

  List<SSTDay> mockDays = []

  public Map<SSTDay, Double> getDailyAverages(boolean mock) {
    getDailyAverages(mock, true)
  }

  //
  // @params mock - create mock data on server
  // @params cache - cache mock results, if any
  //
  public Map<SSTDay, Double> getDailyAverages(boolean mock, boolean cache) {
    log.info("getDailyAverages(mock: $mock, cache: $cache)")

    StopWatch timer = new StopWatch()
    timer.start()

    Map<SSTDay, Double> dailyAverages = new LinkedHashMap<SSTDay, Double>()
    List<SSTDay> days = []
    if( mock ){
      if( mockDays.empty || !cache ) {
        mockDays = [
          createDay(0, 10),
          createDay(1, 20),
          createDay(2, 30),
          createDay(3, 40)
        ]
      }
      days = mockDays
    }
    log.info("getDailyAverages() - got days in: ${timer.time}ms")

    days.each { SSTDay day ->

      timer.split()

      int count = 0
      Double sum = 0
      day.latitudes.each { SSTDayLatitude lat ->
        lat.longitudes.each { SSTDayLongitude lon ->
          count++
          sum += lon.values[0].analysed_sst
        }
      }
      dailyAverages[day] = sum / count

      log.debug "getDailyAverages() - calculated daily average in: ${timer.time - timer.splitTime}ms"
    }

    log.info("getDailyAverages() - DONE in: ${timer.time}ms")

    dailyAverages
  }

  ///////////////

  protected SSTDay createDay(int index, int value) {
    List<SSTDayLatitude> latitudes = []
    (0..99).each {
      latitudes << createLatitude(it, value)
    }
    new SSTDay(sstIndex: index, latitudes: latitudes)
  }

  protected SSTDayLatitude createLatitude(int lat, int analysed_sst) {
    List<SSTDayLatitude> longitudes = []
    int temp = 0
    (0..99).each {
      temp = analysed_sst + RandomUtils.nextInt(10)
      longitudes << new SSTDayLongitude(lon: it,
        values: [
          new SSTDayLongitudeValue(analysed_sst: temp)
        ])
    }
    new SSTDayLatitude(lat: lat, longitudes: longitudes)
  }
}
