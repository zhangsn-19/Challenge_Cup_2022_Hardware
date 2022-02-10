import ipdb
import time
import copy
import sklearn
from sklearn import svm
from sklearn.model_selection import train_test_split
import numpy as np
from sklearn.utils import column_or_1d
from data_process import further_process
from config import PADDING_LENGTH, INNER_PADDING, SNR, AUGMENT

global_counter = 0
def padding(input_list):
    global global_counter
    new_input_list = []
    if isinstance(input_list, tuple):
        for element in zip(input_list[0], input_list[1]):
            new_element_first, new_element_second = element
            if len(new_element_first) < INNER_PADDING:
                for i in range(0, len(new_element_first)):
                    new_element_first[i] = int(new_element_first[i])
                len_first = len(new_element_first)
                new_element_first += [0 for _ in range(-len_first+INNER_PADDING)]
            if len(new_element_second) < INNER_PADDING:
                for i in range(0, len(new_element_second)):
                    new_element_second[i] = int(new_element_second[i])
                len_second = len(new_element_second)
                new_element_second += [0 for _ in range(-len_second+INNER_PADDING)]
            new_input_list.append(new_element_first + list([-1, -1]) + new_element_second + list([global_counter]))
    else:
        for element in input_list:
            new_element = element
            if len(element) < PADDING_LENGTH:
                for i in range(0, len(element)):
                    new_element[i] = int(new_element[i])
                new_element += [0 for _ in range(- len(element) + PADDING_LENGTH)]
            new_input_list.append(new_element + list([global_counter]))    
    global_counter += 1
    return new_input_list

def _add_noise(element, snr):
    snr = 10 ** (snr / 10.0)
    xpower = np.sum(element ** 2) / len(element)
    npower = xpower / snr
    return np.array(np.random.randn(len(element)) * np.sqrt(npower)) + element

def calculate_max(single_list, double_list):
    return max(
        max([len(single_list[_]) for _ in range(len(single_list))]),
        max([len(double_list[_]) for _ in range(len(double_list))])
    )

def add_noise(max_groups, new_single_list, new_double_list):
    ret_single_list, ret_double_list = [], []
    for single_element in new_single_list:
        noise_group_num = max_groups - len(single_element)
        for single_ in single_element:
            ret_single_list.append(np.array(single_))
        for idx in range(0, noise_group_num):
            ret_single_list.append(np.append(
                    _add_noise(np.array(single_element[idx % len(single_element)][:-1]), SNR),
                    np.array(single_element[idx % len(single_element)][-1])
                ))

    for double_element in new_double_list: 
        noise_group_num = max_groups - len(double_element)
        for double_ in double_element:
            ret_double_list.append(np.array(double_))
        for idx in range(0, noise_group_num):
            ret_double_list.append(np.append(
                    _add_noise(np.array(double_element[idx % len(double_element)][:-1]), SNR),
                    np.array(double_element[idx % len(double_element)][-1])
                ))

    return ret_single_list, ret_double_list

def main_flow():
    time_start_preparing = time.time()
    single_list, double_list, single_list_labels, double_list_labels = further_process()
    max_groups = calculate_max(single_list, double_list)
    new_single_list, new_double_list = [], [] 
    for single_element in single_list:
        new_single_list.append(padding(single_element))
    for double_element in double_list:
        new_double_list.append(padding(double_element))
    single_list, double_list = add_noise(max_groups * AUGMENT, new_single_list, new_double_list)
    single_np_array = np.array(single_list)
    double_np_array = np.array(double_list)

    data = np.concatenate((single_np_array, double_np_array), axis=0)

    x, y = np.split(data, (PADDING_LENGTH,), axis=1)

    # x = x[:, :2]
    # TODO: data augmentation 
    # x_after_aug, y_after_aug = augment(x, y)
    x_train, x_test, y_train, y_test = train_test_split(x, y, random_state=1, train_size=0.8, shuffle=True)
    y_train = column_or_1d(y_train, warn=True)
    y_test = column_or_1d(y_test, warn=True)
    time_end_preparing = time.time()
    # Assumed you have, X (predictor) and y (target) for training data set and x_test(predictor) of test_dataset
    # Create SVM classification object 
    model = svm.SVC(kernel='rbf', C=3, gamma='auto', decision_function_shape="ovr", tol=1e-5) # , verbose=1
    # There is various option associated with it, like changing kernel, gamma and C value. Will discuss more # about it in next section.Train the model using the training sets and check score
    time_start_training = time.time()
    model.fit(x_train, y_train)
    acc=model.score(x_train, y_train)
    time_end_training = time.time()
    # Predict Output
    time_start_predicting = time.time()
    predicted = model.predict(x_test)
    time_end_predicting = time.time()
    total_count, correct_count = len(predicted), 0

    for element_index in range(len(predicted)):
        correct_count += (predicted[element_index] == y_test[element_index])

    print("--------------------------------\n")
    print("Total test samples: ", total_count, "\n")
    print("Training acc: ", acc, "\n")
    print("Testing Acc: ", float(correct_count / total_count), "\n")
    print("Training time: ", (time_end_training - time_start_training), "\n")
    print("Preparing time: ", (time_end_preparing - time_start_preparing), "\n")
    print("Testing time: ", (time_end_predicting - time_start_predicting), "\n")
    print("Predicted result: ", predicted, "\n")
    print("\n")
    print("--------------------------------\n")

# Entrance
main_flow()