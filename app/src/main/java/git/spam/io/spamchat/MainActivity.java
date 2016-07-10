package git.spam.io.spamchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.pushy.sdk.Pushy;

public class MainActivity extends AppCompatActivity {

//    EditText message;
    Button send;
    EditText name;
    Button ok;
    ImageView refresh;
    List <UsersModel> usersModels;
    LinearLayout joiner;
    TextView Received_msg;
    ProgressDialog dialog;
    boolean more_users_available=false;
    String msg="Hello";
    private LayoutInflater inflater;
    private  List <String >RegistrationIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Pushy.listen(this);
        setContentView(R.layout.activity_main);

     //   Intent intent=new Intent(this,ChatActivity.class);
     //   startActivity(intent);
       // finish();

//        MessageStorage messageStorage=new MessageStorage(this);
//        messageStorage.open();
//        SqliteDataModel model1=new SqliteDataModel();
//        model1.setMessage("Im not happy");
//        model1.setMessagetype(1);
//        model1.setTimeStamp("54");
//        model1.setUsername("sahil");
//        messageStorage.InsertData(model1);
//        Cursor cursor=messageStorage.getData();
//        cursor.moveToFirst();
//        int indexmessage=cursor.getColumnIndex(MessageHelper.COL_MESSAGE);
//        int indexmessagetype=cursor.getColumnIndex(MessageHelper.COL_MESSAGE_TYPE);
//        int indextimestamp=cursor.getColumnIndex(MessageHelper.COL_TIME_STAMP);
//        int indexusername=cursor.getColumnIndex(MessageHelper.COL_USER_NAME);
//
//        while (!cursor.isAfterLast())
//        {
//            String Message=cursor.getString(indexmessage);
//            String timestamp=cursor.getString(indextimestamp);
//            Log.i ("Message is",Message);
//            Log.i ("Message is",timestamp);
//
//         cursor.moveToNext();
//        }
//
//        messageStorage.close();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        message= (EditText) findViewById(R.id.message);
        send= (Button) findViewById(R.id.send);
        joiner= (LinearLayout) findViewById(R.id.joiner);
        Received_msg= (TextView) findViewById(R.id.msg);
        name= (EditText) findViewById(R.id.name);
        ok= (Button) findViewById(R.id.ok);

        refresh= (ImageView) findViewById(R.id.refresh);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usersModels=new ArrayList<>();

        RegistrationIds=new ArrayList<>();

