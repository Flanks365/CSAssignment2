//
// Created by Mike on 2024-11-10.
//

#include "UploadServlet.hpp"

#include <fstream>
#include <iostream>
#include <dirent.h>

using namespace std;

void UploadServlet::doGet(HttpServletRequest req, HttpServletResponse res) {
    cout << "in doGet" << endl;

    ifstream inFile("Form.html");
    string fileContent((istreambuf_iterator<char>(inFile)), (istreambuf_iterator<char>()));

    ostringstream& os = res.getOutputStream();
    os << "HTTP/1.1 200 OK\r\n" <<
        "Content-Type: text/html\r\n" <<
            "Content-Length: " << fileContent.length() << "\r\n" <<
                "\r\n";

    os << fileContent << "\r\n";
};

void UploadServlet::doPost(HttpServletRequest req, HttpServletResponse res) {
    cout << "in doPost" << endl;

    stringstream filenameStream;
    filenameStream << req.getCaption() << "_" << req.getDate() << "_" << req.getFilename();

    stringstream& is = req.getInputStream();
    string file = is.str();
    string uploadsPath = "./uploads/";
    ofstream outFile{ uploadsPath + filenameStream.str()};
    outFile << file;
    outFile.close();

    stringstream htmlStream;
    htmlStream << "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\">" <<
        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" <<
        "<title>Uploads</title><link rel=\"stylesheet\" href=\"styles.css\"></head>" <<
        "<body><h1>Uploads</h1><ul>";
    DIR *dir;
    struct dirent *ent;
    if ((dir = opendir (uploadsPath.c_str())) != NULL) {
        while ((ent = readdir (dir)) != NULL) {
            htmlStream << "<li>" << ent->d_name << "</li>";
        }
        closedir (dir);
    } else {
        perror ("ERROR: could not find directory");
        return;
    }
    htmlStream << "</ul></body></html>";

    ostringstream& os = res.getOutputStream();
    os << "HTTP/1.1 200 OK\r\n" <<
        "Content-Type: text/html\r\n" <<
            "Content-Length: " << htmlStream.str().length() << "\r\n" <<
                "\r\n";
    os << htmlStream.str() << "\r\n";
}
