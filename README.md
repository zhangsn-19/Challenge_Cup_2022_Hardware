## Challenge Cup Hardware Algorithm

### Update

#### 2022.02.11

增加了蓝牙主控板Bluno Beetle BLE与手机蓝牙通讯的demo。Android设备需要安装BlunoBasicDemo.apk，打开**蓝牙**和**位置信息**之后点击`Scan`搜索设备。使用手机自带的蓝牙搜索无效。过快地实时传输数据会混乱。

同时测量得到：（使用Arduino Uno时）不按压传感器外部电路没有电流；在比较用力的情况下，每多按压一个传感器，电流会增加约0.48mA。

#### 2022.02.10

增加了指纹相关（ZW651）的以及获取六个点位传感器数据（RP-C18.3, RP-C7.6, FSR406）的代码。
