// Libraries
#include <FirebaseESP8266.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266httpUpdate.h>

// Firebase Auth
#define FIREBASE_HOST "water-level-indicator-a555e-default-rtdb.firebaseio.com"  
#define FIREBASE_AUTH "RSycUEGVNj1wiOVGrmXjQkdpE65voJNJmaGPs3Z7" 

// Wifi details
#define WIFI_SSID "sarayi_tf_2.4"
#define WIFI_PASSWORD "code||die"

// Defaults for operation
#define VCC D0
#define TRIGGERPIN D1
#define ECHOPIN    D2
#define GND D3
#define ARRAY_SIZE 10

// For auto updater
const String CURRENT_VERSION = String("__BINARY_NAME:waterLevelSensor__BINARY_VERSION:") + __DATE__ + __TIME__ + "__";
const String BASE = String("/waterLevelSensor");
String latestVersion, downloadUrl;

FirebaseData fd;

int extraDelay = 0;
long distance = 0, duration;
int distances[ARRAY_SIZE];

int getDistance() {
  digitalWrite(TRIGGERPIN, LOW);  
  delayMicroseconds(5); 
  digitalWrite(TRIGGERPIN, HIGH);
  delayMicroseconds(10); 
  digitalWrite(TRIGGERPIN, LOW);

  duration = pulseIn(ECHOPIN, HIGH);
  distance = duration*0.034/2;
  Serial.println(distance);
  return distance;
}

int calculatePercentage(int distance) {
    Firebase.getInt(fd, BASE + "/configuration/maximumDistanceCm");
    int maximumDistanceCm = fd.intData();
    Firebase.getInt(fd, BASE + "/configuration/minimumDistanceCm");
    int minimumDistanceCm = fd.intData();

    return max(min(100, (100 - (((distance - minimumDistanceCm) * 100) / max((maximumDistanceCm - minimumDistanceCm), 1)))), 0);
}

int getPercentage() {
  for (int i=0; i<ARRAY_SIZE; ++i) {
    distances[i] = getDistance();
    delay(1000);
  }
  int sum = 0;
  String distanceString = String("");
  for (int i=0; i<ARRAY_SIZE; ++i) {
    sum += distances[i];
    distanceString += String(distances[i]) + " ";
  }

  Firebase.set(fd, BASE + "/output/rawDistances", distanceString);
  int averageDistance = sum / ARRAY_SIZE;
  int percentage = calculatePercentage(averageDistance);
  return percentage;
}

void setup() {
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
  ESPhttpUpdate.update(client, firmwareUrl); 
}

void checkForUpdates() {
  Firebase.set(fd, BASE + "/firmware/currentVersion", CURRENT_VERSION);
  Firebase.getString(fd, BASE + "/firmware/binaryVersion");
  latestVersion = fd.stringData();

  if (!latestVersion.equalsIgnoreCase(CURRENT_VERSION)) {
    Serial.println("Version mismatch, downloading update");
    Firebase.getString(fd, BASE + "/firmware/downloadUrl");
    downloadUrl = fd.stringData();
    updateFirmware(downloadUrl);
  }
}

void loop() {
  digitalWrite(GND, LOW);
  digitalWrite(VCC, HIGH);
  
  int percentage = getPercentage();

  Firebase.set(fd, BASE + "/output/percentage", percentage);
  Firebase.setTimestamp(fd, BASE + "/output/timestamp");

  checkForUpdates();
  delay(1000);
}
