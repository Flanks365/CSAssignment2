#include "UploadServerThread.hpp"
#include <sys/socket.h>
#include <iostream>
#include <sstream>

#include "HttpServletRequest.hpp"
#include "HttpServletResponse.hpp"
#include "UploadServlet.hpp"

using namespace std;

UploadServerThread::UploadServerThread(Socket socket) : Thread(this), socket(socket) {};

void UploadServerThread::run() {
    cout << "in UploadServerThread" << endl;

    // read request into string stream
    stringstream is(socket.getRequest());
    // stringstream is;
    // socket.getRequest(is);
    // is.seekg(0);
    ostringstream os;

    // Create HTTP stuff
    // HttpServletRequest req(is);
    HttpServletResponse res(os);
    // UploadServlet httpServlet;

    // while (more to read...???)
    cout << "is.str(): " << is.str() << endl;
    string method;
    is >> method;
    // is.seekg(0);
    cout << "Method: " << method << endl;
    // cout << "Method is GET: " << (method == "GET") << endl;

    // use that to determine where to send next
    if (method == "GET") {
        HttpServletRequest req(is);
        // HttpServletResponse res(os);
        UploadServlet httpServlet;
        httpServlet.doGet(req, res);
        // string test = res.getOutputStream().str();
        // cout << test << endl;
    } else if (method == "POST") {
        HttpServletRequest req(is);
        parseRequest(is, req);
        // HttpServletResponse res(os);
        UploadServlet httpServlet;
        httpServlet.doPost(req, res);
        // string test = res.getOutputStream().str();
        // cout << test << endl;
    } else {
        string output = "Method not allowed";
        socket.sendResponse(&output[0]);
        shutdown(socket.getSock(), SHUT_RDWR);
        return;
    }

    ostringstream& resStream = res.getOutputStream();
    string output = resStream.str();
    // resStream.str("");

    // char* t2 = &output[0];
    socket.sendResponse(&output[0]);

    shutdown(socket.getSock(), SHUT_RDWR);
};

std::stringstream &UploadServerThread::parseRequest(stringstream& is, HttpServletRequest& req) {
    string line;
    string contentType;
    string boundary;
    int contentLength;

    while ((line = socket.getRequest()) != "\r\n") {
        cout << "line: " << line;
        istringstream lineStream(line);
        string word;
        lineStream >> word;
        if (word == "Content-Type:") {
            cout << "HIT CONTENT TYPE" << endl;
            lineStream >> contentType;
            lineStream >> boundary;
        } else if (word == "Content-Length:") {
            cout << "HIT CONTENT LENGTH" << endl;
            lineStream >> contentLength;
        }
    }

    cout << "BODY:" << endl;

    bool inFileContent = false;
    string caption;
    string date;
    string filename;
    while (!(line = socket.getRequest()).empty() && !inFileContent) {
        cout << line;
        contentLength -= line.length();
        istringstream lineStream(line);
        string word;
        lineStream >> word;
        if (word == "Content-Disposition:") {
            cout << "HIT CONTENT DISPOSITION" << endl;
            lineStream >> word;
            if (word == "form-data;") {
                lineStream >> word;
                auto pos = word.find('=');
                string field = word.substr(pos + 2, word.length() - pos - 3);
                cout << "field: " << field << endl;

                if (field == "caption") {
                    line = socket.getRequest();
                    contentLength -= line.length();
                    line = socket.getRequest();
                    contentLength -= line.length();
                    caption = line.substr(0, line.length() - 2);
                } else if (field == "date") {
                    line = socket.getRequest();
                    contentLength -= line.length();
                    line = socket.getRequest();
                    contentLength -= line.length();
                    date = line.substr(0, line.length() - 2);
                } else if (field == "fileName\"") {
                    cout << "HIT FILENAME" << endl;
                    lineStream >> word;
                    pos = word.find('=');
                    filename = word.substr(pos + 2, word.length() - pos - 3);
                    cout << "filename: " << filename << endl;
                    inFileContent = true;
                }
            }
        }
    }

    cout << "contentLength: " << contentLength << endl;
    cout << "line: " << line;

    contentLength -= line.length();

    cout << "contentLength: " << contentLength << endl;

    line = socket.getRequest();
    contentLength -= line.length();

    cout << "contentLength: " << contentLength << endl;
    cout << "line: " << line;

    string boundaryLabel = "boundary=";
    cout << "boundary length: " << boundary.length() - boundaryLabel.length();

    // string boundaryLabel = "boundary=";
    contentLength -= 2;
    contentLength -= (boundary.length() - boundaryLabel.length() + 4 + 2);

    cout << "contentLength: " << contentLength << endl;

    char *file = new char[contentLength + 1];

    int fileBytes = socket.getReqFile(file, contentLength);

    cout << "!" << endl;

    if (fileBytes < contentLength) {
        cout << "wrong num bytes" << endl;
    }

    file[contentLength] = '\0';

    is.str("");
    // is << "caption: " << caption << "\n";
    // is << "date: " << date << "\n";
    // is << "file:\n";

    // is << file;

    for (int i = 0; i < contentLength; i++) {
        cout << file[i];
        is.write(file + i, 1);
    }

    cout << "contentType: " << contentType <<
        "\nboundary: " << boundary <<
            "\nlength: " << contentLength <<
                "\ncaption: " << caption <<
                    "\ncaption length: " << caption.length() <<
                    "\ndate: " << date <<
                        "\ndate length: " << date.length() <<
                        "\nfilename: " << filename <<
                            "." << endl;

    cout << "sample filename: " << caption << "_" << date << "_" << filename << endl;

    req.setCaption(caption);
    req.setDate(date);
    req.setFilename(filename);


    return is;
}
