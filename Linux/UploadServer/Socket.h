#pragma once
#include <iosfwd>

class Socket
{
public:
	Socket(int sock);
	char* getRequest();
	char* getRequestLine();
	int getReqFile(char* buf, int n);
	void sendResponse(char* res);
	int getSock();
	~Socket();
private:
	int sock;
	int readn(int fd, void* buf, int n);
};

