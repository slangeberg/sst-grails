package com.greekadonis.sst.services

import com.greekadonis.sst.SSTDayLongitudeValue
import groovy.sql.BatchingPreparedStatementWrapper
import groovy.sql.Sql
import org.apache.commons.lang3.time.StopWatch
import org.hibernate.SessionFactory
import org.hibernate.StatelessSession
import org.hibernate.Transaction
import org.springframework.transaction.annotation.Transactional

import javax.annotation.PostConstruct
import java.sql.Connection
import java.sql.PreparedStatement

@Transactional
class SaveWithSimpleJdbcService {

   static transactional = false

   def dataSource
   SessionFactory sessionFactory


   @PostConstruct
   void setup(){
      log.info "setup()"
   }

//--> Todo: Try moving to batch update and compare times!

   /**
    * Times grew at slowest rate, after gorm insert, then jdbctemplate, now groovy Sql exe()
    2014-09-13 20:36:15,312 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 	3663
    2014-09-13 20:36:23,286 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 	7974
    2014-09-13 20:36:30,084 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 	6797
    2014-09-13 20:36:42,702 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 	12618
    2014-09-13 20:36:52,978 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 	10277
    2014-09-13 20:37:09,643 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 	16664
    2014-09-13 20:37:26,076 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 	16434
    2014-09-13 20:37:47,501 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 	21424
    2014-09-13 20:38:09,343 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 	21842
    2014-09-13 20:38:35,462 [http-bio-8888-exec-4] INFO  data.Sst_ALL_UKMO_L4HRfnd_GLOB_OSTIA_v01_fv02_ReaderService  - getDay() - 10000 LongitudeValue records inserted (no batching) in: 	26120

    now batches of 20 - better right?:
                             http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted batchSize: 10000, in 1038ms
    2014-09-13 23:34:21,209 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted batchSize: 20000, in 2693ms
    2014-09-13 23:34:25,572 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted batchSize: 30000, in 4363ms
    2014-09-13 23:34:31,599 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted batchSize: 40000, in 6027ms
    2014-09-13 23:34:38,943 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted batchSize: 50000, in 7344ms
    2014-09-13 23:34:47,532 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted batchSize: 60000, in 8588ms
    2014-09-13 23:34:57,993 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted batchSize: 70000, in 10460ms
    2014-09-13 23:35:09,961 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted batchSize: 80000, in 11968ms
    2014-09-13 23:35:23,782 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted batchSize: 90000, in 13821ms
    2014-09-13 23:35:39,532 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted batchSize: 100000, in 15750ms

    (groovy sql with prepared statements)
    2014-09-14 15:00:27,610 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - ... batchSize: 20 @ 10000 in 2054ms, total time: 0:00:02.056
    2014-09-14 15:00:32,827 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - ... batchSize: 20 @ 20000 in 5216ms, total time: 0:00:07.277
    2014-09-14 15:00:43,634 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - ... batchSize: 20 @ 30000 in 10807ms, total time: 0:00:18.084
    2014-09-14 15:01:00,591 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - ... batchSize: 20 @ 40000 in 16956ms, total time: 0:00:35.041
    2014-09-14 15:01:24,763 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - ... batchSize: 20 @ 50000 in 24172ms, total time: 0:00:59.213
    2014-09-14 15:01:49,125 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - ... batchSize: 20 @ 60000 in 24362ms, total time: 0:01:23.575
    2014-09-14 15:02:03,195 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - ... batchSize: 20 @ 70000 in 14070ms, total time: 0:01:37.645
    2014-09-14 15:02:21,936 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - ... batchSize: 20 @ 80000 in 18741ms, total time: 0:01:56.386
    2014-09-14 15:02:46,698 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - ... batchSize: 20 @ 90000 in 24761ms, total time: 0:02:21.148
    2014-09-14 15:03:18,268 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - ... batchSize: 20 @ 100000 in 31569ms, total time: 0:02:52.718

    and 200
    2014-09-13 23:38:32,709 [http-bio-8888-exec-7] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 200 @ count: 10000 in 951ms
    2014-09-13 23:38:35,391 [http-bio-8888-exec-7] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 200 @ count: 20000 in 2682ms
    2014-09-13 23:38:39,767 [http-bio-8888-exec-7] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 200 @ count: 30000 in 4376ms
    2014-09-13 23:38:45,815 [http-bio-8888-exec-7] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 200 @ count: 40000 in 6048ms
    2014-09-13 23:38:53,154 [http-bio-8888-exec-7] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 200 @ count: 50000 in 7339ms
    2014-09-13 23:39:01,850 [http-bio-8888-exec-7] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 200 @ count: 60000 in 8696ms
    2014-09-13 23:39:11,930 [http-bio-8888-exec-7] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 200 @ count: 70000 in 10080ms
    2014-09-13 23:39:23,681 [http-bio-8888-exec-7] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 200 @ count: 80000 in 11751ms
    2014-09-13 23:39:37,303 [http-bio-8888-exec-7] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 200 @ count: 90000 in 13622ms
    2014-09-13 23:39:52,793 [http-bio-8888-exec-7] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 200 @ count: 100000 in 15490ms

    and 2000
    2014-09-13 23:43:03,898 [http-bio-8888-exec-3] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 2000 @ count: 10000 in 908ms
    2014-09-13 23:43:06,220 [http-bio-8888-exec-3] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 2000 @ count: 20000 in 2322ms
    2014-09-13 23:43:10,247 [http-bio-8888-exec-3] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 2000 @ count: 30000 in 4027ms
    2014-09-13 23:43:15,813 [http-bio-8888-exec-3] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 2000 @ count: 40000 in 5566ms
    2014-09-13 23:43:22,743 [http-bio-8888-exec-3] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 2000 @ count: 50000 in 6930ms
    2014-09-13 23:43:31,183 [http-bio-8888-exec-3] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 2000 @ count: 60000 in 8440ms
    2014-09-13 23:43:40,942 [http-bio-8888-exec-3] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 2000 @ count: 70000 in 9758ms
    2014-09-13 23:43:52,507 [http-bio-8888-exec-3] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 2000 @ count: 80000 in 11565ms
    2014-09-13 23:44:06,114 [http-bio-8888-exec-3] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 2000 @ count: 90000 in 13606ms
    2014-09-13 23:44:21,289 [http-bio-8888-exec-3] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesGroovySql() - inserted w/batchSize: 2000 @ count: 100000 in 15175ms
    ->> runCatchup() - went thru day in 570994ms



    */
   int insertSstDayLongitudeValue(Short analysed_sst, Long dayId) {
      final String table = "SSTDAY_LONGITUDE_VALUE"
      String query = "insert into $table (VERSION, ANALYSED_SST, DAY_ID) values (?, ?, ?)"
      sql.execute(query, 0, analysed_sst, dayId) ? 1 : 0
   }

