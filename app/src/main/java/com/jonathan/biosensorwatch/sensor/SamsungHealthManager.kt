package com.jonathan.biosensorwatch.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class SamsungHealthManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    private var heartRateSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    @Volatile var latestHeartRate: Int = 0
    @Volatile var latestRRInterval: Int = 0 
    @Volatile var latestSpO2: Int = 0
    @Volatile var latestSkinTemp: Float = 0f
    
    @Volatile var accX: Int = 0
    @Volatile var accY: Int = 0
    @Volatile var accZ: Int = 0

    private var lastHeartBeatTime: Long = 0

    fun connect() {
        Log.d("Sensors", "--- ESCANEANDO TODOS LOS SENSORES DISPONIBLES ---")
        val deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        
        deviceSensors.forEach { sensor ->
            val name = sensor.name.lowercase()
            val type = sensor.type
            
            // Log para debug: asi sabremos que sensores TIENE tu reloj realmente
            if (name.contains("oxygen") || name.contains("spo2") || name.contains("temp") || name.contains("heart")) {
                Log.d("Sensors", "Sensor detectado: $name | Tipo: $type")
            }

            // Suscribirse a HR
            if (type == Sensor.TYPE_HEART_RATE) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
            // Suscribirse a Acelerometro
            if (type == Sensor.TYPE_ACCELEROMETER) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
            // Suscribirse a cualquier cosa que parezca SpO2
            if (name.contains("spo2") || name.contains("oxygen") || type == 69662 || type == 65542) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
            // Suscribirse a cualquier cosa que parezca Temperatura
            if (name.contains("temp") || type == 65538 || type == 65578 || type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    fun disconnect() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val name = event.sensor.name.lowercase()
        val type = event.sensor.type

        when {
            type == Sensor.TYPE_HEART_RATE -> {
                if (event.values[0] > 0) {
                    latestHeartRate = event.values[0].toInt()
                    val currentTime = System.currentTimeMillis()
                    if (lastHeartBeatTime != 0L) {
                        latestRRInterval = (currentTime - lastHeartBeatTime).toInt()
                    }
                    lastHeartBeatTime = currentTime
                }
            }
            type == Sensor.TYPE_ACCELEROMETER -> {
                accX = (event.values[0] * 100).toInt()
                accY = (event.values[1] * 100).toInt()
                accZ = (event.values[2] * 100).toInt()
            }
            name.contains("spo2") || name.contains("oxygen") || type == 69662 || type == 65542 -> {
                if (event.values[0] > 0) {
                    latestSpO2 = event.values[0].toInt()
                    Log.d("Sensors", "DATO RECIBIDO -> SpO2: $latestSpO2")
                }
            }
            name.contains("temp") || type == 65538 || type == 65578 || type == Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                if (event.values[0] > 0) {
                    latestSkinTemp = event.values[0]
                    Log.d("Sensors", "DATO RECIBIDO -> Temp: $latestSkinTemp")
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
