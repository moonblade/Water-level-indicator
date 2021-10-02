/* Arduino example sketch to control a JSN-SR04T ultrasonic distance sensor with Arduino. No library needed. More info: https://www.makerguides.com */
// Define Trig and Echo pin:
#define vcc D0
#define trigPin D1
#define echoPin D2
#define gnd D3
// Define variables:
long duration;
int distance;
void setup() {
  // Define inputs and outputs
  pinMode(trigPin, OUTPUT);
  pinMode(vcc, OUTPUT);
  pinMode(gnd, OUTPUT);
  pinMode(echoPin, INPUT);
  
  // Begin Serial communication at a baudrate of 9600:
  Serial.begin(9600);
}
void loop() {
  // Clear the trigPin by setting it LOW:
  digitalWrite(gnd, LOW);
  digitalWrite(vcc, HIGH);
  
  digitalWrite(trigPin, LOW);
  delayMicroseconds(5);
 // Trigger the sensor by setting the trigPin high for 10 microseconds:
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  
  // Read the echoPin. pulseIn() returns the duration (length of the pulse) in microseconds:
  duration = pulseIn(echoPin, HIGH);
  
  // Calculate the distance:
  distance = duration/58;
  
  // Print the distance on the Serial Monitor (Ctrl+Shift+M):
  Serial.print("Distance = ");
  Serial.print(distance);
  Serial.println(" cm");
  
  delay(500);
}
