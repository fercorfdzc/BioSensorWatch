# ⌚ BioSensorWatch: Asistente al Volante Inteligente

**BioSensorWatch** es un sistema de monitoreo biométrico en tiempo real que utiliza un reloj con Wear OS para detectar estados de fatiga, estrés o relajación en conductores, con el fin de prevenir accidentes viales.

---

## 🚀 Estructura del Proyecto

1.  **Mobile App (Wear OS):** Aplicación en Kotlin que captura datos de sensores (Frecuencia cardíaca, Acelerómetro, SpO2).
2.  **Backend (FastAPI):** Servidor en Python que recibe los datos y los guarda en un dataset (CSV).
3.  **Inteligencia Artificial:** Modelo de Machine Learning (Random Forest) que clasifica el estado del usuario en tiempo real.

---

## 🛠️ Configuración Inicial (Paso a Paso)

### 1. Preparar la Computadora (Servidor)
*   Instala las librerías necesarias:
    ```bash
    cd backend
    pip install -r requirements.txt
    ```
*   Averigua tu IP local:
    *   Abre la terminal y escribe `ipconfig`.
    *   Busca **Dirección IPv4** (Ejemplo: `192.168.101.6`).

### 2. Configurar el Reloj
*   Abre el archivo `RetrofitClient.kt` en Android Studio.
*   Cambia la `BASE_URL` por la IP de tu PC:
    ```kotlin
    private const val BASE_URL = "http://TU_IP_AQUI:8000/"
    ```
*   **IMPORTANTE:** Asegúrate de que el Reloj y la PC estén conectados a la **Misma Red Wi-Fi**.

### 3. Vincular y Conectar
*   En el reloj, abre la app y otorga los permisos de **Sensores**, **Actividad Física** y **Notificaciones**.
*   Si el reloj no conecta, desactiva el **Bluetooth** del reloj para forzar la conexión por Wi-Fi directo.

---

## 📈 Cómo usar el sistema

### Paso 1: Iniciar el Recolector
En tu PC, corre el servidor:
```bash
python main.py
```

### Paso 2: Iniciar la captura en el Reloj
Presiona el botón **INICIAR** en la app del reloj. Verás que en la terminal de la PC empiezan a aparecer los latidos recibidos.

### Paso 3: Entrenar la IA
Para que el sistema aprenda, graba un minuto en estado "Normal" y otro minuto simulando "Fatiga" (cambiando la etiqueta en `main.py`). Luego ejecuta:
```bash
python entrenar_modelo.py
```

### Paso 4: Clasificación en Tiempo Real
Con el servidor y el reloj encendidos, corre el clasificador:
```bash
python api_app.py
```
¡El sistema te dirá si estás **RELAJADO** o en **FATIGA** al instante!

---

## 🌍 Acceso Global (Opcional)
Si quieres que alguien fuera de tu casa vea los datos, usa **ngrok**:
1. Descarga ngrok.
2. Ejecuta: `ngrok http 8000`.
3. Copia la URL de ngrok en el `RetrofitClient.kt` del reloj.

---

**Autor:** Jonathan - Proyecto Escolar "Asistente al Volante"
**Tecnologías:** Android (Kotlin), Compose, FastAPI, Scikit-Learn, Pandas.
