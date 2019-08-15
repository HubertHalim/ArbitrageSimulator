from socket import *
import threading

serverPort = 5000
serverSocket = socket(AF_INET,SOCK_STREAM)
serverSocket.bind(('',serverPort))
serverSocket.listen(1)

print('The server is ready to receive')

while True:
    connectionSocket, addr = serverSocket.accept()

    sentence = connectionSocket.recv(1024).decode() 
    capitalizedSentence = sentence.upper() 
    print(capitalizedSentence)
    connectionSocket.send(capitalizedSentence.encode()) 
    # connectionSocket.close()

connectionSocket.close()
