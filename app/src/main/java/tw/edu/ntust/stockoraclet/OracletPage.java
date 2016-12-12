package tw.edu.ntust.stockoraclet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import tw.edu.ntust.stockoraclet.firebasepackage.sendSubscription;
import tw.edu.ntust.stockoraclet.loginpackage.helper.SQLiteHandler;
import tw.edu.ntust.stockoraclet.loginpackage.helper.SessionManager;
import tw.edu.ntust.stockoraclet.oracletpackage.common;
import tw.edu.ntust.stockoraclet.oracletpackage.oraclet;

/**
 * Created by henrychong on 2016/5/24.
 */
public class OracletPage extends AppCompatActivity {

    private final static String TAG = "activityOne";
    private ProgressDialog progressDialog;
    private AsyncTask retrieveOraclet, sendSub;
    private ListView lvOracletPage;
    private int theNumber , setP , result_status ;
    private double setPrice;
    private SQLiteHandler db;
    private SessionManager session;
    private String email , predictor;
    private Button btSubscribe;
    private int isContradict;
    private int oracletNumber;
    public class RetrieveOraclet extends AsyncTask<String, Integer, List<oraclet>> {
        @Override
        protected void onPostExecute(List<oraclet> result) {
//            Log.i("1111", result.get(39).getoccur_time());

            lvOracletPage.setAdapter(new OracletListAdapter(OracletPage.this, result));

            progressDialog.cancel();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OracletPage.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<oraclet> doInBackground(String... params) {
            String url = params[0];

            String jsonIn;
            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("param");
            try {
                jsonIn = getRemoteData(url, jsonObject.toString());
                Log.i(TAG, "jsonIn" + jsonIn);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return null;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<oraclet>>() {
            }.getType();

            Log.e("tag", jsonIn);
            return gson.fromJson(jsonIn, listType);

//            btSubscribe.setOnClickListener(View.OnClickListener);


        }
    }

    private String getRemoteData(String url, String jsonOut) throws IOException {
        StringBuilder jsonIn = new StringBuilder();
        Bundle bundle = getIntent().getExtras();
        theNumber = bundle.getInt("number");
        //Log.i("hereshowsthenumber", Integer.toString(theNumber));
        //HttpURLConnection connection = (HttpURLConnection) new URL(url + URLEncoder.encode("麥格理", "UTF-8")).openConnection();
        HttpURLConnection connection = (HttpURLConnection) new URL(common.URLoraclet + theNumber).openConnection();

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                jsonIn.append(line);
            }
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        connection.disconnect();
        Log.d(TAG, "jsonIn: " + jsonIn);
        return jsonIn.toString();
    }

    private boolean networkConnected() {
        ConnectivityManager conManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oraclet_page_activity);
        btSubscribe =(Button) findViewById(R.id.btSubscribe);
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());


        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        email = user.get("email");

        Log.i("testing", email);





        lvOracletPage = (ListView) findViewById(R.id.lvOracletPage);
        if (networkConnected()) {
            retrieveOraclet = new RetrieveOraclet().execute(common.URL);
        } else {
            showToast(this, R.string.msg_NoNetwork);
        }

    }
    private class OracletListAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<oraclet> oracletList;


        public OracletListAdapter(Context context, List<oraclet> oracletList) {
            this.layoutInflater = LayoutInflater.from(context);
            this.oracletList = oracletList;
        }

        @Override
        public int getCount() {
            return oracletList.size();
        }

        @Override
        public Object getItem(int position) {
            return oracletList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return oracletList.get(position).getpredict_targetcode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.oraclet_activity, parent, false);
            }
            if(position % 2 == 0){
                convertView.setBackgroundColor(Color.rgb(204, 204, 255));
            }else{
                convertView.setBackgroundColor(Color.rgb(0, 50, 220));
            }
            TextView tvOracletAccuracy = (TextView) convertView.findViewById(R.id.tvOracletAccuracy);
            TextView tvResult = (TextView) convertView.findViewById(R.id.tvResult);
            TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            TextView tvResultStatus = (TextView) convertView.findViewById(R.id.tvResultStatus);
            TextView tvPredictTime = (TextView) convertView.findViewById(R.id.tvPredictTime);
            TextView tvEventContent = (TextView) convertView.findViewById(R.id.tvEventContent);
            TextView tvPredictTargetId = (TextView) convertView.findViewById(R.id.tvPredictTargetId);
            TextView tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
            TextView tvHasContradiction = (TextView) convertView.findViewById(R.id.tvHasContradiction);
            TextView tvPredictPeople = (TextView) convertView.findViewById(R.id.tvPredictPeople);
