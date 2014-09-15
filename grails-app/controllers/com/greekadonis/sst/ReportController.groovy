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
    //  task { // could be a while !
         StopWatch timer = new StopWatch()
         timer.start()

         Boolean cache = params.getBoolean('cache')
         Boolean mock = params.getBoolean('mock')

         SSTDay firstDay = SSTDay.findWhere([sstIndex: 0]) //dailyAverages?.keySet()[0]

         List longitudeValues = SSTDayLongitudeValue.findAllWhere([day: firstDay])

         Double sum = 0

         longitudeValues.each { SSTDayLongitudeValue value ->
            sum += value.analysed_sst
         }

         Map<SSTDay, Double> dailyAverages = reportService.getDailyAverages()

         String page = """
   <style>
      td { padding-right: .7em; }
   </style>

   Lat/lon step size (1 is smallest, 2 is every other, etc.): ${systemConfigService.stepSize} <br/>
   #days: ${dailyAverages?.size()},
   #longitudes/day: ${longitudeValues?.size()},
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
    //  }
   }
}