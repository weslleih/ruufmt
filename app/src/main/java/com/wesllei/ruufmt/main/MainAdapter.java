package com.wesllei.ruufmt.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wesllei.ruufmt.data.Card;
import com.wesllei.ruufmt.interfaces.OnAsyncTaskEndJson;
import com.wesllei.ruufmt.util.DownloadAsyncTask;
import com.wesllei.ruufmt.data.Event;
import com.wesllei.ruufmt.event.EventActivity;
import com.wesllei.ruufmt.data.Meal;
import com.wesllei.ruufmt.interfaces.OnDonwloadImage;
import com.wesllei.ruufmt.R;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Wesllei on 04/02/2015.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.GeneralViewHolder> implements OnDonwloadImage, OnAsyncTaskEndJson {
    ArrayList<Card> cardList;
    Context context;

    public MainAdapter(Context context, ArrayList<Card> cardList) {
        super();
        this.context = context;
        this.cardList = cardList;
    }

    @Override
    public GeneralViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        GeneralViewHolder holder;
        if (viewType > 0) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_event, viewGroup, false);
            holder = new EventCardViewHolder(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_meal, viewGroup, false);
            holder = new MealCardViewHolder(v);
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if (cardList.get(position) instanceof Meal) {
            return 0;
        }
        return 1;
    }

    @Override
    public void onBindViewHolder(final GeneralViewHolder viewHolder, final int position) {
        if (getItemViewType(position) > 0) {
            final Event event = (Event) cardList.get(position);
            File imgFile = new File(event.getImageCardFile());
            ImageView imageView = ((EventCardViewHolder) viewHolder).eventCardImage;
            ProgressBar progressBar = ((EventCardViewHolder) viewHolder).progressBar;
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                DownloadAsyncTask dowImage = new DownloadAsyncTask(context, this, imageView, event, 0, progressBar);
                dowImage.execute(event.getImageCardUrl());
            }
            ((EventCardViewHolder) viewHolder).details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EventActivity.class);
                    intent.putExtra("Event", event);
                    context.startActivity(intent);
                }
            });
        } else {
            Meal menu = (Meal) cardList.get(position);
            if (menu.getType() == 0) {
                ((MealCardViewHolder) viewHolder).cardTitle.setText("Almoço");
            } else {
                if (menu.getType() == 1) {
                    ((MealCardViewHolder) viewHolder).cardTitle.setText("Jantar");
                } else {
                    ((MealCardViewHolder) viewHolder).cardTitle.setText("Sábado");
                }
            }
            ((MealCardViewHolder) viewHolder).date.setText(menu.getFormatedDate());
            ((MealCardViewHolder) viewHolder).pp.setText(menu.getPp());
            ((MealCardViewHolder) viewHolder).ov.setText(menu.getOv());
            ((MealCardViewHolder) viewHolder).sa.setText(menu.getSa());
            ((MealCardViewHolder) viewHolder).gu.setText(menu.getGu());
            ((MealCardViewHolder) viewHolder).ac.setText(menu.getAc());
            ((MealCardViewHolder) viewHolder).so.setText(menu.getSo());
            ((MealCardViewHolder) viewHolder).su.setText(menu.getSu());
            ((MealCardViewHolder) viewHolder).shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap b = Bitmap.createBitmap(viewHolder.shareBody.getWidth(), viewHolder.shareBody.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(b);
                    Drawable bgDrawable = viewHolder.shareBody.getBackground();
                    if (bgDrawable != null)
                        bgDrawable.draw(canvas);
                    else
                        canvas.drawColor(Color.WHITE);
                    viewHolder.shareBody.draw(canvas);

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("image/jpg");

                    try {
                        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "ruufmt_temp.jpg");
                        f.createNewFile();
                        FileOutputStream fos = new FileOutputStream(f);
                        b.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                        context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

    }

    public void refreshList(ArrayList<Card> list) {
        int i, v;
        while (this.cardList.size() > 0) {
            this.cardList.remove(0);
            this.notifyItemRemoved(0);
        }
        for (i = 0; i < list.size(); i++) {
            this.cardList.add(list.get(i));
            v = this.cardList.size() - 1;
            this.notifyItemInserted(v);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    @Override
    public void AfterDownloadImage(Bitmap bitmap, ImageView imageView, ProgressBar progressBar) {
        imageView.setImageBitmap(bitmap);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onAsyncTaskEndJson(JSONObject jsonObject) {

    }

    class GeneralViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout shareBody;

        public GeneralViewHolder(View itemView) {
            super(itemView);
            shareBody = (LinearLayout) itemView.findViewById(R.id.share_body);
        }
    }

    class MealCardViewHolder extends GeneralViewHolder {

        public TextView date;
        public TextView pp;
        public TextView ov;
        public TextView sa;
        public TextView gu;
        public TextView ac;
        public TextView so;
        public TextView su;
        public RelativeLayout cardHead;
        public TextView cardTitle;
        public Button shareButton;

        public LinearLayout upButton;
        public TextView upText;
        public LinearLayout upCircle;
        public LinearLayout downButton;
        public TextView downText;
        public LinearLayout downCircle;

        public MealCardViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            pp = (TextView) itemView.findViewById(R.id.pp);
            ov = (TextView) itemView.findViewById(R.id.ov);
            sa = (TextView) itemView.findViewById(R.id.sa);
            gu = (TextView) itemView.findViewById(R.id.gu);
            ac = (TextView) itemView.findViewById(R.id.ac);
            so = (TextView) itemView.findViewById(R.id.so);
            su = (TextView) itemView.findViewById(R.id.su);
            cardHead = (RelativeLayout) itemView.findViewById(R.id.card_head);
            cardTitle = (TextView) itemView.findViewById(R.id.card_title);
            shareButton = (Button) itemView.findViewById(R.id.share_button);

            upButton = (LinearLayout) itemView.findViewById(R.id.up_button);
            upText = (TextView) itemView.findViewById(R.id.up_text);
            upCircle = (LinearLayout) itemView.findViewById(R.id.up_circle);

            downButton = (LinearLayout) itemView.findViewById(R.id.down_button);
            downText = (TextView) itemView.findViewById(R.id.down_text);
            downCircle = (LinearLayout) itemView.findViewById(R.id.down_circle);
        }
    }

    class EventCardViewHolder extends GeneralViewHolder {

        public ImageView eventCardImage;
        public ProgressBar progressBar;
        public RelativeLayout cardHead;
        public Button details;

        public EventCardViewHolder(View itemView) {
            super(itemView);
            eventCardImage = (ImageView) itemView.findViewById(R.id.event_banner_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.event_banner_progess);
            cardHead = (RelativeLayout) itemView.findViewById(R.id.card_head);
            details = (Button) itemView.findViewById(R.id.event_details);
        }
    }
}