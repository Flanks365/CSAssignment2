// import java.io.*;
// import java.net.*;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class UploadClient {
    private String caption;
    private String date;
    private File file;

    public UploadClient(String caption, String date, File file) {
        this.caption = caption;
        this.date = date;
        this.file = file;
    }

    public String uploadFile() {
        String boundary = "===" + System.currentTimeMillis() + "==="; // Boundary for multipart
        String lineEnd = "\r\n";
        String response = "";

        try {
            System.out.println("before connection. Caption: " + caption + ", Date: " + date + ", Filename: " + file.getName());
            URL url = new URL("http://localhost:8081/upload/upload"); // Update the URL to point to your servlet
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            System.out.println("after connection");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());

            // Add caption field
            dos.writeBytes("--" + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"caption\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(caption + lineEnd);

            // Add date field
            dos.writeBytes("--" + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"date\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(date + lineEnd);

            // Add file
            dos.writeBytes("--" + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"fileName\"; filename=\"" + file.getName() + "\"" + lineEnd);
            dos.writeBytes("Content-Type: " + Files.probeContentType(file.toPath()) + lineEnd); // Determine content type
            dos.writeBytes(lineEnd);

            // Read file data
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
            fileInputStream.close();
            
            dos.writeBytes(lineEnd);
            dos.writeBytes("--" + boundary + "--" + lineEnd);
            dos.flush();
            dos.close();

            // Read the server response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line).append("\n");
            }
            reader.close();

            response = responseBuilder.toString();

        } catch (Exception e) {
            System.err.println(e);
        }
        System.out.println("UploadClient returning");
        return response;
    }
}
