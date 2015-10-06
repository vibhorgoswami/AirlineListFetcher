package com.vg.airlinelistapplication.airlines.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * This is the POJO class which is used for GSON.
 * Created by vgoswami on 9/30/15.
 */
public class AirlineClass implements Serializable {

    @SerializedName("__clazz")
    private String __clazz;

    @SerializedName("code")
    private String code;

    @SerializedName("logoURL")
    private String logoURL;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("site")
    private String site;


    public String getClazz() {
        return __clazz;
    }

    public void set__clazz(String clazz) {
        this.__clazz = clazz;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
