#include "Socket.h"

#include <iostream>
// #include <sys/socket.h>
#include <sys/types.h>
// #include <resolv.h>
#include <sstream>
#include <unistd.h>
#include <string.h>
#include <stdio.h>

using namespace std;

Socket::Socket(int sock)
{
	this->sock = sock;
}

char* Socket::getRequest()
{
 //  cout << "In getRequest()" << endl;
 //  int rval;
 //  char *buf = new char[2048];
 //
 //  cout << "reading.." << endl;
 //  if ((rval = read(sock, buf, 2048)) < 0){
 //    perror("reading socket");
 //  }else  {
 //    cout << "printing..." << endl;
 //    printf("%s\n",buf);
 //  }
 //
 //  cout << "end of getRequest()" << endl;
	// return buf;


   // cout << "In getRequest()" << endl;
   int rval;
   char *buf = new char[1024];
  char *pos = buf;
   int count = 0;

  char currChar;
  char lastChar;
  while ((rval = read(sock, pos, 1))) {
    // currChar = *pos;
    // cout << "read, count: " << count << ", rval: " << rval << ", char: " << (buf == pos) << endl;
    if (rval < 1) {
      // cout << "hit end" << endl;
      break;
    }
    currChar = *pos;
    if (lastChar == '\r' && currChar == '\n') {
      // cout << "hit break" << endl;
      count++;
      break;
    }

    lastChar = currChar;
    count++;
    pos++;
  }

  // cout << "count: " << count << "buf: " << *buf << endl;
  char *outBuf = new char[count + 1];
  for (int i = 0; i < count; i++) {
    // cout << "printing: " << buf[i] << endl; //buf[i];
    outBuf[i] = buf[i];
    // cout << outBuf[i];
  }

  outBuf[count] = '\0';

  // pos = buf;
  // while (pos - buf < count) {
  //   cout << "printing: " << *pos << endl;
  //   pos++;
  // }


  // cout << outBuf << endl;

   // cout << "end of getRequest()" << endl;
  // delete buf;
  return outBuf;
}

int Socket::getReqFile(char *buf, int n) {
  cout << "in getReqFile" << endl;
  return readn(sock, buf, n);
}


int Socket::getRequest(stringstream& is)
{
  cout << "In getRequest(is)" << endl;
  int rval;
  char *buf = new char[1024];

  // cout << "reading.." << endl;
  // if ((rval = read(sock, buf, 1024)) < 0){
  //   perror("reading socket");
  // }else  {
  //   cout << "printing..." << endl;
  //   printf("%s\n",buf);
  // }

  cout << "going to read" << endl;
  while ((rval = read(sock, buf, 100))) {
    if (rval != 100)
      break;
    // cout << "reading..." << endl;
    // is.write(buf, rval);
    // cout << "read" << endl;
  }

  cout << "left loop" << endl;

  if (rval  < 0) {
    perror("reading socket");
  }

  delete buf;

  cout << "end of getRequest()" << endl;
  return 0;
}

int Socket::readn(int fd, void* buf, int n) {
  cout << "in readn" << endl;
  int nread;
  char *p = (char *)buf;
  char *q = (char *)buf + n;

  cout << "p: " << *p << ", q: " << *q << endl;
  while (p < q) {
    if ((nread = read(fd, p, q - p)) < 0) {
      if (errno == EINTR)
        continue;
      else
        return -1;
    } else if (nread == 0)
      break;

    p += nread;
  }
  cout << "leaving readn" << endl;
  return p - (char *) buf;
}

void Socket::sendResponse(char *res){
  cout << "In sendResponse()" << endl;
int rval;

  if ((rval = write(sock, res, strlen(res))) < 0){
    perror("writing socket");
  }else  {
    printf("%s\n",res);
  }

  cout << "leaving sendResponse()" << endl;

	return;
}

// void Socket::close() {
//   cout << "closing..." << endl;
//   close();
// }

int Socket::getSock() {
  return sock;
}

Socket::~Socket()
{
}
