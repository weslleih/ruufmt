package com.wesllei.ruufmt.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.wesllei.ruufmt.data.Event;
import com.wesllei.ruufmt.data.EventList;
import com.wesllei.ruufmt.interfaces.OnDonwloadImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wesllei on 24/03/15.
 */
public class DownloadAsyncTask extends AsyncTask<String, Integer, Bitmap> {
    private ImageView imageView;
    private Event event;
    private Context context;
    private int type;
    private ProgressBar progressBar;
    private OnDonwloadImage donwloadImage;

    public DownloadAsyncTask(Context context, OnDonwloadImage donwloadImage, ImageView imageView, Event event, int type, ProgressBar progressBar) {
        this.imageView = imageView;
        this.event = event;
        this.context = context;
        this.type = type;
        this.progressBar = progressBar;
        this.donwloadImage = donwloadImage;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inScaled = false;
            System.out.println();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;
        } catch (Exception e) {
            System.out.print(e);
            return null;
        }
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            donwloadImage.AfterDownloadImage(result, imageView, progressBar);
            FileOutputStream out = null;
            File cacheDir = context.getCacheDir();
            String filePath = cacheDir.getPath() + " - " + event.getIdEvent() + " - " + type + ".png";
            try {
                out = new FileOutputStream(filePath);
                result.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        switch (type) {
                            case 0:
                                this.event.setImageCardFile(filePath);
                                break;
                            case 1:
                                this.event.setImageIconFile(filePath);
                                break;
                            case 2:
                                this.event.setImageBannerFile(filePath);
                                break;
                        }
                        EventList eventList = new EventList(context, null);
                        eventList.addIfNoExist(event);
                        eventList.save();
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
