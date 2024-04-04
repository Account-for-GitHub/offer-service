package com.example.offerservice.app

import com.example.offerservice.app.aggregation.GeoStat
import com.example.offerservice.app.dto.NotFoundResponseDTO
import com.example.offerservice.app.dto.ResponseDTO
import com.example.offerservice.app.init.DatabasePopulator
import com.example.offerservice.app.repositories.OffersRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*


@RestController
class OffersController(
    val repository: OffersRepository,
    val databasePopulator: DatabasePopulator
) {
    @GetMapping("/offers/{geo}")
    fun getOffersByGeo(
        @PathVariable geo: String,
        @RequestParam page: Int = 1,
        @RequestParam size: Int = 5
    ): Mono<ResponseEntity<out Any>> {
        /*
         No uppercase function for geo because lowercase letters are possible like in Wrld geo code.
        */
        val paginationFrom0 = page.coerceAtLeast(1) - 1
        val pageSize = size.coerceIn(1, 20)
        val offersFlux = repository.findAllByGeoCode(geo, PageRequest.of(paginationFrom0, pageSize)).collectList()
        val countOffersMono = repository.count()
        return Mono.zip(offersFlux, countOffersMono).map {
            if ((it.first() as List<*>).isEmpty()) {
                ResponseEntity.status(404).body(
                    NotFoundResponseDTO(
                        message = "no offers found, this geo code is not supported"
                    )
                )
            } else {
                ResponseEntity.status(200).body(
                    ResponseDTO(
                        offers = it.first() as List<*>,
                        total = it.last() as Long
                    )
                )
            }
        }
    }

    @PostMapping("/update-offers")
    @Synchronized
    fun offersDataUpdate() {
        databasePopulator.updateData()
    }

    @GetMapping("/geo-stats")
    fun getGeoStats(): Flux<GeoStat> {
        return repository.getGeoStats()
    }
}