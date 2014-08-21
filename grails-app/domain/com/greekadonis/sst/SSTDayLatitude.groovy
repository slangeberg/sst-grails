package com.greekadonis.sst

class SSTDayLatitude {

   static belongsTo = [day:SSTDay]
   static hasMany = [longitudes: SSTDayLongitude]

   Double lat

   @Override
   String toString() {
      "[SSTDayLatitude - lat: $lat, day.id: ${day?.id}]"
   }
}
