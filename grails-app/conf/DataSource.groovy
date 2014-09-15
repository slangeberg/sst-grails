def h2DataSource = {
   pooled = true
   jmxExport = true
   driverClassName = "org.h2.Driver"
   username = "sa"
   password = ""
}
def mysqlDataSource = {

   //@Note: db loc under XAMPP: /Applications/XAMPP/xamppfiles/var/mysql/sst_grails_dev/

   jdbc.batch_size = 20
   dialect = org.hibernate.dialect.MySQL5InnoDBDialect
   driverClassName = "com.mysql.jdbc.Driver"
   username = "root"
   password = ""
}

def mysqlHostname = "localhost" //127.0.0.1

println "DataSource() - mysqlHostname: ${mysqlHostname}"

dataSource mysqlDataSource

hibernate {
   cache.use_second_level_cache = true
   cache.use_query_cache = false

   jdbc.batch_size = 50 // see if it helps batch insert?

//    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
   cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
   singleSession = true // configure OSIV singleSession mode
   flush.mode = 'manual' // OSIV session flush mode outside of transactional context
}

// environment specific settings
environments {
   development {
      dataSource {
         dbCreate = "create" // one of 'create', 'create-drop', 'update', 'validate', ''
         //url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
         url = "jdbc:mysql://${mysqlHostname}:3306/sst_grails_dev?useUnicode=yes&characterEncoding=UTF-8"
      }
   }
   test {
      dataSource {
         dbCreate = "create-drop"
//            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
         url = "jdbc:mysql://${mysqlHostname}:3306/sst_grails_test?useUnicode=yes&characterEncoding=UTF-8"
      }
   }
   production {
      dataSource {
//           url = "jdbc:h2:data/h2/sst-grails;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
         dbCreate = "update"
//            properties {
//               // See http://grails.org/doc/latest/guide/conf.html#dataSource for documentation
//               jmxEnabled = true
//               initialSize = 5
//               maxActive = 50
//               minIdle = 5
//               maxIdle = 25
//               maxWait = 10000
//               maxAge = 10 * 60000
//               timeBetweenEvictionRunsMillis = 5000
//               minEvictableIdleTimeMillis = 60000
//               validationQuery = "SELECT 1"
//               validationQueryTimeout = 3
//               validationInterval = 15000
//               testOnBorrow = true
//               testWhileIdle = true
//               testOnReturn = false
//               jdbcInterceptors = "ConnectionState"
//               defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
//            }

         url = "jdbc:mysql://${mysqlHostname}:3306/sst_grails?useUnicode=yes&characterEncoding=UTF-8"
      }
   }
}
