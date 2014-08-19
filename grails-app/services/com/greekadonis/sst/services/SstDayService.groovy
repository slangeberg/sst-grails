package com.greekadonis.sst.services

import com.greekadonis.sst.SSTDay
import grails.transaction.Transactional

@Transactional
class SstDayService {
//
//  List<SSTDay> findAllOrderedBySSTIndex() {
//    SSTDay.list(order: 'asc', sort: 'sstIndex')
//  }
  List<SSTDay> findBySstIndex(int index) {
    SSTDay.where{ sstIndex == index }.find()
  }

  SSTDay findFirstLoadedDay() {
    SSTDay.where {
      sstIndex == min(sstIndex)
    }
    .find()
  }

//--> Todo: Account for any gaps in loaded days!

  SSTDay findLastLoadedDay() {
    SSTDay.where {
      sstIndex == max(sstIndex)
    }
    .find()
  }
}