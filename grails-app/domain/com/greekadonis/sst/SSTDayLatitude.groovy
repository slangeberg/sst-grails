package com.greekadonis.sst

class SSTDayLatitude {

   static belongsTo = [day:SSTDay]
   static hasMany = [longitudes: SSTDayLongitude]

   Double lat

   public SSTDayLatitude() {
      longitudes = []
   }

   @Override
   String toString() {
      "[SSTDayLatitude - lat: $lat, day: $day]"
   }
}
