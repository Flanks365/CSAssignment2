import java.io.IOException;

public class UploadAsyncTask extends AsyncTask {
   private int port;

   public UploadAsyncTask(int port) {
      this.port = port;
   }

   protected void onPostExecute(String result) {
      System.out.println(result);
   }

   protected String doInBackground() {
      String uploadFile = null;
      try {
         UploadClient client = new UploadClient();
         uploadFile = client.uploadFile(port);
      } catch (ReadInfoFailException e) {
         System.err.println("ReadInfoFailException: " + e);
      } catch (IOException e) {
         System.err.println("IOException: " + e);
      } catch (Exception e) {
         System.err.println("Exception: " + e);
      }
      return uploadFile;
   }
}

// public class UploadAsyncTask extends AsyncTask {
// private String caption;
// private String date;
// private File file;

// public UploadAsyncTask(String caption, String date, File file) {
// this.caption = caption;
// this.date = date;
// this.file = file;
// }

// protected void onPostExecute(String result) {
// System.out.println(result);
// }

// protected String doInBackground() {
// return new UploadClient(caption, date, file).uploadFile();
// }
// }
