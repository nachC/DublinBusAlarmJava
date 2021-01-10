package com.example.dublinbusalarm.models;

import java.io.Serializable;
import java.util.ArrayList;

import static java.lang.Double.parseDouble;
/**
 * A Route is identified by a route name or id (e.g. 66b)
 * Each route will do different Trips.
 * Each Trip has an Origin and a Destination,
 * a sequence of Stops that the bus follows,
 * a sequence of coordinates called a Shape, used to draw a 'pretty' trajectory on the map.
 * Each Stop has a name and a set of coordinates (lat, lng).
 * */
public class Route implements Serializable {

    private ArrayList<Trip> trips = new ArrayList<>();

    public static class Trip implements Serializable{

        // name of the origin for a trip (e.g. name of a street or square)
        private String origin;
        // name of the destination for a trip (e.g. name of a street or square)
        private String destination;
        // all the coordinate pairs for a trip's path
        // this is for drawing the path in a more aesthetically pleasing way (as per GTFS spec)
        private ArrayList<ArrayList<String>> shapePoints = new ArrayList<>();
        // consider making this a LinkedList? stops for a route
        // come in a particular order (sequence of stops)
        private ArrayList<Stop> stops = new ArrayList<>();

        public static class Stop implements Serializable {

            private String name;
            private ArrayList<String> latlng = new ArrayList<>();

            public Stop() {}

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public ArrayList<String> getLatlng() {
                return latlng;
            }

            public double getLat() {
                return parseDouble(this.getLatlng().get(0));
            }

            public double getLng() {
                return parseDouble(this.getLatlng().get(1));
            }

        }

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

        public ArrayList<ArrayList<String>> getShapePoints() {
            return shapePoints;
        }

        public double getShapePtLat(int point) {
            return parseDouble(this.shapePoints.get(point).get(0));
        }

        public double getShapePtLng(int point) {
            return parseDouble(this.shapePoints.get(point).get(1));
        }

        public void setShapePoints(ArrayList<ArrayList<String>> shapePoints) {
            this.shapePoints = shapePoints;
        }

        public ArrayList<Stop> getStops() {
            return stops;
        }

        public void setStops(ArrayList<Stop> stops) {
            this.stops = stops;
        }
    }

    public ArrayList<Trip> getTrips() {
        return trips;
    }

    public void setTrips(ArrayList<Trip> trips) {
        this.trips = trips;
    }
    public void setTrips(Trip trip) { this.trips.add(trip); }
}

