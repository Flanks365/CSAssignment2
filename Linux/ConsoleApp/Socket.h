#pragma once

#include <string>
#include <iostream>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <fstream>
#include <sstream>

class Socket
{
public:
    Socket(const std::string& serverAddress, int port);
    ~Socket();
    bool connectToServer();
    void sendRequest(const std::string& request);
    std::string receiveResponse();
		void sendPostRequest(const std::string& caption, const std::string& date, const std::string& filePath);
private:
	int sock;
	struct sockaddr_in server;

};

