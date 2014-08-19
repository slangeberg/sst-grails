import com.greekadonis.sst.SSTDay
import com.greekadonis.sst.SSTDayLatitude

class BootStrap {

    def init = { servletContext ->
//        if( SSTDayLatitude.count() > 0 )
//            SSTDayLatitude.where{ id != null }.deleteAll()
//        if( SSTDay.count() > 0 )
//            SSTDay.where{ id != null }.deleteAll()

//        if( SSTDayLatitude.count() == 0 ){
//            SSTDay day = new SSTDay(
//                  sstIndex: 0,
//                  time: new Date(2000, 01, 01))
//                .addToLatitudes(new SSTDayLatitude(lat: 0.5))
//                .addToLatitudes(new SSTDayLatitude(lat: 25.0))
//                .save(flush: true, failOnError: true)
//        }
    }
    def destroy = {
    }
}
