package com.jonathan.biosensorwatch

/**
 * Archivo de configuración global para BioSensorWatch.
 * Cambia la IP aquí cuando cambies de red Wi-Fi.
 */
object Config {
    // Cambia solo este número por tu IP actual (la que sale en ipconfig)
    private const val SERVER_IP = "192.168.101.6"
    
    // URL completa que utiliza la aplicación
    const val BASE_URL = "http://$SERVER_IP:8000/"
}