        final SharedPreferences preferences=getSharedPreferences("regids", Context.MODE_PRIVATE);
        final String id =preferences.getString("UserId", null);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (id==null)
        {
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!name.getText().toString().isEmpty()) {
                        //    Log.i("regid",registrationId);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("Username", name.getText().toString());
                        editor.commit();
                        new registerForPushNotificationsAsync().execute();
                        dialog = new ProgressDialog(MainActivity.this);
                        dialog.setMessage("Registering for device");
                        dialog.setIndeterminate(true);
                        dialog.show();

                    }else{
                        name.setError("Valid Name plzz");
                    }
                }
            });

        }else
        {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Fetching Available Users");
            dialog.setIndeterminate(true);
            dialog.show();

            UsersModel model=new UsersModel();

            model.setUserType(UsersModel.UserSelf);
            model.setUserId(preferences.getString("UserId", null));
            model.setUserName(preferences.getString("Username", null));
            usersModels.add(model);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("User");

//            query.findInBackground(new FindCallback<ParseObject>() {
//                @Override
//                public void done(final List<ParseObject> objects, ParseException e) {
//
//                    if (e==null) {
//                        for (ParseObject object1 : objects) {
//                            if (!object1.getString("UserId").equalsIgnoreCase(preferences.getString("UserId", null))) {
//                                more_users_available = true;
//                                UsersModel model = new UsersModel();
//                                model.setUserName(object1.getString("UserName"));
//                                model.setUserId(object1.getString("UserId"));
//                                model.setUserType(UsersModel.UserOther);
//                                usersModels.add(model);
//                                View v = inflater.inflate(R.layout.available_users, null);
//                                ((TextView) v.findViewById(R.id.available)).setText(object1.getString("UserName") + " is available");
//                                joiner.addView(v);
//                            }
//                        }
//                    }else{
//                        dialog.dismiss();
//                    }
//
//                    dialog.dismiss();
//
//                    if (!more_users_available) {
//                        View v = inflater.inflate(R.layout.available_users, null);
//                        ((TextView) v.findViewById(R.id.available)).setText("No one is available please refresh");
//                        joiner.addView(v);
//                        send.setClickable(false);
//                    } else {
//                        send.setClickable(true);
//                    }
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            joiner.setText(TotalUsers +" are availabel");
//                            dialog.dismiss();
////                            message.setHint("Type message here");
////                            message.setVisibility(View.VISIBLE);
//                            send.setVisibility(View.VISIBLE);
//                            joiner.setVisibility(View.VISIBLE);
//                            refresh.setVisibility(View.VISIBLE);
//                            Received_msg.setVisibility(View.VISIBLE);
//                            name.setVisibility(View.INVISIBLE);
//                            ok.setVisibility(View.INVISIBLE);
//
//                        }
//                    });
//
//                }
//            });

            Intent intent=new Intent(MainActivity.this,ChatActivity.class);
         //   intent.putParcelableArrayListExtra("UserModels", (ArrayList<? extends Parcelable>) usersModels);
            intent.putExtra("frommain", false);
            startActivity(intent);

        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this,ChatActivity.class);
                intent.putParcelableArrayListExtra("UserModels", (ArrayList<? extends Parcelable>) usersModels);
                intent.putExtra("frommain", true);
                startActivity(intent);
         //       finish();

                }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

  //              TotalUsers="";

                joiner.removeAllViews();
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Fetching Available Users");
                dialog.setIndeterminate(true);
                dialog.show();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject object1:objects)
                        {
                            if (!object1.getString("UserId").equalsIgnoreCase(preferences.getString("UserId", null)))
                            {
                                UsersModel model=new UsersModel();
                                model.setUserName(object1.getString("UserName"));
                                model.setUserId(object1.getString("UserId"));
                                model.setUserType(UsersModel.UserOther);
                                usersModels.add(model);
                                more_users_available=true;
                               // TotalUsers+=object1.getString("UserName") + " , ";
                                View v = inflater.inflate(R.layout.available_users, null);
                                ((TextView)v.findViewById(R.id.available)).setText(object1.getString("UserName") + " is available");
                                joiner.addView(v);
                            }
                        }

                        dialog.dismiss();
                        if (!more_users_available){
                            View v = inflater.inflate(R.layout.available_users, null);
                            ((TextView)v.findViewById(R.id.available)).setText("No one is available please refresh");
                            joiner.addView(v);
                            send.setClickable(false);
                        }else{
                            send.setClickable(true);
                        }
