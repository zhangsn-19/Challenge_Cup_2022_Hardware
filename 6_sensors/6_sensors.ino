int fsrReading;

void setup(void) {
  Serial.begin(9600);
}

void loop(void) {
  
  Serial.println("--------------------");

  // 通过注释掉其他的来获取某几个传感器的数据
  
  fsrReading = analogRead(0);
  Serial.print("Reading[0] = ");
  Serial.print(fsrReading); Serial.print("  ");
  
  fsrReading = analogRead(1);
  Serial.print("Reading[1] = ");
  Serial.print(fsrReading); Serial.print("  ");

  fsrReading = analogRead(2);
  Serial.print("Reading[2] = ");
  Serial.print(fsrReading); Serial.print("  ");

  fsrReading = analogRead(3);
  Serial.print("Reading[3] = ");
  Serial.print(fsrReading); Serial.print("  ");

  fsrReading = analogRead(4);
  Serial.print("Reading[4] = ");
  Serial.print(fsrReading); Serial.print("  ");

  fsrReading = analogRead(5);
  Serial.print("Reading[5] = ");
  Serial.print(fsrReading); Serial.print("  ");

//  for(int i = 0; i < 6; i++) {
//    fsrReading = analogRead(i);
//    Serial.print("Reading["); Serial.print(i); Serial.print("] = ");
//    Serial.print(fsrReading); Serial.print("  ");
//  }

  Serial.println("");
}
