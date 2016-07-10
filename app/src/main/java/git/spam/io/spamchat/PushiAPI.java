package git.spam.io.spamchat;

/**
 * Created by sachin on 23/1/16.
 */
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import java.util.Map;

public class PushiAPI
{
    public static ObjectMapper mapper = new ObjectMapper();
    public static final String PUSHY_SECRET_API_KEY = "2cdfd41c0681ea90e21f938d19e1733cfc76621705fdd672dda6669464fc4020";

    public static void sendPush( MainActivity.PushyPushRequest req ) throws Exception
    {
        // Get custom HTTP client
        HttpClient client = new DefaultHttpClient();

        // Create post request
        HttpPost request = new HttpPost( "https://pushy.me/push?api_key=" + PUSHY_SECRET_API_KEY );

        // Set content type to JSON
        request.addHeader("Content-Type", "application/json");

        // Convert API request to JSON
        String json = mapper.writeValueAsString(req);

        // Send post data as string
        request.setEntity(new StringEntity(json));

        // Execute the request
        HttpResponse response = client.execute(request, new BasicHttpContext());

        // Get response JSON as string
        String responseJSON = EntityUtils.toString(response.getEntity());

        // Convert JSON response into HashMap
        Map<String, Object> map = mapper.readValue(responseJSON, Map.class);

        // Got an error?
        if ( map.containsKey("error") )
        {
            // Throw it
            throw new Exception(map.get("error").toString());

        }
    }
}