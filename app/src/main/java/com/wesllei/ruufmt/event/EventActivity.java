package com.wesllei.ruufmt.event;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wesllei.ruufmt.Analytics;
import com.wesllei.ruufmt.R;
import com.wesllei.ruufmt.data.Event;
import com.wesllei.ruufmt.interfaces.OnDonwloadImage;
import com.wesllei.ruufmt.util.DownloadAsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;


public class EventActivity extends AppCompatActivity implements OnDonwloadImage {
    private LayoutInflater inflater;
    private ViewGroup viewGroup;
    private ImageView imageBanner;
    private ScrollView scrollView;
    private Toolbar toolbar;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Intent intent = getIntent();

        event = (Event) intent.getSerializableExtra("Event");
        inflater = getLayoutInflater();
        viewGroup = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
        if (event == null) {
            finish();
        }
        //Analytics
        Tracker tracker = ((Analytics) this.getApplication()).getTracker(Analytics.TrackerName.APP_TRACKER);
        tracker.setScreenName(event.getName());
        tracker.send(new HitBuilders.AppViewBuilder().build());


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.getBackground().setAlpha(0);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageBanner = (ImageView) findViewById(R.id.event_banner_image);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                float position = scrollView.getScrollY();
                float imageHeight = imageBanner.getHeight();
                float alpha;
                if (imageHeight < position) {
                    toolbar.getBackground().setAlpha(255);
                } else {
                    alpha = (1 / imageHeight) * position;
                    toolbar.getBackground().setAlpha((int) (255 * alpha));
                }

                if (position < imageHeight / 2) {
                    toolbar.setTitle("");
                } else {
                    toolbar.setTitle(event.getName());
                }
            }
        });

        findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("image/jpg");
                File image = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "ruufmt_temp.jpg");
                try {
                    InputStream in = new FileInputStream(event.getImageBannerFile());
                    OutputStream out = new FileOutputStream(image);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
                    startActivity(Intent.createChooser(shareIntent, "Share Image"));
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        File imgFile = new File(event.getImageBannerFile());
        ImageView banner = (ImageView) findViewById(R.id.event_banner_image);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.event_banner_progess);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            AfterDownloadImage(myBitmap, banner, progressBar);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            DownloadAsyncTask dowImage = new DownloadAsyncTask(this, this, banner, event, 2, progressBar);
            dowImage.execute(event.getImageBannerUrl());
        }
        LinearLayout layout = (LinearLayout) findViewById(R.id.event_desc);
        ((TextView) findViewById(R.id.event_desc_header)).setText(event.getDesc());

        //Add place child
        if (event.getPlaceMapURL() != null) {
            layout.addView(createChildView(R.drawable.ic_action_maps_pin_drop, event.getPlaceName(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getPlaceMapURL()));
                    startActivity(browserIntent);
                }
            }));
        } else {
            layout.addView(createChildView(R.drawable.ic_action_maps_pin_drop, event.getPlaceName()));
        }

        //Add date child
        layout.addView(createChildView(R.drawable.ic_action_action_event, event.getDateStrig(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(event.getDate());
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", cal.getTimeInMillis());
                intent.putExtra("allDay", false);
                intent.putExtra("rrule", "FREQ=YEARLY");
                intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
                intent.putExtra("title", event.getName());
                intent.putExtra("description", event.getDesc());
                startActivity(intent);
            }
        }));

        //Add price
        if (event.getPrice() != null) {
            layout.addView(createChildView(R.drawable.ic_action_maps_local_play, event.getPrice()));
        }
        //Add Facebook
        if (event.getFacebookUrl() != null) {
            layout.addView(createChildView(R.drawable.ic_action_square_facebook, event.getName(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getFacebookUrl()));
                    startActivity(browserIntent);
                }
            }));
        }

        //Add phone
        if (event.getPhone() != null) {
            layout.addView(createChildView(R.drawable.ic_action_maps_local_phone, event.getPhone(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + event.getPhone()));
                    startActivity(callIntent);
                }
            }));
        }

        //Add whatsapp
        if (event.getWhatsapp() != null) {
            View viewWhats = createChildView(R.drawable.ic_action_whatsapp, event.getWhatsapp(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("smsto:" + event.getWhatsapp());
                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                    i.setPackage("com.whatsapp");
                    startActivity(Intent.createChooser(i, ""));
                }
            });
            ((TextView) viewWhats.findViewById(R.id.event_item_title)).setAutoLinkMask(0);
            layout.addView(viewWhats);
        }
    }


    @Override
    public void AfterDownloadImage(Bitmap bitmap, ImageView imageView, ProgressBar progressBar) {
        imageView.setImageBitmap(bitmap);
        progressBar.setVisibility(View.GONE);
    }

    private View createChildView(int icon, String text) {
        View child = inflater.inflate(R.layout.event_activity_item, viewGroup, false);
        ((ImageView) child.findViewById(R.id.event_item_icon)).setImageResource(icon);
        ((TextView) child.findViewById(R.id.event_item_title)).setText(text);
        return child;
    }

    private View createChildView(int icon, String text, View.OnClickListener clickListener) {
        View child = createChildView(icon, text);
        child.setOnClickListener(clickListener);
        return child;
    }

    private View createChildView(int icon, String text, int sIcon) {
        View child = createChildView(icon, text);
        ((ImageView) child.findViewById(R.id.event_item_s_icon)).setImageResource(sIcon);
        return child;
    }

    private View createChildView(int icon, String text, int sIcon, View.OnClickListener clickListener) {
        View child = createChildView(icon, text, sIcon);
        child.setOnClickListener(clickListener);
        return child;
    }

    @Override
    public void finish() {
        toolbar.getBackground().setAlpha(255);
        super.finish();
    }

    @Override
    protected void onResume() {
        toolbar.getBackground().setAlpha(0);
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }


}
