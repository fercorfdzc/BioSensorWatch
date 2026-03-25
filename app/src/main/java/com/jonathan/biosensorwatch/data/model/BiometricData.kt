package com.jonathan.biosensorwatch.data.model

import com.google.gson.annotations.SerializedName

data class BiometricData(
    @SerializedName("heart_rate")
    val heartRate: Int,
    @SerializedName("rr_interval")
    val rrInterval: Int,
    @SerializedName("spo2")
    val spo2: Int,
    @SerializedName("skin_temperature")
    val skinTemperature: Float,
    @SerializedName("acc_x")
    val accX: Int,
    @SerializedName("acc_y")
    val accY: Int,
    @SerializedName("acc_z")
    val accZ: Int,
    @SerializedName("timestamp")
    val timestamp: String
)
