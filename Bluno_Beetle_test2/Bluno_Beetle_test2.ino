int fsrReading;
String str = "";

void setup() {

  Serial.begin(115200); //initial the Serial

}

void loop(){

  for(int i = 0; i < 4; i++) {
    fsrReading = analogRead(i);
    str += String(fsrReading) + " ";
  }
  Serial.println(str);
  delay(50);
  str = "";
}
