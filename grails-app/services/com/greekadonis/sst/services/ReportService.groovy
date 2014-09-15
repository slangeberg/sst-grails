package com.greekadonis.sst.services

import com.greekadonis.sst.SSTDay
import com.greekadonis.sst.SSTDayLongitudeValue
import com.greekadonis.sst.model.SstDayModel
import com.greekadonis.sst.report.DailyAverageTemp
import grails.transaction.Transactional
import groovyx.gpars.GParsPool
import org.apache.commons.lang3.time.StopWatch

@Transactional
class ReportService {

   public Map<SstDayModel, Double> getDailyAverages() {
      log.info("getDailyAverages()")

      StopWatch timer = new StopWatch()
      timer.start()

      Map<SstDayModel, Double> dailyAverages = new LinkedHashMap<SstDayModel, Double>()
      List<SSTDay> days =  SSTDay.list(sort:'sstIndex', order: 'asc')

      log.info("getDailyAverages() - got ${days?.size()} days at: ${timer.time}ms")

//--> todo: Perform one day at time. parallelize as possible and persist results!

      days.each { SSTDay day ->

         timer.split()

         dailyAverages[day] = getDailyAverage(day)

         log.debug "getDailyAverages() - calculated daily average in: ${timer.time - timer.splitTime}ms"
      }
      log.info("getDailyAverages() - DONE in: ${timer}")

      dailyAverages
   }

   Double getDailyAverage(SSTDay day){
      log.info "getDailyAverage(day.sstIndex: ${day?.sstIndex})"

      DailyAverageTemp averageTemp = DailyAverageTemp.findWhere([day: day])
      if( !averageTemp ){
         averageTemp = new DailyAverageTemp(day: day)
         averageTemp.save()
      }
      averageTemp.value
   }

}