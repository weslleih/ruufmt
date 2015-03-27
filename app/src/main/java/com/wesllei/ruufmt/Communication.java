package com.wesllei.ruufmt;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wesllei on 25/03/15.
 */
public class Communication {
    private final static String SERVER_ADDRESS = "http://192.168.1.7:3000";
    private final static String SERVER_REGISTER = "/users/add/";
    private final static String SERVER_GET_DATA = "/data";
    private static final String PREFS_NAME = "settings";
    private final static String UFA_ADDRESS = "http://www.ufmt.br/ufmt/unidade/index.php/secao/visualizar/3793/RU";
    private final Context context;
    private MainFragment frag;
    private RecyclerView.Adapter adapter;

    public Communication(Context context) {
        this.context = context;
    }

    public void getDataServer(final MainFragment frag, final RecyclerView.Adapter adapter) {
        this.frag = frag;
        this.adapter = adapter;
        new AsyncTask<String, Void, Void>() {
            ArrayList<CardData> items = new ArrayList();

            @Override
            protected Void doInBackground(String... params) {
                String url = SERVER_ADDRESS + SERVER_GET_DATA;
                StringBuilder stringBuilder = new StringBuilder();
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse response = httpClient.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    if (statusCode == 200) {
                        HttpEntity entity = response.getEntity();
                        InputStream inputStream = entity.getContent();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        inputStream.close();
                    } else {
                        Log.d("JSON", "Failed to download file");
                    }
                    JSONObject json = parse(String.valueOf(stringBuilder));
                    ArrayList list = jsonToDataArray(json);
                    saveData(list);
                } catch (Exception e) {
                    Log.d("readJSONFeed", e.getLocalizedMessage());
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            frag.afterRefresh(false);
                        }
                    });
                    return null;
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        frag.afterRefresh(true);
                    }
                });
                return null;
            }
        }.execute();
    }

    private ArrayList jsonToDataArray(JSONObject json) {
        JSONObject lunch = null;
        JSONObject dinner = null;
        JSONObject event = null;
        ArrayList<CardData> list = new ArrayList<CardData>();
        SharedPreferences sharedPref = ((Activity) context).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        try {
            lunch = json.getJSONObject("lunch");
            list.add(new Meal(lunch, 0));
            editor.putString("lastListDate", sdf.format(now));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            dinner = json.getJSONObject("dinner");
            list.add(new Meal(dinner, 1));
            editor.putString("lastListDate", sdf.format(now));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            event = json.getJSONObject("event");
            list.add(new Event(event));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.commit();
        return list;
    }

    private JSONObject parse(String result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (Exception e) {
            Log.d("Object not serializable", e.getLocalizedMessage());
        }
        return jsonObject;
    }

    public void saveData(ArrayList list) {
        try {
            FileOutputStream fos = context.openFileOutput(PREFS_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(list);
            os.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList getLisFromFile() {
        try {
            FileInputStream fis = context.openFileInput(PREFS_NAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            ArrayList list = (ArrayList) is.readObject();
            is.close();
            fis.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList getList(String type) {
        return processList(getLisFromFile(), type);
    }

    public ArrayList processList(ArrayList list, String type) {
        int i;
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.DAY_OF_YEAR, 0);
        Calendar c2 = Calendar.getInstance();

        if (list != null && type != null &&type.contentEquals("card")) {
            for (i = 0; i < list.size(); i++) {
                if (!(list.get(i) instanceof Meal)) {
                    if (list.get(i) instanceof Event) {
                        c2.setTime(((Event) list.get(i)).getPromoteDate());
                        if (!(c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR))) {
                            list.remove(i);
                        }
                    }
                }
            }
        }
        if (list != null) {
            return list;
        } else {
            return new ArrayList();
        }
    }

    private void storeRegistrationId(Context context, String regid) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("GCMId", regid);
        editor.commit();
    }

    public boolean sendGegIdToServer(String regid) {
        String url = SERVER_ADDRESS + SERVER_REGISTER +regid;
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
            JSONObject json = parse(String.valueOf(stringBuilder));
            String message = json.getString("message");
            if(message.equals("sucess")){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
            return false;
        }
    }
}
