package git.spam.io.spamchat;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArraySet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import de.greenrobot.event.EventBus;
import in.co.madhur.chatbubblesdemo.AndroidUtilities;
import in.co.madhur.chatbubblesdemo.ChatListAdapter;
import in.co.madhur.chatbubblesdemo.Constants;
import in.co.madhur.chatbubblesdemo.NotificationCenter;
import in.co.madhur.chatbubblesdemo.model.ChatMessage;
import in.co.madhur.chatbubblesdemo.model.Status;
import in.co.madhur.chatbubblesdemo.model.UserType;
import in.co.madhur.chatbubblesdemo.widgets.Emoji;
import in.co.madhur.chatbubblesdemo.widgets.EmojiView;
import in.co.madhur.chatbubblesdemo.widgets.SizeNotifierRelativeLayout;


public class ChatActivity extends AppCompatActivity implements SizeNotifierRelativeLayout.SizeNotifierRelativeLayoutDelegate, NotificationCenter.NotificationCenterDelegate {

    private ListView chatListView;
    private EditText chatEditText1;
    Toolbar toolbar;
    Set<String> RegistrationIds;
    private ArrayList  <ChatMessage> chatMessages;
    private ImageView enterChatView1, emojiButton;
    private ChatListAdapter listAdapter;
    private EmojiView emojiView;
    List<UsersModel> usersModelList;
    private SizeNotifierRelativeLayout sizeNotifierRelativeLayout;
    private boolean showingEmoji;
    private EventBus bus = EventBus.getDefault();
    private int keyboardHeight;
    private boolean keyboardVisible;
    SharedPreferences preferences;
    private WindowManager.LayoutParams windowLayoutParams;


    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press

                EditText editText = (EditText) v;

                if(v==chatEditText1)
                {
                    sendMessage(editText.getText().toString(), UserType.OTHER);
                }

                chatEditText1.setText("");