//            TextView tvType = (TextView) convertView.findViewById(R.id.tvType);
            TextView tvPredictTargetName = (TextView) convertView.findViewById(R.id.tvPredictTargetName);

            oraclet theOraclet = oracletList.get(position);
            Double accu;
            if(theOraclet.getAccuracy() != null){
                accu = Double.parseDouble(theOraclet.getAccuracy());
                accu = accu*100;
                tvOracletAccuracy.setText("Accuracy: " +String.format("%.2f",accu)+"%");
            }else{
                tvOracletAccuracy.setText("Accuracy: " +"0.00%");
            }
            setPrice = theOraclet.getnow_price();
            result_status = theOraclet.getresult_status();
            isContradict = theOraclet.isHasContradiction();
            predictor = theOraclet.getpredict_people();
            oracletNumber = theOraclet.getNumber();
            String status;
            if (theOraclet.getresult_status() == 1){
                status = "Verified";
            }else{
                status = "Not Verified";
            }
            Log.i("testing", String.valueOf(theOraclet.getNumber()));
            tvDate.setText("Oraclet Date: " + theOraclet.getpredict_time());
            tvResultStatus.setText("Result Verifiying Status: " + status);
            tvPredictTime.setText("Predicted Event Occur Time: " + theOraclet.getoccur_time());
            tvEventContent.setText("Prediction: " + theOraclet.getevent_content());
            tvPredictTargetId.setText("Stock ID: " + theOraclet.getpredict_targetcode());
            tvPrice.setText("Predict Closing Price: " + theOraclet.getnow_price());

            String contradict;
            if (theOraclet.isHasContradiction() == 1){
                contradict = "Yes";
            }else{
                contradict = "No";
            }
            tvHasContradiction.setText("Predict Contradiction: " + contradict);
            tvPredictPeople.setText("Predictor Name: " + theOraclet.getpredict_people());
//            tvType.setText("Type :" + theOraclet.getType());
            tvPredictTargetName.setText("Target Stock Name: " + theOraclet.getpredict_targetname());
//            tvNumber.setText("Oraclet ID :" + theOraclet.getNumber());
            String showResult;
            if(theOraclet.getResult() == "1"){
                showResult = "Correct Prediction";
            }else if(theOraclet.getResult() == "0"){
                showResult = "Wrong Prediction";
            }else{
                showResult = "Result not available yet";
            }
            tvResult.setText("Prediction Result: " +showResult);

            return convertView;
        }
    }

    public void onGraphClick(View view){

        try{
            if(result_status == 1){
                Intent intent = new Intent(this, GraphActivity.class);
                Log.i("intenttttt?", intent.toString());
                Bundle bundle = new Bundle();
                Log.i("111111111111111", intent.toString());
                bundle.putInt("number", theNumber);
                bundle.putDouble("setPrice", setPrice);
                intent.putExtras(bundle);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(), "The prediction hasn't been verified yet.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void onCommentClick(View view){
        try{
                Intent intent2 = new Intent(this, CommentActivity.class);
                Log.i("intentttttforComment?", intent2.toString());
                Bundle bundle2 = new Bundle();
                Log.i("11111111forComment", intent2.toString());
                bundle2.putInt("number", theNumber);
                intent2.putExtras(bundle2);
                startActivity(intent2);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "There is not comment for this oraclet", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    public void onSubscribeClick(View view) {
        try{
            /*
            Intent intent3 = new Intent(this, SubscribeActivity.class);
            Bundle bundle3 = new Bundle();
            bundle3.putString("email",email);
            bundle3.putString("o_number", String.valueOf(oracletNumber));
            Log.i("11111111forComment", bundle3.toString());
            intent3.putExtras(bundle3);
            startActivity(intent3);
            */
            sendSub = new sendSubscription().execute(common.URLSubscription,
                    email, String.valueOf(oracletNumber));

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "There is not comment for this oraclet", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void onContradictionClick (View view) {
        try{
            if(isContradict == 1 ){
                Intent intent4 = new Intent(this, Contradiction.class);
                Bundle bundle4 = new Bundle();
                bundle4.putInt("oracletNumber", oracletNumber);
                Log.i("11111111forComment", bundle4.toString());
                intent4.putExtras(bundle4);
                startActivity(intent4);
            }else{
                Toast.makeText(getApplicationContext(), "There is no contradiction for this oraclet", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "There is not comment for this oraclet", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    @Override
    protected void onPause() {
        if (retrieveOraclet != null) {
            retrieveOraclet.cancel(true);
            retrieveOraclet = null;
        }
        super.onPause();
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private void showToast(Context context, int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }
}
