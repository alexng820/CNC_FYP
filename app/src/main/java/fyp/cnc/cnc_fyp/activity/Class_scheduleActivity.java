package fyp.cnc.cnc_fyp.activity;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fyp.cnc.cnc_fyp.helper.AppController;
import fyp.cnc.cnc_fyp.helper.SQLiteHandler;
import fyp.cnc.cnc_fyp.helper.SessionManager;


public class Class_scheduleActivity extends Class_scheduleAbstract {
    private static final String URL_SEARCHSECTION = "http://35.167.144.165/searchsection.php";;
    private SQLiteHandler db;
    JSONArray section_list=null;
    JSONObject term=null;
    public JSONArray getsection(){
        JSONArray section_list=null;
        db = new SQLiteHandler(getApplicationContext());
        HttpURLConnection urlConnection;

        try {
            URL url = new URL(URL_SEARCHSECTION+"?userID="+db.getUserDetails().get("userID"));
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            JSONObject jsobject = new JSONObject(reader.readLine());

            section_list = jsobject.getJSONArray("section");

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {

        }


        return section_list;

    }
    public JSONObject getterm(){
        JSONObject term=null;
        db = new SQLiteHandler(getApplicationContext());
        HttpURLConnection urlConnection;

        try {
            URL url = new URL(URL_SEARCHSECTION+"?userID="+db.getUserDetails().get("userID"));
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            JSONObject jsobject = new JSONObject(reader.readLine());

            term = jsobject.getJSONObject("term");

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {

        }


        return term;

    }
    private Runnable mutiThread = new Runnable() {
        public void run() {
            // 運行網路連線的程式
            section_list = getsection();
            term=getterm();
        }
    };
    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        Calendar startTime;
        Calendar endTime;


        WeekViewEvent event;
        Thread thread = new Thread(mutiThread);
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int term_start_year=0;
        int term_start_month=0;
        int term_start_week=0;
        int term_end_year=0;
        int term_end_month=0;
        int term_end_week=0;

        try {
            term_start_year=term.getInt("start_year");
            term_start_month=term.getInt("start_month");
            term_start_week=term.getInt("start_week");
            term_end_year=term.getInt("end_year");
            term_end_month=term.getInt("end_month");
            term_end_week=term.getInt("end_week");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(term_start_year<=newYear&&newYear<=term_end_year&&term_start_month<=newMonth&&newMonth<=term_end_month) {
            for (int i = 0; i < section_list.length(); i++) {
                String section_code = null;
                int weekday = 0;
                String starthr = null;
                String endhr = null;
                String startmin = null;
                String endmin = null;
                String location = null;
                String tearcher = null;
                try {
                    JSONObject section = section_list.getJSONObject(i);
                    section_code = section.getString("section_code");
                    weekday = section.getInt("weekday");
                    starthr = section.getString("starttime").split(":")[0];
                    endhr = section.getString("endtime").split(":")[0];
                    startmin = section.getString("starttime").split(":")[1];
                    endmin = section.getString("endtime").split(":")[1];
                    location = section.getString("location");
                    tearcher = section.getString("tearcher");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, newYear);
                cal.set(Calendar.MONTH, newMonth - 1);
                int ndays = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);

                for (int j = 1; j <= ndays - 1; j++) {

                    if(term_start_year>=newYear&&term_start_month>=newMonth&&term_start_week>=j){
                        continue;
                    }
                    if(term_end_year<=newYear&&term_end_month<=newMonth&&term_end_week<j){
                        continue;
                    }

                    startTime = Calendar.getInstance();
                    startTime.set(Calendar.WEEK_OF_MONTH, j);
                    startTime.set(Calendar.DAY_OF_WEEK, weekday);
                    startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(starthr));
                    startTime.set(Calendar.MINUTE, Integer.parseInt(startmin));
                    startTime.set(Calendar.MONTH, newMonth - 1);
                    startTime.set(Calendar.YEAR, newYear);
                    endTime = (Calendar) startTime.clone();
                    endTime.set(Calendar.WEEK_OF_MONTH, j);
                    endTime.set(Calendar.DAY_OF_WEEK, weekday);
                    endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endhr));
                    endTime.set(Calendar.MINUTE, Integer.parseInt(endmin));
                    event = new WeekViewEvent(1, section_code + "_" + location, tearcher, startTime, endTime);
                    events.add(event);

                }
            }
        }

