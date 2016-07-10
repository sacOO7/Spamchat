package git.spam.io.spamchat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sachin on 25/1/16.
 */
public class MessageHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    public static final String DB_NAME = "messages.db";


    public  static String TableNameInbox="Message";


    public static final String COL_MESSAGE = "chatmessage";
    public  static final String COL_TIME_STAMP = "timestamp";
    public  static final String COL_MESSAGE_TYPE = "user";
    public  static String COL_USER_NAME="username";


    private static final String DB_INBOX_MESSAGE_TABLE=
            "CREATE TABLE "+TableNameInbox+
                    "( " + "Id" + " INTEGER PRIMARY KEY AUTOINCREMENT " + " ,"
                    //+ "UserName" + " TEXT " + " ,"
                    + COL_MESSAGE  + " TEXT " + " ,"
                    + COL_TIME_STAMP  + " TEXT " + " ,"
                    + COL_USER_NAME  + " TEXT " + " ,"
                    + COL_MESSAGE_TYPE +" INTEGER "
                    + ")";

    public MessageHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_INBOX_MESSAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
