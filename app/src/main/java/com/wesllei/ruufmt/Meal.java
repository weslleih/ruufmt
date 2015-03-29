package com.wesllei.ruufmt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Meal extends CardData implements Serializable {
    private static final long serialVersionUID = 0x98ED2F00;
    private static final String monthList[] = {"jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez"};
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

    public Meal(JSONObject jsonObject, int type) {
        this.type = type;
        try {
            setPp(jsonObject.getString("pp"));
        } catch (Exception e) {
            this.pp = "";
        }
        try {
            setOv(jsonObject.getString("ov"));
        } catch (Exception e) {
            this.ov = "";
        }
        try {
            setSa(jsonObject.getString("sa"));
        } catch (Exception e) {
            this.sa = "";
        }
        try {
            setGu(jsonObject.getString("gu"));
        } catch (Exception e) {
            this.gu = "";
        }
        try {
            setAc(jsonObject.getString("ac"));
        } catch (Exception e) {
            this.ac = "";
        }
        try {
            setSo(jsonObject.getString("so"));
        } catch (Exception e) {
            this.so = "";
        }
        try {
            setSu(jsonObject.getString("su"));
        } catch (Exception e) {
            this.su = "";
        }
        try {
            setDate(jsonObject.getString("date"));
        } catch (Exception e) {
            this.date = new Date();
        }

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

    public String getFormatedDate() {
        Integer day, month, year;
        StringBuilder strDate = null;
        Date now = new Date();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.date);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        cal.setTime(now);
        if (year == cal.get(Calendar.YEAR) && month == cal.get(Calendar.MONTH)) {
            if (day == cal.get(Calendar.DAY_OF_MONTH)) {
                strDate = new StringBuilder("Hoje");
            } else {
                if (day == cal.get(Calendar.DAY_OF_MONTH) - 1) {
                    strDate = new StringBuilder("Ontem");
                } else {
                    strDate = new StringBuilder().append(day).append(" ").append(this.monthList[month]).append(" ").append(year);
                }
            }
        } else {
            strDate = new StringBuilder(day).append(day).append(" ").append(this.monthList[month]).append(" ").append(year);
        }
        return String.valueOf(strDate);
    }

    ;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
