#include "FirebaseESP8266.h"
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266httpUpdate.h>


#define FIREBASE_HOST "water-level-indicator-a555e-default-rtdb.firebaseio.com"  //Change to your Firebase RTDB project ID e.g. Your_Project_ID.firebaseio.com
#define FIREBASE_AUTH "RSycUEGVNj1wiOVGrmXjQkdpE65voJNJmaGPs3Z7" //Change to your Firebase RTDB secret password
#define WIFI_SSID "sarayi_tf_2.4"
#define WIFI_PASSWORD "code||die"
#define VCC D0
#define TRIGGERPIN D1
#define ECHOPIN    D2
#define GND D3

const String CURRENT_VERSION = String("__BINARY_NAME:waterLevelSensor__BINARY_VERSION:") + __DATE__ + __TIME__ + "__";
String latestVersion, downloadUrl;

//Define Firebase Data objects
FirebaseData waterLevelData;

const String path = "/waterlevel";

int extraDelay = 0;
long distance = 0, duration;

int getDistance() {
  digitalWrite(TRIGGERPIN, LOW);  
  delayMicroseconds(5); 
  digitalWrite(TRIGGERPIN, HIGH);
  delayMicroseconds(10); 
  digitalWrite(TRIGGERPIN, LOW);

  duration = pulseIn(ECHOPIN, HIGH);
  Serial.println(duration);
  distance = duration*0.034/2;
  Serial.println(distance);
  return distance;
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(TRIGGERPIN, OUTPUT);
  pinMode(GND, OUTPUT);
  pinMode(VCC, OUTPUT);
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

void updateFirmware(String firmwareUrl) {
  WiFiClientSecure client;
  client.setInsecure();
  t_httpUpdate_return ret = ESPhttpUpdate.update(client, firmwareUrl); 
  switch(ret) {
      case HTTP_UPDATE_FAILED:
          Serial.println("[update] Update failed.");
          break;
      case HTTP_UPDATE_NO_UPDATES:
          Serial.println("[update] Update no Update.");
          break;
      case HTTP_UPDATE_OK:
          Serial.println("[update] Update ok."); // may not be called since we reboot the ESP
          break;
    }
  }

void checkForUpdates() {
  Firebase.set(waterLevelData, "/waterLevelSensor/firmware/currentVersion", CURRENT_VERSION);
  Firebase.getString(waterLevelData, "/waterLevelSensor/firmware/binaryVersion");
  latestVersion = waterLevelData.stringData();

  if (!latestVersion.equalsIgnoreCase(CURRENT_VERSION)) {
    Serial.println("Version mismatch, downloading update");
    Firebase.getString(waterLevelData, "/waterLevelSensor/firmware/downloadUrl");
    downloadUrl = waterLevelData.stringData();
    updateFirmware(downloadUrl);
  }
}

void loop() {
  digitalWrite(GND, LOW);
  digitalWrite(VCC, HIGH);
  int dist = getDistance();
  int percentage = 0;

  Firebase.set(waterLevelData, path + "/measurement", dist);
  Firebase.setTimestamp(waterLevelData, path + "/timestamp");
  checkForUpdates();
  delay(10000);
}
