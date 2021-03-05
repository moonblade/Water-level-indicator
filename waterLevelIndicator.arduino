#include "FirebaseESP8266.h"
#include <ESP8266WiFi.h>

#define FIREBASE_HOST "water-level-indicator-a555e-default-rtdb.firebaseio.com"  //Change to your Firebase RTDB project ID e.g. Your_Project_ID.firebaseio.com
#define FIREBASE_AUTH "RSycUEGVNj1wiOVGrmXjQkdpE65voJNJmaGPs3Z7" //Change to your Firebase RTDB secret password
#define WIFI_SSID "sarayi_tf_2.4"
#define WIFI_PASSWORD "code||die"
#define TRIGGERPIN D0
#define ECHOPIN    D1

//Define Firebase Data objects
FirebaseData waterLevelData;

const String path = "/waterlevel";

int extraDelay = 0;
long distance = 0, duration;

int getDistance() {
  digitalWrite(TRIGGERPIN, LOW);  
  delayMicroseconds(3); 
  
  digitalWrite(TRIGGERPIN, HIGH);
  delayMicroseconds(12); 
  
  digitalWrite(TRIGGERPIN, LOW);
  duration = pulseIn(ECHOPIN, HIGH);
  distance = (duration/2) / 29.1;
  Serial.println(distance);
  return distance;
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(TRIGGERPIN, OUTPUT);
  pinMode(ECHOPIN, INPUT);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.println("");

  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
}

void loop() {
  int dist = getDistance();
  int percentage = 0;

  Firebase.set(waterLevelData, path + "/measurement", dist);
  Firebase.setTimestamp(waterLevelData, path + "/timestamp");
  delay(10000);
}
