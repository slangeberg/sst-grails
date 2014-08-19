package com.greekadonis.sst.services

import com.greekadonis.sst.SSTDay
import com.greekadonis.sst.catchup.SstCatchupProcess
import grails.transaction.Transactional
import org.apache.commons.lang3.time.StopWatch
import org.joda.time.LocalDateTime

@Transactional
class SstCatchupService {

  def dataLoaderService
  def sstDayService

  /**
   * Catch up next necessary day
   */
  def runNext() {
    boolean isRunning = catchupRunning()
    log.debug("runCatchup() - isRunning: $isRunning")

    //CatchupProcessState catchupProcessState = getProcessState()

    if( isRunning ){
      def running = getAllRunning()
      return "Catchup is already running ${running.size()} processes: <br/>${running}"

    } else {
      //start one

      StopWatch timer = new StopWatch()
      timer.start()

      SSTDay day = sstDayService.findLastLoadedDay()
      day = runCatchupForDay(day ? day.sstIndex + 1 : 0)

      log.info "runCatchup() - went thru days in ${timer.time}ms"

      return "Ran catchup for: $day"
    }
  }

  def runCatchup(){
    boolean isRunning = catchupRunning()
    log.debug("runCatchup() - isRunning: $isRunning")

    //CatchupProcessState catchupProcessState = getProcessState()

    if( isRunning ){
      def running = getAllRunning()
      return "Catchup is already running ${running.size()} processes: <br/>${running}"

    } else {
      //start one

      StopWatch timer = new StopWatch()
      timer.start()

     // SstCatchupProcess process = null

      SSTDay day = null

//--> TODO: Don't hard-code upper limit!!
      for( i in 0..3000 ) {
        day = runCatchupForDay(i)
        if( !day ){
          //let's stop trying, for now ;)
          break
        }
      }
      log.info "runCatchup() - went thru days in ${timer.time}ms"

      return "Last loaded day: $day"
    }
  }

  SSTDay runCatchupForDay( int sstIndex ) {

    log.info "runCatchupForDay($sstIndex)"

    SSTDay day = null

    if( catchupRunning() ){
      log.warn "runCatchupForDay($sstIndex) requested, but process is already running"

    } else {
      SstCatchupProcess process = new SstCatchupProcess(
          sstIndex: sstIndex, running: true, analysed_sst: dataLoaderService.getAnalysedSstParams(sstIndex),
          startDate: LocalDateTime.now())
        .save(flush: true, failOnError: true)

      day = dataLoaderService.loadDay(sstIndex) //@NOTE: Potentially long-running
      if (day) {
        log.debug "runCatchupForDay() - found day: $day"

        if( !day.id ) {
          //verify state of day
          assert day.time
          assert !day.latitudes?.empty
          assert !day.latitudes[0].longitudes?.empty

          // All good,   persist
          day.save(flush: true, failOnError: true)
        }
        process.success = true

      } else {

//--> todo: track why failed?
        // - someday break process into tasks and track each?

        process.success = false

        log.warn "runCatchupForDay() - Day missing at index: ${sstIndex}, check for file"
      }
      process.running = false
      process.endDate = LocalDateTime.now()
      process.save(failOnError: true, flush: true)
    }
    day
  }

  boolean catchupRunning() {
    List running = getAllRunning()
    log.info("catchupRunning(): running: $running")
    !running.empty
  }

  List<SstCatchupProcess> getAllRunning() {
    SstCatchupProcess.findAll(new SstCatchupProcess(running: true))
  }
//
//  CatchupProcessState getProcessState(){
//    CatchupProcessState.findOrSaveWhere([id: 1L]) //only one instance
//  }
}