   int batchSize = 200

   def insertLongitudeValuesGroovySql(Long dayId, List<Short> values){
      StopWatch timer = new StopWatch()
      timer.start()

      final String table = 'SSTDAY_LONGITUDE_VALUE'
      int count = 0
      final String baseQuery = "insert into $table (VERSION, ANALYSED_SST, DAY_ID) values "

      String query = baseQuery

      log.info "insertLongitudeValuesGroovySql(transactional: false) - values.size: ${values?.size()}"

      timer.split()

      Sql mySql = Sql.newInstance(dataSource)

      // All-in batch :)
    //  batchSize = values.size()


//--> TODO: !!!!!!! get this out of transactional hibernate / spring calls - move to controller invoked?


//--> Todo: Make sure we pick up records at end, orphaned after batchSize


      for( Short value : values) {
         count++
         if( count % batchSize == 0 ) {
            query += "(0, $value, $dayId)" //no comma

            mySql.execute query

            query = baseQuery

         } else {
            query += "(0, $value, $dayId), " //yes comma!
         }
         if( count % 10000 == 0 ){
            log.info "... batchSize: $batchSize @ $count in ${timer.time-timer.splitTime}ms, total time: $timer"

            timer.split()
         }
      }
      mySql.close()

      log.info "insertLongitudeValuesGroovySql() - insertion done in $timer"
   }

