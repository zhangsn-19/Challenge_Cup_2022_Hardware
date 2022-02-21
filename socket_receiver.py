import json
import socket
import time

import joblib
import numpy as np
import sklearn
from sklearn import svm
from sklearn.model_selection import train_test_split
from sklearn.utils import column_or_1d

from process import classify, register_naive

try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
except socket.error as e:
    print(f'Failed to create socket. Error: {e}')

addr = ('', 3389)
s.bind(addr)

# 用不用考虑多个手机同时连，或者断连后的重连？
s.listen(1)

print('Begin connecting...')
socket_con, (client_ip, client_port) = s.accept()
print(f'Connection accepted from {client_ip}')
# label = 1
# train_data = [[[0 for _ in range(82)] for i in range(3)] for j in range(16)]
# train_data = np.load('./data-183.172.201.128.npy')
# model = register_naive(train_data, data_process=False)

train_data = []

# ans_list = ['相机切换', ' ']
# ans_list = ['音量增高', '音量降低', ' ']
# ans_list = ['场景缩放', ' ']
# ans_list = ['结束游戏', '进入关卡', '开始游戏', ' ']
# ans_list = ['柱体变高', '柱体变矮', '柱体左移', ' ']

ans_list = ['空手势', '柱体变高', '柱体前进', '柱体后退', '柱体变矮', '柱体左移', '音量增高', '音量降低', '空手势', '空手势', '结束游戏', '进入关卡', '开始游戏', '视角移动', '场景缩放', '相机切换']

while True:
    data = socket_con.recv(4096).decode('utf-8')
    if data is not None and len(data) != 0:
        if data[0] == 'R':
            data = data[2:].split('|')
            for i in range(len(data)):
                data[i] = [int(j) for j in data[i].split(' ')]
            train_data.append(data)
            print(data)
            # if label < 16:
            #     train_data[label] = data
            # if label != 7:
            #     label += 1
            # else:
            #     label = 10         
        elif data[0] == 'O':
            print('train_data: over')
            print(train_data)
            # np.save(f'./test_data.npy', train_data)
            model, pca_model = register_naive(train_data, data_process=False, _kernel="poly")
            socket_con.send('OK'.encode())
            # joblib.dump(model, f'../data/{client_ip}-{client_port}.m')
        elif data[0] == 'T':
            data = [int(i) for i in data[2:].split(' ')]
            print(f'test: {data}')
            # model = joblib.load(f'../data/{client_ip}-{client_port}.m')
            ans = int(classify([data], model, pca_model))
            socket_con.send(ans_list[ans].encode())
            # socket_con.send(str(ans).encode())
            print(f'Ans: {ans}')

        else:
            print(data)
        


