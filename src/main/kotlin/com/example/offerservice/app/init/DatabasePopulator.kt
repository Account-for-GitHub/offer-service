package com.example.offerservice.app.init

import com.example.offerservice.app.entities.Offer
import com.example.offerservice.app.entities.OffersObj
import jakarta.annotation.PostConstruct
import com.example.offerservice.app.repositories.OffersRepository
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import java.util.logging.Level
import java.util.logging.Logger

@Component
class DatabasePopulator(val repository: OffersRepository) {
    companion object {
        const val SOURCE_URL = "https://cityads.com/api/rest/webmaster/v2/offers/list"
    }

    private val logger = Logger.getLogger(DatabasePopulator::class.java.name)
    private val webClient = WebClient.builder()
        .baseUrl(SOURCE_URL)
        .codecs { configurer -> configurer.defaultCodecs().maxInMemorySize(32 * 1024 * 1024) }
        .build()

    @PostConstruct
    fun updateData() {
        logger.log(Level.INFO, "Offers data is loading from https://cityads.com external source to MongoDB")
        repository.deleteAll().subscribe()
        var page = 1
        var hasNextOffers = true
        while (hasNextOffers) {
            val offersListOne = getDataFilterItAndSaveItToDb(page)
            hasNextOffers = offersListOne.isNotEmpty()
            ++page
        }
    }

    fun filterWrldFromOfferList(offersList: List<Offer>): List<Offer> {
        return offersList.filter { offer ->
            offer.geo?.let { listOfGeosInOffer ->
                listOfGeosInOffer.forEach { geoElement ->
                    if (geoElement.code == "Wrld") {
                        return@let false
                    }
                }
                return@let true
            } ?: true
        }
    }

    fun getDataFilterItAndSaveItToDb(page: Int): List<Offer> {
        val offersList = getOffersListFromSourceForPage(page)
        filterWrldFromOfferList(offersList).let { filteredOffersList ->
            repository.saveAll(filteredOffersList).subscribe()
        }
        return offersList
    }

    private fun getOffersListFromSourceForPage(page: Int): List<Offer> {
        val offersObj = runBlocking {
            try {
                webClient.get()
                    .uri("?page=$page&perpage=10000")
                    .awaitExchange { response ->
                        if (response.statusCode() == HttpStatus.OK) {
                            response.awaitBody<OffersObj>()
                        } else {
                            logger.log(Level.WARNING, "External data source https://cityads.com is unavailable")
                            OffersObj(emptyList())
                        }
                    }
            } catch (e: WebClientResponseException) {
                logger.log(Level.WARNING, e.message)
                logger.log(Level.WARNING, e.responseBodyAsString)
                OffersObj(emptyList())
            }
        }
        return offersObj.offers
    }
}