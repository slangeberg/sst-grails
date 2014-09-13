package com.greekadonis.sst.model

import org.joda.time.LocalDate

class SstDayModel {

   LocalDate time

   int sstIndex
   String dataset

       //  latitudes: new ArrayList<SSTDayLatitude>(),
   List latValues = new ArrayList<Double>() // Numeric latitude in degrees from feed
   List lonValues = new ArrayList<Double>() // Numeric longitude from feed

   @Override
   String toString() {
      "[SstDayModel - id: $id, sstIndex: $sstIndex, time: $time}]"
   }
}
