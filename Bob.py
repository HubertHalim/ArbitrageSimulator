from socket import *
import sys
import os
import zlib

def createCheckSum(data):
	return zlib.crc32(data)

def onesComplement(n):
    return ((1 << 32) - 1) ^ n

def handleChecksum(checksum, data):
	check = zlib.crc32(data)
	try:
		if (int(check) + int(checksum) == 4294967295):
			return True
		else:
			return False	
	except:
		return False

rcvPort = int(sys.argv[1])
rcvSocket = socket(AF_INET, SOCK_DGRAM)
rcvSocket.bind(("", rcvPort))

while True:
    
    incoming, addr = rcvSocket.recvfrom(4096)

    nextSequence = 0
    while incoming:
        
        data = incoming.decode()
        sequence = data[0]
        checksum = data[1:11]
        payload = data[11:len(data)]
        correct = handleChecksum(checksum, payload.encode())
        
        if (correct == False or int(nextSequence) != int(sequence)):
        	#print("duplicate/corrupted")
        	message = "ACK" + str(sequence)
        else:
        	nextSequence = (nextSequence + 1)%2
        	print(payload, end="")
        	message = "ACK" + str(sequence)

        ackChecksum = createCheckSum(message[0:3].encode())
        complement = onesComplement(ackChecksum)
        complementStr = str(complement)

        while(len(complementStr) < 10):
        	complementStr = "0" + complementStr

        message = complementStr + message

        rcvSocket.sendto(message.encode(), addr)
        incoming = rcvSocket.recv(4096)