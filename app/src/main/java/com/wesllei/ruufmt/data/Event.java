package com.wesllei.ruufmt.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wesllei on 24/03/15.
 */
public class Event extends Card implements Serializable {

    private static final String monthList[] = {"janeiro", "fevereiro", "março", "abrril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};
    private int idEvent;

    private String name;
    private String mail;
    private String phone;
    private String phoneClaro;
    private String phoneOi;
    private String phoneTim;
    private String phoneVivo;
    private String imageCardUrl;
    private String imageCardFile;
    private String imageBannerUrl;
    private String imageBannerFile;
    private String pageUrl;
    private String facebookUrl;
    private String placeName;
    private String placeMapURL;
    private Date date;
    private Date promoteDate;
    private String price;
    private String priceFemale;
    private String whatsapp;
    private String desc;
    private String imageIconFile;
    private String imageIconUrl;

    public Event() {
        this.setImageCardFile("");
    }

    public Event(JSONObject jsonObject) throws JSONException {

        setIdEvent(Integer.parseInt(jsonObject.getString("idevent")));
        setName(jsonObject.getString("name"));
        setDesc(jsonObject.getString("desc"));
        setMail(jsonObject.getString("mail"));
        setPhone(jsonObject.getString("phone"));
        setPhoneClaro(jsonObject.getString("phoneClaro"));
        setPhoneOi(jsonObject.getString("phoneOi"));
        setPhoneTim(jsonObject.getString("phoneTim"));
        setPhoneVivo(jsonObject.getString("phoneVivo"));
        setImageCardUrl(jsonObject.getString("imageCardUrl"));
        setImageCardFile("");
        setImageIconUrl(jsonObject.getString("imageIcondUrl"));
        setImageIconFile("");
        setImageBannerUrl(jsonObject.getString("imageBannerUrl"));
        setImageBannerFile("");
        setPageUrl(jsonObject.getString("pageUrl"));
        setFacebookUrl(jsonObject.getString("facebookUrl"));
        setPlaceName(jsonObject.getString("placeName"));
        setPlaceMapURL(jsonObject.getString("placeMapURL"));
        setDate(jsonObject.getString("date"));
        setPromoteDate(jsonObject.getString("promoteDate"));
        setPrice(jsonObject.getString("price"));
        setPriceFemale(jsonObject.getString("priceFemale"));
        setWhatsapp(jsonObject.getString("whatsapp"));
    }

    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int id) {
        this.idEvent = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneClaro() {
        return phoneClaro;
    }

    public void setPhoneClaro(String phoneClaro) {
        this.phoneClaro = phoneClaro;
    }

    public String getPhoneOi() {
        return phoneOi;
    }

    public void setPhoneOi(String phoneOi) {
        this.phoneOi = phoneOi;
    }

    public String getPhoneTim() {
        return phoneTim;
    }

    public void setPhoneTim(String phoneTim) {
        this.phoneTim = phoneTim;
    }

    public String getPhoneVivo() {
        return phoneVivo;
    }

    public void setPhoneVivo(String phoneVivo) {
        this.phoneVivo = phoneVivo;
    }

    public String getImageCardUrl() {
        return imageCardUrl;
    }

    public void setImageCardUrl(String imageCardUrl) {
        this.imageCardUrl = imageCardUrl;
    }

    public String getImageCardFile() {
        return imageCardFile;
    }

    public void setImageCardFile(String imageCardFile) {
        this.imageCardFile = imageCardFile;
    }

    public String getImageBannerUrl() {
        return imageBannerUrl;
    }

    public void setImageBannerUrl(String imageBannerUrl) {
        this.imageBannerUrl = imageBannerUrl;
    }

    public String getImageBannerFile() {
        return imageBannerFile;
    }

    public void setImageBannerFile(String imageBannerFile) {
        this.imageBannerFile = imageBannerFile;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceMapURL() {
        return placeMapURL;
    }

    public void setPlaceMapURL(String placeMapURL) {
        this.placeMapURL = placeMapURL;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = null;
        try {
            newDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.date = newDate;
    }

    public String getDateStrig() {
        if (date == null) {
            return "";
        }
        Integer hour, minute, day, month, year;
        StringBuilder strDate = new StringBuilder();
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.date);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        cal.setTime(now);
        if (year == cal.get(Calendar.YEAR)) {
            if (month == cal.get(Calendar.MONTH)) {
                if (day == cal.get(Calendar.DAY_OF_MONTH)) {
                    strDate.append("Hoje");
                } else if (day + 1 == cal.get(Calendar.DAY_OF_MONTH)) {
                    strDate.append("Amanhã");
                } else {
                    strDate.append(day).append(" de ").append(this.monthList[month]);
                }
            } else {
                strDate.append(day).append(" de ").append(this.monthList[month]);
            }
        } else {
            strDate.append(day).append(" de ").append(this.monthList[month]).append(" de ").append(year);
        }
        strDate.append(" às " + hour + ":" + minute);
        return String.valueOf(strDate);
    }

    public void setPromoteDate(String date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = new Date();
        try {
            newDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.promoteDate = newDate;
    }

    public Date getPromoteDate() {
        return promoteDate;
    }

    public void setPromoteDate(Date promoteDate) {
        this.promoteDate = promoteDate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceFemale() {
        return priceFemale;
    }

    public void setPriceFemale(String priceFemale) {
        this.priceFemale = priceFemale;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageIconFile() {
        return imageIconFile;
    }

    public void setImageIconFile(String imageIconFile) {
        this.imageIconFile = imageIconFile;
    }

    public void setImageIconUrl(String imageIconUrl) {
        this.imageIconUrl = imageIconUrl;
    }

    public String getImageIconUrl() {
        return imageIconUrl;
    }
}
