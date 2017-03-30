package fyp.cnc.cnc_fyp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import fyp.cnc.cnc_fyp.R;

public class TestActivity extends AppCompatActivity {
    String json_string=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        TextView tv_test=(TextView)findViewById(R.id.tv_test);
        Thread thread = new Thread(get_json);
        thread.start();
        tv_test.setText(json_string);
    }
    private Runnable get_json = new Runnable() {
        public void run() {
            // 運行網路連線的程式
            JSONObject temp = get_data();

            try {
                json_string = temp.getString("1");
            } catch (Exception e) {
                Log.e("JSON Parser", "Error due tuen to a string ");
                json_string="f";
            }

        }
    };
    public JSONObject get_data() {
        InputStream is;
        JSONObject json = null;
        String output = "";
        URL _url;
        HttpURLConnection urlConnection;
        try {
            _url = new URL("http://35.167.144.165/test.php");
            urlConnection = (HttpURLConnection) _url.openConnection();
        }
        catch (MalformedURLException e) {
            Log.e("JSON Parser", "Error due to a malformed URL " + e.toString());
            return null;
        }
        catch (IOException e) {
            Log.e("JSON Parser", "IO error " + e.toString());
            return null;
        }
        try {
            is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder total = new StringBuilder(is.available());
            String line;
            while ((line = reader.readLine()) != null) {
                total.append(line).append('\n');
            }
            output = total.toString();
        }
        catch (IOException e) {
            Log.e("JSON Parser", "IO error " + e.toString());
            return null;
        }
        finally{
            urlConnection.disconnect();
        }
        try {
            json = new JSONObject(output);
        }
        catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return json;
    }


}