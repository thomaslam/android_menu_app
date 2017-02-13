package assignment.cs342.thomas.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by tlaminator on 2/1/17.
 */
public class DisplayDayMenu extends AppCompatActivity {
    public final static String EXTRA_MEAL_MENU = "assignment.cs342.thomas.menuapp.MEALMENU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_day_menu);
    }

    public void displayMealMenu(View view) {
        Log.d("DisplayDayMenu", view.toString());
        int mealID = view.getId();
        Log.d("DisplayDayMenu", "Intent payload "+mealID);
        Intent intent = new Intent(this, DisplayMealMenu.class);
        intent.putExtra(EXTRA_MEAL_MENU, mealID);
        switch(mealID) {
            case R.id.breakfast_id:
                Log.d("DisplayDayMenu", "Breakfast Menu");
                break;
            case R.id.lunch_id:
                Log.d("DisplayDayMenu", "Lunch Menu");
                break;
            case R.id.dinner_id:
                Log.d("DisplayDayMenu", "Dinner Menu");
                break;
        }
        startActivity(intent);
    }
}
