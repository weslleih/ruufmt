package com.wesllei.ruufmt.data;

import android.content.Context;

import com.wesllei.ruufmt.interfaces.OnAsyncTaskEnd;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by wesllei on 12/05/15.
 */
public class CardList implements OnAsyncTaskEnd {

    private EventList eventList;
    private OnAsyncTaskEnd onAsyncTaskEnd;
    private Context context;
    private MealList mealList;
    private boolean eventListIdDone;
    private ArrayList<Card> cards;

    public CardList(Context context, OnAsyncTaskEnd onAsyncTaskEnd) {
        this.context = context;
        this.onAsyncTaskEnd = onAsyncTaskEnd;
        this.mealList = new MealList(context, this);
        this.eventList = new EventList(context, this);
        cards = new ArrayList();
        Event event = eventList.gePromoted();
        if(event != null){
            cards.add(event);
        }
        cards.addAll(mealList.getList());
    }

    public ArrayList getList() {
        return cards;
    }
    public void save() {
        mealList.save();
        eventList.save();
    }

    public void getLast() {
        eventListIdDone = false;
        mealList.getLast();
    }

    public void remove(int position){
        if (cards.get(position) instanceof Meal) {
            mealList.remove(cards.get(position));
            mealList.save();
            cards.remove(position);
        }else{
            if (cards.get(position) instanceof Event) {
                eventList.removePromote(cards.get(position));
                eventList.save();
                cards.remove(position);
            }
        }
    }

    @Override
    public void onAsyncTaskEnd() {
        if (eventListIdDone) {
            try {
                mealList.load();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                eventList.load();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            cards.clear();
            Event event = eventList.gePromoted();
            if(event != null){
                cards.add(event);
            }
            cards.addAll(mealList.getList());
            onAsyncTaskEnd.onAsyncTaskEnd();
        }else{
            eventListIdDone = true;
            eventList.setEventRemovedOfPromoteds(0);
            eventList.getLast();
        }
    }

    public Card get(int position) {
        return cards.get(position);
    }

    public int size() {
        return cards.size();
    }


    public void load() {
        cards.clear();
        try {
            eventList.load();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            mealList.load();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Event event = eventList.gePromoted();
        if(event != null){
            cards.add(event);
        }
        cards.addAll(mealList.getList());
    }
}