   def insertLongitudeValuesGroovySqlBatch(Long dayId, List<Short> values){
      StopWatch timer = new StopWatch()
      timer.start()

      int count = 0
      final String baseQuery = 'insert into SSTDAY_LONGITUDE_VALUE (VERSION, ANALYSED_SST, DAY_ID) values (?, ?, ?)'

      log.info "insertLongitudeValuesGroovySqlBatch(transactional: false) - values.size: ${values?.size()}"

      timer.split()

      Sql mySql = Sql.newInstance(dataSource)

//--> TODO: !!!!!!! get this out of transactional hibernate / spring calls - move to controller invoked?

      int batchSize = 20

      sql.withBatch(batchSize, baseQuery ) { BatchingPreparedStatementWrapper ps ->
         for( Short value : values) {
            count++

            ps.addBatch 0, value, dayId

            if( count % 10000 == 0 ){
               log.info "... batchSize: $batchSize @ $count in ${timer.time-timer.splitTime}ms, total time: $timer"

               timer.split()
            }
         }
      }

      mySql.close()

      log.info "insertLongitudeValuesGroovySqlBatch() - insertion done in $timer"
   }

   def insertLongitudeValuesViaCSV(Long dayId, List<Short> values){
      StopWatch timer = new StopWatch()
      timer.start()

      int count = 0
      final String baseQuery = 'insert into SSTDAY_LONGITUDE_VALUE (VERSION, ANALYSED_SST, DAY_ID) values (?, ?, ?)'

      log.info "insertLongitudeValuesViaCSV(transactional: false) - values.size: ${values?.size()}"

      timer.split()

      Sql mysql = Sql.newInstance(dataSource)
      String query = "insert into SSTDAY_LONGITUDE_VALUE (VERSION, ANALYSED_SST, DAY_ID) SELECT * FROM CSVREAD('data/test.csv')"
      mysql.execute(query) { isResultSet, result ->
         log.info "isResultSet: $isResultSet, result: $result"
      }

      mysql.close()

      log.info "insertLongitudeValuesViaCSV() - insertion done in $timer"
   }

   private Sql _sql

   Sql getSql() {
      _sql = _sql ?: Sql.newInstance(dataSource)//db.url, db.user, db.password, db.driver)
   }

   final int BATCH_SIZE = 50

   /*
..trying stateless session!!...
2014-09-13 21:34:24,531 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValues() - last id: 10000 - 10000 (stateless) LV records @ 10000 saved: 	2436
2014-09-13 21:34:29,545 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValues() - last id: 20000 - 10000 (stateless) LV records @ 20000 saved: 	5014
2014-09-13 21:34:39,529 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValues() - last id: 30000 - 10000 (stateless) LV records @ 30000 saved: 	9982
2014-09-13 21:34:48,892 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValues() - last id: 40000 - 10000 (stateless) LV records @ 40000 saved: 	9363
2014-09-13 21:34:58,592 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValues() - last id: 50000 - 10000 (stateless) LV records @ 50000 saved: 	9700
2014-09-13 21:35:13,595 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValues() - last id: 60000 - 10000 (stateless) LV records @ 60000 saved: 	15003
2014-09-13 21:35:34,268 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValues() - last id: 70000 - 10000 (stateless) LV records @ 70000 saved: 	20673
2014-09-13 21:36:00,593 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValues() - last id: 80000 - 10000 (stateless) LV records @ 80000 saved: 	26325
2014-09-13 21:36:33,037 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValues() - last id: 90000 - 10000 (stateless) LV records @ 90000 saved: 	32444
2014-09-13 21:37:11,943 [http-bio-8888-exec-4] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValues() - last id: 100000 - 10000 (stateless) LV records @ 100000 saved: 	38906


.. def better than stateful..

    */
   void insertLongitudeValues(List<SSTDayLongitudeValue> values){

      StopWatch timer = new StopWatch()
      timer.start()

      StatelessSession session = sessionFactory.openStatelessSession();
      Transaction tx = session.beginTransaction();
      Iterator<SSTDayLongitudeValue> iter = values.iterator()
      int i = 0

      log.info "insertLongValues(values.size: ${values?.size()}, trans open at ${timer.time}ms"

      timer.split()

      def id = null

      while(iter.hasNext()){
         id = session.insert(iter.next());
         if( ++i % 10000 == 0 ){
           log.info "insertLongitudeValues() - last id: $id - 10000 (stateless) LV records @ $i saved: ${timer.time-timer.splitTime}ms"
            timer.split()
         }
//         if ( ++i % BATCH_SIZE == 0 ) { //same as the JDBC batch size
//            //flush a batch of inserts and release memory:
//            session.flush();
//            session.clear();
//         }
      }
      tx.commit();
      session.close();
   }

