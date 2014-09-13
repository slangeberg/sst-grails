package com.greekadonis.sst

class SSTDayLatitude {

   static belongsTo = [day:SSTDay]
   static hasMany = [longitudes: SSTDayLongitude]

   Double lat

   void setDay(SSTDay day) {
      this.day = day
      longitudes.each { SSTDayLongitude longitude -> longitude.day = day }
   }

   @Override
   String toString() {
      "[SSTDayLatitude - lat: $lat, day: $day, longitudes: $longitudes]"
   }
}
