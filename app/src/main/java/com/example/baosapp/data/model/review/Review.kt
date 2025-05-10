package com.example.baosapp.data.model.review

data class Review(
    val id: Long,
    val nombreBano: String,
    val valoracion: Int,   // 1..5
    val limpieza: Int,     // 1..5
    val olor: Int          // 1..5
)