package com.example.dublinbusalarm.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.dublinbusalarm.models.BusRoute;
import com.example.dublinbusalarm.R;
import com.example.dublinbusalarm.models.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class RoutesActivity extends AppCompatActivity {

    BusRoute busRoute;
    ListView routesListView;
    List<Route> routes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        routesListView = findViewById(R.id.routesListView);

        // get the intent with the bus route information
        Intent intent = getIntent();
        busRoute = intent.getParcelableExtra("busRoute");

        // populate the routes object with the routes for this bus route
        assert busRoute != null;
        routes = busRoute.getRoutes();

        // we'll use this predicate to filter the routes to get only the latest ones
        Predicate<Route> byDateUpdated = route -> route.getLastUpdated().matches("(.*)2020(.*)");

        // hashmap to use on the click listener:
        // key: (String) route origin -> destination
        // value: (Route) route
        Map<String, Route> updatedRoutes = new HashMap<>();

        // array list necessary for displaying the list view
        // contains the route's "origin -> destination" string
        ArrayList<String> originToDestination = new ArrayList<>();

        for (Route route : routes) {
            if (byDateUpdated.test(route)) {
                updatedRoutes.put(route.getOrigin() + " to " + route.getDestination(), route);
                originToDestination.add(route.getOrigin() + " to " + route.getDestination());
            }
        }

        // display the list view with the routes
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, originToDestination);
        routesListView.setAdapter(arrayAdapter);

        // we set the listener for the list view
        // clicking an item (route) will start the maps activity with the route information
        routesListView.setOnItemClickListener((parent, view, position, id) -> {
            // start Google Maps Activity
            Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
            mapIntent.putExtra("route", updatedRoutes.get(parent.getItemAtPosition(position).toString()));
            startActivity(mapIntent);
        });
    }
}