//            for(int i=1;i<=4;i++) {
//                startTime = Calendar.getInstance();
//                startTime.set(Calendar.WEEK_OF_MONTH, i);
//                startTime.set(Calendar.DAY_OF_WEEK, 2);
//                startTime.set(Calendar.HOUR_OF_DAY, 11);
//                startTime.set(Calendar.MINUTE, 0);
//                startTime.set(Calendar.MONTH, newMonth - 1);
//                startTime.set(Calendar.YEAR, newYear);
//                endTime = (Calendar) startTime.clone();
//                endTime.set(Calendar.WEEK_OF_MONTH, i);
//                endTime.set(Calendar.DAY_OF_WEEK, 2);
//                endTime.set(Calendar.HOUR_OF_DAY, 13);
//                endTime.set(Calendar.MINUTE, 0);
//                event = new WeekViewEvent(1, "COMP S382F - P02 - C0411","SO WING CHEUNG RAYMOND", startTime, endTime);
//                events.add(event);
//
//                startTime = Calendar.getInstance();
//                startTime.set(Calendar.WEEK_OF_MONTH, i);
//                startTime.set(Calendar.DAY_OF_WEEK, 5);
//                startTime.set(Calendar.HOUR_OF_DAY, 9);
//                startTime.set(Calendar.MINUTE, 0);
//                startTime.set(Calendar.MONTH, newMonth - 1);
//                startTime.set(Calendar.YEAR, newYear);
//                endTime = (Calendar) startTime.clone();
//                endTime.set(Calendar.WEEK_OF_MONTH, i);
//                endTime.set(Calendar.DAY_OF_WEEK, 5);
//                endTime.set(Calendar.HOUR_OF_DAY, 11);
//                endTime.set(Calendar.MINUTE, 0);
//                event = new WeekViewEvent(1, "COMP S382F - L02 - G29","SO WING CHEUNG RAYMOND", startTime, endTime);
//                events.add(event);
//
//                startTime = Calendar.getInstance();
//                startTime.set(Calendar.WEEK_OF_MONTH, i);
//                startTime.set(Calendar.DAY_OF_WEEK, 2);
//                startTime.set(Calendar.HOUR_OF_DAY, 14);
//                startTime.set(Calendar.MINUTE, 0);
//                startTime.set(Calendar.MONTH, newMonth - 1);
//                startTime.set(Calendar.YEAR, newYear);
//                endTime = (Calendar) startTime.clone();
//                endTime.set(Calendar.WEEK_OF_MONTH, i);
//                endTime.set(Calendar.DAY_OF_WEEK, 2);
//                endTime.set(Calendar.HOUR_OF_DAY, 16);
//                endTime.set(Calendar.MINUTE, 0);
//                event = new WeekViewEvent(10, "COMP S380F - P03 - C0415","LEE LAP KEI", startTime, endTime);
//                events.add(event);
//
//                startTime = Calendar.getInstance();
//                startTime.set(Calendar.WEEK_OF_MONTH, i);
//                startTime.set(Calendar.DAY_OF_WEEK, 6);
//                startTime.set(Calendar.HOUR_OF_DAY, 9);
//                startTime.set(Calendar.MINUTE, 0);
//                startTime.set(Calendar.MONTH, newMonth - 1);
//                startTime.set(Calendar.YEAR, newYear);
//                endTime = (Calendar) startTime.clone();
//                endTime.set(Calendar.WEEK_OF_MONTH, i);
//                endTime.set(Calendar.DAY_OF_WEEK, 6);
//                endTime.set(Calendar.HOUR_OF_DAY, 11);
//                endTime.set(Calendar.MINUTE, 0);
//                event = new WeekViewEvent(10, "COMP S380F - L01 - G29","LEE LAP KEI", startTime, endTime);
//                events.add(event);
//
//                startTime = Calendar.getInstance();
//                startTime.set(Calendar.WEEK_OF_MONTH, i);
//                startTime.set(Calendar.DAY_OF_WEEK, 3);
//                startTime.set(Calendar.HOUR_OF_DAY, 11);
//                startTime.set(Calendar.MINUTE, 0);
//                startTime.set(Calendar.MONTH, newMonth - 1);
//                startTime.set(Calendar.YEAR, newYear);
//                endTime = (Calendar) startTime.clone();
//                endTime.set(Calendar.WEEK_OF_MONTH, i);
//                endTime.set(Calendar.DAY_OF_WEEK, 3);
//                endTime.set(Calendar.HOUR_OF_DAY, 13);
//                endTime.set(Calendar.MINUTE, 0);
//                event = new WeekViewEvent(10, "COMP S412F - P02 - C0411","TSE Ka Wing", startTime, endTime);
//                events.add(event);
//
//                startTime = Calendar.getInstance();
//                startTime.set(Calendar.WEEK_OF_MONTH, i);
//                startTime.set(Calendar.DAY_OF_WEEK, 6);
//                startTime.set(Calendar.HOUR_OF_DAY, 14);
//                startTime.set(Calendar.MINUTE, 0);
//                startTime.set(Calendar.MONTH, newMonth - 1);
//                startTime.set(Calendar.YEAR, newYear);
//                endTime = (Calendar) startTime.clone();
//                endTime.set(Calendar.WEEK_OF_MONTH, i);
//                endTime.set(Calendar.DAY_OF_WEEK, 5);
//                endTime.set(Calendar.HOUR_OF_DAY, 16);
//                endTime.set(Calendar.MINUTE, 0);
//                event = new WeekViewEvent(10, "COMP S412F - L01 - C0411","NG Sin Chun", startTime, endTime);
//                events.add(event);
//            }
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 4);
//        startTime.set(Calendar.MINUTE, 20);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.set(Calendar.HOUR_OF_DAY, 5);
//        endTime.set(Calendar.MINUTE, 0);
//        event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 5);
//        startTime.set(Calendar.MINUTE, 30);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 2);
//        endTime.set(Calendar.MONTH, newMonth-1);
//        event = new WeekViewEvent(2, getEventTitle(startTime), startTime, endTime);
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 5);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth - 1);
//        startTime.set(Calendar.YEAR, newYear);
//        startTime.add(Calendar.DATE, 1);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        endTime.set(Calendar.MONTH, newMonth - 1);
//        event = new WeekViewEvent(3, getEventTitle(startTime), startTime, endTime);
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 15);
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(4, getEventTitle(startTime), startTime, endTime);
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 1);
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, startTime.getActualMaximum(Calendar.DAY_OF_MONTH));
//        startTime.set(Calendar.HOUR_OF_DAY, 15);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
//        events.add(event);
//
//        //AllDay event
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 0);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 23);
//        event = new WeekViewEvent(7, getEventTitle(startTime),null, startTime, endTime);
//        events.add(event);
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 8);
//        startTime.set(Calendar.HOUR_OF_DAY, 2);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.set(Calendar.DAY_OF_MONTH, 10);
//        endTime.set(Calendar.HOUR_OF_DAY, 23);
//        event = new WeekViewEvent(8, getEventTitle(startTime),null, startTime, endTime);
//        events.add(event);
//
//        // All day event until 00:00 next day
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 10);
//        startTime.set(Calendar.HOUR_OF_DAY, 0);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.SECOND, 0);
//        startTime.set(Calendar.MILLISECOND, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.set(Calendar.DAY_OF_MONTH, 11);
//        event = new WeekViewEvent(8, getEventTitle(startTime), null, startTime, endTime);
//        events.add(event);

        return events;
    }
}
