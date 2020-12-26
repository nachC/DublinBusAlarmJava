# Dublin Bus Alarm

Location based alarm app for Dublin Bus Service.

Dublin Bus Alarm is a native android app (Java), where the user can choose a Bus stop (from the Dublin Bus Service) from Google Maps and an alarm will let the user know when the stop was reached.

1st -> user inputs the bus line

2nd -> select the route direction (inbound/outbound)

3rd -> user selects the stop where they want the alarm to be set off, by tapping a marker in Google Maps

4th -> when the the user is close to the selected stop, the alarm will be set off.

The GTFS information was processed using Python to extract the exact data needed for the app. Said data was exported in JSON format to be used in Firebase Realtime DB.
