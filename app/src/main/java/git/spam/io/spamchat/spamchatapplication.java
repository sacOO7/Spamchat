package git.spam.io.spamchat;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;

import java.net.URL;
import java.util.Set;

import in.co.madhur.chatbubblesdemo.NativeLoader;
import me.pushy.sdk.Pushy;

/**
 * Created by sachin on 22/1/16.
 */
public class spamchatapplication extends Application {

    private static spamchatapplication Instance;
    public static volatile Handler applicationHandler = null;

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        Instance=this;

        applicationHandler = new Handler(getInstance().getMainLooper());

        NativeLoader.initNativeLibs(spamchatapplication.getInstance());
    }

    public static spamchatapplication getInstance()
    {
        return Instance;
    }

}
