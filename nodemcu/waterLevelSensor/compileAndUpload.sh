arduino-cli compile waterLevelIndicator --fqbn esp8266:esp8266:nodemcu
arduino-cli upload -p ${1:-/dev/cu.usbserial-0001} --fqbn esp8266:esp8266:nodemcu waterLevelIndicator.ino
