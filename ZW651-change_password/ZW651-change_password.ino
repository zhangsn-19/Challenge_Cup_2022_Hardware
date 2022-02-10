#include <Adafruit_Fingerprint.h>

SoftwareSerial mySerial(2, 3);

// Using sensor without password
Adafruit_Fingerprint finger = Adafruit_Fingerprint(&mySerial);

// Using sensor with password
//Adafruit_Fingerprint finger = Adafruit_Fingerprint(&mySerial, 2333);

void setup()
{
  while (!Serial);

  Serial.begin(9600);
  Serial.println("Adafruit fingerprint sensor, change password example");

  // set the data rate for the sensor serial port
  finger.begin(57600);

  if (finger.verifyPassword()) {
    Serial.println("Found fingerprint sensor!");
  } else {
    Serial.println("Did not find fingerprint sensor ");
    while (1);
  }

  Serial.print("Set password... ");
  uint8_t p = finger.setPassword(2333);
  if (p == FINGERPRINT_OK) {
    Serial.println("OK"); // Password is set
  } else {
    Serial.println("ERROR"); // Failed to set password
  }
}

void loop()
{

}
