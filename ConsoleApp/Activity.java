// import java.io.*;
// public class Activity {
//    public static void main(String[] args) throws IOException {
//        new Activity().onCreate();
//     }
//    public Activity() {
//    }
//    public void onCreate() {
//       AsyncTask UploadAsyncTask = new UploadAsyncTask().execute(); 
//       System.out.println("Waiting for Callback");
//       try { 
//          BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//          br.readLine();
//       } catch (Exception e) { }
//    }
// }

import java.io.*;

public class Activity {
    public static void main(String[] args) throws IOException {
        new Activity().onCreate();
    }

    public Activity() {
    }

    public void onCreate() {
        // Example data - you can replace this with user input
        String caption = "Sample Image";
        String date = "2024-10-05"; // Date format as per requirement
        File file = new File("AndroidLogo.png"); // Ensure the file exists

        UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(caption, date, file).execute();
        System.out.println("Waiting for Callback");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
