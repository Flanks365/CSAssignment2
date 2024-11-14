#pragma once
#include <iosfwd>

class Socket
{
public:
	Socket(int sock);
	char* getRequest();
	int getReqFile(char* buf, int n);
	int getRequest(std::stringstream& is);
	void sendResponse(char* res);
	int getSock();
	~Socket();
private:
	int sock;
	int readn(int fd, void* buf, int n);
};

