package com.greekadonis.sst.services

import grails.transaction.Transactional

//@Transactional
class SystemConfigService {

   // @see: DDS
   public static final int MAX_LAT = 3600 - 1;
   public static final int MAX_LON = 7200 - 1;

   def grailsApplication

   //how many to skip: 1 = every step, 2 = every other, etc.
   int getStepSize() {
      int latLonStepSize = grailsApplication.config.com.greekadonis.sst.latLonStepSize
      log.info "getStepSize(): $latLonStepSize"
      latLonStepSize
   }

   String getLatitudeParameters(){
      "[0:$stepSize:$MAX_LAT]"
   }
   String getLongitudeParameters(){
      "[0:$stepSize:$MAX_LON]"
   }
}