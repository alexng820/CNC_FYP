package fyp.cnc.cnc_fyp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fyp.cnc.cnc_fyp.R;
import fyp.cnc.cnc_fyp.helper.SQLiteHandler;
import fyp.cnc.cnc_fyp.helper.SessionManager;

public class Event_listActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String URL_GETEVENT= "http://35.167.144.165/getevent.php";
    private static final String URL_JOINEVENT= "http://35.167.144.165/joinevent.php";
    private static final String URL_GETEVENTSTATUS= "http://35.167.144.165/geteventstatus.php";

    private SQLiteHandler db;
    private SessionManager session;
    int finish=0;
    JSONArray event_list=new JSONArray();
    JSONArray event_status=new JSONArray();
    ListView list;
    LazyAdapter adapter;
    ArrayList<HashMap<String, String>> events = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Thread thread = new Thread(newThread_getEvent);
        thread.start();
        while(finish<2){

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i<event_list.length(); i++) {
            // creating new HashMap

            HashMap<String, String> map = new HashMap<String, String>();
            try {
                JSONObject res=event_list.getJSONObject(i);
                map.put("event_id",res.getString("event_id"));
                map.put("topic",res.getString("topic"));
                map.put("guest",res.getString("guest"));
                map.put("limit_participant",res.getString("limit_participant"));
                map.put("need_approved",res.getString("need_approved"));
                map.put("location",res.getString("location"));
                map.put("postime",res.getString("postime"));
                map.put("date",res.getString("date"));
                map.put("count_approved",res.getString("count_approved"));
                map.put("count_waiting",res.getString("count_waiting"));
                map.put("count_deny",res.getString("count_deny"));
                for(int j=0;j<event_status.length();j++){
                    JSONObject status = event_status.getJSONObject(j);
                    if(status.getString("event_id").equals(res.getString("event_id"))){
                        map.put("participant",status.getString("status"));
                        break;
                    }else{
                        if(res.getInt("limit_participant")==0){
                            map.put("participant", "No limit");
                        }else {
                            if (res.getInt("count_approved") < res.getInt("limit_participant")) {
                                map.put("participant", res.getString("count_approved") + "/" + res.getString("limit_participant"));
                            } else {
                                map.put("participant", "Full");
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // adding HashList to ArrayList
            events.add(map);
        }

        list=(ListView)findViewById(R.id.list);
        adapter=new LazyAdapter(this, events);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                showEventDetail(position);
            }
        });
        try {
            Uri data = getIntent().getData();
            String scheme = data.getScheme(); // "myapp"
            String host = data.getHost(); // "path.com"
            List params = data.getPathSegments();
            String first = params.get(0).toString(); // "pathPrefix"
            if (first != null) {
                for(int i=0 ;i<event_list.length();i++){
                    JSONObject event=event_list.getJSONObject(i);
                    if(first.equals(event.getString("event_id"))){
                        showEventDetail(i);
                        break;
                    }

                }
            }
        }catch (Exception e){}
    }
    public void joinevent(int position){
        String event_id= null;
        int quota=0;
        String parm="?";
        try {
            event_id = event_list.getJSONObject(position).getString("event_id");
            quota=Integer.parseInt(event_list.getJSONObject(position).getString("limit_participant"));
            parm+="event_id="+event_id;
            parm+="&participant_id="+db.getUserDetails().get("userID");

            if(Integer.parseInt(event_list.getJSONObject(position).getString("need_approved"))!=0){
                parm+="&status=approved";
            }else {
                parm+="&status=waiting_approval";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //if(checkecentquota(event_id,quota)) {
            String url = URL_JOINEVENT + parm;
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            if(response.getString("success")=="true"){
                                showToast("Join successed");
                            }else{
                                showToast("You have already joined. Your status is "+response.getString("success"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    },
                    error -> {
                    }
            );

            queue.add(getRequest);
//        }else{
//            showToast("it's full");
//        }

    }

    private Toast toast;
    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
    public void showEventDetail(int position){
        int participant=0;
        int limit=0;
        String positivetext=null;
        try {
            participant=Integer.parseInt(event_list.getJSONObject(position).getString("count_approved")) ;
            limit=Integer.parseInt(event_list.getJSONObject(position).getString("limit_participant")) ;
            for(int j=0;j<event_status.length();j++){
                JSONObject status = event_status.getJSONObject(j);
                if(status.getString("event_id").equals(event_list.getJSONObject(position).getString("event_id"))){
                    positivetext=status.getString("status");
                    break;
                }else{
                    if(participant>=limit&&limit!=0){
                        positivetext="Full";
                    }else {
                        positivetext="Join";
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Event Detail")
                .customView(R.layout.dialog_eventdetail, true)
                .positiveText(positivetext)
                .onPositive(
                        (dialog1, which) -> joinevent(position))
                .negativeText(android.R.string.cancel)
                .build();

        View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        if(!positivetext.equals("Join")){
            positiveAction.setEnabled(false);
        }



        TextView event_topic=(TextView)dialog.getCustomView().findViewById(R.id.event_topic);
        TextView event_guest=(TextView)dialog.getCustomView().findViewById(R.id.event_guest);
        TextView event_location=(TextView)dialog.getCustomView().findViewById(R.id.event_location);
        TextView event_date=(TextView)dialog.getCustomView().findViewById(R.id.event_date);
        TextView event_participant=(TextView)dialog.getCustomView().findViewById(R.id.event_participant);

        try {
            event_topic.setText(event_list.getJSONObject(position).getString("topic"));
            event_guest.setText(event_list.getJSONObject(position).getString("guest"));
            event_location.setText(event_list.getJSONObject(position).getString("location"));
            event_date.setText(event_list.getJSONObject(position).getString("date"));
            if(event_list.getJSONObject(position).getInt("limit_participant")==0){
                event_participant.setText("No limit");
            }else{
                event_participant.setText(event_list.getJSONObject(position).getString("count_approved")+"/"+event_list.getJSONObject(position).getString("limit_participant"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        dialog.show();
    }
    private Runnable newThread_joinEvent = new Runnable() {
        public void run() {
            event_list = getevent();
        }
    };
    private Runnable newThread_getEvent = new Runnable() {
        public void run() {
            event_list = getevent();
            event_status=geteventstatus();
        }
    };

    public JSONArray getevent(){
        JSONArray events=null;
        HttpURLConnection urlConnection;

        try {
            URL url = new URL(URL_GETEVENT);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            JSONObject res=new JSONObject(reader.readLine());

            events =  res.getJSONArray("events");
            finish++;
        }catch( Exception e) {
            e.printStackTrace();
        }


        return events;

    }
    public JSONArray geteventstatus(){
        JSONArray eventstatus=null;
        HttpURLConnection urlConnection;

        try {
            String query = URL_GETEVENTSTATUS+"?participant_id="+db.getUserDetails().get("userID");
            URL url = new URL(query);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            JSONObject res=new JSONObject(reader.readLine());

            eventstatus =  res.getJSONArray("eventstatus");
            finish++;
        }catch( Exception e) {
            e.printStackTrace();
        }


        return eventstatus;

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.drawer_class_schedule_button) {
            Intent intent = new Intent(this, Class_scheduleActivity.class);
            startActivity(intent);
            finish();
        }

        if (id == R.id.drawer_class_news_button) {
            Intent intent = new Intent(this, PressNewsActivity.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.drawer_class_event_button) {
            Intent intent = new Intent(this, Event_listActivity.class);
            startActivity(intent);
            finish();
        }


        if (id == R.id.drawer_class_logout_button) {
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
