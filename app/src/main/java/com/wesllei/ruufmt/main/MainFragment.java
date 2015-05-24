package com.wesllei.ruufmt.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wesllei.ruufmt.Analytics;
import com.wesllei.ruufmt.interfaces.OnAsyncTaskEnd;
import com.wesllei.ruufmt.data.Card;
import com.wesllei.ruufmt.data.CardList;
import com.wesllei.ruufmt.R;

import java.util.ArrayList;

/**
 * Created by wesllei on 09/05/15.
 */
public class MainFragment extends Fragment implements OnAsyncTaskEnd {
    private static final String SCREEN_NAME = "Lista de Cards";
    ArrayList<Card> itemArrayList;
    private CardList cardItems;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Analytics
        Tracker tracker = ((Analytics) getActivity().getApplication()).getTracker(Analytics.TrackerName.APP_TRACKER);
        tracker.setScreenName(SCREEN_NAME);
        tracker.send(new HitBuilders.AppViewBuilder().build());


        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        //Set list items
        cardItems = new CardList(context, this);
        itemArrayList = (ArrayList<Card>) cardItems.getList().clone();

        //Configure refresh in swipe
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_main);
        swipeRefreshLayout.setColorSchemeColors(R.color.primaryColor);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cardItems.getLast();
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_main);
        //recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipe(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    itemArrayList.remove(position);
                                    cardItems.remove(position);
                                    mainAdapter.notifyItemRemoved(position);
                                }
                                mainAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    itemArrayList.remove(position);
                                    cardItems.remove(position);
                                    mainAdapter.notifyItemRemoved(position);
                                }
                                mainAdapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);

        if (cardItems.size() < 1) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    cardItems.getLast();
                }
            });
        }

        mainAdapter = new MainAdapter(context, itemArrayList);
        recyclerView.setAdapter(mainAdapter);
        return view;
    }

    @Override
    public void onAsyncTaskEnd() {
        cardItems.load();
        if (cardItems.size() > 0) {
            mainAdapter.refreshList(cardItems.getList());
        }
        swipeRefreshLayout.setRefreshing(false);
    }
}
