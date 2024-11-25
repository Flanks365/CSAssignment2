#include "UploadServerThread.hpp"
#include <sys/socket.h>
#include <iostream>
#include <sstream>

#include "HttpServletRequest.hpp"
#include "HttpServletResponse.hpp"
#include "UploadServlet.hpp"

using namespace std;

UploadServerThread::UploadServerThread(Socket* socket) : Thread(this), socket(socket) {};

void UploadServerThread::run() {
    cout << "in UploadServerThread" << endl;

    // read request into string stream
    stringstream is(socket->getRequestLine());
    ostringstream os;

    HttpServletRequest req(is);
    HttpServletResponse res(os);

    string method;
    is >> method;
    if (method == "GET") {
        // HttpServletRequest req(is);
        UploadServlet httpServlet;
        httpServlet.doGet(req, res);
    } else if (method == "POST") {
        // HttpServletRequest req(is);
        parseRequest(is, req);
        UploadServlet httpServlet;
        httpServlet.doPost(req, res);
    } else {
        string output = "Method not allowed";
        socket->sendResponse(&output[0]);
        cout << "Shutting down socket..." << endl;
        socket->closeSocket();
        delete socket;
        return;
    }

    ostringstream& resStream = res.getOutputStream();
    string output = resStream.str();
    socket->sendResponse(&output[0]);

    cout << "Shutting down socket..." << endl;
    shutdown(socket->getSock(), SHUT_WR);
    socket->closeSocket();
    delete socket;
};

std::stringstream &UploadServerThread::parseRequest(stringstream& is, HttpServletRequest& req) {
    string line;
    string contentType;
    string boundary;
    int contentLength;

    // Get data from request headers
    while ((line = socket->getRequestLine()) != "\r\n") {
        istringstream lineStream(line);
        string word;
        lineStream >> word;
        if (word == "Content-Type:") {
            lineStream >> contentType;
            lineStream >> boundary;
        } else if (word == "Content-Length:") {
            lineStream >> contentLength;
        }
    }

    // Get form data
    bool inFileContent = false;
    string caption;
    string date;
    string filename;
    while (!(line = socket->getRequestLine()).empty() && !inFileContent) {
        contentLength -= line.length();
        istringstream lineStream(line);
        string word;
        lineStream >> word;
        if (word == "Content-Disposition:") {
            lineStream >> word;
            if (word == "form-data;") {
                lineStream >> word;
                auto pos = word.find('=');
                string field = word.substr(pos + 2, word.length() - pos - 3);

                if (field == "caption") {
                    line = socket->getRequestLine();
                    contentLength -= line.length();
                    line = socket->getRequestLine();
                    contentLength -= line.length();
                    caption = line.substr(0, line.length() - 2);
                } else if (field == "date") {
                    line = socket->getRequestLine();
                    contentLength -= line.length();
                    line = socket->getRequestLine();
                    contentLength -= line.length();
                    date = line.substr(0, line.length() - 2);
                } else if (field == "fileName\"" || field == "file\"") {
                    lineStream >> word;
                    pos = word.find('=');
                    filename = word.substr(pos + 2, word.length() - pos - 3);
                    auto pos = filename.find('.');
                    string fileExtension = filename.substr(pos + 1, word.length() - pos - 1);

                    // Correction for bug where text files from java console are missing last two chars
                    if (field == "file\"" && fileExtension == "txt") {
                        contentLength += 2;
                    }
                    inFileContent = true;
                }
            }
        }
    }

    // Exclude current line (from final while check) and empty line before file content from file length
    contentLength -= line.length();
    line = socket->getRequestLine();
    contentLength -= line.length();

    // Exclude final boundary + four extra "-" + two line delimiting chars
    string boundaryLabel = "boundary=";
    contentLength -= (boundary.length() - boundaryLabel.length() + 4 + 2);

    // Exclude two line delimiting chars
    contentLength -= 2;

    // Read only as many bytes from socket as contained in file content
    char *file = new char[contentLength + 1];
    int fileBytes = socket->getReqFile(file, contentLength);
    if (fileBytes < contentLength) {
        cout << "ERROR: incorrect file content length" << endl;
    }
    file[contentLength] = '\0';

    // Write file content into stream
    is.str("");
    for (int i = 0; i < contentLength; i++) {
        is.write(file + i, 1);
    }

    req.setCaption(caption);
    req.setDate(date);
    req.setFilename(filename);

    return is;
}
