package com.greekadonis.sst

/*
The actual temp value at analysed_sst[time][lat][lon]
 */
class SSTDayLongitudeValue {

   public static final short EMPTY_VALUE = -32768;

   static belongsTo = [longitude:SSTDayLongitude]

   Short analysed_sst

   boolean isEmptyValue() {
      analysed_sst == EMPTY_VALUE
   }
}
