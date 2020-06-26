package com.example.dublinbusalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

        Intent intent = getIntent();
        busRoute = intent.getParcelableExtra("busRoute");

        routes = busRoute.getRoutes();

        Predicate<Route> byDateUpdated = route -> route.getLastUpdated().matches("(.*)2020(.*)");

        // consider putting this in a helper function
        Map<String, Route> updatedRoutes = new HashMap<>();
        ArrayList<String> originToDestination = new ArrayList<>();
        for (Route route : routes) {
            if (byDateUpdated.test(route)) {
                updatedRoutes.put(route.getOrigin() + " -> " + route.getDestination(), route);
                originToDestination.add(route.getOrigin() + " -> " + route.getDestination());
            }
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, originToDestination);
        routesListView.setAdapter(arrayAdapter);

        routesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Item clicked", parent.getItemAtPosition(position).toString());


                // Start Google Maps
                Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
                mapIntent.putExtra("route", updatedRoutes.get(parent.getItemAtPosition(position).toString()));
                startActivity(mapIntent);
            }
        });
    }
}
