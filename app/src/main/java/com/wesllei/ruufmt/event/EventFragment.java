package com.wesllei.ruufmt.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wesllei.ruufmt.R;
import com.wesllei.ruufmt.Analytics;
import com.wesllei.ruufmt.data.Event;
import com.wesllei.ruufmt.data.EventList;
import com.wesllei.ruufmt.interfaces.OnAsyncTaskEnd;
import com.wesllei.ruufmt.util.RecyclerItemClickListener;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by wesllei on 09/05/15.
 */
public class EventFragment extends Fragment implements OnAsyncTaskEnd {

    private static final String SCREEN_NAME = "Lista de eventos";
    ArrayList<Event> eventArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private EventList eventList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        //Analytics
        Tracker tracker = ((Analytics) getActivity().getApplication()).getTracker(Analytics.TrackerName.APP_TRACKER);
        tracker.setScreenName(SCREEN_NAME);
        tracker.send(new HitBuilders.AppViewBuilder().build());
        

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        //Set list items
        eventList = new EventList(getActivity(), this);
        eventArrayList = (ArrayList<Event>) eventList.getList().clone();

        //Configure refresh in swipe
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_main);
        swipeRefreshLayout.setColorSchemeColors(R.color.primaryColor);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                eventList.getLast();
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_main);
        //recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(context, EventActivity.class);
                        Event event = eventList.getList().get(position);
                        intent.putExtra("Event", event);
                        context.startActivity(intent);
                    }
                })
        );


        if (eventList.size() < 1) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    eventList.getLast();
                }
            });
        }
        eventAdapter = new EventAdapter(context, eventArrayList);
        recyclerView.setAdapter(eventAdapter);
        return view;
    }

    @Override
    public void onAsyncTaskEnd() {
        try {
            eventList.load();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (eventList.size() > 0) {
            eventAdapter.refreshList(eventList.getList());
        }
        swipeRefreshLayout.setRefreshing(false);
    }
}
