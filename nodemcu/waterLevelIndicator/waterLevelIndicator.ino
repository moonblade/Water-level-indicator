// Libraries
#include "FirebaseESP8266.h"
#include <ESP8266WiFi.h>
#include <LedControl.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266httpUpdate.h>

// Firebase Auth
#define FIREBASE_HOST "water-level-indicator-a555e-default-rtdb.firebaseio.com"  
#define FIREBASE_AUTH "RSycUEGVNj1wiOVGrmXjQkdpE65voJNJmaGPs3Z7" 

// Wifi details
#define WIFI_SSID "Sarayi_ff_24"
#define WIFI_PASSWORD "abduljabbar"

// Defaults for operation
#define DIN D5
#define CS D6
#define CLK D7

const String CURRENT_VERSION = String("__BINARY_NAME:waterLevelIndicator__BINARY_VERSION:") + __DATE__ + __TIME__ + "__";
const String BASE = String("/waterLevelIndicator");

String latestVersion, downloadUrl;
//Define Firebase Data objects
FirebaseData fd;
int maxVal, minVal, anomalyDist, printMode, percentage, measurement, numrows, brightness, switchToDirectModeAfterMins, percent;
int rev_numbers[][3] = {{0x7c, 0x44, 0x7c}, {0, 0, 0x7c}, {0x5c, 0x54, 0x74}, {0x54, 0x54, 0x7c}, {0x70, 0x10, 0x7c}, {0x74, 0x54, 0x5c}, {0x7c, 0x54, 0x5c}, {0x40, 0x40, 0x7c}, {0x7c, 0x54, 0x7c}, {0x74, 0x54, 0x7c}};
int numbers[][3] = {{0x3e, 0x22, 0x3e}, {0x0, 0x0, 0x3e}, {0x3a, 0x2a, 0x2e}, {0x2a, 0x2a, 0x3e}, {0xe, 0x8, 0x3e}, {0x2e, 0x2a, 0x3a}, {0x3e, 0x2a, 0x3a}, {0x2, 0x2, 0x3e}, {0x3e, 0x2a, 0x3e}, {0x2e, 0x2a, 0x3e}};

LedControl lc= LedControl(DIN, CLK, CS, 0);

void printlns(String statement) {
  Serial.println(statement);
  Firebase.set(fd, "/waterLevelIndicator/logs/" + String(millis()), statement);
}

void printnumber(int digit, int isTens) {
  int startCol = 1;
  if (!isTens) {
     startCol = 5;
  }
  for (int i=0; i <3; ++i) {
    lc.setRow(0, i+startCol, numbers[digit][i]);
  }
}

void lightLED(int percent) {
  printlns("ledPercent: " + String(percent));
  if (percent == 100) {
    for (int i=0; i<numrows; ++i) {
      lc.setColumn(0, i, 0xff);
    }
    return;
  }
  int ones = percent % 10;
  int tens = percent / 10;
  lc.clearDisplay(0);
  printnumber(ones, false);
  printnumber(tens, true);
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
  Firebase.set(fd, "/waterLevelIndicator/firmware/currentVersion", CURRENT_VERSION);
  Firebase.getString(fd, "/waterLevelIndicator/firmware/binaryVersion");
  latestVersion = fd.stringData();

  if (!latestVersion.equalsIgnoreCase(CURRENT_VERSION)) {
    Serial.println("Version mismatch, downloading update");
    Firebase.getString(fd, "/waterLevelIndicator/firmware/downloadUrl");
    downloadUrl = fd.stringData();
    updateFirmware(downloadUrl);
  }
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.println("");

  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }

  lc.shutdown(0, false);
  lc.setIntensity(0, 10);
  lc.clearDisplay(0);

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
}

void loop() {
  Firebase.getInt(fd, "/waterLevelSensor/output/percentage");
  percentage = fd.intData();

  Firebase.getInt(fd, BASE + "/configuration/brightness");
  brightness = fd.intData();
  lc.setIntensity(0, brightness);
   
  lightLED(percentage);

  checkForUpdates();
  delay(10000);
}
