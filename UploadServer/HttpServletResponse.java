import java.io.*;
public class HttpServletResponse {
   private OutputStream outputStream = null;
   public HttpServletResponse(OutputStream outputStream) {
      this.outputStream = outputStream;
      System.out.println("HttpServletResponse created");
   }
   public OutputStream getOutputStream() {return outputStream;}
}