#include "HttpServletRequest.hpp"

HttpServletRequest::HttpServletRequest(stringstream& is) : is(is) {};

stringstream& HttpServletRequest::getInputStream() {
    return is;
};

string HttpServletRequest::getCaption() {
    return caption;
}

string HttpServletRequest::getDate() {
    return date;
}

string HttpServletRequest::getFilename() {
    return filename;
}

// char * HttpServletRequest::getFile() {
//     return file;
// }
//
// int HttpServletRequest::getFileLength() {
//     return fileLength;
// }

void HttpServletRequest::setCaption(string caption) {
    this->caption = caption;
}

void HttpServletRequest::setDate(string date) {
    this->date = date;
}

void HttpServletRequest::setFilename(string filename) {
    this->filename = filename;
}

// void HttpServletRequest::setFile(char *file, int fileLength) {
//     this->file = new char[fileLength];
// }
//
// void HttpServletRequest::setFileLength(int length) {
// }


