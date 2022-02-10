import ipdb

def process():
    flag = 0
    clusters_single = {}
    label = ""
    with open("../raw_data_single.txt", "r") as in_file:
        read_lines = in_file.readlines()
        for line in read_lines:
            if line.startswith("###"):
                label = line.strip("\n").split("###")[1].strip(" ")
                flag = 1
            elif line.startswith("~~~"):
                flag += 1
                if flag == 2:
                    clusters_single[label] = []
                elif flag == 3:
                    flag = 0
            else:
                line = line.strip("\n")
                if (not line.endswith("-")) and line.startswith("1"):
                    clusters_single[label].append(line.strip("\n").split(" -> ")[1].split("=")[1].strip("\n").strip(" "))

    print(clusters_single)
    print("\n")
    print("\n")

    clusters_double = {}
    flag = 0
    with open("../raw_data_double.txt", "r") as in_file:
        read_lines = in_file.readlines()
        for line in read_lines:
            if line.startswith("##"):
                label = line.strip("\n").split("##")[1].strip("#").strip(" ")
                flag = 1
            elif line.startswith("~~~"):
                flag += 1
                if flag == 2:
                    clusters_double[label] = ([], [])
                elif flag == 3:
                    flag = 0
            else:
                line = line.strip("\n")
                if (not line.endswith("-")) and line.startswith("1"):
                    try:
                        clusters_double[label][0].append(line.strip("\n").split(" -> ")[1].split("=")[1].strip(" ").split(" ")[0].strip(" "))
                        clusters_double[label][1].append(line.strip("\n").split(" -> ")[1].split("=")[2].strip("\n").strip(" "))
                    except:
                        ipdb.set_trace()

    print(clusters_double)
    return clusters_single, clusters_double
            

