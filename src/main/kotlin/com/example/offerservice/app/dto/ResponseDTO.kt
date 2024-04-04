package com.example.offerservice.app.dto

data class ResponseDTO(
    val offers: List<*>,
    val total: Long
)