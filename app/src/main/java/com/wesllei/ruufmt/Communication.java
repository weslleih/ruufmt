package com.wesllei.ruufmt;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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
    private final static String SERVER_ADDRESS = "http://ruufmt-weslleih.rhcloud.com";
    private final static String SERVER_REGISTER = "/users/add/";
    private final static String SERVER_GET_DATA = "/data";
    private static final String PREFS_NAME = "settings";
    private final Context context;
    private MainFragment frag;
    private RecyclerView.Adapter adapter;
    private int afterGetData;

    public Communication(Context context) {
        this.context = context;
    }

    public void getDataServer(final MainFragment frag, final RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        this.frag = frag;
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                String url = SERVER_ADDRESS + SERVER_GET_DATA;
                StringBuilder stringBuilder = new StringBuilder();
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                final HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
                httpClient = new DefaultHttpClient(httpParams);
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
                    afterGetData(true);
                } catch (Exception e) {
                    afterGetData(false);
                    Log.d("readJSONFeed", e.getLocalizedMessage());
                }
                return null;
            }
        }.execute();
    }

    private void afterGetData(final Boolean success) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                frag.afterRefresh(success);
            }
        });
    }

    private ArrayList jsonToDataArray(JSONObject json) {
        JSONObject lunch = null;
        JSONObject dinner = null;
        JSONObject saturday = null;
        JSONObject event = null;
        ArrayList<CardData> list = new ArrayList<CardData>();
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.SharePrefs), Context.MODE_PRIVATE);
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
            saturday = json.getJSONObject("saturday");
            list.add(new Meal(saturday, 2));
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

    public void setAfterGetData(int afterGetData) {
        this.afterGetData = afterGetData;
    }

    ;

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
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        Calendar itemTime = Calendar.getInstance();
        ArrayList newList = new ArrayList();

        /*if (list != null && type != null && type.contentEquals("card")) {
            for (i = 0; i < list.size(); i++) {
                if ((list.get(i) instanceof Meal)) {
                    itemTime.setTime(((Meal) list.get(i)).getDate());
                    if (itemTime.get(Calendar.DAY_OF_MONTH) >= now.get(Calendar.DAY_OF_MONTH)) {
                        if ((((Meal) list.get(i)).getType() == 0 || ((Meal) list.get(i)).getType() == 2) && now.get(Calendar.HOUR_OF_DAY) < 14 && now.get(Calendar.MINUTE) < 31) {
                            newList.add(list.get(i));
                        } else {
                            if (((Meal) list.get(i)).getType() == 1 && now.get(Calendar.HOUR_OF_DAY) < 17 && now.get(Calendar.MINUTE) < 1) {
                                newList.add(list.get(i));
                            }
                        }
                    }
                }
            }
        }*/
        if (list != null) {
            return list;
        } else {
            return new ArrayList();
        }
    }

    public boolean sendGegIdToServer(String regid) {
        String url = SERVER_ADDRESS + SERVER_REGISTER + regid;
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
            if (message.equals("sucess")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
            return false;
        }
    }


}
