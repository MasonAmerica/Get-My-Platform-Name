package com.support.getmyplatformname;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import masonamerica.platform.DeviceIdentifiers;
import masonamerica.platform.MasonFramework;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button getName;
    private TextView name;
    private String imei;
    private String deviceName;

    private DeviceIdentifiers di;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getName = (Button) findViewById(R.id.button);
        getName.setOnClickListener(mButtonListener);
        name = (TextView) findViewById(R.id.textView);

        // use Mason Framework to get device IMEI number for use in PlatformName API call
        // Mason security exception will be caught if running locally in
        try{
            di = MasonFramework.get(getBaseContext(), DeviceIdentifiers.class);
            imei = di.getIMEI(0);
        }catch(Exception e){
            imei = "123456789";
        }
    }

    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            // API Request to get Device name
            new PlatformName().execute("https://api.bymason.com/v1/default/device",imei);
        }
    };

    private class PlatformName extends AsyncTask<String,Void,String> {
        public final MediaType mediaType = MediaType.parse("text/plain");
        final OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... params){
            RequestBody body = RequestBody.create(mediaType,"");
            // Add your API_KEY
            Request request = new Request.Builder()
                    .url(params[0]+"?n="+params[1])
                    .method("GET",null)
                    .addHeader("Authorization","ADD_YOUR_API_KEY")
                    .build();
            try(Response response = client.newCall(request).execute()){
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "executed";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                JSONArray data = jsonResult.getJSONArray("data");
                JSONObject data_index0 = data.getJSONObject(0);

                name.setText(data_index0.getString("name"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}