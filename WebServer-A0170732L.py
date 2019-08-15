from socket import *
import sys
import os

database = {}

class handleRequest:

    def handle(content, self):
        if content[0].lower() == "get":
            return self.do_GET(content, self)
        elif content[0].lower() == "post":
            return self.do_POST(content, self)
        else:
            return self.do_DEL(content, self)

    def do_GET(content, self):
        filepath = content[1].split("/")
        response = [] 
        if filepath[1] == "file":
            path = filepath[len(filepath) - 1]
            path = "./" + path
            try:
                with open(path, "rb") as f:
                    responseHead = "200 OK "
                    byte = f.read()
                    size = len(byte)
                    responseHead +=  "Content-Length " + str(size)
                    responseHead += "  "
                    responseBody = byte
                    response.append(responseHead)
                    response.append(responseBody)
                return response
            except (FileNotFoundError):
                response.append("404 NotFound  ")
                return response
            except :
                response.append("403 AccessDenied  ")
                return response
        elif filepath[1] == "key":
            key = filepath[len(filepath)-1]
            if key in database.keys():
                responseHead = "200 OK "
                value = database[key]
                size = len(value)
                responseHead += "Content-Length " + str(size)
                responseHead += "  "
                responseBody = value
                response.append(responseHead)
                response.append(responseBody)
                return response
            else:
                response.append("404 NotFound  ")
                return response

    def do_POST(content, self):
        keypath = content[1].split("/")
        response = []
        if keypath[1] == "key":
            key = keypath[len(keypath) - 1]
            for i in range(len(content)):
                if content[i].lower() == "content-length":
                    break
            size = int(content[i + 1])
            value = content[len(content) - 1]
            database[key] = value
            response.append("200 Okay  ")
        else:
            response.append("404 NotFound ")
        return response

    def do_DEL(content, self):
        keypath = content[1].split("/")
        response = []
        if keypath[1] == "key":
            key = keypath[len(keypath) - 1]
            if key in database.keys():
                responseHead = "200 OK Content-Length "
                value = database[key]
                size = len(value)
                responseHead += str(size) + "  "
                responseBody = value
                response.append(responseHead)
                response.append(responseBody)
                del database[key]
                return response
            else:
                 response.append("404 NotFound")
                 return response

serverPort = int(sys.argv[1])
serverSocket = socket(AF_INET, SOCK_STREAM)
serverSocket.bind(("", serverPort))
serverSocket.listen(1)

while True:
    connectionSocket, addr = serverSocket.accept()
    done = False
    
    incoming = connectionSocket.recv(4096)
    
    while incoming:
        more = False
        for k in range (0, len(incoming)):
            if incoming[k:k+4].decode() == "POST" or incoming[k:k+3].decode() == "GET" or incoming[k:k+6].decode() == "DELETE":
                more = True
                break
        
        while more:
            length = 0
            more = False
            content = ""
            notDone = True
            while notDone:
                for i in range (0, len(incoming)):
                    if incoming[i:i+2].decode() == "  ":
                        content += (incoming[0:i+2]).decode()
                        incoming = incoming[i+2:len(incoming)]
                        tempParsed = content.split(" ")
                        notDone = False
                        for j in range (0, len(tempParsed)):
                            if tempParsed[j].lower() == "content-length":
                                length = int(tempParsed[j + 1])
                                break
                        break
                if notDone:
                    content = incoming.decode()
                    incoming = connectionSocket.recv(4096) 
            
            parsed = content.split(" ")

            if length > 0:
                body = bytearray()
                while length > 0:
                    if len(incoming) >= length:
                        for b in range (0, length):
                            body.append(incoming[b])
                        incoming = incoming[length:len(incoming)]
                        length = 0 
                    else:
                        for b in range (0, len(incoming)):
                            body.append(incoming[b])
                        length = length - len(incoming[0:len(incoming)])
                        incoming = connectionSocket.recv(4096)
                parsed.append(bytes(body))        
                        
            for j in range (0, len(incoming)):
                if incoming[j:j+4].decode() == "POST" or incoming[j:j+3].decode() == "GET" or incoming[j:j+6].decode() == "DELETE":
                    more = True
                    break       

            response = handleRequest.handle(parsed, handleRequest)
            connectionSocket.send(response[0].encode())
            if (len(response) > 1):
                if (len(response[1]) > 0):
                    responseBody = response[1]
                    connectionSocket.send(responseBody)       

        if len(incoming.decode()) < 1:            
            incoming = connectionSocket.recv(4096)
        else:
            incoming += connectionSocket.recv(4096) 
            
    connectionSocket.close()

