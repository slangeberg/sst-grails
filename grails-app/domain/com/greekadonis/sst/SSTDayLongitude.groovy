package com.greekadonis.sst

class SSTDayLongitude {

   static belongsTo = [day:SSTDay, latitude:SSTDayLatitude]

   Double lon
   //SSTDayLongitudeValue value

   @Override
   String toString() {
      "[SSTDayLongitude - lon: $lon, latitude.lat: ${latitude.lat}]"
   }
}
