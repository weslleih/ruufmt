package com.wesllei.ruufmt;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;

import java.util.ArrayList;

/**
 * Created by Wesllei on 04/02/2015.
 */
public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private ArrayList<Object> itemList;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter ca;
    private String type;
    private Boolean toRefresh;

    public void setLit(ArrayList<Object> itemList) {
        if(itemList == null) {
            this.itemList = new ArrayList<>();
        }else{
            this.itemList = itemList;
        }
    }

    public void setType(String type){
        this.type = type;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.mainRecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        if(type != null && type.contentEquals("event")){
            ca = new EventAdapter(getActivity(), this.itemList);
        }else{
            ca = new CardAdapter(getActivity(), this.itemList);
        }
        recyclerView.setAdapter(ca);
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
                                    itemList.remove(position);
                                    ca.notifyItemRemoved(position);
                                    Communication communication = new Communication(context);
                                    communication.saveData(itemList);
                                }
                                ca.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    itemList.remove(position);
                                    ca.notifyItemRemoved(position);
                                    Communication communication = new Communication(context);
                                    communication.saveData(itemList);
                                }
                                ca.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
        if(toRefresh != null && toRefresh){
            final MainFragment frag = this;
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    Communication connection = new Communication(context);
                    connection.getDataServer(frag, ca);
                }
            });
        }else{
            this.afterRefresh(true);
        }
        return view;
    }
    @Override public void onRefresh() {
        Communication connection = new Communication(context);
        connection.getDataServer(this, ca);
    }

    public void afterRefresh(Boolean bol){
        swipeRefreshLayout.setRefreshing(false);
        if(bol){
            Communication communication = new Communication(context);
            ArrayList list = communication.getList(type);
            if(type != null && type.contentEquals("card")) {
                ((CardAdapter) ca).refreshList(list);
            }else{
                //((EventAdapter) ca).refreshList(list);
            }
        }
    }
    public void setToRefresh(Boolean toRefresh) {
        this.toRefresh = toRefresh;
    }
}
