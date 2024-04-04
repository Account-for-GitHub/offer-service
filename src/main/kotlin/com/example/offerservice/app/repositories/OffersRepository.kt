package com.example.offerservice.app.repositories

import com.example.offerservice.app.aggregation.GeoStat
import com.example.offerservice.app.entities.Offer
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface OffersRepository : ReactiveCrudRepository<Offer, Int> {
    fun findAllByGeoCode(geo: String, pageRequest: Pageable): Flux<Offer>

    @Aggregation(
        "{\$unwind: \$geo}",
        "{\$group: {_id: \$geo, code: {\$first: '\$geo.code'}, total: { \$sum: 1 }}}",
        "{\$project: {code: 1, total: 1}}",
        "{\$sort: {total: -1}}"
    )
    fun getGeoStats(): Flux<GeoStat>
}
