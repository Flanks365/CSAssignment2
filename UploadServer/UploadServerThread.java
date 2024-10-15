import java.net.*;
import java.io.*;

public class UploadServerThread extends Thread {
    private Socket socket = null;

    public UploadServerThread(Socket socket) {
        super("DirServerThread");
        this.socket = socket;
    }

    public void run() {
        try {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            int bytesRead;
            // Read the first line of the request (GET / POST)
            String requestLine = reader.readLine();
            if (requestLine != null) {
                String method = "";
                if (requestLine.contains("GET")) {
                    method = "GET";
                    System.out.println("Method: " + method);
                } else if (requestLine.contains("POST")) {
                    method = "POST";
                    System.out.println("Method: " + method);
                }

                // Initialize variables for headers
                String boundary = "";
                String temp;
                int contentLength = -1;

                // Read headers
                while (!(temp = reader.readLine()).isEmpty()) {
                    if (temp.contains("boundary=")) {
                        boundary = temp.substring(temp.indexOf("boundary=") + 9);
                        boundary = boundary.trim();
                        System.out.println("Boundary: " + boundary);
                    } else if (temp.toLowerCase().startsWith("content-length:")) {
                        contentLength = Integer.parseInt(temp.substring(16).trim());
                        System.out.println("Content-Length: " + contentLength);
                    }
                }

                // Cache the entire input stream into a byte array if POST request
                if ("POST".equalsIgnoreCase(method)) {
                    // Only read as much as Content-Length specifies
                    byte[] requestBody = new byte[contentLength];
                    int totalBytesRead = 0;
                    while (totalBytesRead < contentLength && (bytesRead = in.read(requestBody, totalBytesRead, contentLength - totalBytesRead)) != -1) {
                        System.out.println("Bytes read: " + bytesRead);
                        totalBytesRead += bytesRead;
                    }
                    // Now wrap it into a new InputStream that we can pass to HttpServletRequest
                    ByteArrayInputStream cachedInputStream = new ByteArrayInputStream(requestBody);

                    HttpServletRequest req = new HttpServletRequest(cachedInputStream);
                    req.setBoundary(boundary);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    HttpServletResponse res = new HttpServletResponse(baos);
                    HttpServlet httpServlet = new UploadServlet();

                    // Call the doPost method of the servlet
                    httpServlet.doPost(req, res);

                    // Send the response back to the client
                    out.write(baos.toByteArray());

                } else if ("GET".equalsIgnoreCase(method)) {
                    // Serve the HTML file for GET request
                    File htmlFile = new File("Form.html");
                    FileInputStream fileInputStream = new FileInputStream(htmlFile);
                    String content = "HTTP/1.1 200 OK\r\n" +
                                     "Content-Type: text/html\r\n" +
                                     "Content-Length: " + htmlFile.length() + "\r\n" +
                                     "\r\n";
                    out.write(content.getBytes());
                    byte[] buffer = new byte[4096];
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    fileInputStream.close();
                }

                // Flush and close the socket
                out.flush();
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
