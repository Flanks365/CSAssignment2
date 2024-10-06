// public class UploadAsyncTask extends AsyncTask {
//    protected void onPostExecute(String result) {
//       System.out.println(result);
//    }
//    protected String doInBackground() {
//      return new UploadClient().uploadFile();
//    }  
// }

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
    }

    protected String doInBackground() {
        return new UploadClient(caption, date, file).uploadFile();
    }
}
