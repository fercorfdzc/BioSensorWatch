import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
import joblib
import os

def entrenar():
    if not os.path.exists('dataset_entrenamiento.csv'):
        print("❌ No se encontró el archivo 'dataset_entrenamiento.csv'.")
        return

    # 1. Cargar datos
    df = pd.read_csv('dataset_entrenamiento.csv')

    # 2. Limpieza: Quitar filas incompletas y la columna de tiempo
    df = df.dropna()

    if len(df['label'].unique()) < 2:
        print("\n⚠️  ATENCIÓN: Solo tienes datos de tipo 'normal'.")
        print("Para que el modelo funcione, cambia 'current_label' en main.py a 'fatiga',")
        print("mueve el reloj bruscamente y graba unos segundos más.")
        return

    # 3. Seleccionar características (X) y objetivo (y)
    X = df[['heart_rate', 'rr_interval', 'acc_x', 'acc_y', 'acc_z']]
    y = df['label']

    # 4. Entrenar el modelo (Bosque Aleatorio)
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    modelo = RandomForestClassifier(n_estimators=100)
    modelo.fit(X_train, y_train)

    # 5. Guardar el "cerebro" del modelo
    joblib.dump(modelo, 'modelo_biometrico.joblib')

    precision = modelo.score(X_test, y_test)
    print(f"\n✅ ¡Modelo entrenado con éxito!")
    print(f"🎯 Precisión: {precision * 100:.2f}%")
    print("Archivo guardado: 'modelo_biometrico.joblib'")

if __name__ == "__main__":
    entrenar()