//                        joiner.setText(TotalUsers + " are availabel");
                    }
                });
            }
        });

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else{
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PushyPushRequest
    {
        public Object data;
        public String[] registration_ids;

        public PushyPushRequest(Object data, String[] registrationIDs)
        {
            this.data = data;
            this.registration_ids = registrationIDs;
        }
    }

    private class registerForPushNotificationsAsync extends AsyncTask<Void, Void, Exception>
    {
        protected Exception doInBackground(Void... params)
        {
            try
            {
                // Acquire a unique registration ID for this device
                SharedPreferences preferences=getSharedPreferences("regids", Context.MODE_PRIVATE);
                    String registrationId = Pushy.register(getApplicationContext());

                    //    Log.i("regid",registrationId);

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("UserId", registrationId);
                    editor.commit();

                    UsersModel model=new UsersModel();

                    model.setUserType(UsersModel.UserSelf);
                    model.setUserId(preferences.getString("UserId", null));
                    model.setUserName(preferences.getString("Username", null));
                    usersModels.add(model);


                // Send the registration ID to your backend server and store it for later
                    sendRegistrationIdToBackendServer(registrationId);

            }
            catch( Exception exc )
            {
                // Return exc to onPostExecute
                return exc;
            }

            // We're good
            return null;
        }

        @Override
        protected void onPostExecute(Exception exc)
        {
            // Failed?
            if ( exc != null )
            {
                // Show error as toast message
                Toast.makeText(getApplicationContext(), exc.toString(), Toast.LENGTH_LONG).show();
                return;
            }else {
                final SharedPreferences preferences = getSharedPreferences("regids", Context.MODE_PRIVATE);
                final ParseObject object=new ParseObject("User");
                object.put("UserId",preferences.getString("UserId", null));
                object.put("UserName", name.getText().toString());

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e==null) {
                                    dialog.dismiss();
                                    dialog.setMessage("Fetching Available Users");
                                    dialog.show();

                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(final List<ParseObject> objects, ParseException e) {

                                                    for (ParseObject object1:objects)
                                                    {
                                                        if (!object1.getString("UserId").equalsIgnoreCase(preferences.getString("UserId", null)))
                                                        {
                                                            more_users_available=true;
                                                            UsersModel model=new UsersModel();
                                                            model.setUserName(object1.getString("UserName"));
                                                            model.setUserId(object1.getString("UserId"));
                                                            RegistrationIds.add(object1.getString("UserId"));
                                                            model.setUserType(UsersModel.UserOther);
                                                            usersModels.add(model);
                                                            View v = inflater.inflate(R.layout.available_users, null);
                                                            ((TextView)v.findViewById(R.id.available)).setText(object1.getString("UserName") + " is available");
                                                            joiner.addView(v);
                                                        }
                                                    }
                                            sendSamplePush(preferences.getString("Username", null), preferences.getString("UserId", null));

                                            dialog.dismiss();
                                            if (!more_users_available){
                                                View v = inflater.inflate(R.layout.available_users, null);
                                                ((TextView)v.findViewById(R.id.available)).setText("No one is available please refresh");
                                                joiner.addView(v);
                                                send.setClickable(false);
                                            }else{
                                                send.setClickable(true);
                                            }


                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
//                                                    AnotherUserName= objects.get(0).getString("UserName");
//                                                    joiner.setText(TotalUsers +" are availabel");
                                                    dialog.dismiss();
                                                  //  message.setHint("Type message here");
                                                  //  message.setVisibility(View.VISIBLE);
                                                    send.setVisibility(View.VISIBLE);
                                                    refresh.setVisibility(View.VISIBLE);
                                                    name.setVisibility(View.INVISIBLE);
                                                    ok.setVisibility(View.INVISIBLE);
                                                    joiner.setVisibility(View.VISIBLE);
                                                    Received_msg.setVisibility(View.VISIBLE);
                                                }
                                            });

                                        }
                                    });

                        }else
                        {
                            e.printStackTrace();
                        }
                    }
                });


            }
            // Succeeded, do something to alert the user
        }

        // Example implementation
        void sendRegistrationIdToBackendServer(String registrationId) throws Exception
        {
            // The URL to the function in your backend API that stores registration IDs
            URL sendRegIdRequest = new URL("https://2cdfd41c0681ea90e21f938d19e1733cfc76621705fdd672dda6669464fc4020/register/device?registration_id=" + registrationId);

            // Send the registration ID by executing the GET request
            sendRegIdRequest.openConnection();

        }
    }

    public void sendSamplePush(String username,String userid)
    {

        // Set payload (any object, it will be serialized to JSON)
        Map<String, String> payload = new HashMap<String, String>();

        // Add test message
//        Log.i ("userid",userid);

        payload.put("message", username +" has joined the discussion ");


        payload.put("userid", userid);
        payload.put("user_name", "anon123");

//        payload.put("extraName",username);


        // Prepare the push request
        final MainActivity.PushyPushRequest push = new MainActivity.PushyPushRequest(payload, RegistrationIds.toArray( new String[RegistrationIds.size()] ));
        // Try sending the push notification
        // Try sending the push notification
        new AsyncTask<Object,Object,Object>(){
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    PushiAPI.sendPush(push);
                } catch (Exception e) {
                    Log.i("erroris",e.getMessage());
                }
                return null;
            }
        }.execute();

    }
}
