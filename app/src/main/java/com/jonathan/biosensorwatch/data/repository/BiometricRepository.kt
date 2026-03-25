package com.jonathan.biosensorwatch.data.repository

import android.util.Log
import com.jonathan.biosensorwatch.data.api.RetrofitClient
import com.jonathan.biosensorwatch.data.model.BiometricData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BiometricRepository {
    private val api = RetrofitClient.biometricApi

    suspend fun sendData(data: BiometricData): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.sendBiometricData(data)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Log.e("BiometricRepository", "Error sending data: ${response.code()}")
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("BiometricRepository", "Network error", e)
            Result.failure(e)
        }
    }
}
