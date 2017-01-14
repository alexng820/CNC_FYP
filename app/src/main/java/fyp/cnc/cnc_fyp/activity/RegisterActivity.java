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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fyp.cnc.cnc_fyp.R;
import fyp.cnc.cnc_fyp.app.AppConfig;
import fyp.cnc.cnc_fyp.app.AppController;
import fyp.cnc.cnc_fyp.helper.SQLiteHandler;
import fyp.cnc.cnc_fyp.helper.SessionManager;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button buttonRegister;
    private Button buttonLinkToLogin;
    private EditText inputUserEmail;
    private EditText inputUserPass;
    private EditText inputConfirmPass;
    private ProgressDialog progressDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputUserEmail = (EditText) findViewById(R.id.userEmail);
        inputUserPass = (EditText) findViewById(R.id.userPass);
        inputConfirmPass = (EditText) findViewById(R.id.userConfirmPass);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonLinkToLogin = (Button) findViewById(R.id.buttonLinkToLogin);

        //Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //Session manager
        session = new SessionManager(getApplicationContext());

        //SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        //Check if user is already logged in or not
        if (session.isLoggedIn()) {
            //User is already logged in
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        //Register button onClick event
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String userEmail = inputUserEmail.getText().toString().trim();
                String userPass = inputUserPass.getText().toString().trim();
                String userConfirmPass = inputConfirmPass.getText().toString().trim();

                //Check for empty data in form
                if (!userEmail.isEmpty() && !userPass.isEmpty() && !userConfirmPass.isEmpty()) {
                    //Check if 2 password input matches
                    if (Objects.equals(userPass, userConfirmPass)) {
                        //Register
                        registerUser(userEmail, userPass);
                    } else {
                        //Password not match
                        Toast.makeText(getApplicationContext(), "Please enter same password twice.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //Required credentials are missing
                    Toast.makeText(getApplicationContext(), "Please enter your login credentials.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Link to login
        buttonLinkToLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    //Register user
    private void registerUser(final String userEmail, final String userPass) {
        //Tag used to cancel the request
        String tag_string_req = "req_register";

        progressDialog.setMessage("Registering...");
        showDialog();

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    //Check for error
                    if (!error) {
                        //Register Successful
                        JSONObject user = jsonObject.getJSONObject("user");
                        String userEmail = user.getString("userEmail");
                        String userRole = user.getString("userRole");
                        String userStatus = user.getString("userStatus");

                        //Insert row to user table
                        db.addUser(userEmail, userRole, userStatus);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Please login again.", Toast.LENGTH_LONG).show();

                        //Launch login activity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //Get error
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
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
                Map<String, String> params = new HashMap<String, String>();
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
