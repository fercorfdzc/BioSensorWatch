package com.jonathan.biosensorwatch.service

import android.annotation.SuppressLint
import android.app.*
import android.bluetooth.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.jonathan.biosensorwatch.sensor.SamsungHealthManager
import kotlinx.coroutines.*
import java.util.*

class BluetoothBiometricService : Service() {
    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // UUID Serial estándar
    private var serverSocket: BluetoothServerSocket? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var healthManager: SamsungHealthManager

    override fun onCreate() {
        super.onCreate()
        healthManager = SamsungHealthManager(this)
        createNotificationChannel()
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, "bt_channel")
            .setContentTitle("BioSensor Bluetooth")
            .setContentText("Esperando conexión de PC...")
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setOngoing(true)
            .build()

        startForeground(2, notification)
        healthManager.connect()
        
        try {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            serverSocket = adapter.listenUsingRfcommWithServiceRecord("BioSensor", uuid)
            startBluetoothTransmission()
        } catch (e: Exception) {
            stopSelf()
        }
        
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startBluetoothTransmission() {
        serviceScope.launch {
            try {
                val socket = serverSocket?.accept() // Bloqueante, espera a la PC
                val outputStream = socket?.outputStream
                
                while (isActive) {
                    val data = "${healthManager.latestHeartRate},${healthManager.latestRRInterval}," +
                               "${healthManager.accX},${healthManager.accY},${healthManager.accZ}\n"
                    outputStream?.write(data.toByteArray())
                    delay(1000)
                }
            } catch (e: Exception) {
                stopSelf()
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "bt_channel", 
            "Bluetooth Biometric", 
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        try {
            serverSocket?.close()
        } catch (e: Exception) {}
        healthManager.disconnect()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
