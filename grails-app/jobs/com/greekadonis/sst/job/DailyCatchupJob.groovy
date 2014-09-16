package com.greekadonis.sst.job

import com.greekadonis.sst.services.SstCatchupService

class DailyCatchupJob {

   static int DELAY_IN_SECONDS = 2 * 60

   static triggers = {
      //simple repeatInterval: DELAY_IN_SECONDS * 1000l // execute job once in N seconds

      //cron name:'cronTrigger', startDelay:10000, cronExpression: '0/6 * 15 * * ?'

      //fire every 6 second during 15th hour (15:00:00, 15:00:06, 15:00:12, … — this config
      //   0/6 * 15 * * ?

      // Fire every 5 minutes starting at 2pm and ending at 2:55pm, every day
      //   0 0/5 14 * * ?


      // Fire every 2 minutes starting at 6pm and ending at 11:58pm, every day
      // - second trigger covers midnight to 8
      cron name:'dailyCatchupTriggerPM', startDelay:10000, cronExpression: '0 0/2 18-23 * * ?'
      cron name:'dailyCatchupTriggerAM', startDelay:10000, cronExpression: '0 0/2 0-8 * * ?'
   }

   def concurrent = false

   SstCatchupService sstCatchupService

   def execute() {
      println "======================================="
      print "DailyCatchupJob.execute() - delay (sec): $DELAY_IN_SECONDS, concurrent: $concurrent"
      println "---------------------------------------"
      sstCatchupService.runNext()
   }
}
//class MyJob {
//   static triggers = {
//      simple name: 'mySimpleTrigger', startDelay: 60000, repeatInterval: 1000
//   }
//   def group = "MyGroup"
//   def description = "Example job with Simple Trigger"
//   def execute(){
//      print "Job run!"
//   }
//}