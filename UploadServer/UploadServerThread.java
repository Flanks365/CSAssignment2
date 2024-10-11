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
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            // Read the first line of the request
            String requestLine = "";
            if (requestLine != null) {
                String method = "";
                while ((requestLine = reader.readLine()) != null) {
                    if (requestLine.contains("GET") || requestLine.contains("get")) {
                        method = "GET";
                        break;
                    } else if (requestLine.contains("POST")|| requestLine.contains("post")) {
                        method = "POST";
                        break;
                    }
                }

                String boundary = "";
                String temp;
                int contentLength = -1;

                // Read headers
                while (!(temp = reader.readLine()).isEmpty()) {
                    if (temp.contains("boundary=")) {
                        boundary = temp.substring(temp.indexOf("boundary=") + 9);
                    } else if (temp.toLowerCase().startsWith("content-length:")) {
                        contentLength = Integer.parseInt(temp.substring(16).trim());
                        System.out.println("Content-Length: " + contentLength);
                    }
                }

                // Check the HTTP method
                if ("POST".equalsIgnoreCase(method)) {
                    // Only read as much as Content-Length specifies
                    byte[] requestBody = new byte[contentLength];
                    int totalBytesRead = 0;
                    int bytesRead;
                    while (totalBytesRead < contentLength && (bytesRead = in.read(requestBody, totalBytesRead,
                            contentLength - totalBytesRead)) != -1) {
                        totalBytesRead += bytesRead;
                        System.out.println("Bytes read: " + bytesRead + ", Total so far: " + totalBytesRead);
                    }

                    if (totalBytesRead < contentLength) {
                        System.out.println("Error: Not enough data received. Expected " + contentLength + " but got " + totalBytesRead);
                        out.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes());
                        socket.close();
                        return;
                    }

                    System.out.println("Body fully received. Total bytes: " + totalBytesRead);

                    // Now wrap it into a new InputStream that we can pass to HttpServletRequest
                    ByteArrayInputStream cachedInputStream = new ByteArrayInputStream(requestBody);

                    HttpServletRequest req = new HttpServletRequest(cachedInputStream);
                    req.setBoundary(boundary);
                    OutputStream baos = new ByteArrayOutputStream();
                    HttpServletResponse res = new HttpServletResponse(baos);
                    HttpServlet httpServlet = new UploadServlet();

                    // Call the doPost method of the servlet
                    httpServlet.doPost(req, res);

                    // Send the response back to the client
                    out.write(((ByteArrayOutputStream) baos).toByteArray());
                } else if ("GET".equalsIgnoreCase(method)) {
                    System.out.println("GET request received");
                    // Serve the HTML file for GET request
                    int bytesRead;
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
