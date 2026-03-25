package com.jonathan.biosensorwatch.data.api

import com.jonathan.biosensorwatch.data.model.BiometricData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BiometricApi {
    @POST("/data")
    suspend fun sendBiometricData(@Body data: BiometricData): Response<Unit>
}
