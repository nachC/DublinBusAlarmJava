package com.example.dublinbusalarm;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Route implements Parcelable {

    @SerializedName("origin")
    @Expose
    String origin; //holds 'origin' field from the JSON response
    @SerializedName("destination")
    @Expose
    String destination; //holds 'destination' field from the JSON response
    @SerializedName("lastupdated")
    @Expose
    String lastUpdated;
    @SerializedName("stops")
    @Expose
    List<Stop> stops; //holds the 'stops' list from the JSON response

    public Route() {};

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    @Override
    public String toString() {
        return "Route{" +
                "origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", stops=" + stops +
                '}';
    }

    protected Route(Parcel in) {
        origin = in.readString();
        destination = in.readString();
        lastUpdated = in.readString();
        if (in.readByte() == 0x01) {
            stops = new ArrayList<Stop>();
            in.readList(stops, Stop.class.getClassLoader());
        } else {
            stops = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(origin);
        dest.writeString(destination);
        dest.writeString(lastUpdated);
        if (stops == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(stops);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}

