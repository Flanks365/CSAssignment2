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
        new Activity().onCreate(args[0], args[1], args[2]);
    }

    public Activity() {
    }

    public void onCreate(String caption, String date, String filename) {
        // Example data - you can replace this with user input
        // String caption = "Sample Image";
        // String date = "2024-10-05"; // Date format as per requirement
        // File file = new File("AndroidLogo.png"); // Ensure the file exists
        File file = new File(filename); // Ensure the file exists

        System.out.println(caption + date + filename);

        AsyncTask uploadAsyncTask = new UploadAsyncTask(caption, date, file).execute();
        System.out.println("Waiting for Callback");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.readLine();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("onCreate complete");
    }
}
