package com.example.dublinbusalarm.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.dublinbusalarm.models.Route;
import com.example.dublinbusalarm.models.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseCall {

    private final String TAG = "FirebaseCall";

    public void FirebaseCall() {}

    // This methods gets the route data from the dataSnapshot object
    // and populates and returns a route instance with said data
    @NonNull
    public Route getRouteData(@NonNull DataSnapshot dataSnapshot) {
        Route route = new Route();
        Route.Trip trip;
        ArrayList<ArrayList<String>> shape;
        ArrayList<Route.Trip.Stop> stops;

        if (dataSnapshot.getValue() == null) {
            Log.d(TAG, "No results found");
            return null;
        }
        for (DataSnapshot tripData : dataSnapshot.getChildren()) {
            trip = new Route.Trip();
            shape = new ArrayList<>();
            stops = new ArrayList<>();

            for ( DataSnapshot shapeData : tripData.child("shape").getChildren()) {
                // shapeData is [lat, lng] for shape point
                shape.add((ArrayList<String>) shapeData.getValue());
            }
            for ( DataSnapshot stopData: tripData.child("stopSequence").getChildren()) {
                stops.add(stopData.getValue(Route.Trip.Stop.class));
            }
            // set all trip instance properties
            trip.setDestination(tripData.child("destination").getValue().toString());
            trip.setOrigin(tripData.child("origin").getValue().toString());
            trip.setShapePoints(shape);
            trip.setStops(stops);
            // set the trip for this route
            route.setTrips(trip);
        }
        return route;
    }

    // This method saves the session data to the database
    public void saveSessionData(DatabaseReference databaseRef, Session session) {
        DatabaseReference sessionRef = databaseRef.child("sessions");
        DatabaseReference newSessionRef = sessionRef.push();
        newSessionRef.setValue(session);
    }

    private void deleteAllSessionData(DatabaseReference databaseRef) {
        databaseRef.child("sessions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
