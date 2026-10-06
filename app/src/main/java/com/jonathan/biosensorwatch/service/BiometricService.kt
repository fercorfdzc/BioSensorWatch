package com.jonathan.biosensorwatch.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.jonathan.biosensorwatch.data.model.BiometricData
import com.jonathan.biosensorwatch.data.repository.BiometricRepository
import com.jonathan.biosensorwatch.sensor.SamsungHealthManager
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BiometricService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var healthManager: SamsungHealthManager
    private val repository = BiometricRepository()
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun onCreate() {
        super.onCreate()
        healthManager = SamsungHealthManager(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, "biometric_channel")
            .setContentTitle("BioSensor Activo")
            .setContentText("Enviando datos al servidor...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
        healthManager.connect()
        startDataStream()
        
        return START_STICKY
    }

    private fun startDataStream() {
        serviceScope.launch {
            while (isActive) {
                try {
                    val data = BiometricData(
                        heartRate = healthManager.latestHeartRate,
                        rrInterval = healthManager.latestRRInterval,
                        spo2 = healthManager.latestSpO2,
                        skinTemperature = healthManager.latestSkinTemp,
                        accX = healthManager.accX, 
                        accY = healthManager.accY, 
                        accZ = healthManager.accZ,
                        timestamp = LocalDateTime.now().format(formatter)
                    )
                    repository.sendData(data)
                } catch (e: Exception) {
                    // Ignorar errores de red temporales
                }
                delay(1000) // Captura cada segundo
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "biometric_channel", 
            "Servicio Biométrico",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        healthManager.disconnect()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
