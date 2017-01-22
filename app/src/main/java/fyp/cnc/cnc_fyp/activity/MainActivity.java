package fyp.cnc.cnc_fyp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import fyp.cnc.cnc_fyp.R;
import fyp.cnc.cnc_fyp.helper.SQLiteHandler;
import fyp.cnc.cnc_fyp.helper.SessionManager;

public class MainActivity extends Activity {

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textUserID = (TextView) findViewById(R.id.textUserID);
        TextView textName = (TextView) findViewById(R.id.textName);
        TextView textGender = (TextView) findViewById(R.id.textGender);
        TextView textRole = (TextView) findViewById(R.id.textRole);
        TextView textStatus = (TextView) findViewById(R.id.textStatus);
        TextView textEmail = (TextView) findViewById(R.id.textEmail);
        Button buttonLogout = (Button) findViewById(R.id.buttonLogout);

        //SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        //Session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        //Fetch user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        String userEmail = "Registered Email: " + user.get("userEmail");
        String userID = "ID: " + user.get("userID");
        String userName = "Name: " + user.get("userName");
        String userGender = "Gender: " + user.get("userGender");
        String userRole = "Role: " + user.get("userRole");
        String userStatus = "Status: " + user.get("userStatus");

        //Display user details
        textEmail.setText(userEmail);
        textUserID.setText(userID);
        textName.setText(userName);
        textGender.setText(userGender);
        textRole.setText(userRole);
        textStatus.setText(userStatus);

        //Logout button onClick event
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    //Log out user
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        //Link to login
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
