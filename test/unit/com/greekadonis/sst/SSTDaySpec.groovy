package com.greekadonis.sst

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import spock.lang.Specification

import java.text.DateFormat

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
//import grails.test.mixin.hibernate.*

//awaiting update for test mixin
//@TestMixin(HibernateTestMixin)
@TestFor(SSTDay)
class SSTDaySpec extends Specification {
//    def setup() {
//    }
//
//    def cleanup() {
//    }

    void "Someday i will test thee"() {
        expect: new SSTDay() != null
    }

//    void "Can persist day"() {
//        new SSTDay().save()
//        expect:
//        SSTDay.count() == 1
//    }
}