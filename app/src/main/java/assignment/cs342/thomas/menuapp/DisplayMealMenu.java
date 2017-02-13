package assignment.cs342.thomas.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by tlaminator on 2/1/17.
 */
public class DisplayMealMenu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_meal_menu);

        Intent intent = getIntent();
        int mealID = intent.getIntExtra(DisplayDayMenu.EXTRA_MEAL_MENU, -1);
        Log.d("DisplayMealMenu", "Retrieved intent payload " +mealID);

        FragmentManager fm = getSupportFragmentManager();
        Fragment menu_frag = fm.findFragmentById(R.id.menu_fragment_container);
        if (menu_frag == null) {
            menu_frag = new MenuFragment();

            Bundle args = new Bundle();
            args.putInt(DisplayDayMenu.EXTRA_MEAL_MENU, mealID);
            menu_frag.setArguments(args);
            fm.beginTransaction().add(R.id.menu_fragment_container, menu_frag).commit();
        }
    }
}
