import java.io.*;

public class UploadServlet extends HttpServlet {
   protected void doPost(HttpServletRequest request, HttpServletResponse response) {
      try {
         // Get boundary string
         String boundaryString = request.getBoundary();
         System.out.println("Boundary: " + boundaryString);

         // Convert boundary to byte array for comparison
         byte[] boundaryBytes = ("--" + boundaryString).getBytes("UTF-8");

         // InputStream for reading the raw data
         InputStream in = request.getInputStream();
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         byte[] data = new byte[4096];
         int bytesRead;

         // Read the entire request body into a buffer
         while ((bytesRead = in.read(data)) != -1) {
            buffer.write(data, 0, bytesRead);
         }

         byte[] requestBody = buffer.toByteArray();
         int boundaryPosition = findBoundaryPosition(requestBody, boundaryBytes, 0);
         if (boundaryPosition == -1) {
            throw new Exception("Boundary not found in the request body");
         }

         // Parse the multipart content
         int position = boundaryPosition + boundaryBytes.length + 2; // Skip past boundary

         String caption = "";
         String date = "";
         String filename = "";

         System.out.println("position: " + position);
         System.out.println("requestBody.length: " + requestBody.length);
         while (position < requestBody.length) {
            // Read the headers of the next part
            int partHeaderEnd = findBoundaryPosition(requestBody, "\r\n\r\n".getBytes("UTF-8"), position);
            if (partHeaderEnd == -1)
               break;

            String partHeader = new String(requestBody, position, partHeaderEnd - position, "UTF-8");
            position = partHeaderEnd + 4; // Skip past header

            // Check if it's the caption or date field
            if (partHeader.contains("name=\"caption\"")) {
               int partEnd = findBoundaryPosition(requestBody, boundaryBytes, position);
               caption = new String(requestBody, position, partEnd - position - 2, "UTF-8"); // -2 to skip trailing \r\n
               System.out.println("Caption: " + caption);
               position = partEnd + boundaryBytes.length + 2;
            } else if (partHeader.contains("name=\"date\"")) {
               int partEnd = findBoundaryPosition(requestBody, boundaryBytes, position);
               date = new String(requestBody, position, partEnd - position - 2, "UTF-8");
               position = partEnd + boundaryBytes.length + 2;
               System.out.println("Date: " + date);
            } else if (partHeader.contains("filename=")) {
               filename = extractFilename(partHeader);

               // Read the file content
               int fileDataEnd = findBoundaryPosition(requestBody, boundaryBytes, position);
               byte[] fileData = new byte[fileDataEnd - position - 2]; // -2 to remove trailing \r\n
               System.arraycopy(requestBody, position, fileData, 0, fileData.length);
               position = fileDataEnd + boundaryBytes.length + 2;
               System.out.println("File received: " + filename);

               // Save the file to disk
               String saveFileName = filename + "_" + caption + "_" + date + ".jpeg";
               try (FileOutputStream fos = new FileOutputStream(saveFileName)) {
                  fos.write(fileData);
                  System.out.println("File saved as: " + saveFileName);
               }
            }
         }
      } catch (Exception ex) {
         System.err.println(ex);
      }
   }

   // Helper method to find the position of a boundary or substring in a byte array
   private int findBoundaryPosition(byte[] data, byte[] boundary, int start) {
      for (int i = start; i <= data.length - boundary.length; i++) {
         boolean found = true;
         for (int j = 0; j < boundary.length; j++) {
            if (data[i + j] != boundary[j]) {
               found = false;
               break;
            }
         }
         if (found) {
            return i;
         }
      }
      return -1;
   }

   // Helper method to extract the filename from the Content-Disposition header
   private String extractFilename(String contentDisposition) {
      String filenameKey = "filename=";
      int startIndex = contentDisposition.indexOf(filenameKey) + filenameKey.length();
      int endIndex = contentDisposition.indexOf("\"", startIndex + 1);
      if (startIndex > 0 && endIndex > 0) {
         return contentDisposition.substring(startIndex, endIndex).replace("\"", "");
      }
      return null;
   }

}
