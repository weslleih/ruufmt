package com.wesllei.ruufmt.data;

import android.content.Context;

import com.wesllei.ruufmt.util.Communicator;
import com.wesllei.ruufmt.interfaces.OnAsyncTaskEnd;
import com.wesllei.ruufmt.interfaces.OnAsyncTaskEndJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wesllei on 09/05/15.
 */
public class MealList implements Serializable, OnAsyncTaskEndJson {
    static final long serialVersionUID = 6448518800661853633L;
    private static final String URL_SUFFIX = "/meal/list";
    private static final String FILE_NAME = "menu";
    private transient Context context;
    private transient OnAsyncTaskEnd onAsyncTaskEnd;
    private ArrayList<Meal> meals;
    private Meal lunch;

    public MealList(Context context, OnAsyncTaskEnd onAsyncTaskEnd) {
        this.context = context;
        this.onAsyncTaskEnd = onAsyncTaskEnd;
        try {
            load();
        } catch (Exception e) {
            e.printStackTrace();
            meals = new ArrayList<>();
        }
    }

    public MealList(Context context) {
        this.context = context;
        try {
            load();
        } catch (Exception e) {
            e.printStackTrace();
            meals = new ArrayList<>();
        }
    }

    public void getLast() {
        Communicator communicator = new Communicator(context, URL_SUFFIX, this);
        if (onAsyncTaskEnd != null) {
            communicator.getJson();
        }
    }

    public int size() {
        return meals.size();
    }

    @Override
    public void onAsyncTaskEndJson(JSONObject jsonObject) {
        processJson(jsonObject);
        onAsyncTaskEnd.onAsyncTaskEnd();
    }

    public void processJson(JSONObject jsonObject) {
        ArrayList temp = new ArrayList();
        try {
            temp.add(new Meal(jsonObject.getJSONObject("lunch"), 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            temp.add(new Meal(jsonObject.getJSONObject("dinner"), 1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            temp.add(new Meal(jsonObject.getJSONObject("saturday"), 2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (temp.size() > 0) {
            meals = temp;
            save();
        }
    }

    public ArrayList getList() {
        return meals;
    }

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

    public void load() throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(FILE_NAME);
        ObjectInputStream is = new ObjectInputStream(fis);
        MealList mealList = (MealList) is.readObject();
        this.meals = mealList.getList();
        is.close();
        fis.close();
    }

    public void remove(Card item) {
        meals.remove(item);
    }

    public void remove(int position) {
        meals.remove(position);
    }

    public Meal getMeal(int position) {
        return meals.get(position);
    }

    public void add(Meal meal) {
        meals.add(meal);
    }

    public Meal getMealByType(int i) {
        for(Meal meal:meals){
            if(meal.getType() == i){
                return meal;
            }
        }
        return new Meal(i);
    }
}
