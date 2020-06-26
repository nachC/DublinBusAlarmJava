package com.example.dublinbusalarm;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BusRoute implements Parcelable {
    @SerializedName("errorcode")
    @Expose
    String errorCode;
    @SerializedName("errormessage")
    @Expose
    String errorMessage;
    @SerializedName("numberofresults")
    @Expose
    String numberOfResults;
    @SerializedName("route")
    @Expose
    String routeName; //hold the "route" field from the JSON response
    @SerializedName("results")
    @Expose
    List<Route> routes; //holds the "results" list form the JSON response

    public BusRoute() {};

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getNumberOfResults() {
        return numberOfResults;
    }

    public void setNumberOfResults(String numberOfResults) {
        this.numberOfResults = numberOfResults;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    @Override
    public String toString() {
        return "BusRoute{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", numberOfResults='" + numberOfResults + '\'' +
                ", routeName='" + routeName + '\'' +
                ", routes=" + routes +
                '}';
    }

    protected BusRoute(Parcel in) {
        errorCode = in.readString();
        errorMessage = in.readString();
        numberOfResults = in.readString();
        routeName = in.readString();
        if (in.readByte() == 0x01) {
            routes = new ArrayList<Route>();
            in.readList(routes, Route.class.getClassLoader());
        } else {
            routes = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(errorCode);
        dest.writeString(errorMessage);
        dest.writeString(numberOfResults);
        dest.writeString(routeName);
        if (routes == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(routes);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BusRoute> CREATOR = new Parcelable.Creator<BusRoute>() {
        @Override
        public BusRoute createFromParcel(Parcel in) {
            return new BusRoute(in);
        }

        @Override
        public BusRoute[] newArray(int size) {
            return new BusRoute[size];
        }
    };
}
