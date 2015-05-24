package com.wesllei.ruufmt.event;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wesllei.ruufmt.R;
import com.wesllei.ruufmt.data.Event;
import com.wesllei.ruufmt.interfaces.OnDonwloadImage;
import com.wesllei.ruufmt.util.DownloadAsyncTask;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by wesllei on 09/05/15.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> implements OnDonwloadImage {
    private ArrayList<Event> eventList;
    private Context context;


    public EventAdapter(Context context, ArrayList<Event> eventList) {
        super();
        this.context = context;
        this.eventList = eventList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Event event = eventList.get(position);
        File imgFile = new File(event.getImageIconFile());
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            viewHolder.eventIcon.setImageBitmap(myBitmap);
            viewHolder.eventIconProgress.setVisibility(View.GONE);
        } else {
            viewHolder.eventIconProgress.setVisibility(View.VISIBLE);
            DownloadAsyncTask dowImage = new DownloadAsyncTask(context, this, viewHolder.eventIcon, event, 1, viewHolder.eventIconProgress);
            dowImage.execute(event.getImageIconUrl());
        }

        viewHolder.eventTitle.setText(event.getName());
        viewHolder.eventSubtitle.setText(event.getDateStrig() + " - " + event.getPlaceName());
    }

    public void refreshList(ArrayList<Event> list) {
        int i, v;
        while (this.eventList.size() > 0) {
            this.eventList.remove(0);
            this.notifyItemRemoved(0);
        }
        for (i = 0; i < list.size(); i++) {
            this.eventList.add(list.get(i));
            v = this.eventList.size() - 1;
            this.notifyItemInserted(v);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public void AfterDownloadImage(Bitmap bitmap, ImageView imageView, ProgressBar progressBar) {
        imageView.setImageBitmap(bitmap);
        progressBar.setVisibility(View.GONE);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar eventIconProgress;
        private ImageView eventIcon;
        private TextView eventTitle;
        private TextView eventSubtitle;

        public ViewHolder(View itemView) {
            super(itemView);
            this.eventIcon = (ImageView) itemView.findViewById(R.id.event_icon);
            this.eventIconProgress = (ProgressBar) itemView.findViewById(R.id.event_icon_progress);
            this.eventTitle = (TextView) itemView.findViewById(R.id.event_title);
            this.eventSubtitle = (TextView) itemView.findViewById(R.id.event_subtitle);
        }

    }
}
