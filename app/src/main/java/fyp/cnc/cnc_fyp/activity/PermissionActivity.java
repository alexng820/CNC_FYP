package fyp.cnc.cnc_fyp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fyp.cnc.cnc_fyp.R;
import fyp.cnc.cnc_fyp.helper.LocationHandler;

//Helper for granting location permission in service

public class PermissionActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST = 2701;
    private TextView permissionHint;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        permissionHint = (TextView) findViewById(R.id.permissionHint);
        buttonBack = (Button) findViewById(R.id.button_back);
        permissionHint.setVisibility(View.INVISIBLE);
        buttonBack.setVisibility(View.GONE);

        //Check if user already granted location permission
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //If not, request permissions
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST);
        }

        //If user does not approve location permission, retry
        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(PermissionActivity.this, LocationHandler.class);
                startService(intent);
                intent = new Intent(PermissionActivity.this, Class_scheduleActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //User granted permission, retry location service
                    Intent intent = new Intent(this, LocationHandler.class);
                    startService(intent);
                    intent = new Intent(this, Class_scheduleActivity.class);
                    startActivity(intent);
                } else {
                    //Show hints
                    permissionHint.setVisibility(View.VISIBLE);
                    buttonBack.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
