// Libraries
#include <FirebaseESP8266.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266httpUpdate.h>
#include <Wire.h>
#include <VL53L0X.h>

// Firebase Auth
#define FIREBASE_HOST "water-level-indicator-a555e-default-rtdb.firebaseio.com"  
#define FIREBASE_AUTH "RSycUEGVNj1wiOVGrmXjQkdpE65voJNJmaGPs3Z7" 

// Wifi details
#define WIFI_SSID "sarayi_tf_2.4"
#define WIFI_PASSWORD "code||die"

// Defaults for operation
#define VCC D0
#define SCL D1
#define SDA D2
#define GND D3
#define UPDATE_INTERVAL 10000
#define ARRAY_SIZE 20
#define ROUND_UPTO 5

// For auto updater
const String CURRENT_VERSION = String("__BINARY_NAME:waterLevelSensor__BINARY_VERSION:") + __DATE__ + __TIME__ + "__";
const String BASE = String("/waterLevelSensor");
String latestVersion, downloadUrl;

FirebaseData fd;
VL53L0X sensor;

int extraDelay = 0;
long distance = 0, duration;
int distances[ARRAY_SIZE];

int getDistance() {
  distance = sensor.readRangeSingleMillimeters();
}

int calculatePercentage(int distance) {
    Firebase.getInt(fd, BASE + "/configuration/maximumDistanceCm");
    int maximumDistanceCm = fd.intData();
    Firebase.getInt(fd, BASE + "/configuration/minimumDistanceCm");
    int minimumDistanceCm = fd.intData();

    return max(min(100, (100 - (((distance - minimumDistanceCm) * 100) / max((maximumDistanceCm - minimumDistanceCm), 1)))), 0);
}

// qsort requires you to create a sort function
int sort_desc(const void *cmp1, const void *cmp2)
{
  int a = *((int *)cmp1);
  int b = *((int *)cmp2);
  return b - a;
}

int findMode() {
  qsort(distances, ARRAY_SIZE, sizeof(distances[0]), sort_desc);
  int number = distances[0];
  int mode = number;
  int count = 1;
  int countMode = 1;
  
  for (int i=1; i<ARRAY_SIZE; i++)
  {
    if (distances[i] == number) { 
       ++count;
    }
    else { // now this is a different number
      if (count > countMode) {
            countMode = count; // mode is the biggest ocurrences
            mode = number;
      }
      count = 1; // reset count for the new number
      number = distances[i];
    }
  }
  return mode;
}

int getPercentage() {
  String distanceString = String("");
  for (int i=0; i<ARRAY_SIZE; ++i) {
    distances[i] = getDistance();
    distanceString += String(distances[i]) + " ";
    int remainder = distances[i] % ROUND_UPTO;
    if (remainder > 0) {
      distances[i] += ROUND_UPTO - remainder;
    }
    delay(UPDATE_INTERVAL / ARRAY_SIZE);
  }
  Firebase.set(fd, BASE + "/output/rawDistances", distanceString);

  // Find mode of the data
  int finalDistance = findMode();
  Firebase.set(fd, BASE + "/output/calculatedDistance", finalDistance);

  int percentage = calculatePercentage(finalDistance);
  return percentage;
}

void setup() {
  Serial.begin(9600);
  pinMode(GND, OUTPUT);
  pinMode(VCC, OUTPUT);
  Wire.begin(SDA, SCL);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.println("");

  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);

  sensor.setTimeout(500);
  sensor.init();
  sensor.setMeasurementTimingBudget(200000);
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

  /* checkForUpdates(); */
  delay(1000);
}
