#pragma once
#include <sstream>

using namespace std;

class HttpServletRequest {
  stringstream& is;
  string caption;
  string date;
  string filename;
  // char file[];
  // int fileLength;

public:
  HttpServletRequest(stringstream& is);

  stringstream& getInputStream();
  string getCaption();
  string getDate();
  string getFilename();
  // char* getFile();
  // int getFileLength();

  void setCaption(string caption);
  void setDate(string date);
  void setFilename(string filename);
  // void setFile(char* file, int fileLength);
  // void setFileLength(int length);
};
