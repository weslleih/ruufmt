package com.wesllei.ruufmt.data;

import android.content.Context;

import com.wesllei.ruufmt.util.Communicator;
import com.wesllei.ruufmt.interfaces.OnAsyncTaskEnd;
import com.wesllei.ruufmt.interfaces.OnAsyncTaskEndJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by wesllei on 11/05/15.
 */
public class EventList implements Serializable, OnAsyncTaskEndJson {
    static final long serialVersionUID = 5525696873722394745L;
    private static final String URL_SUFFIX = "/event/list";
    private static final String FILE_NAME = "eventList";
    private transient Context context;
    private transient OnAsyncTaskEnd onAsyncTaskEnd;
    private ArrayList<Event> events;
    private int eventRemovedOfPromoteds;


    /**
     * Load a list of events from file
     *
     * @param context        The current context
     * @param onAsyncTaskEnd The class that should be executed method onAsyncTaskEnd()
     */
    public EventList(Context context, OnAsyncTaskEnd onAsyncTaskEnd) {
        this.context = context;
        this.onAsyncTaskEnd = onAsyncTaskEnd;
        eventRemovedOfPromoteds = 0;
        try {
            load();
        } catch (Exception e) {
            e.printStackTrace();
            events = new ArrayList<>();
        }
    }

    /**
     * Load a list from file
     *
     * @param context The current context
     */
    public EventList(Context context) {
        this.context = context;
        eventRemovedOfPromoteds = 0;
        try {
            load();
        } catch (Exception e) {
            e.printStackTrace();
            events = new ArrayList<>();
        }
    }

    /**
     * Sete the current context
     *
     * @param context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Save the current list
     */
    public void save() {
        FileOutputStream fos = null;
        ObjectOutputStream os = null;
        try {
            fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open and assign the list of events from file
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void load() throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(FILE_NAME);
        ObjectInputStream is = new ObjectInputStream(fis);
        EventList eventList = (EventList) is.readObject();
        eventList.setContext(context);
        this.events = eventList.getList();
        this.eventRemovedOfPromoteds = eventList.getEventRemovedOfPromoteds();
        is.close();
        fis.close();
    }

    /**
     * Get latest list of events from server
     */
    public void getLast() {
        Communicator communicator = new Communicator(context, URL_SUFFIX, this);
        if (onAsyncTaskEnd != null) {
            communicator.getJson();
        }
    }

    /**
     * Method to run after of requesting something asynchronous
     *
     * @param jsonObject JSONObject received from the server
     */
    @Override
    public void onAsyncTaskEndJson(JSONObject jsonObject) {
        try {
            JSONArray array = jsonObject.getJSONArray("events");
            for (int i = 0; i < array.length(); i++) {
                addIfNoExist(new Event(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        save();
        onAsyncTaskEnd.onAsyncTaskEnd();
    }

    /**
     * @return Size of event list
     */
    public int size() {
        return events.size();
    }

    /**
     * Remove a list item
     *
     * @param item The item to be removed
     */
    public void remove(Card item) {
        remove(events.indexOf(item));
    }

    /**
     * Remove an item from the promoted event list
     *
     * @param item The promoted item to be removed
     */
    public void removePromote(Card item) {
        int index = events.indexOf(item);
        eventRemovedOfPromoteds = events.get(index).getIdEvent();
    }

    /**
     * Remove a list item
     *
     * @param position The index of the event to be removed
     */
    public void remove(int position) {
        File file;
        file = new File(events.get(position).getImageBannerFile());
        file.delete();
        file = new File(events.get(position).getImageCardFile());
        file.delete();
        file = new File(events.get(position).getImageIconFile());
        file.delete();
        events.remove(position);
    }

    /**
     * @return All events or only promoted
     */
    public ArrayList<Event> getList() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        Boolean wasEdited = false;
            for (Event event : events) {
                if (event.getDate() != null && event.getDate().before(now.getTime())) {
                    remove(event);
                    wasEdited = true;
                }
            }
            if (wasEdited) {
                this.save();
            }
        return events;
    }

    /**
     * Get event by index position
     *
     * @param position The index os event
     * @return The event of the index
     */
    public Event getEvent(int position) {
        return events.get(position);
    }

    /**
     * Add an event on the end of the list
     *
     * @param event The event to be added
     */
    public void add(Event event) {
        events.add(event);
    }

    /**
     * Add an event on the end of the list only if it does not exist
     * If it already exists is replaced
     *
     * @param event The event to be added
     */
    public void addIfNoExist(Event event) {
        Boolean control = true;
        outerloop:
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getIdEvent() == event.getIdEvent()) {
                events.remove(i);
                events.add(event);
                control = false;
                break outerloop;
            }
        }
        if (control) {
            events.add(event);
        }
    }

    public int getEventRemovedOfPromoteds() {
        return eventRemovedOfPromoteds;
    }

    public void setEventRemovedOfPromoteds(int eventRemovedOfPromoteds) {
        this.eventRemovedOfPromoteds = eventRemovedOfPromoteds;
    }

    public Event gePromoted() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        for(Event event: events){
            if (event.getPromoteDate() != null && event.getPromoteDate().compareTo(now.getTime()) == 0 && event.getIdEvent() != eventRemovedOfPromoteds) {
                return event;
            }
        }
        return null;
    }
}
