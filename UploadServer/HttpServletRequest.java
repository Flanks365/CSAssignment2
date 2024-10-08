import java.io.*;
public class HttpServletRequest {
   private InputStream inputStream = null;
   private String boundary = "";
   public HttpServletRequest(InputStream inputStream) {
      this.inputStream = inputStream;
   }
   public InputStream getInputStream() {return inputStream;}
   public void setBoundary(String boundary) {this.boundary = boundary;}
   public String getBoundary() {return boundary;}
}