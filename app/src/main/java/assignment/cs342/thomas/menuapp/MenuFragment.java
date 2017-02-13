package assignment.cs342.thomas.menuapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tlaminator on 2/1/17.
 */
public class MenuFragment extends Fragment {
    ListView menuSubLV;
    String menuType;

    public MenuFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        TextView menuHeadingTV =  (TextView) v.findViewById(R.id.menu_heading);

        int mealType = getArguments().getInt(DisplayDayMenu.EXTRA_MEAL_MENU);
        String menuHeading = "";
        switch(mealType) {
            case R.id.breakfast_id:
                menuHeading = "Breakfast";
                break;
            case R.id.lunch_id:
                menuHeading = "Lunch";
                break;
            case R.id.dinner_id:
                menuHeading = "Dinner";
        }

        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = Calendar.getInstance().getTime();
        String dateStr = formatter.format(date);

        menuHeadingTV.setText(menuHeading + " " + dateStr);

        menuSubLV = (ListView) v.findViewById(R.id.menu_subsection_list_view);

        // TODO: change this to request URL instead
        ArrayList<MenuSubsection> mSubArray = FileUtil.readFromFile(getActivity(),
                mealType);

        MenuSubsectionArrayAdapter adapter = new MenuSubsectionArrayAdapter(getActivity(),
                mSubArray);
        menuSubLV.setAdapter(adapter);
        return v;
    }
}
