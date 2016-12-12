package tw.edu.ntust.stockoraclet.firebasepackage;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by user on 2016/8/29.
 */
public class sendSubscription extends AsyncTask<String, Integer, Void> {
    @Override
    protected Void doInBackground(String... params) {
        String url = params[0];
        String u_email = params[1];
        String o_number = params[2];
        Log.d("連接", "ING");
        try {
            Log.d("連接", "try");
            HttpURLConnection connection = (HttpURLConnection) new URL(url +
                    u_email + "&o_number=" + o_number).openConnection();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                Log.d("連接", "成功");
                URLEncoder.encode("test", "UTF-8");

            }
            else
                Log.d("連接", "sub失敗");

        } catch (IOException e) {
            Log.d("連接", e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