                return true;
            }
            return false;

        }
    };

    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v==enterChatView1)
            {
                sendMessage(chatEditText1.getText().toString(), UserType.OTHER);
            }

            chatEditText1.setText("");

        }
    };

    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (chatEditText1.getText().toString().equals("")) {

            } else {
                enterChatView1.setImageResource(R.drawable.ic_chat_send);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length()==0){
                enterChatView1.setImageResource(R.drawable.ic_chat_send);
            }else{
                enterChatView1.setImageResource(R.drawable.ic_chat_send_active);
            }
        }
    };
    private ProgressDialog dialog1;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        setContentView(R.layout.activity_chat);

        AndroidUtilities.statusBarHeight = getStatusBarHeight();

        chatMessages = new ArrayList<>();

        RegistrationIds= new HashSet<>();

        MessageStorage messageStorage=new MessageStorage(this);
        messageStorage.open();

        Cursor cursor=messageStorage.getData();
        cursor.moveToFirst();
        int indexmessage=cursor.getColumnIndex(MessageHelper.COL_MESSAGE);
        int indexmessagetype=cursor.getColumnIndex(MessageHelper.COL_MESSAGE_TYPE);
        int indextimestamp=cursor.getColumnIndex(MessageHelper.COL_TIME_STAMP);
        int indexusername=cursor.getColumnIndex(MessageHelper.COL_USER_NAME);

        while (!cursor.isAfterLast())
        {
            String Message=cursor.getString(indexmessage);
            String timestamp=cursor.getString(indextimestamp);
            int messagetype=cursor.getInt(indexmessagetype);
            String username=cursor.getString(indexusername);

            ChatMessage message=new ChatMessage();
            message.setUsername(username);
            message.setMessageStatus(Status.DELIVERED);
            message.setMessageText(Message);
            message.setMessageTime(Long.parseLong(timestamp));
            if (messagetype==1) {
                message.setUserType(UserType.OTHER);
            }
            else if (messagetype==2){
                message.setUserType(UserType.SELF);
            }else
            {
                message.setUserType(UserType.PRO);
            }
            chatMessages.add(message);
            cursor.moveToNext();
        }

        messageStorage.close();


        chatListView = (ListView) findViewById(R.id.chat_list_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences=getSharedPreferences("regids", Context.MODE_PRIVATE);


        if (getIntent().getBooleanExtra("frommain",false)) {
            usersModelList = getIntent().getParcelableArrayListExtra("UserModels");
            for (UsersModel model :usersModelList)
            {
                RegistrationIds.add(model.getUserId());
            }
            RegistrationIds.remove(preferences.getString("UserId", null));

            SharedPreferences.Editor editor=preferences.edit();
            editor.putStringSet("registration_set", RegistrationIds);
            editor.commit();

        }else{

             RegistrationIds=preferences.getStringSet("registration_set",null);
             RegistrationIds.remove(preferences.getString("UserId", null));

//            usersModelList=new ArrayList<>();
//            dialog = new ProgressDialog(ChatActivity.this);
//            dialog.setMessage("Fetching Available Users");
//            dialog.setIndeterminate(true);
//            dialog.show();
//
//
//
//
//            UsersModel model=new UsersModel();
//            model.setUserType(UsersModel.UserSelf);
//            model.setUserId(preferences.getString("UserId", null));
//            model.setUserName(preferences.getString("Username", null));
//            usersModelList.add(model);
//
//
//            ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
//            query.findInBackground(new FindCallback<ParseObject>() {
//                @Override
//                public void done(List<ParseObject> objects, ParseException e) {
//
//                    for (ParseObject object1:objects) {
//                        if (!object1.getString("UserId").equalsIgnoreCase(preferences.getString("UserId", null))) {
//                            UsersModel model = new UsersModel();
//                            model.setUserName(object1.getString("UserName"));
//                            model.setUserId(object1.getString("UserId"));
//                            model.setUserType(UsersModel.UserOther);
//                            usersModelList.add(model);
//                        }
//                    }
//                    dialog.dismiss();
//                }
//            });


        }

//        for (UsersModel model :usersModelList)
//        {
//            Log.i ("name is",model.getUserName());
//            Log.i ("id id",model.getUserId());
//            Log.i ("Type is",String.valueOf(model.isUserType()));
//
//        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chatEditText1 = (EditText) findViewById(R.id.chat_edit_text1);
        enterChatView1 = (ImageView) findViewById(R.id.enter_chat1);

        // Hide the emoji on click of edit text
        chatEditText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showingEmoji)
                    hideEmojiPopup();
            }
        });


        emojiButton = (ImageView)findViewById(R.id.emojiButton);

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmojiPopup(!showingEmoji);
            }
        });

        listAdapter = new ChatListAdapter(chatMessages, this);
        chatListView.setAdapter(listAdapter);
        if (chatMessages.size()>0)
        {
            chatListView.setSelection(chatMessages.size()-1);
        }

        chatEditText1.setOnKeyListener(keyListener);

        enterChatView1.setOnClickListener(clickListener);

        chatEditText1.addTextChangedListener(watcher1);

        sizeNotifierRelativeLayout = (SizeNotifierRelativeLayout) findViewById(R.id.chat_layout);
        sizeNotifierRelativeLayout.delegate = this;

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    }

    private void sendMessage(String messageText,UserType userType)
    {
        if(messageText.trim().length()==0)
            return;

        ChatMessage message = new ChatMessage();
        message.setMessageStatus(Status.SENT);
        message.setMessageText(messageText);
        message.setUserType(userType);
        message.setMessageTime(new Date().getTime());
        chatMessages.add(message);

//        Log.i ("messageis",messageText);

        MessageStorage messageStorage=new MessageStorage(this);
        messageStorage.open();
        SqliteDataModel model1=new SqliteDataModel();
        model1.setMessage(message.getMessageText());
        model1.setUsername(preferences.getString("Username", null));
        model1.setTimeStamp(String.valueOf(message.getMessageTime()));
        model1.setMessagetype(1);


      //  model1.setMessagetype(1);
      //  model1.setTimeStamp("54");
      //  model1.setUsername("sahil");
        messageStorage.InsertData(model1);
        messageStorage.close();


        if(listAdapter!=null) {
            listAdapter.notifyDataSetChanged();
            chatListView.smoothScrollToPosition(chatMessages.size()-1);
        }

        sendSamplePush(messageText,null);

        // Mark message as delivered after one second

//        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },2000);

//        exec.schedule(new Runnable(){
//            @Override
//            public void run(){
//
//
//
//
//
//            }
//        }, 1, TimeUnit.SECONDS);

    }

    private Activity getActivity()
    {
        return this;
    }


    /**
     * Show or hide the emoji popup
     *
     * @param show
     */
    private void showEmojiPopup(boolean show) {
        showingEmoji = show;

        if (show) {
            if (emojiView == null) {
                if (getActivity() == null) {
                    return;
                }
                emojiView = new EmojiView(getActivity());

                emojiView.setListener(new EmojiView.Listener() {
                    public void onBackspace() {
                        chatEditText1.dispatchKeyEvent(new KeyEvent(0, 67));
                    }

                    public void onEmojiSelected(String symbol) {
                        int i = chatEditText1.getSelectionEnd();
                        if (i < 0) {
                            i = 0;
                        }
                        try {
                            CharSequence localCharSequence = Emoji.replaceEmoji(symbol, chatEditText1.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20));
                            chatEditText1.setText(chatEditText1.getText().insert(i, localCharSequence));
                            int j = i + localCharSequence.length();
                            chatEditText1.setSelection(j, j);
                        } catch (Exception e) {
                            Log.e(Constants.TAG, "Error showing emoji");
                        }
                    }
                });


                windowLayoutParams = new WindowManager.LayoutParams();
                windowLayoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
                if (Build.VERSION.SDK_INT >= 21) {
                    windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                } else {
                    windowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
                    windowLayoutParams.token = getActivity().getWindow().getDecorView().getWindowToken();
                }
                windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }

            final int currentHeight;

            if (keyboardHeight <= 0)
                keyboardHeight = spamchatapplication.getInstance().getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200));

            currentHeight = keyboardHeight;

            WindowManager wm = (WindowManager) spamchatapplication.getInstance().getSystemService(Activity.WINDOW_SERVICE);

            windowLayoutParams.height = currentHeight;
            windowLayoutParams.width = AndroidUtilities.displaySize.x;

            try {
                if (emojiView.getParent() != null) {
                    wm.removeViewImmediate(emojiView);
                }
            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
            }

            try {
                wm.addView(emojiView, windowLayoutParams);
            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
                return;
            }

            if (!keyboardVisible) {
                if (sizeNotifierRelativeLayout != null) {
                    sizeNotifierRelativeLayout.setPadding(0, 0, 0, currentHeight);
                }

                return;
            }

        }
        else {
            removeEmojiWindow();
            if (sizeNotifierRelativeLayout != null) {
                sizeNotifierRelativeLayout.post(new Runnable() {
                    public void run() {
                        if (sizeNotifierRelativeLayout != null) {
                            sizeNotifierRelativeLayout.setPadding(0, 0, 0, 0);
                        }
                    }
                });
            }
        }


    }


    /**
     * Remove emoji window
     */
    private void removeEmojiWindow() {
        if (emojiView == null) {
            return;
        }
        try {
            if (emojiView.getParent() != null) {
                WindowManager wm = (WindowManager) spamchatapplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
                wm.removeViewImmediate(emojiView);
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, e.getMessage());
        }
    }



    /**
     * Hides the emoji popup
     */
    public void hideEmojiPopup() {
        if (showingEmoji) {
            showEmojiPopup(false);
        }
    }

    /**
     * Check if the emoji popup is showing
     *
     * @return
     */
    public boolean isEmojiPopupShowing() {
        return showingEmoji;
    }



    /**
     * Updates emoji views when they are complete loading
     *
     * @param id
     * @param args
     */
    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded) {
            if (emojiView != null) {
                emojiView.invalidateViews();
            }

            if (chatListView != null) {
                chatListView.invalidateViews();
            }
        }
    }

    @Override
    public void onSizeChanged(int height) {

        Rect localRect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);

        WindowManager wm = (WindowManager) spamchatapplication.getInstance().getSystemService(Activity.WINDOW_SERVICE);
        if (wm == null || wm.getDefaultDisplay() == null) {
            return;
        }


        if (height > AndroidUtilities.dp(50) && keyboardVisible) {
            keyboardHeight = height;
            spamchatapplication.getInstance().getSharedPreferences("emoji", 0).edit().putInt("kbd_height", keyboardHeight).commit();
        }


        if (showingEmoji) {
            int newHeight = 0;

            newHeight = keyboardHeight;

            if (windowLayoutParams.width != AndroidUtilities.displaySize.x || windowLayoutParams.height != newHeight) {
                windowLayoutParams.width = AndroidUtilities.displaySize.x;
                windowLayoutParams.height = newHeight;

                wm.updateViewLayout(emojiView, windowLayoutParams);
                if (!keyboardVisible) {
                    sizeNotifierRelativeLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            if (sizeNotifierRelativeLayout != null) {
                                sizeNotifierRelativeLayout.setPadding(0, 0, 0, windowLayoutParams.height);
                                sizeNotifierRelativeLayout.requestLayout();
                            }
                        }
                    });
                }
            }
        }


        boolean oldValue = keyboardVisible;
        keyboardVisible = height > 0;
        if (keyboardVisible && sizeNotifierRelativeLayout.getPaddingBottom() > 0) {
            showEmojiPopup(false);
        } else if (!keyboardVisible && keyboardVisible != oldValue && showingEmoji) {
            showEmojiPopup(false);
        }
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    }

    /**
     * Get the system status bar height
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onPause() {
        super.onPause();

        hideEmojiPopup();
    }

    public void sendSamplePush(String msg,String extra)
    {
        // Prepare registration IDs array


        // Add your registration IDs here
//        for (UsersModel model :usersModelList)
//        {
//            if (!model.isUserType())
//            registrationIDs.add(model.getUserId());
//        }


        // Set payload (any object, it will be serialized to JSON)
        Map<String, String> payload = new HashMap<String, String>();

        // Add test message
        payload.put("message", msg);
        if (extra==null) {
            payload.put("user_name", preferences.getString("Username", null));
        }else{
            payload.put("user_name", extra);
        }

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

            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            final SharedPreferences preferences=getSharedPreferences("regids", Context.MODE_PRIVATE);
            builder.setTitle("Name");
            final EditText input=new EditText(this);
            input.setHint(preferences.getString("Username",null));
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog1 = new ProgressDialog(ChatActivity.this);
                    dialog1.setMessage("Changing your name");
                    dialog1.setIndeterminate(true);
                    dialog1.show();

                    ParseQuery<ParseObject> query=ParseQuery.getQuery("User");
                    query.whereEqualTo("UserId",preferences.getString("UserId", null));
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e==null)
                            {
                                object.put("UserName",input.getText().toString());
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {

                                        String messagetext=preferences.getString("Username", null) + " changed his name to " + input.getText().toString();
                                        sendSamplePush(messagetext, "anon123");
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("Username", input.getText().toString());
                                        editor.commit();
                                    //    usersModelList.get(0).setUserName(input.getText().toString());
                                        dialog1.dismiss();
                                        ChatMessage message=new ChatMessage();
                                        message.setUserType(UserType.PRO);
                                        message.setMessageText(messagetext);
                                        message.setMessageStatus(Status.DELIVERED);
                                        message.setMessageTime(new Date().getTime());
                                        message.setUsername("anon123");
                                        chatMessages.add(message);

                                        MessageStorage messageStorage=new MessageStorage(ChatActivity.this);
                                        messageStorage.open();
                                        SqliteDataModel model1=new SqliteDataModel();
                                        model1.setMessage(message.getMessageText());
                                        model1.setUsername("anon123");
                                        model1.setTimeStamp(String.valueOf(message.getMessageTime()));
                                        model1.setMessagetype(3);


                                        //  model1.setMessagetype(1);
                                        //  model1.setTimeStamp("54");
                                        //  model1.setUsername("sahil");
                                        messageStorage.InsertData(model1);
                                        messageStorage.close();


                                        listAdapter.notifyDataSetChanged();
                                        chatListView.smoothScrollToPosition(chatMessages.size() - 1);
                                    }
                                });
                            }else
                            {
                                dialog1.dismiss();
                                Toast.makeText(ChatActivity.this,"Network error",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

//                    Toast.makeText(ChatActivity.this,"name is"+input.getText().toString(),Toast.LENGTH_LONG).show();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();


            return true;
        }else{
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEvent(MessageEvent event){
     //   Toast.makeText(this, "Message received", Toast.LENGTH_SHORT).show();
    //    String username=event.getMap().get("user_name");
    //    String messaage=event.getMap().get("message");

       //chatMessages.get(chatMessages.size()-1).setMessageStatus(Status.DELIVERED);


        ChatMessage message = new ChatMessage();
        message.setMessageStatus(Status.SENT);
        message.setMessageText(event.getMap().get("message"));
        message.setUserType(UserType.SELF);
        message.setUsername(event.getMap().get("user_name"));

        if (event.getMap().get("user_name").equalsIgnoreCase("anon123")) {
            message.setUserType(UserType.PRO);
        }

        message.setMessageTime(new Date().getTime());
        chatMessages.add(message);







        ChatActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                listAdapter.notifyDataSetChanged();
                chatListView.smoothScrollToPosition(chatMessages.size()-1);
            }
        });

     //   Received_msg.setText();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
       super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
}
