import java.net.*;
import java.io.*;
import java.util.StringTokenizer;

public class UploadServerThread extends Thread {
    private Socket socket = null;

    public UploadServerThread(Socket socket) {
        super("UploadServerThread");
        this.socket = socket;
    }

    public void run() {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // Read the HTTP request
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String initialLine = reader.readLine();

            if (initialLine == null) return;

            StringTokenizer tokenizer = new StringTokenizer(initialLine);
            String method = tokenizer.nextToken(); // GET or POST
            String path = tokenizer.nextToken();

            // Handle GET request for the HTML form
            if (method.equals("GET")) {
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
            }
            // Handle POST request for file upload
            else if (method.equals("POST")) {
                // Read the request body into a ByteArrayOutputStream
                ByteArrayOutputStream requestBody = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    requestBody.write(buffer, 0, bytesRead);
                }

                // Create HttpServletRequest and HttpServletResponse objects
                HttpServletRequest req = new HttpServletRequest(new ByteArrayInputStream(requestBody.toByteArray()));
                HttpServletResponse res = new HttpServletResponse(out);

                // Create the servlet instance
                UploadServlet servlet = new UploadServlet();

                // Call the servlet's doPost method
                servlet.doPost(req, res);
            }

            out.flush();
        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Could not close socket: " + e.getMessage());
            }
        }
    }
}
