package com.example.offerservice.app.entities

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OfferTest {
    @Test
    fun offerRatingTest() {
        var offer = Offer(id = 0)
        assertTrue(offer.rating == null)

        offer = Offer(id = 0, approvalTime = 0, paymentTime = 0, stat = Stat(ecpl=0.0))
        assertTrue(offer.rating == 0)

        offer = Offer(id = 0, approvalTime = 1, paymentTime = 1, stat = Stat(ecpl=1.1))
        assertTrue(offer.rating == 1075)

        offer = Offer(id = 0, approvalTime = 10, paymentTime = 10, stat = Stat(ecpl=10.0))
        assertTrue(offer.rating == 7901)

        offer = Offer(id = 0, approvalTime = 100, paymentTime = 100, stat = Stat(ecpl=100.0))
        assertTrue(offer.rating == 1234)
    }

    @Test
    fun getCoeff1Test() {
        val offer = Offer(0)
        assertEquals(21.11111111111111, offer.getCoeff1(-100))
        assertEquals(10.11111111111111, offer.getCoeff1(-1))
        assertEquals(10.0, offer.getCoeff1(0))
        assertEquals(9.88888888888889, offer.getCoeff1(1))
        assertEquals(-1.1111111111111116, offer.getCoeff1(100))
    }


    @Test
    fun getCoeff2Test() {
        val offer = Offer(0)
        assertEquals(211.11111111111111, offer.getCoeff2(-100))
        assertEquals(101.11111111111111, offer.getCoeff2(-1))
        assertEquals(100.0, offer.getCoeff2(0))
        assertEquals(98.88888888888889, offer.getCoeff2(1))
        assertEquals(-11.111111111111116, offer.getCoeff2(100))
    }
}