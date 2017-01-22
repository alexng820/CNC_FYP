package fyp.cnc.cnc_fyp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fyp.cnc.cnc_fyp.R;
import fyp.cnc.cnc_fyp.app.AppConfig;
import fyp.cnc.cnc_fyp.app.AppController;
import fyp.cnc.cnc_fyp.helper.SQLiteHandler;
import fyp.cnc.cnc_fyp.helper.SessionManager;

public class LoginActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText inputUserEmail;
    private EditText inputUserPass;
    private ProgressDialog progressDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUserEmail = (EditText) findViewById(R.id.userEmail);
        inputUserPass = (EditText) findViewById(R.id.userPass);
        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
        Button buttonLinkToRegister = (Button) findViewById(R.id.buttonToRegister);

        //Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        //Session manager
        session = new SessionManager(getApplicationContext());

        //Check if user is already logged in or not
        if (session.isLoggedIn()) {
            //User is already logged in
            Intent intent = new Intent(LoginActivity.this, Class_scheduleActivity.class);
            startActivity(intent);
            finish();
        }

        //Login button onClick event
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String userEmail = inputUserEmail.getText().toString().trim();
                String userPass = inputUserPass.getText().toString().trim();

                //Check for empty data in form
                if (!userEmail.isEmpty() && !userPass.isEmpty()) {
                    //Login
                    checkLogin(userEmail, userPass);
                } else {
                    //Required credentials are missing
                    Toast.makeText(getApplicationContext(), "Please enter your login credentials.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Link to register
        buttonLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    //Verify login details
    private void checkLogin(final String userEmail, final String userPass) {
        //Tag used to cancel the request
        String tag_string_req = "req_login";

        progressDialog.setMessage("Logging in...");
        showDialog();

        StringRequest strRequest = new StringRequest(Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    //Check for error
                    if (!error) {
                        //Log in successful
                        session.setLogin(true);

                        //Store the ser in SQLite
                        JSONObject user = jsonObject.getJSONObject("user");
                        String userID = user.getString("userID");
                        String userName = user.getString("userName");
                        String userGender = user.getString("userGender");
                        String userRole = user.getString("userRole");
                        String userStatus = user.getString("userStatus");
                        String userEmail = user.getString("userEmail");

                        //Insert row to user table
                        db.addUser(userID, userName, userGender, userRole, userStatus, userEmail);

                        //Launch main activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //Get error
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    //JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //Post parameters to login URL
                Map<String, String> params = new HashMap<>();
                params.put("userEmail", userEmail);
                params.put("userPass", userPass);

                return params;
            }
        };

        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, tag_string_req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
