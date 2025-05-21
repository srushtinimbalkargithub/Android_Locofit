package com.rutvik.locofit.exerciseActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rutvik.locofit.R;
import com.rutvik.locofit.models.Biking;
import com.rutvik.locofit.models.Hiking;
import com.rutvik.locofit.models.Running;
import com.rutvik.locofit.models.Sprinting;
import com.rutvik.locofit.models.Swimming;
import com.rutvik.locofit.models.User;
import com.rutvik.locofit.models.Walking;
import com.rutvik.locofit.util.DBHandler;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class StatisticsActivity extends Activity implements OnMapReadyCallback {
    private TextView statisticsExerciseLabel, nameView, dateView;
    private TextView attributeLabel1, attributeLabel2, attributeLabel3, attributeLabel4, attributeLabel5, attributeLabel6, attributeLabel7;
    private TextView attribute1, attribute2, attribute3, attribute4, attribute5, attribute6, attribute7;

    private ImageView statisticsProfilepic, exerciseImg;
    private MapView statisticsMapView;
    private GoogleMap myMap;
    private List<LatLng> pathPoints;
    private SharedPreferences sharedPreferences;
    private User user;

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Initialize Views
        statisticsMapView = findViewById(R.id.statisticsMapView);
        statisticsMapView.onCreate(savedInstanceState);
        statisticsMapView.getMapAsync(this);

        statisticsExerciseLabel = findViewById(R.id.statisticsExerciseLabel);
        nameView = findViewById(R.id.nameView);
        dateView = findViewById(R.id.dateView);
        statisticsProfilepic = findViewById(R.id.statisticsProfilepic);
        exerciseImg = findViewById(R.id.exerciseImg);
        attributeLabel1 = findViewById(R.id.attributeLabel1);
        attributeLabel2 = findViewById(R.id.attributeLabel2);
        attributeLabel3 = findViewById(R.id.attributeLabel3);
        attributeLabel4 = findViewById(R.id.attributeLabel4);
        attributeLabel5 = findViewById(R.id.attributeLabel5);
        attributeLabel6 = findViewById(R.id.attributeLabel6);
        attributeLabel7 = findViewById(R.id.attributeLabel7);
        attribute1 = findViewById(R.id.attribute1);
        attribute2 = findViewById(R.id.attribute2);
        attribute3 = findViewById(R.id.attribute3);
        attribute4 = findViewById(R.id.attribute4);
        attribute5 = findViewById(R.id.attribute5);
        attribute6 = findViewById(R.id.attribute6);
        attribute7 = findViewById(R.id.attribute7);

        // Hide unused attributes initially
        attributeLabel5.setVisibility(View.GONE);
        attributeLabel6.setVisibility(View.GONE);
        attributeLabel7.setVisibility(View.GONE);
        attribute5.setVisibility(View.GONE);
        attribute6.setVisibility(View.GONE);
        attribute7.setVisibility(View.GONE);

        // Get user data and set up statistics
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String type = intent.getStringExtra("type");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");

        sharedPreferences = getSharedPreferences("com.rutvik.locofit.SHAREDPREFERENCES", Context.MODE_PRIVATE);
        dbHandler = new DBHandler(StatisticsActivity.this);
        user = dbHandler.getUser(username, password);

        // Load user profile picture
        String imagePath = sharedPreferences.getString("profileSrc", null);
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            statisticsProfilepic.setImageBitmap(bitmap);
        } else {
            statisticsProfilepic.setImageResource(R.drawable.defaultprofilepic);
        }

        // Format and display date and time
        formatDateAndTime(date, time);

        // Set user name
        nameView.setText(user.getFirstName() + " " + user.getLastName());

        // Deserialize path points
        pathPoints = loadStatistics(type, date, time);
    }

    private void formatDateAndTime(String date, String time) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("hh:mm a");

        try {
            Date inputDate = inputDateFormat.parse(date);
            String outputDateString = outputDateFormat.format(inputDate);
            Date inputTime = inputTimeFormat.parse(time);
            String outputTime = outputTimeFormat.format(inputTime);
            dateView.setText(outputDateString + ", " + outputTime);
        } catch (ParseException e) {
            e.printStackTrace(); // Log the error for debugging
        }
    }

    private List<LatLng> loadStatistics(String type, String date, String time) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<LatLng>>() {}.getType();
        List<LatLng> pathPoints = null;

        switch (type) {
            case "walking":
                Walking walking = dbHandler.getWalking(user, date, time);
                updateStatistics(walking.getExerciseType(), walking.getImgResource(), walking.getDistance(),
                        walking.getDuration(), walking.getCaloriesBurned(), walking.getSpeed(),
                        walking.getStepCount(), null, null, null);
                pathPoints = gson.fromJson(walking.getLocation(), listType);
                break;
            case "hiking":
                Hiking hiking = dbHandler.getHiking(user, date, time);
                updateStatistics(hiking.getExerciseType(), hiking.getImgResource(), hiking.getDistance(),
                        hiking.getDuration(), hiking.getCaloriesBurned(), hiking.getSpeed(),
                        null, hiking.getElevationGain(), hiking.getTerrainDifficultyRating(), null);
                pathPoints = gson.fromJson(hiking.getLocation(), listType);
                break;
            case "biking":
                Biking biking = dbHandler.getBiking(user, date, time);
                updateStatistics(biking.getExerciseType(), biking.getImgResource(), biking.getDistance(),
                        biking.getDuration(), biking.getCaloriesBurned(), biking.getSpeed(),
                        null, biking.getElevationGain(), biking.getType(), null);
                pathPoints = gson.fromJson(biking.getLocation(), listType);
                break;
            case "running":
                Running running = dbHandler.getRunning(user, date, time);
                updateStatistics(running.getExerciseType(), running.getImgResource(), running.getDistance(),
                        running.getDuration(), running.getCaloriesBurned(), running.getSpeed(),
                        null, null, null, null);
                pathPoints = gson.fromJson(running.getLocation(), listType);
                break;
            case "sprinting":
                Sprinting sprinting = dbHandler.getSprinting(user, date, time);
                // Updated to pass null for stepsCount, and add acceleration to the correct argument slot
                updateStatistics(sprinting.getExerciseType(), R.drawable.sprinting, sprinting.getDistance(),
                        sprinting.getDuration(), sprinting.getCaloriesBurned(), sprinting.getSpeed(),
                        null, null, null, sprinting.getAcceleration());
                pathPoints = gson.fromJson(sprinting.getLocation(), listType);
                break;
            case "swimming":
                Swimming swimming = dbHandler.getSwimming(user, date, time);
                updateStatistics(swimming.getExerciseType(), R.drawable.swimming, swimming.getDistance(),
                        swimming.getDuration(), swimming.getCaloriesBurned(), null,
                        null, null, swimming.getStyle(), null);
                pathPoints = gson.fromJson(swimming.getLocation(), listType);
                break;
            default:
                break;
        }
        return pathPoints;
    }

    private void updateStatistics(String exerciseType, int imgResource, Double distance, String duration,
                                  Double caloriesBurned, Double speed, Integer stepsCount,
                                  Double elevationGain, String terrainOrBikingType, Double acceleration) {
        statisticsExerciseLabel.setText(exerciseType.toUpperCase() + " Statistics");
        exerciseImg.setImageResource(imgResource);
        attributeLabel1.setText("Distance");
        attribute1.setText(String.format("%.2f km", (distance * 0.001)));
        attributeLabel2.setText("Duration");
        attribute2.setText(duration);
        attributeLabel3.setText("Calories Burned");
        attribute3.setText(String.format("%.2f", caloriesBurned) + " kcal");

        // Show step count or speed
        if (stepsCount != null) {
            attributeLabel4.setText("Step Count");
            attribute4.setText(String.valueOf(stepsCount));
        } else if (speed != null) {
            attributeLabel4.setText("Average Speed");
            attribute4.setText(String.format("%.2f km/h", speed));
        }

        // Handle additional attributes for specific exercises
        if (elevationGain != null) {
            attributeLabel5.setVisibility(View.VISIBLE);
            attributeLabel5.setText("Elevation Gain");
            attribute5.setVisibility(View.VISIBLE);
            attribute5.setText(String.format("%.2f m", elevationGain));
        }

        if (terrainOrBikingType != null) {
            attributeLabel6.setVisibility(View.VISIBLE);
            attributeLabel6.setText("Terrain Type");
            attribute6.setVisibility(View.VISIBLE);
            attribute6.setText(terrainOrBikingType);
        }

        if (acceleration != null) {
            attributeLabel7.setVisibility(View.VISIBLE);
            attributeLabel7.setText("Acceleration");
            attribute7.setVisibility(View.VISIBLE);
            attribute7.setText(String.format("%.2f m/s²", acceleration));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        if (pathPoints != null && !pathPoints.isEmpty()) {
            PolylineOptions polylineOptions = new PolylineOptions().clickable(false);
            polylineOptions.color(ContextCompat.getColor(this, R.color.polyline_color));

            for (LatLng point : pathPoints) {
                polylineOptions.add(point);
            }

            Polyline polyline = myMap.addPolyline(polylineOptions);

            LatLng startPoint = pathPoints.get(0);
            LatLng endPoint = pathPoints.get(pathPoints.size() - 1);

            // Add start and end markers
            myMap.addMarker(new MarkerOptions().position(startPoint).title("Start"));
            myMap.addMarker(new MarkerOptions().position(endPoint).title("End"));

            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 16.5f));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        statisticsMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        statisticsMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        statisticsMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        statisticsMapView.onLowMemory();
    }
}
