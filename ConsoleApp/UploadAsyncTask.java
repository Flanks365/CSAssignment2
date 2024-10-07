<<<<<<< HEAD
// public class UploadAsyncTask extends AsyncTask {
//    protected void onPostExecute(String result) {
//       System.out.println(result);
//    }
//    protected String doInBackground() {
//      return new UploadClient().uploadFile();
//    }  
// }

import java.io.*;

public class UploadAsyncTask extends AsyncTask {
    private String caption;
    private String date;
    private File file;

    public UploadAsyncTask(String caption, String date, File file) {
        this.caption = caption;
        this.date = date;
        this.file = file;
    }

    protected void onPostExecute(String result) {
        System.out.println(result);
        System.out.println("post execute");
    }

    protected String doInBackground() {
        return new UploadClient(caption, date, file).uploadFile();
    }
=======
public class UploadAsyncTask extends AsyncTask {
   protected void onPostExecute(String result) {
      System.out.println(result);
   }
   protected String doInBackground() {
     return new UploadClient().uploadFile();
   }  
>>>>>>> fea356fe2dfa3c3c12e78cc0e84698091595f469
}

// public class UploadAsyncTask extends AsyncTask {
//     private String caption;
//     private String date;
//     private File file;

//     public UploadAsyncTask(String caption, String date, File file) {
//         this.caption = caption;
//         this.date = date;
//         this.file = file;
//     }

//     protected void onPostExecute(String result) {
//         System.out.println(result);
//     }

//     protected String doInBackground() {
//         return new UploadClient(caption, date, file).uploadFile();
//     }
// }
