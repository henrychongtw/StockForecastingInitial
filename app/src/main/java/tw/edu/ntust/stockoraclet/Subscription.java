package tw.edu.ntust.stockoraclet;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import tw.edu.ntust.stockoraclet.loginpackage.helper.SQLiteHandler;
import tw.edu.ntust.stockoraclet.loginpackage.helper.SessionManager;
import tw.edu.ntust.stockoraclet.oracletpackage.SubscriptionInfo;
import tw.edu.ntust.stockoraclet.oracletpackage.common;

/**
 * Created by henrychong on 2016/5/26.
 */
public class Subscription extends Fragment{
    private final static String TAG = "activityOne";
    private ProgressDialog progressDialog;
    private AsyncTask retrieveSubscription;
    private RecyclerView recyclerView;
    private String email;
    private SQLiteHandler db;
    private SessionManager session;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View subscription = inflater.inflate(R.layout.subscription_activity, container, false);

        recyclerView = (RecyclerView) subscription.findViewById(R.id.recycleSubscription);
        db = new SQLiteHandler(getActivity().getApplicationContext());

        // session manager
        session = new SessionManager(getActivity().getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        email = user.get("email");
        Log.i("testing", email);
        Log.i("testing", "myemail");


        if (networkConnected()) {
            retrieveSubscription = new RetrieveSubscription().execute(common.URLAllPredictor + email);
        } else {
            showToast(getActivity(), R.string.msg_NoNetwork);
        }
        return subscription;
    }
    public class RetrieveSubscription extends AsyncTask<String, Integer, List<SubscriptionInfo>> {
        @Override
        protected void onPostExecute(List<SubscriptionInfo> result) {
            //Log.i("1111", result.get(1).getOccur_time());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new SubscriptionListAdapter(getActivity().getLayoutInflater(), result));
            progressDialog.cancel();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<SubscriptionInfo> doInBackground(String... params) {
            String url = params[0];

            String jsonIn;
            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("param");
            try {
                jsonIn = getRemoteData(url, jsonObject.toString());
                Log.i("hereIsInput", "jsonIn" + jsonIn);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return null;
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<SubscriptionInfo>>() {
            }.getType();

            Log.i("taggggforHOT", jsonIn);
            return gson.fromJson(jsonIn, listType);

        }
    }

    private String getRemoteData(String url, String jsonOut) throws IOException {
        StringBuilder jsonIn = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

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
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onPause() {
        if (retrieveSubscription != null) {
            retrieveSubscription.cancel(true);
            retrieveSubscription = null;
        }
        super.onPause();
    }

    private void showToast(Context context, int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }

    private class SubscriptionListAdapter extends RecyclerView.Adapter<SubscriptionListAdapter.ViewHolder> {
        private LayoutInflater layoutInflater;
        private List<SubscriptionInfo> subscriptionInfoList;

        public SubscriptionListAdapter(LayoutInflater layoutInflater, List<SubscriptionInfo> subscriptionInfoList) {
            this.layoutInflater = layoutInflater;
            this.subscriptionInfoList = subscriptionInfoList;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = layoutInflater.inflate(R.layout.recyclerview_subscription, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {
            final SubscriptionInfo theSubscribed = subscriptionInfoList.get(position);
            viewHolder.tvSubscribedPredictPeople.setText(String.valueOf(theSubscribed.getPredict_people()));
            Double accu;
            if(theSubscribed.getAccuracy() != null){
                accu = Double.parseDouble(theSubscribed.getAccuracy());
                accu = accu*100;
                viewHolder.tvAccuracy2.setText(String.format("%.2f",accu)+"%");
            }else{
                viewHolder.tvAccuracy2.setText("0.00%");
            }
            String check;
            if(theSubscribed.getNotification() == 1){
                check = "New Update!";
                viewHolder.tvNotification.setText(String.valueOf(check));
                viewHolder.tvNotification.setTextColor(Color.YELLOW);
            }else{
                check = "No Update";
                viewHolder.tvNotification.setText(String.valueOf(check));
                viewHolder.tvNotification.setTextColor(Color.WHITE);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(theSubscribed.getNotification() == 1) {
                        theSubscribed.setNotification(0);
                        notifyDataSetChanged();
                    }
                    String predictorname = viewHolder.tvSubscribedPredictPeople.getText().toString();
                    Intent intent = new Intent(getActivity(), predictorAllOraclet.class);
                    Log.i("what the heck?", predictorname);
                    Bundle bundle = new Bundle();
                    bundle.putString("predictor", predictorname);
                    Log.i("what the heck?", bundle.toString());

                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return subscriptionInfoList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSubscribedPredictPeople, tvNotification , tvAccuracy2;

            public ViewHolder(View itemView) {
                super(itemView);
                tvSubscribedPredictPeople = (TextView) itemView.findViewById(R.id.tvSubscribedPredictPeople);
                tvNotification = (TextView) itemView.findViewById(R.id.tvNotification);
                tvAccuracy2 = (TextView) itemView.findViewById(R.id.tvAccuracy2);
            }
        }
    }
}
