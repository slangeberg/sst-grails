package com.greekadonis.sst.catchup

import static grails.async.Promises.*

class SstCatchupProcessController {

  static scaffold = true

  def sstCatchupService

  def report() {
    render {
      if( sstCatchupService.catchupRunning() ) {
        render 'Catchup is running: ' + sstCatchupService.getAllRunning()

      } else {
        render {
          p 'None are running'
         // p catchupService.getProcessState()
          br()
          a(href: './run', 'Start catchup...')
          br()
          // a( href: './tbd', 'TBD...' )
        }
      }
    }
  }

  def run() {
    task { // just render async for now - async remote request, later?
      //render 'Started catchup: ' + sstCatchupService.runCatchup()
      render 'TBD...'
    }
  }

  def runNext() {
    task { // just render async for now - async remote request, later?
      render 'Ran next: ' + sstCatchupService.runNext()
    }
  }
}