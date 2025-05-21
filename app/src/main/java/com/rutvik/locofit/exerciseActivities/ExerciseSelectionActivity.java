package com.rutvik.locofit.exerciseActivities;
import com.rutvik.locofit.R;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ExerciseSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_selection);

        // Buttons for selecting exercises
        Button exercise1Button = findViewById(R.id.exercise1Button);
        Button exercise2Button = findViewById(R.id.exercise2Button);

        // Set up onClickListeners for each button
        exercise1Button.setOnClickListener(v -> promptForDemo("Exercise 1", "https://www.youtube.com/watch?v=gYajoeR_UF8&ab_channel=JamesDunne"));
        exercise2Button.setOnClickListener(v -> promptForDemo("Exercise 2", "https://www.youtube.com/watch?v=AdqrTg_hpEQ&ab_channel=WalkatHome"));
    }

    private void promptForDemo(String exerciseName, String videoUrl) {
        // Show a dialog to ask the user if they want to see the demo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(exerciseName);
        builder.setMessage("Do you want to see the demo for " + exerciseName + "?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Launch the demo video activity with the video URL
            Intent intent = new Intent(ExerciseSelectionActivity.this, ExerciseVideoActivity.class);
            intent.putExtra("VIDEO_URL", videoUrl);  // Pass the video URL to the next activity
            startActivity(intent);
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
