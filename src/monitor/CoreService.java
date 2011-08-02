package monitor;

import java.net.*;
import java.io.*;

public class CoreService {

    private String host;
    private String apiKey;
    
    public CoreService(String host, String apiKey) {
        this.host = host;
        this.apiKey = apiKey;
    }  

    public void notifyExperimentComplete(String experimentName) throws Exception {
        URL url = new URL("http://" + host + "/api/yeastRlsExperimentComplete"
                + "?number=" + experimentName + "&key=" + apiKey);
        System.out.println(url);
            
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
        System.out.println(conn.getResponseCode());
        System.out.println(conn.getResponseMessage());
    }
}
