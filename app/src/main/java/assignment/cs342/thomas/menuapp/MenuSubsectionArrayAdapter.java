package assignment.cs342.thomas.menuapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tlaminator on 2/1/17.
 */
public class MenuSubsectionArrayAdapter extends ArrayAdapter<MenuSubsection> {
    private final Context context;
    ArrayList<MenuSubsection> msArray;

    String priorSection = "";

    public MenuSubsectionArrayAdapter(Context ctx, ArrayList<MenuSubsection> msArray) {
        super(ctx, R.layout.menu_subsection);
        this.context = ctx;

        this.msArray = msArray;
        Log.d("MSArrayAdapter", "Read " + msArray.size() + " MenuSection objects");
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.menu_subsection, parent, false);
        TextView textView1 = (TextView) rowView.findViewById(R.id.section);
        TextView textView2 = (TextView) rowView.findViewById(R.id.items);

        String subsection = msArray.get(position).getSubsection();
        textView1.setText(subsection);

        String items = msArray.get(position).getItems();
        textView2.setText(items);
        return rowView;
    }

    public int getCount() { return msArray.size(); }
    public MenuSubsection getItem(int position) { return msArray.get(position);}
    public long getItemId(int position) { return position; }
}
