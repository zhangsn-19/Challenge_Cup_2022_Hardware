package com.dfrobot.angelo.blunobasicdemo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOError;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BlunoLibrary {

	// 所有属性的作用都在之后相应位置标出

	private Button buttonScan;
	private Button buttonRegister;
	private Button buttonTest;
	private Button buttonSerialSend;
	private EditText serialSendText;
	private TextView serialReceivedText;
	private TextView testRes;
	private Socket socket;
	private List<String> msgList = new ArrayList<>();
	private List<String> registerList = new ArrayList<>();
	private List<String> testList = new ArrayList<>();
	private Boolean TestLock = true;
	public static Boolean RegisterLock = true;
	private Boolean isRegister = false;
	private FrameLayout frame;
	private Boolean meetFirstNotZero = false;
	private int zeroCount = 0;
	private int timeCount = 0;
	public static Boolean inProgress = false;
	public static int sensorType = 1;
	public static int sensor1 = 0;
	public static int sensor2 = 0;
	public static Boolean sendLock = true;
	private Boolean sendRegisterOverLock = true;
	private String registerMsg = "";
	private String testMsg = "";
	private int count = 0;
	private boolean isTest = false;
	private boolean needSleep = false;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (!inProgress) {
			return super.dispatchTouchEvent(ev);
		}
		// 如果正在注册中，禁用所有点击事件
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		request(1000, new OnPermissionsResult() {
			@Override
			public void OnSuccess() {
				Toast.makeText(MainActivity.this, "权限请求成功", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void OnFail(List<String> noPermissions) {
				Toast.makeText(MainActivity.this, "权限请求失败", Toast.LENGTH_SHORT).show();
			}
		});

		onCreateProcess();                                                        //onCreate Process by BlunoLibrary


		serialBegin(115200);                                                    //set the Uart Baudrate on BLE chip to 115200

//        serialReceivedText=(TextView) findViewById(R.id.serialReveicedText);	//initial the EditText of the received data
//        serialSendText=(EditText) findViewById(R.id.serialSendText);			//initial the EditText of the sending data

//        buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend);		//initial the button for sending the data
//        buttonSerialSend.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//				serialSend(serialSendText.getText().toString());				//send the data to the BLUNO
//			}
//		});

		testRes = (TextView) findViewById(R.id.testRes);

		buttonScan = (Button) findViewById(R.id.buttonScan);                    //initial the button for scanning the BLE device
		buttonScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				buttonScanOnClickProcess();                                        //Alert Dialog for selecting the BLE device
			}
		});

		// 打开注册fragment
		buttonRegister = (Button) findViewById(R.id.buttonRegister);
		buttonRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				isRegister = true;
				RegisterFragment showRegister = RegisterFragment.newInstance();
				FragmentManager manager = getFragmentManager();
				FragmentTransaction transaction = manager.beginTransaction();
				transaction.add(R.id.register, showRegister);
				transaction.commit();
				frame = (FrameLayout) findViewById(R.id.register);
				frame.setVisibility(View.VISIBLE);

			}
		});

		// 打开or关闭测试锁
		buttonTest = (Button) findViewById(R.id.buttonTest);
		buttonTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!isTest) {
					buttonTest.setText("Running");
					Toast.makeText(MainActivity.this, "Running...", Toast.LENGTH_SHORT).show();
					TestLock = false;
					meetFirstNotZero = false;
					isTest = true;
				} else {
					buttonTest.setText("Waiting");
					Toast.makeText(MainActivity.this, "Waiting...", Toast.LENGTH_SHORT).show();
					isTest = false;
					TestLock = true;
				}
			}
		});

		// Socket传输线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Log.i("Socket: ", "Start Connecting");
					socket = new Socket("1.13.174.7", 3389);
					if (socket == null) {
						Log.e("Socket: ", "Connection Error");
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(MainActivity.this, "服务器连接成功！", Toast.LENGTH_SHORT).show();
						}
					});
					Log.i("Socket: ", "Connected");
					while (true) {
						if (!msgList.isEmpty()) {
							if (!sendLock) {
								// 传输手势data
								String str = msgList.remove(0);
								Log.i("Socket: ", str);
								socket.getOutputStream().write(str.getBytes());
								sendLock = true;
								if(!TestLock) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											Toast.makeText(MainActivity.this, "手势已发送", Toast.LENGTH_SHORT).show();
										}
									});
									// 若在test，则还要接收返回的predict结果
									final byte[] bytes = new byte[1024];
									socket.getInputStream().read(bytes);
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											testRes.setText("检测结果为：" + new String(bytes));
										}
									});
								}
							}
						}
						// 传输注册完毕信号，并接收model训练完毕信号
						if (!sendRegisterOverLock) {
							Log.i("register over signal", "Socket: ");
							socket.getOutputStream().write("O".getBytes());
							byte[] bytes = new byte[1024];
							socket.getInputStream().read(bytes);
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(MainActivity.this, "注册完毕", Toast.LENGTH_SHORT).show();
								}
							});
							sendRegisterOverLock = true;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	protected void onResume() {
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();                                                        //onResume Process by BlunoLibrary
	}

	@Override
	public void onBackPressed() {
		// 注册界面返回时默认算注册完毕，准备发送数据
		if (isRegister) {
			frame.setVisibility(View.GONE);
			isRegister = false;
			inProgress = false;
			sendRegisterOverLock = false;
			Toast.makeText(MainActivity.this, "数据已上传，正在注册，请等待注册完毕信息", Toast.LENGTH_LONG).show();
			Log.i("register over", "onBackPressed: ");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);                    //onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		onPauseProcess();                                                        //onPause Process by BlunoLibrary
	}

	protected void onStop() {
		super.onStop();
		onStopProcess();                                                        //onStop Process by BlunoLibrary
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		onDestroyProcess();                                                        //onDestroy Process by BlunoLibrary
	}

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
		switch (theConnectionState) {                                            //Four connection state
			case isConnected:
				buttonScan.setText("Connected");
				break;
			case isConnecting:
				buttonScan.setText("Connecting");
				break;
			case isToScan:
				buttonScan.setText("Scan");
				break;
			case isScanning:
				buttonScan.setText("Scanning");
				break;
			case isDisconnecting:
				buttonScan.setText("isDisconnecting");
				break;
			default:
				break;
		}
	}

	@Override
	public void onSerialReceived(String theString) {
		theString = theString.trim();

		/*
			蓝牙传输的数据(theString)格式为：sensor0Data sensor1Data sensor2Data sensor3Data
			注意！由于检测速度必须较快（目前FPS=50），theString数据有一定概率丢失一部分
			所以使用之前必须首先判断是不是够四个数，每个数能不能parseInt
			不满足的目前直接丢弃
		**/

		// 在相邻两次test手势之间需要sleep一会，因为如果连续检测的话可能本来只想做一次手势但是会传输两次数据
		if (needSleep) {
			return;
		}
		// 这里写得很丑，但是既然现在跑得没问题就不敢动了（
		// 若打开了测试锁
		if (!TestLock) {
			String tmp[] = theString.split(" ");
			//
			if (tmp.length == 4) {
				// 如果还没遇到第一个有效数据
				if (!meetFirstNotZero) {
					for (int i = 0; i < 4; i++) {
						try {
							// 如果有某个sensor的读数>100，就算开始点击了
							if (Integer.parseInt(tmp[i]) > 100) {
								meetFirstNotZero = true;
								zeroCount = 0; // 用来记录开始点击后有几次theString全0，用来快速识别除长按以外的操作
								timeCount = 0; // 用来记录开始点击后的总时间，用来避免长按太长
								testList = new ArrayList<>(); // 存储这一次手势读取到的所有数据
								testList.add(theString);
								testMsg = "T "; // 最后传输给服务器的String，T开头表明是在test
								sensorType = 1;
								sensor1 = sensor2 = -1; // 由于并不知道用户会点击几个传感器，统一先初始化成这样；sensorType=1代表用了一个，=2用了两个，sensor1是用的第一个传感器的编号，sensor2同理
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}
					}
				} else {
					boolean flag = false, zeroFlag = false;
					Log.i(theString, "onSerialReceived: ");
					testList.add(theString);
					for (int i = 0; i < tmp.length; i++) {
						try {
							// 从第一次有效点击之后，每次蓝牙传的四个数据中若有一个>20就算作非0数据
							if (Integer.parseInt(tmp[i]) > 20) {
								zeroFlag = true;
								// 更新用了几个传感器
								if (sensorType == 1) {
									if (sensor1 == -1) {
										sensor1 = i;
									}
									if (sensor1 >= 0 && i != sensor1) {
										sensor2 = i;
										sensorType = 2;
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							flag = true;
							break;
						}
					}
					if (!zeroFlag) {
						zeroCount++;
					}
					if (!flag) {
						timeCount++;
						testMsg = "T ";
						// 如果全0数据够多了或者时间够长了
						if (zeroCount > 5 || timeCount > 40) {
							meetFirstNotZero = false;
							// 根据传感器使用情况处理所有的数据，处理完毕后将testMsg添加到msgList中，并打开发送信息锁
							// msgList一旦非空，且打开了发送信息锁，Socket线程就会将msgList中的第一个数据发送出去，并关闭发送信息锁
							if (sensor1 != -1) {
								if (sensorType == 1) {
									int i;
									for (i = 0; i < testList.size() && i < 40; i++) {
										tmp = testList.get(i).split(" ");
										int num = Integer.parseInt(tmp[sensor1]);
										testMsg += num + " ";
									}
									for (; i < 82; i++) {
										testMsg += "0 ";
									}
									testMsg = testMsg.trim();
									msgList.add(testMsg);
									sendLock = false;
								} else if (sensorType == 2) {
									int i;
									for (i = 0; i < testList.size() && i < 40; i++) {
										tmp = testList.get(i).split(" ");
										int num = Integer.parseInt(tmp[sensor1]);
										testMsg += num + " ";
									}
									for (; i < 42; i++) {
										testMsg += "0 ";
									}
									for (i = 0; i < testList.size() && i < 40; i++) {
										tmp = testList.get(i).split(" ");
										int num = Integer.parseInt(tmp[sensor2]);
										testMsg += num + " ";
									}
									for (; i < 40; i++) {
										testMsg += "0 ";
									}
									testMsg = testMsg.trim();
									msgList.add(testMsg);
									sendLock = false;
								}
								// 停顿0.5s，减少误触情况
								needSleep = true;
								new Thread(new Runnable() {
									@Override
									public void run() {
										try {
											Thread.sleep(500);
										} catch (Exception e) {
											e.printStackTrace();
										}
										needSleep = false;
									}
								}).start();
							}
						}
					}
				}
			}
		// 若打开了注册锁，和测试流程基本一样，只是注册时需要连续录入三次，用属性count记录；数据格式类似
		} else if (!RegisterLock) {
			if (meetFirstNotZero) {
				Log.i(theString, "onSerialReceived: ");
				String tmp[] = theString.split(" ");
				if (tmp.length == 4) {
					boolean flag = false, zeroFlag = false;
					Log.i(theString, "onSerialReceived: ");
					registerList.add(theString);
					for (int i = 0; i < tmp.length; i++) {
						try {
							if (Integer.parseInt(tmp[i]) > 20) {
								zeroFlag = true;
								if (sensorType == 1) {
									if (sensor1 == -1) {
										sensor1 = i;
									}
									if (sensor1 >= 0 && i != sensor1) {
										sensor2 = i;
										sensorType = 2;
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							flag = true;
							break;
						}
					}
					if (!zeroFlag) {
						zeroCount++;
					}
					if (!flag) {
						timeCount++;
						if (zeroCount > 20 || timeCount > 40) {
							Log.i("" + count + " " + sensorType + " " + sensor1 + " " + sensor2, "onSerialReceived: ");
							count++;
							meetFirstNotZero = false;
							RegisterLock = true;
							if (sensor1 != -1) {
								if (sensorType == 1) {
									if (count == 1) {
										registerMsg = "R ";
									}
									int i;
									for (i = 0; i < registerList.size() && i < 40; i++) {
										tmp = registerList.get(i).split(" ");
										int num = Integer.parseInt(tmp[sensor1]);
										registerMsg += num + " ";
									}
									for (; i < 82; i++) {
										registerMsg += "0 ";
									}
									registerMsg = registerMsg.trim();
									if (count != 3) {
										registerMsg += "|";
									} else {
										msgList.add(registerMsg);
										sendLock = false;
										count = 0;
										registerMsg = "";
										Log.i("Socket Unlock", "onSerialReceivedUnlock: ");
									}
									Log.i(registerMsg, "onSerialReceived!: ");
								} else if (sensorType == 2) {
									if (count == 1) {
										registerMsg = "R ";
									}
									int i;
									for (i = 0; i < registerList.size() && i < 40; i++) {
										tmp = registerList.get(i).split(" ");
										int num = Integer.parseInt(tmp[sensor1]);
										registerMsg += num + " ";
									}
									for (; i < 42; i++) {
										registerMsg += "0 ";
									}
									for (i = 0; i < registerList.size() && i < 40; i++) {
										tmp = registerList.get(i).split(" ");
										int num = Integer.parseInt(tmp[sensor2]);
										registerMsg += num + " ";
									}
									for (; i < 40; i++) {
										registerMsg += "0 ";
									}
									registerMsg = registerMsg.trim();
									if (count != 3) {
										registerMsg += "|";
									} else {
										msgList.add(registerMsg);
										sendLock = false;
										count = 0;
										registerMsg = "";
									}
								}

								registerList = new ArrayList<>();
							}
						}
					}
				}
			} else {
				String tmp[] = theString.split(" ");
				if (tmp.length == 4) {
					for (int i = 0; i < 4; i++) {
						try {
							if (Integer.parseInt(tmp[i]) > 100) {
								meetFirstNotZero = true;
								zeroCount = 0;
								timeCount = 0;
								registerList = new ArrayList<>();
								registerList.add(theString);
								sensorType = 1;
								sensor1 = sensor2 = -1;
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}
					}
				}
			}
		}
//		serialReceivedText.append(theString);							//append the text into the EditText
		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
//		((ScrollView)serialReceivedText.getParent()).fullScroll(View.FOCUS_DOWN);
	}
}
