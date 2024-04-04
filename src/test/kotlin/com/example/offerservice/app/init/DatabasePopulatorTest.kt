package com.example.offerservice.app.init

import com.example.offerservice.app.entities.Geo
import com.example.offerservice.app.repositories.OffersRepository
import com.example.offerservice.app.entities.Offer
import org.junit.jupiter.api.Test
import io.mockk.*

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class DatabasePopulatorTest {

    private val repositoryMock = mockk<OffersRepository>()
    private lateinit var databasePopulator: DatabasePopulator

    @BeforeEach
    fun setUp() {
        databasePopulator = DatabasePopulator(repositoryMock)
    }

    @Test
    fun filterWrldFromOfferList() {
        val offersListWithWrld = listOf(
            Offer(0, geo = listOf(Geo("RU", "Russia"))),
            Offer(0, geo = listOf(Geo("RU", "Russia"))),
            Offer(0, geo = listOf(Geo("Wrld", "World")))
        )
        val offersList = listOf(
            Offer(0, geo = listOf(Geo("RU", "Russia"))),
            Offer(0, geo = listOf(Geo("RU", "Russia")))
        )
        val anotherOffersList = listOf(
            Offer(0, geo = listOf(Geo("RU", "Russia"))),
            Offer(0, geo = listOf(Geo("RU", "Russia")))
        )

        assertNotEquals(offersListWithWrld, offersList)
        val filteredList = databasePopulator.filterWrldFromOfferList(offersListWithWrld)
        assertEquals(filteredList, offersList)

        assertEquals(offersList, anotherOffersList)
        val anotherFilteredList = databasePopulator.filterWrldFromOfferList(offersList)
        assertEquals(anotherFilteredList, anotherOffersList)
    }


}