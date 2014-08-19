package com.greekadonis.sst

class SSTDayLongitude {

   static hasMany = [values: SSTDayLongitudeValue]

//    static belongsTo = [day:SSTDayLatitude]

   Double lon

   public SSTDayLongitude() {
      values = []
   }
}
