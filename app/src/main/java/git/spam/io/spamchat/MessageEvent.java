package git.spam.io.spamchat;

import java.util.HashMap;

/**
 * Created by sachin on 23/1/16.
 */
public class MessageEvent {

    HashMap <String,String> map;
    MessageEvent (HashMap <String,String> map)
    {
            this.map=map;
    }

    public HashMap<String, String> getMap() {
        return map;
    }
}
