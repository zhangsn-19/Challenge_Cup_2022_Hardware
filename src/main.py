import ipdb
import copy
import sklearn
from sklearn import svm
from sklearn.model_selection import train_test_split
import numpy as np
from sklearn.utils import column_or_1d
from data_process import further_process
from config import PADDING_LENGTH, INNER_PADDING

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

single_list, double_list, single_list_labels, double_list_labels = further_process()
new_single_list, new_double_list = [], [] 
for single_element in single_list:
    new_single_list += padding(single_element)
for double_element in double_list:
    new_double_list += padding(double_element)
single_list, double_list = new_single_list, new_double_list 
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
# Assumed you have, X (predictor) and y (target) for training data set and x_test(predictor) of test_dataset
# Create SVM classification object 
model = svm.SVC(kernel='rbf', C=3, gamma='auto', decision_function_shape="ovr", tol=1e-5, verbose=1) 
# There is various option associated with it, like changing kernel, gamma and C value. Will discuss more # about it in next section.Train the model using the training sets and check score
model.fit(x_train, y_train)
acc=model.score(x_train, y_train)
print("\n--------------------------------\n")
print(acc)
print("--------------------------------\n")
# Predict Output
predicted = model.predict(x_test)
total_count, correct_count = len(predicted), 0

for element_index in range(len(predicted)):
    correct_count += (predicted[element_index] == y_test[element_index])

print("--------------------------------\n")
print("Acc: ", float(correct_count / total_count), "\n")
print(predicted)
print("\n")
print("--------------------------------\n")