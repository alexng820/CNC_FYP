package fyp.cnc.cnc_fyp.helper;

//Class for handling database operations

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();

    //All Static variables
    //Database version
    private static final int DATABASE_VERSION = 1;

    //Database name
    private static final String DATABASE_NAME = "cncfyp";

    //Login table name
    private static final String TABLE_USER = "user";

    //login table columns names
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_STATUS = "status";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Createing tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "(" + KEY_EMAIL + " VARCHAR(50) PRIMARY KEY, " + KEY_ROLE + " VARCHAR(25), " + KEY_STATUS + " VARCHAR(25))";
        db.execSQL(CREATE_LOGIN_TABLE);;

        Log.d(TAG, "Database table created.");
    }

    //Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        //Create table
        onCreate(db);
    }

    //Storing user details in database
    public void addUser(String userEmail, String userRole, String userStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, userEmail);
        values.put(KEY_ROLE, userRole);
        values.put(KEY_STATUS, userStatus);

        //Inserting row
        long id = db.insert(TABLE_USER, null, values);
        db.close();

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    //Getting user data from database
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("userEmail", cursor.getString(1));
            user.put("userRole", cursor.getString(2));
            user.put("userStatus", cursor.getString(3));
        }
        cursor.close();
        db.close();

        Log.d(TAG, "Fetching user from sqlite: " + user.toString());

        //Return user
        return user;
    }

    //Delete all rows
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user data from sqlite");
    }
}
