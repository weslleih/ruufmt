package com.wesllei.ruufmt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
    private Boolean card;
    private ProgressBar progressBar;
    private CardAdapter adapter;

    public DownloadAsyncTask(Context context, CardAdapter adapter, ImageView imageView, Event event, Boolean card, ProgressBar progressBar) {
        this.imageView = imageView;
        this.event = event;
        this.context = context;
        this.card = card;
        this.progressBar = progressBar;
        this.adapter = adapter;
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
            adapter.setEventImage(result,imageView,progressBar);
            FileOutputStream out = null;
            File cacheDir = context.getCacheDir();
            String filePath = cacheDir.getPath()+event.getId() + ".png";
            try {
                out = new FileOutputStream(filePath);
                result.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        if (this.card) {
                            this.event.setImageCardFile(filePath);
                        } else {
                            this.event.setImageBannerFile(filePath);
                        }
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
