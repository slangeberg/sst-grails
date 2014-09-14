package com.greekadonis.sst.services

import com.greekadonis.sst.SSTDayLongitudeValue
import groovy.sql.Sql
import org.apache.commons.lang3.time.StopWatch
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy

import javax.annotation.PostConstruct
import javax.sql.DataSource

class SaveWithSimpleJdbcService {

   static transactional = true

   def dataSource
   def sessionFactory


   @PostConstruct
   void setup(){
      log.info "setup()"
   }

//--> Todo: Try moving to batch update and compare times!

   /**
    * Times grew at slowest rate, after gorm insert, then jdbctemplate, now groovy Sql exe()
    2014-09-13 20:36:15,312 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 3663ms
    2014-09-13 20:36:23,286 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 7974ms
    2014-09-13 20:36:30,084 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 6797ms
    2014-09-13 20:36:42,702 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 12618ms
    2014-09-13 20:36:52,978 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 10277ms
    2014-09-13 20:37:09,643 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 16664ms
    2014-09-13 20:37:26,076 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 16434ms
    2014-09-13 20:37:47,501 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 21424ms
    2014-09-13 20:38:09,343 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 21842ms
    2014-09-13 20:38:35,462 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 26120ms
    */
   int insertSstDayLongitudeValue(Short analysed_sst, Long dayId) {
      final String table = "SSTDAY_LONGITUDE_VALUE"
      String query = "insert into $table (VERSION, ANALYSED_SST, DAY_ID) values (?, ?, ?)"
      sql.execute(query, 0, analysed_sst, dayId) ? 1 : 0
   }
   private Sql _sql

   Sql getSql() {
      _sql = _sql ?: Sql.newInstance(dataSource)//db.url, db.user, db.password, db.driver)
   }

   final int BATCH_SIZE = 50

   void insertLongitudeValues(List<SSTDayLongitudeValue> values){

      StopWatch timer = new StopWatch()
      timer.start()

      Session session = sessionFactory.openSession();
      Transaction tx = session.beginTransaction();
      Iterator<SSTDayLongitudeValue> iter = values.iterator()
      int i = 0

      log.info "insertLongValues(values.size: ${values?.size()}, trans open at ${timer.time}"

      timer.split()

      while(iter.hasNext()){
         session.save(iter.next());
         if( i % 10000 == 0 ){
            log.info "insertLongitudeValues() - 10000 LV records @ $i saved: ${timer.time-timer.splitTime}ms"
            timer.split()
         }
         if ( ++i % BATCH_SIZE == 0 ) { //same as the JDBC batch size
            //flush a batch of inserts and release memory:
            session.flush();
            session.clear();
         }
      }
      tx.commit();
      session.close();
   }
}
