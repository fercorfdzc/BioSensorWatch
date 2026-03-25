package com.jonathan.biosensorwatch.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class SamsungHealthManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    // Lista de sensores que intentaremos activar
    private var heartRateSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    // Sensores de Salud (pueden no estar en todos los modelos vía Android estándar)
    private var spo2Sensor: Sensor? = sensorManager.getDefaultSensor(69662) // Código común para SpO2 nativo
    private var tempSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) ?: sensorManager.getDefaultSensor(65538)

    @Volatile var latestHeartRate: Int = 0
    @Volatile var latestRRInterval: Int = 0 
    @Volatile var latestSpO2: Int = 0
    @Volatile var latestSkinTemp: Float = 0f
    
    @Volatile var accX: Int = 0
    @Volatile var accY: Int = 0
    @Volatile var accZ: Int = 0

    private var lastHeartBeatTime: Long = 0

    fun connect() {
        Log.d("SamsungHealthManager", "Iniciando captura de 5 parámetros")
        
        heartRateSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
        spo2Sensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
        tempSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    fun disconnect() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_HEART_RATE -> {
                if (event.values[0] > 0) {
                    latestHeartRate = event.values[0].toInt()
                    // Cálculo aproximado del RR Interval en ms
                    val currentTime = System.currentTimeMillis()
                    if (lastHeartBeatTime != 0L) {
                        latestRRInterval = (currentTime - lastHeartBeatTime).toInt()
                    }
                    lastHeartBeatTime = currentTime
                }
            }
            Sensor.TYPE_ACCELEROMETER -> {
                accX = (event.values[0] * 100).toInt()
                accY = (event.values[1] * 100).toInt()
                accZ = (event.values[2] * 100).toInt()
            }
            // Intento de captura de SpO2 y Temperatura si el hardware lo permite vía Android
            69662 -> { latestSpO2 = event.values[0].toInt() }
            Sensor.TYPE_AMBIENT_TEMPERATURE, 65538 -> { latestSkinTemp = event.values[0] }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
