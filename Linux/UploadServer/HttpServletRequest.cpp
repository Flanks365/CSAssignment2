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

void HttpServletRequest::setCaption(string caption) {
    this->caption = caption;
}

void HttpServletRequest::setDate(string date) {
    this->date = date;
}

void HttpServletRequest::setFilename(string filename) {
    this->filename = filename;
}


