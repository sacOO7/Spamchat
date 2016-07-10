package git.spam.io.spamchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by sachin on 25/1/16.
 */
public class MessageStorage {

    private SQLiteDatabase mDatabase;
    private MessageHelper messageHelper;
    private Context mContext;

    MessageStorage(Context context)
    {
        mContext=context;
        messageHelper=new MessageHelper(mContext);
    }

    public  void  open()
    {
        mDatabase=messageHelper.getWritableDatabase();
    }

    public void close()
    {
        mDatabase.close();
    }

    public void InsertData(SqliteDataModel model)
    {
        mDatabase.beginTransaction();
        ContentValues contentValues=new ContentValues();
        contentValues.put(MessageHelper.COL_MESSAGE,model.getMessage());
        contentValues.put(MessageHelper.COL_MESSAGE_TYPE,model.getMessagetype());
        contentValues.put(MessageHelper.COL_TIME_STAMP,model.getTimeStamp());
        contentValues.put(MessageHelper.COL_USER_NAME,model.getUsername());
        mDatabase.insert(MessageHelper.TableNameInbox,null,contentValues);
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public Cursor getData ()
    {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM (SELECT * FROM "+MessageHelper.TableNameInbox+" ORDER BY Id DESC LIMIT 100) ORDER BY Id ASC",null);
        return cursor;
    }

}
