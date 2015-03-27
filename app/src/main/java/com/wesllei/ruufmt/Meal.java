package com.wesllei.ruufmt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Meal extends CardData implements Serializable {
    private static final long serialVersionUID = 0x98ED2F00;
    private String pp;
    private String ov;
    private String sa;
    private String gu;
    private String ac;
    private String so;
    private String su;
    private Date date;
    private int type;

    public Meal(int type) {
        this.type = type;
    }
    public Meal(JSONObject jsonObject,int type) throws JSONException {
        setPp(jsonObject.getString("pp"));
        setOv(jsonObject.getString("ov"));
        setSa(jsonObject.getString("sa"));
        setGu(jsonObject.getString("gu"));
        setAc(jsonObject.getString("ac"));
        setSo(jsonObject.getString("so"));
        setSu(jsonObject.getString("su"));
        this.type = type;
        this.date = new Date();
    }
    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public String getOv() {
        return ov;
    }

    public void setOv(String ov) {
        this.ov = ov;
    }

    public String getSa() {
        return sa;
    }

    public void setSa(String sa) {
        this.sa = sa;
    }

    public String getGu() {
        return gu;
    }

    public void setGu(String gu) {
        this.gu = gu;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getSo() {
        return so;
    }

    public void setSo(String so) {
        this.so = so;
    }

    public String getSu() {
        return su;
    }

    public void setSu(String su) {
        this.su = su;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(String date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = (Date) formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.date = newDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
