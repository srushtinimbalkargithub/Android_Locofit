package com.rutvik.locofit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.rutvik.locofit.auth.LoginActivity;
import com.rutvik.locofit.auth.RegisterActivity;
import com.rutvik.locofit.exerciseActivities.StartExerciseActivity;
import com.rutvik.locofit.models.User;
import com.rutvik.locofit.util.DBHandler;
import com.rutvik.locofit.util.ImageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends Activity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private TextView showUserFirstName;
    private ImageView logoutBtn, historyBtn;
    private CircleImageView profilePicView;
    private LinearLayout walkingWidget, runningWidget, sprintingWidget, swimmingWidget, bikingWidget, hikingWidget;
    private User user;
    DBHandler dbHandler = new DBHandler(BaseActivity.this);
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        showUserFirstName = findViewById(R.id.showUserFirstName);
        logoutBtn = findViewById(R.id.logoutBtn);
        historyBtn = findViewById(R.id.historyBtn);
        profilePicView = findViewById(R.id.profilePicView);
        walkingWidget = findViewById(R.id.walkingWidget);
        runningWidget = findViewById(R.id.runningWidget);
        sprintingWidget = findViewById(R.id.sprintingWidget);
        swimmingWidget = findViewById(R.id.swimmingWidget);
        bikingWidget = findViewById(R.id.bikingWidget);
        hikingWidget = findViewById(R.id.hikingWidget);
        sharedPreferences = getSharedPreferences("com.rutvik.locofit.SHAREDPREFERENCES", MODE_PRIVATE);
        editor = sharedPreferences.edit();
//        editor.clear();
//        editor.commit();
        String username = sharedPreferences.getString("username", null);
        String password = sharedPreferences.getString("password", null);
//        editor.remove("profileSrc");


        if(username == null || password == null){
            Intent intent = new Intent(BaseActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
        else {
            user = dbHandler.getUser(username, password);
            showUserFirstName.setText("Hi " + user.getFirstName() + "!");
            loadProfilePicture();
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.clear();
                editor.commit();
//                if (dbHandler.deleteUser(user)){
//                    Toast.makeText(BaseActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
//                    dbHandler.deleteAllExcercise(user);
//                }else {
//                    Toast.makeText(BaseActivity.this, "User delection failed", Toast.LENGTH_SHORT).show();
//                }

                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, HistoryActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });

        profilePicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProfilePhoto(view);
            }
        });

        walkingWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExerciseActivity("walking");
            }
        });

        runningWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExerciseActivity("running");
            }
        });

        sprintingWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExerciseActivity("sprinting");
            }
        });

        swimmingWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExerciseActivity("swimming");
            }
        });

        bikingWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExerciseActivity("biking");
            }
        });

        hikingWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExerciseActivity("hiking");
            }
        });
    }

    public void selectProfilePhoto(View view) {
        // Use an Intent to open the image picker
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImageUri = data.getData();
                Bitmap selectedImageBitmap = ImageUtil.getBitmapFromUri(BaseActivity.this, selectedImageUri);
                profilePicView.setImageBitmap(selectedImageBitmap);
                onProfilePictureChosen(selectedImageBitmap);
            }
        }
    }
    private void loadProfilePicture() {
        sharedPreferences = getSharedPreferences("com.rutvik.locofit.SHAREDPREFERENCES", Context.MODE_PRIVATE);
        user.setProfilePicSrc(sharedPreferences.getString("profileSrc", null));
//        user.setProfilePicSrc(dbHandler.getUserProfilePic(user));

        if (user.getProfilePicSrc() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(user.getProfilePicSrc());
            profilePicView.setImageBitmap(bitmap);
        } else {
            profilePicView.setImageResource(R.drawable.defaultprofilepic);
        }
    }

    private void saveProfilePicture(Bitmap bitmap) {
        // Save the profile picture as a file
        try {
            File file = new File(getFilesDir(), "profileSrc");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();

//             Save the file path in SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences("com.rutvik.locofit.SHAREDPREFERENCES", Context.MODE_PRIVATE).edit();
            editor.putString("profileSrc", file.getAbsolutePath());
            editor.apply();
            user.setProfilePicSrc(file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onProfilePictureChosen(Bitmap profilePictureBitmap) {
        saveProfilePicture(profilePictureBitmap);
    }

    private void startExerciseActivity(String exercise) {
        Intent intent = new Intent(BaseActivity.this, StartExerciseActivity.class);
        intent.putExtra("username", user.getUsername());
        intent.putExtra("password", user.getPassword());
        intent.putExtra("exercise", exercise);
        startActivity(intent);
    }
    @Override
    public void onBackPressed(){
        finishAffinity();
    }

}
