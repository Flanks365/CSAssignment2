#include "Socket.h"

Socket::Socket(const std::string& serverAddress, int port) {
    sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        std::cerr << "Error creating socket" << std::endl;
    }

    server.sin_family = AF_INET;
    server.sin_port = htons(port);
    inet_pton(AF_INET, serverAddress.c_str(), &server.sin_addr);
}

Socket::~Socket() {
    close(sock);
}

bool Socket::connectToServer() {
    return connect(sock, (struct sockaddr*)&server, sizeof(server)) >= 0;
}

void Socket::sendRequest(const std::string& request) {
    send(sock, request.c_str(), request.length(), 0);
}

std::string Socket::receiveResponse() {
    char buffer[4096];
    int bytesRead = recv(sock, buffer, sizeof(buffer) - 1, 0);
    if (bytesRead > 0) {
        buffer[bytesRead] = '\0'; // Null-terminate the buffer
        return std::string(buffer);
    }
    return "";
}

void Socket::sendPostRequest(const std::string& caption, const std::string& date, const std::string& filePath) {
    std::string boundary = "----Boundary123456";

    // Construct the headers
    std::ostringstream request;
    request << "POST / HTTP/1.1\r\n";
    request << "Host: 127.0.0.1\r\n";
    request << "Connection: close\r\n";
    request << "Content-Type: multipart/form-data; boundary=" << boundary << "\r\n";

    // Construct the body
    std::ostringstream body;

    // Caption field
    body << "--" << boundary << "\r\n";
    body << "Content-Disposition: form-data; name=\"caption\"\r\n\r\n";
    body << caption << "\r\n";

    // Date field
    body << "--" << boundary << "\r\n";
    body << "Content-Disposition: form-data; name=\"date\"\r\n\r\n";
    body << date << "\r\n";

    // File field (only if the file exists)
    std::ifstream file(filePath, std::ios::binary);
    if (file) {
        body << "--" << boundary << "\r\n";
        body << "Content-Disposition: form-data; name=\"fileName\"; filename=\"" << filePath << "\"\r\n";
        body << "Content-Type: application/octet-stream\r\n\r\n";
        body << file.rdbuf() << "\r\n";  // Insert file content
        file.close();
    } else {
        std::cerr << "File " << filePath << " not found, skipping file upload." << std::endl;
    }

    // Closing boundary
    body << "--" << boundary << "--\r\n";

    // Combine headers and body
    request << "Content-Length: " << body.str().length() << "\r\n\r\n";
    request << body.str();

    // Send the request
    sendRequest(request.str());

    // Receive and print the response
    std::string response = receiveResponse();
    std::cout << "Response from server:\n" << response << std::endl;
}