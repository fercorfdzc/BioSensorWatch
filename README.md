# BioSensorWatch: Plataforma de Extraccion de Datos Biometricos

BioSensorWatch es una infraestructura de ingenieria diseñada para capturar, centralizar y exponer datos de salud y movimiento en tiempo real desde dispositivos Wear OS (Samsung Galaxy Watch).

### Finalidad del Proyecto
El sistema funciona como una pasarela (gateway) de datos abierta. Su unico objetivo es extraer informacion vital directamente del hardware del reloj y ponerla a disposicion de investigadores, desarrolladores o sistemas externos de forma estructurada (CSV y API REST), eliminando las barreras de los ecosistemas de salud cerrados.

---

## Arquitectura del Sistema

1. Wear OS Data Collector (Reloj): Aplicacion desarrollada en Kotlin que extrae datos crudos de los sensores y mantiene una transmision constante mediante un servicio de primer plano (Foreground Service).
2. Central Data API (Computadora): Servidor receptor en Python que organiza la informacion en un dataset local y expone los datos para consumo externo inmediato.

---

## Datos Extraidos (Diccionario de Datos)

El sistema captura los siguientes parametros del hardware del reloj cada segundo:

| Campo | Descripcion | Unidad |
| :--- | :--- | :--- |
| heart_rate | Frecuencia cardiaca (Pulso) | BPM |
| rr_interval | Tiempo exacto entre latidos | ms |
| spo2 | Saturacion de oxigeno en sangre | % |
| skin_temperature | Temperatura detectada en la piel | C |
| acc_x | Movimiento horizontal (Acelerometro) | m/s2 |
| acc_y | Movimiento vertical (Acelerometro) | m/s2 |
| acc_z | Movimiento de profundidad (Acelerometro) | m/s2 |
| timestamp | Fecha y hora precisa de la captura | ISO 8601 |

---

## Interaccion con la API (FastAPI)

Cualquier sistema externo puede comunicarse con los datos mediante peticiones HTTP. El servidor corre por defecto en el puerto 8000.

### 1. Obtener el ultimo dato en tiempo real
Para integrar los datos en otro programa (Python, JavaScript, etc.) o verlos en un navegador, se debe realizar una peticion GET al siguiente endpoint:

**URL:** `http://LA_IP_DE_TU_PC:8000/latest`

**Formato de respuesta (JSON):**
```json
{
  "heart_rate": 75,
  "rr_interval": 800,
  "spo2": 98,
  "skin_temperature": 34.5,
  "acc_x": 120,
  "acc_y": -45,
  "acc_z": 980,
  "timestamp": "2026-10-05T20:05:30.646"
}
```

### 2. Verificar estado del servidor
**URL:** `http://LA_IP_DE_TU_PC:8000/`
Devuelve un mensaje de confirmacion si el receptor esta encendido y listo para trabajar.

---

## Guia de Configuracion Paso a Paso (Computadora)

### 1. Preparacion del Entorno
* Asegurate de tener Python 3.10 o superior instalado en tu PC.
* Instala las librerias necesarias abriendo una terminal en la carpeta backend e instalando los requisitos:
  `pip install -r requirements.txt`

### 2. Identificacion en la Red
Para que el reloj sepa a donde enviar los datos, necesitas conocer la direccion IP de tu computadora:
1. Pulsa la tecla Windows + R, escribe cmd y pulsa Enter.
2. Escribe el comando ipconfig y busca la linea que dice Direccion IPv4.
3. Anotala para el siguiente paso.

### 3. Encendido del Receptor
En la terminal dentro de la carpeta backend, ejecuta:
`python main.py`
No cierres esta ventana durante la recoleccion.

---

## Guia de Configuracion Paso a Paso (Reloj)

### 1. Vincular con la IP de la PC
1. En Android Studio, abre el archivo: `app/src/main/java/com/jonathan/biosensorwatch/Config.kt`
2. Modifica la variable SERVER_IP con tu numero de IP:
   `private const val SERVER_IP = "192.168.X.X"`
3. Pulsa el boton Run (triangulo verde) para instalar la app en el reloj.

### 2. Conexion de Red
* El reloj y la PC deben estar en el mismo Wi-Fi.
* Apaga el Bluetooth del reloj para evitar interferencias con la red local.

---

Desarrollado por: Jonathan
Proposito: Herramienta cientifica de extraccion de datos biometricos Wear OS.
Tecnologias: Kotlin, Wear OS, FastAPI, Pandas, REST API.
