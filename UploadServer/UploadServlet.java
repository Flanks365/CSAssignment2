import java.io.*;
import java.nio.charset.StandardCharsets;

public class UploadServlet extends HttpServlet {
   protected void doPost(HttpServletRequest request, HttpServletResponse response) {
      try {
         InputStream in = request.getInputStream();   
         BufferedInputStream bin = new BufferedInputStream(in);
         BufferedReader reader = new BufferedReader(new InputStreamReader(bin));
         String caption = "";
         String date = "";
         String filename = "";
         String temp = "";
         while ((temp = reader.readLine()) != null) {
            if (temp.contains("caption")) {
               while (caption.equals("")) {
                  caption = reader.readLine();
                  caption = caption.replace("\r\n", "");
               }
               System.out.println("Caption: " + caption);
            }
            if (temp.contains("date")) {
               while (date.equals("")) {
                  date = reader.readLine();
                  date = date.replace("\r\n", "");
               }
               System.out.println("Date: " + date);
            }
            if (temp.contains("filename=")) {
               filename = temp.substring(temp.indexOf("filename=") + 9);
               filename = filename.replace("\"", "");
               System.out.println("Filename: " + filename);
            }
         }

         String boundaryString = request.getBoundary();
         System.out.println("Boundary: " + boundaryString);
         // Convert the boundary string into bytes
         byte[] boundaryBytes = boundaryString.getBytes();

         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         int nRead;
         byte[] data = new byte[1];

         while ((nRead = bin.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
         }

         buffer.flush();
         byte[] array = buffer.toByteArray();

         System.out.println("Array Length: " + array.length);
         System.out.println("Array: " + new String(array));
         System.out.println("Boundary Bytes: " + new String(boundaryBytes));
         // Find the boundary in the input stream
         int boundaryPosition = indexOf(array, boundaryBytes);
         System.out.println("Boundary Position: " + boundaryPosition);

         if (boundaryPosition != -1) {
            // Extract the JPEG data (up to the boundary)
            byte[] jpegData = new byte[boundaryPosition];
            System.out.println("Byte Array: ");
            String strArr = new String(array);
            System.out.println(strArr);
            System.arraycopy(array, 0, jpegData, 0, boundaryPosition);

            // Write the extracted JPEG data to a file
            String newfileName = filename + caption + date;
            try (FileOutputStream outputStream = new FileOutputStream(newfileName + ".jpeg")) {
               outputStream.write(jpegData);
               String str = new String(jpegData);
               System.out.println("JPEG Data: ");
               System.out.println(str);
               outputStream.close();
            }
         }  else {
            System.out.println("Boundary not found in the stream.");
         }

      } catch(Exception ex) {
         System.err.println(ex);
      }
   }
   // Helper method to find the index of the boundary in the byte array
   private static int indexOf(byte[] data, byte[] pattern) {
      for (int i = 0; i <= data.length - pattern.length; i++) {
            boolean found = true;
            for (int j = 0; j < pattern.length; j++) {
               if (data[i + j] != pattern[j]) {
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
 
}
