package git.spam.io.spamchat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * Created by sachin on 22/1/16.
 */
public class PushReceiver extends BroadcastReceiver
{
    String notificationDesc = null;
    String username="anonymous";
    HashMap <String,String> map=new HashMap<>();
    private EventBus bus = EventBus.getDefault();
    private Set<String> RegistrationIds;

    @Override
    public void onReceive(Context context, Intent intent)
    {



        if ( intent.getStringExtra("message") != null )
        {
            notificationDesc = intent.getStringExtra("message");
            username=intent.getStringExtra("user_name");

            if (intent.getStringExtra("userid")!=null)
            {
//                Log.i ("userid",intent.getStringExtra("userid"));
                SharedPreferences preferences=context.getSharedPreferences("regids", Context.MODE_PRIVATE);
                RegistrationIds=preferences.getStringSet("registration_set",null);
                if(RegistrationIds==null)
                {
                    RegistrationIds=new HashSet<>();
                }
                if (!RegistrationIds.contains(intent.getStringExtra("userid"))) {
                    RegistrationIds.add(intent.getStringExtra("userid"));
                    preferences.edit().putStringSet("registration_set", RegistrationIds).commit();
                }

            }

            map.put("message",notificationDesc);
            map.put("user_name",username);
            bus.post(new MessageEvent(map));
//            Log.i("messageis", notificationDesc);
        }
        NotificationCompat.Builder mBuilder =
        (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(username)
                .setOngoing(false)
                .setContentText(notificationDesc);

        mBuilder.setAutoCancel(true);
        mBuilder.setOnlyAlertOnce(true);

        Intent resultant=new Intent(context,ChatActivity.class);
                        PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                0,
                                resultant,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

        mBuilder.setContentIntent(resultPendingIntent);

        MessageStorage messageStorage=new MessageStorage(context);
        messageStorage.open();
        SqliteDataModel model1=new SqliteDataModel();
        model1.setMessage(notificationDesc);
        model1.setMessagetype(2);
        model1.setUsername(username);
        model1.setTimeStamp(String.valueOf(new Date().getTime()));
        if (username.equalsIgnoreCase("anon123"))
        {
            model1.setMessagetype(3);
            messageStorage.InsertData(model1);
            messageStorage.close();
        }else {
            messageStorage.InsertData(model1);
            messageStorage.close();

            //  model1.setMessagetype(1);
            //  model1.setTimeStamp("54");
            //  model1.setUsername("sahil");


            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            notificationManager.notify(123, mBuilder.build());
        }

    }
}