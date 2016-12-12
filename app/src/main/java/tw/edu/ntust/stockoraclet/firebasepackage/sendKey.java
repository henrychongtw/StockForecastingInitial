package tw.edu.ntust.stockoraclet.firebasepackage;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by user on 2016/7/25.
 */
public class sendKey extends AsyncTask<String, Integer, Void> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        String url = params[0];
        String email = params[1];
        Log.d("連接", "ING");
        try {
            Log.d("連接", "try");
            HttpURLConnection connection = (HttpURLConnection) new URL(url +
                    FirebaseInstanceId.getInstance().getToken() + "&email=" + email).openConnection();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                Log.d("連接", "成功");
                URLEncoder.encode("test", "UTF-8");
            }
            else
                Log.d("連接", "失敗");

        } catch (IOException e) {
            Log.d("連接", e.getMessage());
        }return null;
    }
}
