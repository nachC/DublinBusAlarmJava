package com.example.dublinbusalarm;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Stop implements Parcelable {

    @SerializedName("stopid")
    @Expose
    private int stopid; //holds 'stopid' field from JSON response
    @SerializedName("shortname")
    @Expose
    private String name; //holds 'shortname' field from JSON response
    @SerializedName("latitude")
    @Expose
    private float lat; //holds 'latitude' field from the JSON response
    @SerializedName("longitude")
    @Expose
    private float lng; //holds 'longitude' field form the JSON response

    public Stop() {}

    public int getStopid() {
        return stopid;
    }

    public void setStopid(int stopid) {
        this.stopid = stopid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Stop{" +
                "stopid=" + stopid +
                ", name='" + name + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }

    protected Stop(Parcel in) {
        stopid = in.readInt();
        name = in.readString();
        lat = in.readFloat();
        lng = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(stopid);
        dest.writeString(name);
        dest.writeFloat(lat);
        dest.writeFloat(lng);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Stop> CREATOR = new Parcelable.Creator<Stop>() {
        @Override
        public Stop createFromParcel(Parcel in) {
            return new Stop(in);
        }

        @Override
        public Stop[] newArray(int size) {
            return new Stop[size];
        }
    };
}

