cmake_minimum_required(VERSION 3.29)
project(UploadServer)

set(CMAKE_CXX_STANDARD 14)

include_directories(.)

add_executable(UploadServer
    ServerSocket.cpp
    ServerSocket.h
    SimpleThread.cpp
    SimpleThread.h
    Socket.cpp
    Socket.h
    Thread.cpp
    Thread.h
    UploadServer.cpp
    UploadServerThread.cpp
    UploadServerThread.hpp
        HttpServletRequest.hpp
        HttpServletRequest.cpp
        HttpServletResponse.cpp
        HttpServletResponse.hpp
        HttpServlet.cpp
        HttpServlet.hpp
        UploadServlet.cpp
        UploadServlet.hpp)
