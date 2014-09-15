package com.greekadonis.sst.services

import com.greekadonis.sst.SSTDayLongitudeValue
import groovy.sql.BatchingPreparedStatementWrapper
import groovy.sql.Sql
import org.apache.commons.lang3.time.StopWatch
import org.hibernate.SessionFactory
import org.hibernate.StatelessSession
import org.hibernate.Transaction

class SaveWithSimpleJdbcService {

   static transactional = true

   def dataSource
   SessionFactory sessionFactory

   final int BATCH_SIZE = 50 //matches jdbc_batch setting, if that helps

   def insertLongitudeValuesGroovySqlBatch(List<SSTDayLongitudeValue> values) {
      StopWatch timer = new StopWatch()
      timer.start()

      final String query = 'insert into SSTDAY_LONGITUDE_VALUE (VERSION, ANALYSED_SST, DAY_ID) values (?, ?, ?)'

      Sql mySql = Sql.newInstance(dataSource)
      Long dayId = values[0].dayId

      log.info "insertLongitudeValuesGroovySqlBatch() dayId: $dayId, values.size: ${values?.size()}"

      timer.split()

      mySql.withBatch(BATCH_SIZE, query ) { BatchingPreparedStatementWrapper ps ->
         for( SSTDayLongitudeValue value : values) {
            ps.addBatch 0, value.analysed_sst, dayId
         }
      }

      mySql.close()

      log.info "insertLongitudeValuesGroovySqlBatch() - inserts with batchSize: $BATCH_SIZE done in $timer"
   }


   void insertLongitudeValuesWithStatelessSession(List<SSTDayLongitudeValue> values){

      StopWatch timer = new StopWatch()
      timer.start()

      StatelessSession session = sessionFactory.openStatelessSession();
      Transaction tx = session.beginTransaction();
      Iterator<SSTDayLongitudeValue> iter = values.iterator()
      int i = 0

      log.info "insertLongitudeValuesWithStatelessSession(values.size: ${values?.size()}, trans open at ${timer.time}ms"

      timer.split()

      def id = null

      while(iter.hasNext())

         id = session.insert(iter.next());

         if( ++i % 10000 == 0 ){
           log.info "insertLongitudeValuesWithStatelessSession() - last id: $id - 10000 (stateless) LV records @ $i saved: ${timer.time-timer.splitTime}ms"
           timer.split(){
         }
      }
      tx.commit();
      session.close();
   }
}
