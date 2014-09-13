package com.greekadonis.sst.services

import com.greekadonis.sst.SSTDayLongitudeValue
import org.springframework.jdbc.core.JdbcTemplate

class SaveWithSimpleJdbcService {

   static transactional = true

   def dataSource

//--> Todo: Try moving to batch update and compare times!

   int insertSstDayLongitudeValue(Short analysed_sst, Long dayId) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource)
      final String table = "SSTDAY_LONGITUDE_VALUE"
      log.trace "datasource: $dataSource"
//      println "tables: "+jdbcTemplate.queryForList("show tables")
//      println "describe: "+jdbcTemplate.queryForList("show columns from $table")

      //println "$table.count: " + jdbcTemplate.queryForList("select count(*) from $table")

      String query = "insert into $table (VERSION, ANALYSED_SST, DAY_ID) values (?, ?, ?)"
      jdbcTemplate.update(query, 0, analysed_sst, dayId)
   }
}
