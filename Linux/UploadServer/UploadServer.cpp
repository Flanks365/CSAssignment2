#include "Socket.h"
#include "ServerSocket.h"
#include <stddef.h>
#include <iostream>

#include "UploadServerThread.hpp"

using namespace std;

int main() {
	ServerSocket *ss; // = new ServerSocket(8888);
	try {
		ss = new ServerSocket(8888);
		cout << "Made socket" << endl;
	} catch (...) {
		cout << "ERROR" << endl;
		exit(EXIT_FAILURE);
	}

	while (true) {
		if (ss != NULL) {
			Socket *cs = ss->Accept();
			UploadServerThread uploadThread(cs);
			uploadThread.start();
		}
	}
}
