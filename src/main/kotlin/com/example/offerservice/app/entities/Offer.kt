package com.example.offerservice.app.entities

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Offer(
    @Id
    val id: Int,
    val name: String? = null,
    @field:JsonProperty("offer_currency")
    val offerCurrency: OfferCurrency? = null,
    @field:JsonProperty("approval_time")
    val approvalTime: Int? = null,
    @field:JsonProperty("site_url")
    val siteUrl: String? = null,
    val logo: String? = null,
    val geo: List<Geo>? = null,
    val stat: Stat? = null,
    @field:JsonProperty("payment_time")
    val paymentTime: Int? = null
) {
    var rating: Int? = run {
        if (approvalTime == null || paymentTime == null || stat?.ecpl == null) return@run null
        val coeff1: Double = getCoeff1(approvalTime)
        val coeff2: Double = getCoeff2(paymentTime)
        val coeff1orOne = if (coeff1 == 0.0) 1.0 else coeff1
        val coeff2orOne = if (coeff2 == 0.0) 1.0 else coeff2
        (stat.ecpl * coeff1orOne * coeff2orOne).toInt()
    }

    fun getCoeff1(value: Int): Double = 10 * (1 - (value.toDouble() / 90))

    fun getCoeff2(value: Int): Double = 100 * (1 - (value.toDouble() / 90))
}
