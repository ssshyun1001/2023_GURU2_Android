package com.example.guru2_dsjouju_app

data class PoliceStation(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

data class CCTV(
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val cameraId: Int,
    val installationDate: String
)