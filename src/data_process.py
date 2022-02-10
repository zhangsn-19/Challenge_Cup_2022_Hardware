from process import process


def further_process():
    single_list, double_list = [], []
    single_list_labels, double_list_labels = [], []
    cluster_single, cluster_double = process()
    for cluster in cluster_single.keys():
        single_list.append([])
        single_list_labels.append(cluster)
        flag, raised, start, end = 0, 0, 0, 0
        for count, element in enumerate(cluster_single[cluster]):
            if element == '0':
                if raised == 0:
                    flag += 1
                    if flag == 2:
                        end = count
                    elif flag > 4:
                        flag, raised = 0, 1
                        if end != 1:
                            single_list[-1].append(cluster_single[cluster][start:end])
            else:
                if flag == 0:
                    flag, raised = 1, 0
                    start = count 
    print(single_list)

    for cluster in cluster_double.keys():
        double_list.append(([], []))
        double_list_labels.append(cluster)
        flag, raised, start, end = 0, 0, 0, 0
        for count, element in enumerate(zip(cluster_double[cluster][0], cluster_double[cluster][1])):
            # a bit complicated
            first_touch, second_touch = element
            if first_touch == '0' and second_touch == '0':
                if raised == 0:
                    flag += 1
                    if flag == 2:
                        end = count
                    elif flag > 5:
                        flag, raised = 0, 1
                        if end != 1:
                            double_list[-1][0].append(cluster_double[cluster][0][start:end])
                            double_list[-1][1].append(cluster_double[cluster][1][start:end])
            else:
                if flag == 0:
                    flag, raised = 1, 0
                    start = count
    print(double_list)
    return single_list, double_list, single_list_labels, double_list_labels
# further_process()
# To be checked



