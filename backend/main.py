from fastapi import FastAPI
from pydantic import BaseModel
import pandas as pd
import os
from datetime import datetime

app = FastAPI(title="BioSensor Data Hub")

CSV_FILE = "recoleccion_biometrica.csv"
last_data = {}

class BiometricData(BaseModel):
    heart_rate: int
    rr_interval: int
    spo2: int
    skin_temperature: float
    acc_x: int
    acc_y: int
    acc_z: int
    timestamp: str

@app.get("/")
async def root():
    return {"status": "Servidor Funcionando", "puerto": 8000, "instrucciones": "Entra a /latest para ver el ultimo dato"}

@app.post("/data")
async def receive_data(data: BiometricData):
    global last_data
    try:
        last_data = data.model_dump()
        df = pd.DataFrame([last_data])
        if not os.path.isfile(CSV_FILE):
            df.to_csv(CSV_FILE, index=False)
        else:
            df.to_csv(CSV_FILE, mode='a', header=False, index=False)

        # MUESTRA TODOS LOS DATOS EN PANTALLA SIN EMOJIS
        print(f"--------------------------------------------------")
        print(f"DATOS RECIBIDOS")
        print(f"Frecuencia Cardiaca: {data.heart_rate} BPM")
        print(f"Intervalo RR: {data.rr_interval} ms")
        print(f"Oxigeno (SpO2): {data.spo2}%")
        print(f"Temperatura: {data.skin_temperature} C")
        print(f"Acelerometro: X={data.acc_x} | Y={data.acc_y} | Z={data.acc_z}")
        print(f"Fecha: {data.timestamp}")

        return {"status": "success"}
    except Exception as e:
        print(f"Error: {e}")
        return {"status": "error"}

@app.get("/latest")
async def get_latest():
    if not last_data:
        return {"message": "Aun no se reciben datos del reloj. Presiona INICIAR en el reloj."}
    return last_data

if __name__ == "__main__":
    import uvicorn
    print("\nINICIANDO RECEPTOR DE DATOS BIOMETRICOS")
    print("------------------------------------------")
    uvicorn.run(app, host="0.0.0.0", port=8000)
