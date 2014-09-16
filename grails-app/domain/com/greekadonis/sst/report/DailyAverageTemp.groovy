package com.greekadonis.sst.report

import com.greekadonis.sst.SSTDay
import com.greekadonis.sst.SSTDayLongitudeValue
import groovyx.gpars.GParsPool
import org.apache.commons.lang3.time.StopWatch

class DailyAverageTemp {

   static belongsTo = [day: SSTDay]

   Double value

   Double getValue() {
      if( value ){
         log.debug "getValue() - db HIT"

      } else {
         log.debug "getValue() - db MISS"

         StopWatch timer = new StopWatch()
         timer.start()

         Double result = null

         int count = 0
         Double sum = 0

         List<SSTDayLongitudeValue> values = SSTDayLongitudeValue.findAllWhere([day: day])
         values*.discard()

         GParsPool.withPool {
            sum = values.parallel
               .map { SSTDayLongitudeValue value ->
               short sst = 0
               if (value.isNotEmptyValue()) {
                  count++
                  sst = value.analysed_sst
               }
               sst
            }
            .sum()
         }
         if (count > 0) {
            result = sum / count
         }

         value = result

         save()

         if( log.infoEnabled ) {
            log.info "getValue() day.sstIndex: ${day?.sstIndex}, sum: $sum, count: $count, result: $result"
            log.info "getValue() - done and saved() in ${timer}"
         }
      }
      value
   }

   Double triggerCalculation(){
      getValue()
   }
}
