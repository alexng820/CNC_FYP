package fyp.cnc.cnc_fyp.helper;

//Class for maintains session data across the app

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    //Shared preferences file name
    private static final String PREF_NAME = "AppLogin";
    //Shared Preferences key name
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    //LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
    //Shared Preferences
    private SharedPreferences pref;
    private Editor editor;

    public SessionManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        //Commit
        editor.commit();

        Log.d(TAG, "User login session modified.");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}