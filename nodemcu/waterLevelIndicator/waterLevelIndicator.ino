#include "FirebaseESP8266.h"
#include <ESP8266WiFi.h>
#include <LedControl.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266httpUpdate.h>

#define FIREBASE_HOST "water-level-indicator-a555e-default-rtdb.firebaseio.com"  //Change to your Firebase RTDB project ID e.g. Your_Project_ID.firebaseio.com
#define FIREBASE_AUTH "RSycUEGVNj1wiOVGrmXjQkdpE65voJNJmaGPs3Z7" //Change to your Firebase RTDB secret password
/* #define WIFI_SSID "Sarayi_ff_24" */
/* #define WIFI_PASSWORD "abduljabbar" */
#define WIFI_SSID "sarayi_2"
#define WIFI_PASSWORD "code||die"

#define DIN D5
#define CS D6
#define CLK D7

const String CURRENT_VERSION = String("__BINARY_NAME:waterLevelIndicator__BINARY_VERSION:") + __DATE__ + __TIME__ + "__";

//Define Firebase Data objects
FirebaseData fd;
double timestamp, lastMeasurement, lastPercentage;
String latestVersion, downloadUrl;
int maxVal, minVal, anomalyDist, printMode, percentage, measurement, numrows, brightness, switchToDirectModeAfterMins, percent;
int numbers[][3] = {{0x7c, 0x44, 0x7c}, {0, 0, 0x7c}, {0x5c, 0x54, 0x74}, {0x54, 0x54, 0x7c}, {0x70, 0x10, 0x7c}, {0x74, 0x54, 0x5c}, {0x7c, 0x54, 0x5c}, {0x40, 0x40, 0x7c}, {0x7c, 0x54, 0x7c}, {0x74, 0x54, 0x7c}};
int revNumbers[][3] = {{0x3e, 0x22, 0x3e}, {0x0, 0x0, 0x3e}, {0x3a, 0x2a, 0x2e}, {0x2a, 0x2a, 0x3e}, {0xe, 0x8, 0x3e}, {0x2e, 0x2a, 0x3a}, {0x3e, 0x2a, 0x3a}, {0x2, 0x2, 0x3e}, {0x3e, 0x2a, 0x3e}, {0x2e, 0x2a, 0x3e}};

LedControl lc= LedControl(DIN, CLK, CS, 0);

void printnumber(int digit, int isTens) {
  int startCol = 1;
  if (!isTens) {
     startCol = 5;
  }
  for (int i=0; i <3; ++i) {
    lc.setRow(0, i+startCol, revNumbers[digit][i]);
  }
}

void numberPercent(int percent) {
  if (percent == 100) {
    lightBar(percent);
    return;
  }
  int ones = percent % 10;
  int tens = percent / 10;
  lc.clearDisplay(0);
  printnumber(ones, false);
  printnumber(tens, true);
}

void lightBar(int percent) {
  if (percent == 100) {
    percent = 99;
  } 
  numrows = percent * 8 / 100;
  lc.clearDisplay(0);
  for (int i=0; i<numrows; ++i) {
    lc.setColumn(0, i, 0xff);
  }
  Serial.print("Number of rows to light up: ");
  Serial.println(numrows);
}

void lightLED(int percent, int printMode) {
  if (printMode == 0) {
    lightBar(percent);
  } else if (printMode == 1) {
    numberPercent(percent);    
  }
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

int calculateNormalPercentage() {
    Firebase.getInt(fd, "/waterlevel/measurement");
    measurement = fd.intData();
    Firebase.getInt(fd, "settings/maximumValue");
    maxVal = fd.intData();
    Firebase.getInt(fd, "settings/minimumValue");
    minVal = fd.intData();

    percent = max(min(100, (100 - (((measurement - minVal) * 100) / max((maxVal - minVal), 1)))), 0);
    return percent;
}

void loop() {
  checkForUpdates();
  Firebase.getInt(fd, "/waterlevel/percentage");
  percentage = fd.intData();

  Firebase.getDouble(fd, "/waterlevel/timestamp");
  lastMeasurement = fd.doubleData();

  Firebase.getDouble(fd, "/waterlevel/percentageUpdatedAt");
  lastPercentage = fd.doubleData();


  Firebase.getInt(fd, "/settings/switchToDirectModeAfterMins");
  switchToDirectModeAfterMins = fd.intData();

  if (lastMeasurement - lastPercentage > switchToDirectModeAfterMins * 60 * 1000) {
    Serial.println("Direct mode");
    percentage = calculateNormalPercentage();
    Serial.println(percentage);
  }

  Firebase.getInt(fd, "settings/printMode");
  printMode = fd.intData();
  Firebase.getInt(fd, "settings/brightness");
  brightness = fd.intData();
    
  lc.setIntensity(0, brightness);
   
  lightLED(percentage, printMode);
  Serial.println(percentage);
  Serial.println(timestamp);
  Serial.println(".");
  delay(10000);
}
