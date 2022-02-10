#include <Adafruit_Fingerprint.h>

SoftwareSerial mySerial(2, 3);

Adafruit_Fingerprint finger = Adafruit_Fingerprint(&mySerial, 2333);

void setup()  
{
  Serial.begin(9600);
  while (!Serial);
  delay(100);

  Serial.println("Deleting all fingerprint templates!");
  Serial.println("press 'Y' key to continue");

  while (1) {
    if (Serial.available() && (Serial.read() == 'Y')) {
      break;
    }
  }

  // set the data rate for the sensor serial port
  finger.begin(57600);
  
  if (finger.verifyPassword()) {
    Serial.println("Found fingerprint sensor!");
  } else {
    Serial.println("Did not find fingerprint sensor ");
    while (1);
  }
  
  finger.emptyDatabase();

  Serial.println("Now database is empty ");
}

void loop() {
}
