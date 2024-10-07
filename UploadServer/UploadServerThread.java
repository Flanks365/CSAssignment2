import java.net.*;
import java.io.*;
import java.time.Clock;
public class UploadServerThread extends Thread {
   private Socket socket = null;
   public UploadServerThread(Socket socket) {
      super("DirServerThread");
      this.socket = socket;
   }
   public void run() {
      try {
         InputStream in = socket.getInputStream();
         HttpServletRequest req = new HttpServletRequest(in);  
         OutputStream baos = new ByteArrayOutputStream(); 
         HttpServletResponse res = new HttpServletResponse(baos); 
         HttpServlet httpServlet = new UploadServlet();

         System.out.println("in uploadservlet");
         // InputStream in = request.getInputStream();
         BufferedReader br = new BufferedReader(new InputStreamReader(in));
         String inputLine, outputLine;
         // KnockKnockProtocol kkp = new KnockKnockProtocol();
         // outputLine = kkp.processInput(null);
         // out.println(outputLine);

         while ((inputLine = br.readLine()) != null) {
            // outputLine = kkp.processInput(inputLine);
            System.out.println(inputLine);
            if (inputLine.indexOf("GET") > -1) {
               System.out.println("got get request");
            }
            // if (outputLine.equals("Bye"))
            //    break;
         }

         // httpServlet.doPost(req, res);
         OutputStream out = socket.getOutputStream(); 
         out.write(((ByteArrayOutputStream) baos).toByteArray());
         socket.close();
      } catch (Exception e) { e.printStackTrace(); }
   }
}
