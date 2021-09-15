arduino-cli compile waterLevelIndicator --fqbn esp8266:esp8266:nodemcu -e
arduino-cli upload -p /dev/cu.usbserial-0001 --fqbn esp8266:esp8266:nodemcu waterLevelIndicator.ino
