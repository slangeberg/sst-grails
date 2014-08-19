package com.greekadonis.sst

import org.apache.commons.lang3.time.StopWatch

class ReportController {

  def reportService

  def index() {
    render """
<h3>Reports</h3>
<p>
  <a href='./averages'>Averages</a>
</p>
"""
  }

  def averages() {

    StopWatch timer = new StopWatch()
    timer.start()

    Boolean cache = params.getBoolean('cache')

    Map<SSTDay, Double> dailyAverages = reportService.getDailyAverages(true, cache != null ? cache : true)

    SSTDay firstDay = dailyAverages.keySet()[0]

    String page = """
#days: ${dailyAverages.size()}, #latitudes/day: ${firstDay.latitudes.size()}, #longitudes/day: ${firstDay.latitudes[0].longitudes.size()}, time: ${timer.time}ms<br/>
<br/>
<h3>avg daily temps:</h3> <br/>
<table>
  <hr>
    <td>SST Index</td>
    <td>Daily avg.</td>
  </hr>
"""
    dailyAverages.each{ SSTDay key, Double val ->
      page += "<tr><td>${key.sstIndex}</td><td>${val}</td></tr>"
    }
    page += "</table>"
    render page
  }
}