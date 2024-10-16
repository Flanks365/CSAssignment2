import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class UploadClient {
    private String caption;
    private String date;
    private File file;

    public UploadClient() throws ReadInfoFailException, IOException{
        try {
            System.out.println("Enter your image caption: ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            caption = br.readLine();
            if (caption.equals("")) {
                throw new ReadInfoFailException("Caption is empty");
            }
            date = Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE);
            System.out.println("Enter the path of the file you want to upload: ");
            String imagePath = br.readLine();
            file = new File(imagePath);
        } catch (ReadInfoFailException e) {
            System.err.println("ReadInfoFailException: " + e);
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        } catch (Exception e) {
            System.err.println("Exception: " + e);
        }
    }

    public String uploadFile() {
        String boundary = "===" + System.currentTimeMillis() + "==="; // Boundary for multipart
        String lineEnd = "\r\n";
        String response = "";

        try {
            String host = "localhost";
            int port = 8999;
            String path = "/upload/upload";
            Socket socket = new Socket(host, port);
            OutputStream out = socket.getOutputStream();

            // Start of the multipart body
            StringBuilder formFieldPart = new StringBuilder();
            formFieldPart.append("--").append(boundary).append(lineEnd)
                    .append("Content-Disposition: form-data; name=\"caption\"").append(lineEnd)
                    .append(lineEnd)
                    .append(caption).append(lineEnd)
                    .append("--").append(boundary).append(lineEnd)
                    .append("Content-Disposition: form-data; name=\"date\"").append(lineEnd)
                    .append(lineEnd)
                    .append(date).append(lineEnd);

            // File header
            StringBuilder fileHeaderPart = new StringBuilder();
            fileHeaderPart.append("--").append(boundary).append(lineEnd)
                    .append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                    .append(file.getName()).append("\"").append(lineEnd)
                    .append("Content-Type: ").append(Files.probeContentType(file.toPath())).append(lineEnd)
                    .append(lineEnd);

            // Closing boundary
            String closingBoundary = "--" + boundary + "--" + lineEnd;

            // Calculate Content-Length (form data + file + closing boundary)
            long contentLength = formFieldPart.length() + fileHeaderPart.length() + file.length()
                    + closingBoundary.length();

            BufferedWriter dos = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

            // Write the POST request headers
            dos.write("POST " + path + " HTTP/1.1\r\n");
            dos.write("Host: " + host + ":" + port + "\r\n");
            dos.write("Content-Type: multipart/form-data; boundary=" + boundary + "\r\n");
            dos.write("Content-Length: " + contentLength + "\r\n");
            dos.write("Connection: close\r\n");
            dos.write("\r\n"); // End of headers
            dos.flush();

            // Write form fields (caption, date)
            dos.write(formFieldPart.toString());

            // Write file header
            dos.write(fileHeaderPart.toString());
            dos.flush();

            // Write file content
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            fileInputStream.close();

            // Write closing boundary
            dos.write(lineEnd);
            dos.write(closingBoundary);
            dos.flush();
            // dos.close();

            // Read the server response
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socket.shutdownOutput();
            String htmlLineString = "";
            while ((htmlLineString = in.readLine()) != null) {
                response += htmlLineString + "\n";
            }

            socket.shutdownInput();
            socket.close();
        } catch (Exception e) {
            System.err.println(e);
        }
        return response;
    }
}
