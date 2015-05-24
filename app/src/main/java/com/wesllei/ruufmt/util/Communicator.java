package com.wesllei.ruufmt.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.wesllei.ruufmt.R;
import com.wesllei.ruufmt.interfaces.OnAsyncTaskEndJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * Created by wesllei on 09/05/15.
 * Class to make requests to the server
 */
public class Communicator {
    //private final static String SERVER_ADDRESS = "http://ruufmt-weslleih.rhcloud.com";
    private final static String SERVER_ADDRESS = "http://192.168.1.7:3000";

    private Context context;
    private OnAsyncTaskEndJson onAsyncTaskEndJson = null;
    private String url;

    /**
     * Requests data in JSON format from the server
     *
     * @param context            The current context
     * @param urlPath            The path of URL
     * @param onAsyncTaskEndJson The class that should be executed method onAsyncTaskEndJson(JSONObject jsonobjetc)
     */
    public Communicator(Context context, String urlPath, OnAsyncTaskEndJson onAsyncTaskEndJson) {
        this.context = context;
        this.onAsyncTaskEndJson = onAsyncTaskEndJson;
        this.url = SERVER_ADDRESS.concat(urlPath);
    }

    /**
     * Get Json file from server and rum OnAsyncTaskEndJson.onAsyncTaskEndJson();
     */
    public void getJson() {
        if (this.onAsyncTaskEndJson == null) {
            return;
        }
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject result = new JSONObject();
                try {
                    result = readJsonFromUrl();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }

            protected void onPostExecute(JSONObject result) {
                Log.d("Json", result.toString());
                if (result.length() < 1) {
                    Toast toast = Toast.makeText(context, R.string.conection_error, Toast.LENGTH_SHORT);
                    toast.show();
                }
                onAsyncTaskEndJson.onAsyncTaskEndJson(result);
            }
        }.execute();
    }

    /**
     * Open and read data from URL
     *
     * @return JSONObject with data from the url
     * @throws IOException
     * @throws JSONException
     */
    private JSONObject readJsonFromUrl() throws IOException, JSONException {
        JSONObject json = new JSONObject();
        InputStream is = null;
        try {
            URL urlCon = new URL(url);
            URLConnection con = urlCon.openConnection();
            con.setConnectTimeout(3000);
            is = con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            json = new JSONObject(jsonText);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Read and build the string of a BufferedReader
     *
     * @param rd BufferedReader
     * @return The string built
     * @throws IOException
     */
    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
