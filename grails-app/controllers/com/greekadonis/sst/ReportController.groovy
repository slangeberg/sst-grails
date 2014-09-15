package com.greekadonis.sst

import static grails.async.Promises.*

import org.apache.commons.lang3.time.StopWatch

class ReportController {

   def reportService
   def systemConfigService

   def index() {
      render """
<h3>Reports</h3>
<p>
  <a href='./averages'>Averages</a>
</p>
"""
   }

   def averages() {
      task { // could be a while !
         StopWatch timer = new StopWatch()
         timer.start()


 //--> todo: Just return DailyAverageTemp objects instead of maps - then can get things like LongitudeValue counts, etc, right off that class??

         Map<SSTDay, Double> dailyAverages = reportService.getDailyAverages()
         SSTDay firstDay = dailyAverages?.keySet()[0]
         Long numLongValues = SSTDayLongitudeValue.countByDay(firstDay)

         String page = """
   <style>
      td { padding-right: .7em; }
   </style>

   Lat/lon step size (1 is smallest, 2 is every other, etc.): ${systemConfigService.stepSize} <br/>
   #days: ${dailyAverages?.size()},
   #longitudes/day: $numLongValues,
   time: $timer<br/>
   <br/>
   <h3>avg daily temps:</h3> <br/>
   <table>
     <tr>
       <th>SST Index</th>
       <th>Date</th>
       <th>Daily avg.</th>
     </tr>
   """
         dailyAverages.each { SSTDay day, Double average ->
            page += "<tr><td>${day?.sstIndex}</td><td>${day?.time}</td><td>${average}</td></tr>"
         }
         page += "</table>"
         render page
      }
   }
}