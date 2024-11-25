#include "HttpServletResponse.hpp"

HttpServletResponse::HttpServletResponse(ostringstream& os) : os(os) {};

ostringstream& HttpServletResponse::getOutputStream() {
    return os;
};