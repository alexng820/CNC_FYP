package fyp.cnc.cnc_fyp.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import fyp.cnc.cnc_fyp.Globals;
import fyp.cnc.cnc_fyp.R;
import fyp.cnc.cnc_fyp.activity.Class_scheduleActivity;

//Service that check how much time left for next class and notify user if he needs to set off to school

public class ClassAlertManager extends Service {
    public static final long NOTIFY_INTERVAL = 15 * 60 * 1000; // 15 minutes
    private static final String TAG = ClassAlertManager.class.getSimpleName();
    private int secondsTillNextLesson;
    private String nextCourseCode;

    //SQLite database
    private SQLiteHandler db;
    //Run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    //Timer handling
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        //Cancel timer if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            //Create new timer
            mTimer = new Timer();
        }
        //Schedule task
        mTimer.scheduleAtFixedRate(new ClassChecker(), 0, NOTIFY_INTERVAL);
    }

    //Check how much time till next lesson starts
    private void checkClass() {
        String userID = db.getUserDetails().get("userID");
        String URL_SEARCHSECTION = "http://35.167.144.165/searchsection.php?userID=" + userID;

        //Tag used to cancel the request
        String tag_string_req = "req_class_info";

        StringRequest strRequest = new StringRequest(Request.Method.GET, URL_SEARCHSECTION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Get the class information from server
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray courseList = jsonObject.getJSONArray("section");
                    if (courseList != null) {
                        //Loop through all course information
                        for (int i = 0; i < courseList.length(); i++) {
                            JSONObject course = courseList.getJSONObject(i);
                            int weekDay = course.getInt("weekday");
                            int startHour = Integer.parseInt(course.getString("starttime").split(":")[0]);
                            int startMin = Integer.parseInt(course.getString("starttime").split(":")[1]);

                            //Get current time
                            Calendar dateTime = Calendar.getInstance();
                            while (checkWeekday(dateTime) != weekDay) {
                                //Get date and time of next lesson
                                dateTime.add(Calendar.DATE, 1);
                            }
                            dateTime.set(Calendar.HOUR_OF_DAY, startHour);
                            dateTime.set(Calendar.MINUTE, startMin);
                            dateTime.set(Calendar.SECOND, 0);
                            dateTime.set(Calendar.MILLISECOND, 0);
                            //Get next lesson Unix time
                            int nextLessonUnixTime = (int) (dateTime.getTimeInMillis() / 1000L);
                            //Get current Unix time
                            int unixTime = (int) (System.currentTimeMillis() / 1000L);

                            //If lesson already passed, proceed to next lesson
                            if ((nextLessonUnixTime - unixTime) < 0) {
                                continue;
                            }

                            //If next lesson of this course is the closest, register it in global variables
                            if ((secondsTillNextLesson == 0) || ((nextLessonUnixTime - unixTime) < secondsTillNextLesson)) {
                                secondsTillNextLesson = (nextLessonUnixTime - unixTime);
                                nextCourseCode = course.getString("section_code");
                            }
                        }
                    }
                } catch (JSONException e) {
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
            }
        });

        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, tag_string_req);
    }

    //Check day of week in number form
    private int checkWeekday(Calendar dateTime) {
        int day = dateTime.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                return 0;
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
        }
        return 0;
    }

    private void showNotification() {
        //Build the notification
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Class coming soon!")
                .setContentText("Class " + nextCourseCode + " starting in " + (secondsTillNextLesson / 60) + " mins.")
                .setPriority(Notification.PRIORITY_HIGH);
        //Set the onClick intent of the notification
        Intent resultIntent = new Intent(this, Class_scheduleActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Class_scheduleActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private class ClassChecker extends TimerTask {
        @Override
        public void run() {
            //Run task on new thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    checkClass();
                    if ((secondsTillNextLesson != 0) && (Globals.secondsToSchool != 0)) {
                        int minutesBuffer = ((secondsTillNextLesson - Globals.secondsToSchool) / 60);
                        //If user have 15 minutes of less buffer to go to school from his location, send a notification
                        if ((-15 < minutesBuffer) && (minutesBuffer <= 15)) {
                            showNotification();
                        }
                    }
                }
            });
        }
    }
}
