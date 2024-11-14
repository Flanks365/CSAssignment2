//
// Created by Mike on 2024-11-10.
//

#include "UploadServlet.hpp"

#include <fstream>
#include <iostream>

using namespace std;

void UploadServlet::doGet(HttpServletRequest req, HttpServletResponse res) {
    cout << "in doGet" << endl;

    ifstream inFile("Form.html");

    string fileContent((istreambuf_iterator<char>(inFile)), (istreambuf_iterator<char>()));
    cout << "inFile length: " << fileContent.length() << endl;

    ostringstream& os = res.getOutputStream();
    os << "HTTP/1.1 200 OK\r\n" <<
        "Content-Type: text/html\r\n" <<
            "Content-Length: " << fileContent.length() << "\r\n" <<
                "\r\n";

    os << fileContent << endl;
};

void UploadServlet::doPost(HttpServletRequest req, HttpServletResponse res) {
    cout << "in doPost" << endl;

    stringstream& is = req.getInputStream();

    string file = is.str();
    cout << "file length: " << file.length() << endl;

    stringstream test;
    test << req.getCaption() << req.getDate() << req.getFilename();
    string t = test.str();

    ofstream outFile{"./" + req.getFilename()};
    outFile << file;
    outFile.close();

    ostringstream& os = res.getOutputStream();
    // os << is.str();
    os << "HTTP/1.1 200 OK\r\n" <<
        "Content-Type: text/html\r\n" <<
            "Content-Length: " << t.length() << "\r\n" <<
                "\r\n";
    os << t << endl;
    return;

    string line;
    string contentType;
    string boundary;
    int contentLength;

    while (getline(is, line) && !line.empty() && line != "\r") {
        // cout << "line: " << line << endl;
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

    bool inFileContent = false;
    string caption;
    string date;
    while (getline(is, line) && !line.empty() && !inFileContent) {
        cout << "line: " << line << endl;
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
                getline(is, line);
                getline(is, line);
                if (field == "caption") {
                    caption = line.substr(0, line.length() - 1);
                } else if (field == "date") {
                    date = line.substr(0, line.length() - 1);
                } else if (field == "fileName\"") {
                    cout << "HIT FILENAME" << endl;
                    inFileContent = true;
                }
            }
        }
    }



    // string test = is.str();
    // cout << "file contents: " << endl;
    // for (char &c : test) {
    //     cout << "char: " << c << endl;
    // }



    // ostringstream& os = res.getOutputStream();
    // os << "contentType: " << contentType <<
    //     ", boundary: " << boundary <<
    //         ", length: " << contentLength <<
    //             ", caption: " << caption <<
    //                 ", date: " << date <<
    //                     "." << endl;
    //
    // os << "file contents: " << endl;



}
