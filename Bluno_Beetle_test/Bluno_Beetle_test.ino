int fsrReading;

void setup() {

  Serial.begin(115200); //initial the Serial

}

void loop(){

  fsrReading = analogRead(0);
  Serial.print("Reading = ");
  Serial.println(fsrReading);

  delay(100);
  
}
