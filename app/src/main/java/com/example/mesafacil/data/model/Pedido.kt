package com.example.mesafacil.data.model

data class Pedido(
    val cliente: String = "",
    val total: Double = 0.0,
    val status: String = "novo"
)