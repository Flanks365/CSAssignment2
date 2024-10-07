
// public class UploadClient {
//     public UploadClient() { }
//     public String uploadFile() {
//         String listing = "";
//         try {
//             Socket socket = new Socket("localhost", 8999);
//             BufferedReader in = new BufferedReader(
//                 new InputStreamReader(socket.getInputStream()));
//             OutputStream out = socket.getOutputStream();
//             FileInputStream fis = new FileInputStream("AndroidLogo.png");
//             byte[] bytes = fis.readAllBytes();
//             out.write(bytes);
//             socket.shutdownOutput();
//             fis.close();
//             System.out.println("Came this far\n");
//             String filename = "";
//             while ((filename = in.readLine()) != null) {
//                 listing += filename;
//             }
//             socket.shutdownInput();
//         } catch (Exception e) {
//             System.err.println(e);
//         }
//         return listing;
//     }
// }

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
    public UploadClient() {
        try {
            System.out.println("Enter your image caption: ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            caption = br.readLine();
            date = Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE);
            System.out.println("Enter the path of the file you want to upload: ");
            String imagePath = br.readLine();
            file = new File(imagePath);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public String uploadFile() {
        String boundary = "===" + System.currentTimeMillis() + "==="; // Boundary for multipart
        String lineEnd = "\r\n";
        String response = "";

        try {
            String hostname = "localhost:8081";
            String path = "/upload/upload";
            Socket socket = new Socket("localhost", 8081);
            OutputStream out = socket.getOutputStream();

            // Form fields
            String formFieldPart = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"caption\"\r\n\r\n" + caption +"\r\n" +
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"date\"\r\n\r\n" +
                date +"\r\n";

            // Start of the file part
            String fileHeaderPart = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"File\"; filename=\"" + file.getName() + "\"\r\n" +
                "Content-Type: " + Files.probeContentType(file.toPath()) + "\r\n\r\n";

            // End of the multipart form data
            String closingBoundary = "\r\n--" + boundary + "--\r\n";

            // Calculate Content-Length
            long contentLength = formFieldPart.length() + fileHeaderPart.length() + file.length() + closingBoundary.length();
            
            BufferedWriter dos = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

            // Write the POST request headers
            dos.write("POST " + path + " HTTP/1.1\r\n");
            dos.write("Host: " + hostname + "\r\n");
            dos.write("Content-Type: multipart/form-data; boundary=" + boundary + "\r\n");
            dos.write("Content-Length: " + contentLength + "\r\n");
            dos.write("Connection: close\r\n");
            dos.write("\r\n");  // End of headers
            dos.flush();

            // Add caption field
            dos.write("--" + boundary + lineEnd);
            dos.write("Content-Disposition: form-data; name=\"caption\"" + lineEnd);
            dos.write(lineEnd);
            dos.write(caption + lineEnd);

            // Add date field
            dos.write("--" + boundary + lineEnd);
            dos.write("Content-Disposition: form-data; name=\"date\"" + lineEnd);
            dos.write(lineEnd);
            dos.write(date + lineEnd);

            // Add file
            dos.write("--" + boundary + lineEnd);
            dos.write("Content-Disposition: form-data; name=\"fileName\"; filename=\"" + file.getName() + "\"" + lineEnd);
            dos.write("Content-Type: " + Files.probeContentType(file.toPath()) + lineEnd); // Determine content type
            dos.write(lineEnd);

            // Read file data
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            fileInputStream.close();
            
            dos.write(lineEnd);
            dos.write("--" + boundary + "--" + lineEnd);
            dos.flush();
            dos.close();

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
