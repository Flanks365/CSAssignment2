// import java.io.*;
// import java.time.Clock;
// public class UploadServlet extends HttpServlet {
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
//       try {
//          InputStream in = request.getInputStream();   
//          ByteArrayOutputStream baos = new ByteArrayOutputStream();  
//          byte[] content = new byte[1];
//          int bytesRead = -1;      
//          while( ( bytesRead = in.read( content ) ) != -1 ) {  
//             baos.write( content, 0, bytesRead );  
//          }
//          Clock clock = Clock.systemDefaultZone();
//          long milliSeconds=clock.millis();
//          OutputStream outputStream = new FileOutputStream(new File(String.valueOf(milliSeconds) + ".png"));
//          baos.writeTo(outputStream);
//          outputStream.close();
//          PrintWriter out = new PrintWriter(response.getOutputStream(), true);
//          File dir = new File(".");
//          String[] chld = dir.list();
//       	 for(int i = 0; i < chld.length; i++){
//             String fileName = chld[i];
//             out.println(fileName+"\n");
//             System.out.println(fileName);
//          }
//       } catch(Exception ex) {
//          System.err.println(ex);
//       }
//    }
// }
import java.io.*;
import java.time.Clock;

public class UploadServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = new PrintWriter(response.getOutputStream(), true);
            // Build the HTML response
            String htmlResponse = "<!DOCTYPE html>" +
                    "<html>" +
                    "<body>" +
                    "<h2>Upload File</h2>" +
                    "<form action=\"/upload\" method=\"post\" enctype=\"multipart/form-data\">" +
                    "Caption: <input type=\"text\" name=\"caption\"/><br/><br/>" +
                    "Date: <input type=\"date\" name=\"date\"/><br/><br/>" +
                    "File: <input type=\"file\" name=\"fileName\"/><br/><br/>" +
                    "<input type=\"submit\" value=\"Submit\"/>" +
                    "</form>" +
                    "</body>" +
                    "</html>";

            // Write the HTTP headers
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("Content-Length: " + htmlResponse.length());
            out.println();
            // Write the HTML content
            out.print(htmlResponse);
            out.flush();

        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            InputStream in = request.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] content = new byte[1];
            int bytesRead = -1;
            while ((bytesRead = in.read(content)) != -1) {
                baos.write(content, 0, bytesRead);
            }
            Clock clock = Clock.systemDefaultZone();
            long milliSeconds = clock.millis();
            OutputStream outputStream = new FileOutputStream(new File(String.valueOf(milliSeconds) + ".png"));
            baos.writeTo(outputStream);
            outputStream.close();

            PrintWriter out = new PrintWriter(response.getOutputStream(), true);
            File dir = new File(".");
            String[] chld = dir.list();
            for (String fileName : chld) {
                out.println(fileName + "\n");
                System.out.println(fileName);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}
