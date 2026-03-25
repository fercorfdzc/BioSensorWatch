from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from datetime import datetime
import pandas as pd
import os
import time

app = FastAPI(title="BioSensorWatch - Recolector para ML")

CSV_FILE = "dataset_entrenamiento.csv"

# Variables globales
last_received_data = None
last_update_time = 0

# --- CONFIGURACIÓN DE ETIQUETA ---
current_label = "normal"

class BiometricData(BaseModel):
    heart_rate: int
    rr_interval: int
    spo2: int
    skin_temperature: float
    acc_x: int
    acc_y: int
    acc_z: int
    timestamp: str

def save_to_ml_csv(data: BiometricData):
    data_dict = data.model_dump()
    data_dict['label'] = current_label
    df = pd.DataFrame([data_dict])
    if not os.path.isfile(CSV_FILE):
        df.to_csv(CSV_FILE, index=False)
    else:
        df.to_csv(CSV_FILE, mode='a', header=False, index=False)

@app.post("/data")
async def receive_data(data: BiometricData):
    global last_received_data, last_update_time
    try:
        last_received_data = data.model_dump()
        last_update_time = time.time()  # Guardamos el momento exacto del recibo
        save_to_ml_csv(data)
        print(f"📥 Recibido -> HR: {data.heart_rate}")
        return {"status": "success"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/latest")
async def get_latest():
    global last_received_data

    # Si han pasado más de 3 segundos sin datos nuevos, consideramos que la app se detuvo
    if last_received_data is None or (time.time() - last_update_time) > 3:
        return None  # Retornamos vacío para que el clasificador sepa que no hay nada nuevo

    return last_received_data

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
