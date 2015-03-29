package com.wesllei.ruufmt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Wesllei on 04/02/2015.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.GeneralViewHolder> {
    ArrayList<CardData> cardList;
    Context context;

    public CardAdapter(Context context, ArrayList cardList) {
        super();
        this.context = context;
        this.cardList = cardList;
    }

    @Override
    public GeneralViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        GeneralViewHolder holder;
        if (viewType > 0) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.event_card, viewGroup, false);
            holder = new EventCardViewHolder(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.meal_card, viewGroup, false);
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
    public void onBindViewHolder(GeneralViewHolder viewHolder, int position) {
        if (getItemViewType(position) > 0) {
            Event event = (Event) cardList.get(position);
            File imgFile = new File(event.getImageCardFile());
            ImageView imageView = ((EventCardViewHolder) viewHolder).eventCardImage;
            ProgressBar progressBar = ((EventCardViewHolder) viewHolder).progressBar;
            ((EventCardViewHolder) viewHolder).cardTitle.setText(event.getName());
            ((EventCardViewHolder) viewHolder).cardHead.setBackgroundColor(Color.parseColor(event.getHeadColor()));
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                setEventImage(myBitmap, imageView, progressBar);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                DownloadAsyncTask dowImage = new DownloadAsyncTask(context, this, imageView, event, true, progressBar);
                dowImage.execute(event.getImageCardUrl());
            }
        } else {
            Meal menu = (Meal) cardList.get(position);
            if (menu.getType() == 0) {
                ((MealCardViewHolder) viewHolder).iconHead.setImageResource(R.drawable.ic_wb_sunny);
                ((MealCardViewHolder) viewHolder).cardHead.setBackground(context.getResources().getDrawable(R.color.backgroundDay));
                ((MealCardViewHolder) viewHolder).cardTitle.setText("Almoço");
            } else {
                if (menu.getType() == 1) {
                    ((MealCardViewHolder) viewHolder).iconHead.setImageResource(R.drawable.ic_brightness_3);
                    ((MealCardViewHolder) viewHolder).cardHead.setBackground(context.getResources().getDrawable(R.color.backgroundNight));
                    ((MealCardViewHolder) viewHolder).cardTitle.setText("Jantar");
                }else{
                    ((MealCardViewHolder) viewHolder).iconHead.setImageResource(R.drawable.ic_wb_sunny);
                    ((MealCardViewHolder) viewHolder).cardHead.setBackground(context.getResources().getDrawable(R.color.backgroundDay));
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
        }
    }

    public void refreshList(ArrayList list) {
        int i, v;
        while (this.cardList.size() > 0) {
            this.cardList.remove(0);
            this.notifyItemRemoved(0);
        }
        for (i = 0; i < list.size(); i++) {
            this.cardList.add((CardData) list.get(i));
            v = this.cardList.size() - 1;
            this.notifyItemInserted(v);
        }
        this.notifyDataSetChanged();
    }

    public void setEventImage(final Bitmap myBitmap, final ImageView imageView, final ProgressBar progressBar) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                double w = myBitmap.getWidth();
                double h = myBitmap.getHeight();
                double ratio = w / h;
                Integer nh = Integer.valueOf((int) Math.round(imageView.getWidth() / ratio));
                imageView.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, imageView.getWidth(), nh, true));
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    class GeneralViewHolder extends RecyclerView.ViewHolder {

        public GeneralViewHolder(View itemView) {
            super(itemView);
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
        public ImageView iconHead;
        public RelativeLayout cardHead;
        public TextView cardTitle;

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
            iconHead = (ImageView) itemView.findViewById(R.id.icon_head);
            cardHead = (RelativeLayout) itemView.findViewById(R.id.card_head);
            cardTitle = (TextView) itemView.findViewById(R.id.card_title);
        }
    }

    class EventCardViewHolder extends GeneralViewHolder {

        public ImageView eventCardImage;
        public ProgressBar progressBar;
        public TextView cardTitle;
        public RelativeLayout cardHead;

        public EventCardViewHolder(View itemView) {
            super(itemView);
            eventCardImage = (ImageView) itemView.findViewById(R.id.event_card_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.event_progress_card);
            cardTitle = (TextView) itemView.findViewById(R.id.card_title);
            cardHead = (RelativeLayout) itemView.findViewById(R.id.card_head);
        }
    }
}