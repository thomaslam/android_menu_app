package assignment.cs342.thomas.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by tlaminator on 2/11/17.
 */

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Log.d("OptionsActivity", username);

        TextView welcomeTV = (TextView) findViewById(R.id.welcome_user);
        welcomeTV.setText("Hi " + username);
        welcomeTV.setTextSize(getResources().getDimension(R.dimen.text_size));

    }

    public void displayDayMenu(View view) {
        Intent intent = new Intent(this, DisplayDayMenu.class);
        startActivity(intent);
    }
}
