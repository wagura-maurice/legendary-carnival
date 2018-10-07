package ke.co.waguramaurice.myreader;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Henry on 16-Aug-16.
 */
public class BackgroundWorker extends AsyncTask<String,Void, String> {
    Context ctx;
    BackgroundWorker(Context ctx)
    {
        this.ctx = ctx;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {
        String find_url = "https://sms-qwetu.cf/api/v1/qwetu/store";

        {

            String activity_id = params[1];
            String admission_no = params[2];

            try {
                URL url = new URL(find_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput (true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode ("activity_id", "UTF-8") +"="+ URLEncoder.encode(activity_id,"UTF-8")+"&"+
                        URLEncoder.encode ("admission_no", "UTF-8") +"="+ URLEncoder.encode(admission_no,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
                return "Message Sent.";

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return "Unable to Send.";
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {

        Toast toast = Toast.makeText(ctx, result, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 130);
        toast.show();

    }

    private void finish() {
    }
}
