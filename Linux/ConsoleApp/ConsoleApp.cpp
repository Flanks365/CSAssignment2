#include "Socket.h"
#include <stddef.h>
#include <chrono>
#include <string.h>
#include <string>
#include <iostream>

std::string getCurrentDate() {
    // Get current time as system time
    auto now = std::chrono::system_clock::now();
    
    // Convert to time_t (for formatting)
    std::time_t now_time = std::chrono::system_clock::to_time_t(now);
    
    // Format the time to "YYYY-MM-DD"
    std::tm* now_tm = std::localtime(&now_time);
    
    // Use stringstream to format and return as a string
    std::stringstream ss;
    char buffer[11];
    std::strftime(buffer, sizeof(buffer), "%Y-%m-%d", now_tm);
    ss << buffer;
    
    return ss.str();
}

main() {
    // Connect to a server (replace with your server's address and port)
    Socket client("127.0.0.1", 8082);

    if (!client.connectToServer()) {
        std::cerr << "Failed to connect to server." << std::endl;
        return 1;
    }
    std::cout << "Connected to server." << std::endl;

		std::string caption;
		std::string date = getCurrentDate();

		std::string filePath;
		std::cout << "Enter caption: ";
		std::cin >> caption;
		std::cout << "Enter file path: ";
		std::cin >> filePath;

		client.sendPostRequest(caption, date, filePath);
    return 0;
}
