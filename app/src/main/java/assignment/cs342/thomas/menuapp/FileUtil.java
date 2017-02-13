package assignment.cs342.thomas.menuapp;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by tlaminator on 2/3/17.
 */

public class FileUtil {

    public static ArrayList<MenuSubsection> readFromFile(Context ctx, int menuID) {
        ArrayList<MenuSubsection> msArray = new ArrayList<MenuSubsection>();

//        InputStream inputStream = ctx.getResources().openRawResource(R.raw.val);
//        InputStreamReader reader = new InputStreamReader(inputStream)
//        BufferedReader bf = new BufferedReader(reader);

        String line;

        try {
            InputStreamReader inputStream = new InputStreamReader(ctx.openFileInput(MainActivity.SAVED_MENUS_FILE));
            BufferedReader bf = new BufferedReader(inputStream);
            while ((line = bf.readLine()) != null) {
                String[] parts = line.split(",");
                String type = parts[0];
                Log.d("FileUtil", "type: " + type);

                String subsection = parts[1];
                String items = parts[2];

                String menuType = "";
                switch(menuID) {
                    case R.id.breakfast_id:
                        menuType = "Breakfast";
                        break;
                    case R.id.lunch_id:
                        menuType = "Lunch";
                        break;
                    case R.id.dinner_id:
                        menuType = "Dinner";
                }
                Log.d("FileUtil", "menuType: " + menuType);
                if (type.equals(menuType)) {
                    MenuSubsection m = new MenuSubsection();
                    m.setSubsection(subsection);
                    m.setItems(items);
                    Log.d("FileUtil", menuType + " MenuSubsection added");
                    msArray.add(m);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msArray;
    }
}
