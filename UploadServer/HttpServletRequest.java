import java.io.*;
public class HttpServletRequest {
   private InputStream inputStream = null;
   public HttpServletRequest(InputStream inputStream) {
      this.inputStream = inputStream;
      System.out.println("HttpServletRequest created");
   }
   public InputStream getInputStream() {return inputStream;}
}