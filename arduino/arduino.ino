#include "FirebaseESP8266.h"
#include <ESP8266WiFi.h>


#define FIREBASE_HOST "prueba-46c52-default-rtdb.firebaseio.com/"  
#define FIREBASE_AUTH "UDmiQjoogTTD75wbWZr9VI09C9s8i5jXgmw2pXG5" 
#define WIFI_SSID "ssid"
#define WIFI_PASSWORD ".password"

//Define Firebase Data objects
FirebaseData firebaseData1;

int limiteMaximoBateria;
int relay = 2;
void setup()
{

    Serial.begin(115200);

    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Connecting to Wi-Fi");
    while (WiFi.status() != WL_CONNECTED)
    {
        Serial.print(".");
        delay(300);
    }
    Serial.println();
    Serial.print("Connected with IP: ");
    Serial.println(WiFi.localIP());
    Serial.println();

    Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
    Firebase.reconnectWiFi(true);
    

    if (!Firebase.beginStream(firebaseData1, path + "/" + nodeID))
    {
        Serial.println("Could not begin stream");
        Serial.println("REASON: " + firebaseData1.errorReason());
        Serial.println();
    }

    limiteMaximoBateria = calibrarBateria(1000, 470, 5);
}

void loop()
{
    if (!Firebase.readStream(firebaseData1))
    {
        Serial.println();
        Serial.println("Can't read stream data");
        Serial.println("REASON: " + firebaseData1.errorReason());
        Serial.println();
    }

    if (firebaseData1.streamTimeout())
    {
        Serial.println();
        Serial.println("Stream timeout, resume streaming...");
        Serial.println();
    }
    int porcentajeBateria = leerPorcentajeBateria(limiteMaximoBateria);
    delay(1000);

   Firebase.setFloat("number", porcentajeBateria);
  // handle error
  if (Firebase.failed()) {
      Serial.print("setting /number failed:");
      Serial.println(Firebase.error());  
      return;
  }
}

int calibrarBateria(float rBajo, float rArriba, float vIn) {
  float vMax = (rBajo / (rBajo + rArriba)) * vIn;
  int limiteMaximoBateria = (int)(vMax * (1023 / 5));
  Serial.print("Calibrado! - El valor analogico maximo de bateria es = ");
  Serial.println(limiteMaximoBateria);
  delay(1000);
  return limiteMaximoBateria;
}


int leerPorcentajeBateria(int limiteMaximoBateria) {
  int bateria = analogRead(pinA1);
  delay(5); // permite que se estabilice el convertidor analógico-digital (ADC).
  int porcentajeBateria = map((int) bateria, 0, limiteMaximoBateria, 0, 100);
  return porcentajeBateria;
}

int compararNivelBateria(int porcentajeBateria) {
  int nivelBateriaMayor = 100;
  if ((porcentajeBateria <= nivelBateriaMayor)) {
    nivelBateriaMayor = porcentajeBateria;
  }
  if ((porcentajeBateria > nivelBateriaMayor - 5)) {
    nivelBateriaMayor = porcentajeBateria;
  }
  return nivelBateriaMayor;
}


void activar_ibutton(){
  digitalWrite(relay,HIGH);
  Serial.println("ACTIVADO");
  delay(5000);
}

void desactivar_ibutton(){
  digitalWrite(relay,LOW);
  Serial.println("DESACTIVADO");
  delay(1000);
}
