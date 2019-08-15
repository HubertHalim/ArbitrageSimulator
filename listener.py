from socket import *
import threading
import pandas as pd
from datetime import datetime

#importing excel
filepath = 'C:/Users/Ze Hong/Documents/GitHub/ArbitrageSimulator/datasets/'
reu_data = pd.read_csv( filepath + "reu.csv", header = None)
# ebs_data = pd.read_csv( filepath + "ebs.csv", header = None)
# bbg_data = pd.read_csv( filepath + "bbg.csv", header = None)

# ttl = {"reu" : 3,"ebs" : 4,"bbg" : 5}
ttl = 3
# creating dictionaries for 3 providers
reu_dict = {}
# ebs_dict = {}
# bbg_dict = {}
#creating an array that has company name (key) and transaction data (values)
# comb_dict = {}

#inserting timestamp (key) and bid, ask price (values) into dictionary
for index, row in reu_data.iterrows():
    reu_dict[row[0]] = [row[2], row[3]]

# comb_dict['reu'] = reu_dict
#
# for index, row in ebs_data.iterrows():
#     ebs_dict[row[0]] = [row[2], row[3]]
#
# comb_dict['ebs'] = ebs_dict
#
#
# for index, row in bbg_data.iterrows():
#     bbg_dict[row[0]] = [row[2], row[3]]
#
# comb_dict['bbg'] = bbg_dict



serverPort = 5000
serverSocket = socket(AF_INET,SOCK_STREAM)
serverSocket.bind(('',serverPort))
serverSocket.listen(1)
starttime = datetime.now()
start_timestamp = datetime.timestamp(starttime)
print('The server is ready to receive')

def handle_client_connection(client_socket):
    counter = -1
    return_string = ''
    request = client_socket.recv(1024)
    now = datetime.now()
    now_timestamp = datetime.timestamp(now)
    #get time difference between listening and receiving msg (in milliseconds)
    diff = now_timestamp - start_timestamp
    #handle received message
    sentence = request.decode()
    # returns an array of string values eg ['b','00:00:00','bbg','s','00:00:01','reu']
    record = sentence.split(",")
    #receive some transaction
    if len(record) > 0:
        #company names for buy and sell
        buy_coy = record[2]
        sell_coy = record[5]
        if buy_coy ==  'reu':
            #time in milliseconds + ttl need to handle the string datetime here to convert to milliseconds
            total_valid_time = record[2] + ttl*1000
            if total_valid_time > diff:
                #transaction is valid
                return_string += 's,'
            else:
                #transaction is invalid
                return_string += 'f,'
        elif sell_coy == 'reu':
            # time in milliseconds + ttl need to handle the string datetime here to convert to milliseconds
            total_valid_time = record[4] + ttl * 1000
            if total_valid_time > diff:
                # transaction is valid
                return_string += 's,'
            else:
                # transaction is invalid
                return_string += 'f,'
    else:
        return_string += 'n,'

    # add newest record in dictionary to return string
    if counter != len(reu_dict.keys) - 1:
        counter += 1
        return_string += reu_dict.keys[counter] + ','
        return_string += str(reu_dict[reu_dict.keys[counter]][0]) + ',' + str(reu_dict[reu_dict.keys[counter]][1])
    client_socket.send(return_string)
    client_socket.close()

while True:
    connectionSocket, addr = serverSocket.accept()

    sentence = connectionSocket.recv(1024).decode() 
    capitalizedSentence = sentence.upper() 
    print(capitalizedSentence)
    connectionSocket.send(capitalizedSentence.encode()) 
    # connectionSocket.close()

connectionSocket.close()
