package com.sanjay.openfire.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sanjay.openfire.utilies.Logger;


/**
 * Created by RAJ ARYAN on 02/08/19.
 */
public class DatabaseHandlerClass extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHandlerClass.class.getSimpleName();
    public static SQLiteDatabase database;
    private Context context;


    public DatabaseHandlerClass(Context mContext) {
        super(mContext, DBConstants.DB_NAME, null, DBConstants.VERSION);
        context = mContext;
        try {

            initDatabase();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onCreate method", e);
        }
    }

    public void closeCursor(Cursor cursor) {
        if (cursor != null)
            cursor.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createMessageTable(sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String upgradeQuery = "ALTER TABLE tbl_messages ADD COLUMN read_status TEXT DEFAULT '0'";
        if (newVersion>oldVersion)
            sqLiteDatabase.execSQL(upgradeQuery);

    }



    public void initDatabase() {
        if (database == null || !database.isOpen() || database.isReadOnly())
            database = this.getWritableDatabase();

    }

    public void closeDatabase() {
        /*if (database!=null)
            database.close();*/
    }




    private void createMessageTable(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE IF NOT EXISTS tbl_messages (" +
                "uuid TEXT," +
                "message TEXT," +
                "from_user TEXT," +
                "to_user TEXT," +
                "message_date TEXT," +
                "status integer DEFAULT 2 )";
        Logger.logD(TAG, "Database creation query :" + query);
        sqLiteDatabase.execSQL(query);
    }


    public SQLiteDatabase getWriteDb() {
        if (database != null && database.isOpen())
            return database;
        else
            return getWritableDatabase();
    }


}