   /** jdbc times
    * 2014-09-13 22:18:25,346 [http-bio-8888-exec-6] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesJdbcBatch() - 10000 (jdbc batch) LV records @ 10000 in: 	1225
    2014-09-13 22:18:29,553 [http-bio-8888-exec-6] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesJdbcBatch() - 10000 (jdbc batch) LV records @ 20000 in: 	4207
    2014-09-13 22:18:38,777 [http-bio-8888-exec-6] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesJdbcBatch() - 10000 (jdbc batch) LV records @ 30000 in: 	9224
    2014-09-13 22:18:52,727 [http-bio-8888-exec-6] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesJdbcBatch() - 10000 (jdbc batch) LV records @ 40000 in: 	13950
    2014-09-13 22:19:05,189 [http-bio-8888-exec-6] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesJdbcBatch() - 10000 (jdbc batch) LV records @ 50000 in: 	12462
    2014-09-13 22:19:20,071 [http-bio-8888-exec-6] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesJdbcBatch() - 10000 (jdbc batch) LV records @ 60000 in: 	14881
    2014-09-13 22:19:40,440 [http-bio-8888-exec-6] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesJdbcBatch() - 10000 (jdbc batch) LV records @ 70000 in: 	20369
    2014-09-13 22:20:06,802 [http-bio-8888-exec-6] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesJdbcBatch() - 10000 (jdbc batch) LV records @ 80000 in: 	26362
    2014-09-13 22:20:38,343 [http-bio-8888-exec-6] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesJdbcBatch() - 10000 (jdbc batch) LV records @ 90000 in: 	31541
    2014-09-13 22:21:16,142 [http-bio-8888-exec-6] INFO  services.SaveWithSimpleJdbcService  - insertLongitudeValuesJdbcBatch() - 10000 (jdbc batch) LV records @ 100000 in: 	37799

    */
   void insertLongitudeValuesJdbcBatch(List<SSTDayLongitudeValue> values){
      //DataSource ds = jdbcTemplate.getDataSource();

      StopWatch timer = new StopWatch()
      timer.start()

      Connection connection = dataSource.getConnection();
      connection.setAutoCommit(false);

      final String table = "SSTDAY_LONGITUDE_VALUE"
      String query = "insert into $table (VERSION, ANALYSED_SST, DAY_ID) values (?, ?, ?)"

      PreparedStatement ps = connection.prepareStatement(query);

      final int batchSize = 1000;
      int count = 0;

      Long dayId = values[0].dayId

      log.info "insertLongitudeValuesJdbcBatch(values.size: ${values?.size()}, connection open at ${timer.time}ms"

      timer.split()

      for (SSTDayLongitudeValue value : values) {

         ps.setLong(1, 0);
         ps.setShort(2, value.analysed_sst);
         ps.setLong(3, dayId);
         ps.addBatch();

         count++

         if(count % batchSize == 0) {
            ps.executeBatch();
            ps.clearBatch();
         }
         if( count % 10000 == 0 ){
            log.info "insertLongitudeValuesJdbcBatch() - 10000 (jdbc batch) LV records @ $count in: ${timer.time-timer.splitTime}ms"
            timer.split()
         }
      }
      ps.executeBatch(); // insert remaining records
      ps.clearBatch();
      connection.commit();
      ps.close();
   }

   void insertLongitudeValuesJdbcBatch(long dayId, List<Short> values){
      //DataSource ds = jdbcTemplate.getDataSource();

      StopWatch timer = new StopWatch()
      timer.start()

      Connection connection = dataSource.getConnection();
      connection.setAutoCommit(false);

      final String table = "SSTDAY_LONGITUDE_VALUE"
      String query = "insert into $table (VERSION, ANALYSED_SST, DAY_ID) values (?, ?, ?)"

      PreparedStatement ps = connection.prepareStatement(query);

      final int batchSize = 1000;
      int count = 0;

      log.info "insertLongitudeValuesJdbcBatch(2) values.size: ${values?.size()}, connection open at ${timer.time}ms"

      timer.split()

      for (Short value : values) {

         ps.setLong(1, 0);
         ps.setShort(2, value);
         ps.setLong(3, dayId);
         ps.addBatch();

         count++

         if(count % batchSize == 0) {
            ps.executeBatch();
            ps.clearBatch();
         }
         if( count % 10000 == 0 ){
            log.info "insertLongitudeValuesJdbcBatch(2) - 10000 (jdbc batch) LV records @ $count in: ${timer.time-timer.splitTime}ms"
            timer.split()
         }
      }
      ps.executeBatch(); // insert remaining records
      ps.clearBatch();
      connection.commit();
      ps.close();
   }


}
