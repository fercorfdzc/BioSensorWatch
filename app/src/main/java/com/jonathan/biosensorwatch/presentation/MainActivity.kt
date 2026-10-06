package com.jonathan.biosensorwatch.presentation

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.jonathan.biosensorwatch.Config
import com.jonathan.biosensorwatch.presentation.theme.BioSensorWatchTheme
import com.jonathan.biosensorwatch.service.BiometricService

class MainActivity : ComponentActivity() {

    private val requiredPermissions = mutableListOf(
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.ACTIVITY_RECOGNITION
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
        // Permiso vital para salud en segundo plano
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(Manifest.permission.BODY_SENSORS_BACKGROUND)
        }
    }.toTypedArray()

    private var permissionsGrantedState by mutableStateOf(false)
    private var isServiceRunning by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        permissionsGrantedState = checkAllPermissions()
        isServiceRunning = isServiceRunning(BiometricService::class.java)

        setContent {
            BioSensorWatchTheme {
                MainScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        permissionsGrantedState = checkAllPermissions()
        isServiceRunning = isServiceRunning(BiometricService::class.java)
    }

    @Composable
    fun MainScreen() {
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissionsGrantedState = permissions.values.all { it }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(10.dp)) {
                if (permissionsGrantedState) {
                    if (isServiceRunning) {
                        Text("SISTEMA ACTIVO", color = Color.Green, style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { 
                                stopDataCollectionService()
                                isServiceRunning = false
                            }, 
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("DETENER")
                        }
                    } else {
                        Text("LISTO PARA INICIAR", color = Color.Cyan, style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { 
                                startDataCollectionService()
                                isServiceRunning = true
                            }, 
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("INICIAR")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Host: ${Config.BASE_URL.removePrefix("http://").removeSuffix(":8000/")}", 
                         color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    
                } else {
                    Text("FALTAN PERMISOS", color = Color.Red, style = MaterialTheme.typography.labelSmall)
                    Text("Debe elegir 'Permitir siempre'", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { launcher.launch(requiredPermissions) }) { 
                        Text("CONCEDER") 
                    }
                }
            }
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        val services = manager.getRunningServices(Int.MAX_VALUE)
        for (service in services) {
            if (serviceClass.name == service.service.className) return true
        }
        return false
    }

    private fun checkAllPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startDataCollectionService() {
        val intent = Intent(this, BiometricService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopDataCollectionService() {
        val intent = Intent(this, BiometricService::class.java)
        stopService(intent)
    }
}
