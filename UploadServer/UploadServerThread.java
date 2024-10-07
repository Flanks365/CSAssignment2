// import java.net.*;
// import java.io.*;
// import java.time.Clock;
// public class UploadServerThread extends Thread {
//    private Socket socket = null;
//    public UploadServerThread(Socket socket) {
//       super("DirServerThread");
//       this.socket = socket;
//    }
//    public void run() {
//       try {
//          InputStream in = socket.getInputStream(); 
//          HttpServletRequest req = new HttpServletRequest(in);  
//          OutputStream baos = new ByteArrayOutputStream(); 
//          HttpServletResponse res = new HttpServletResponse(baos); 
//          HttpServlet httpServlet = new UploadServlet();
//          httpServlet.doPost(req, res);
//          OutputStream out = socket.getOutputStream(); 
//          out.write(((ByteArrayOutputStream) baos).toByteArray());
//          socket.close();
//       } catch (Exception e) { e.printStackTrace(); }
//    }
// }

import java.net.*;
import java.io.*;
import java.util.StringTokenizer;


public class UploadServerThread extends Thread {
    private Socket socket = null;

    public UploadServerThread(Socket socket) {
        super("DirServerThread");
        this.socket = socket;
    }

    public void run() {
      OutputStream out = socket.getOutputStream();
        try {
            // Use BufferedInputStream to read the request
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            
            // Read the first line of the request
            String requestLine = reader.readLine();
            if (requestLine != null) {
                String[] requestParts = requestLine.split(" ");
                String method = requestParts[0];
                String uri = requestParts[1];

                HttpServletRequest req = new HttpServletRequest(in);
                OutputStream baos = new ByteArrayOutputStream();
                HttpServletResponse res = new HttpServletResponse(baos);
                HttpServlet httpServlet = new UploadServlet();

                // Check the HTTP method
                if ("GET".equalsIgnoreCase(method) && "/".equals(uri)) {
                    // Serve the HTML file
                File htmlFile = new File("Form.html");
                FileInputStream fileInputStream = new FileInputStream(htmlFile);
                String content = "HTTP/1.1 200 OK\r\n" +
                                 "Content-Type: text/html\r\n" +
                                 "Content-Length: " + htmlFile.length() + "\r\n" +
                                 "\r\n";
                out.write(content.getBytes());
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
                } else if ("POST".equalsIgnoreCase(method)) {
                    httpServlet.doPost(req, res);
                } else {
                    // Handle unsupported methods
                    res.getOutputStream().write("HTTP/1.1 405 Method Not Allowed\r\n\r\n".getBytes());
                }

                // Send the response back to the client
                OutputStream out = socket.getOutputStream();
                out.write(((ByteArrayOutputStream) baos).toByteArray());
                out.flush();
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
