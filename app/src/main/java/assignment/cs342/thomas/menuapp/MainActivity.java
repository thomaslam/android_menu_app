package assignment.cs342.thomas.menuapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {
    public final static String DINING_URL = "https://www.amherst.edu/campuslife/housing-dining/dining/menu";
    public final static String SAVED_MENUS_FILE = "val_menus";
    private boolean stopValMenuTask = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ValMenuTask().execute(this);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.activity_main);
        if (fragment == null) {
            fragment = new MainFragment();
            fm.beginTransaction().add(R.id.activity_main, fragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        stopValMenuTask = true;
        super.onDestroy();
    }

    public void goToMapActivity(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void thirdPartySignIn(View view) {
        Intent intent = new Intent(this, ThirdPartySignInActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public void signIn(View view) {
        EditText usernameET = (EditText) findViewById(R.id.username_sign_in);
        EditText passwordET = (EditText) findViewById(R.id.password_sign_in);
        String input_username = usernameET.getText().toString();
        String input_password = passwordET.getText().toString();

        BufferedReader br = null;

        Context ctx = getApplicationContext();
        CharSequence successfulSignIn = "Succesfully signed in!";
        CharSequence failSignIn = "No user found!";
        int duration = Toast.LENGTH_SHORT;
        boolean found = false;

        try {
            InputStreamReader reader = new InputStreamReader(openFileInput(SignUpActivity.FILE_NAME));
            br = new BufferedReader(reader);
            String line = "";
            while ((line = br.readLine()) != null) {
                Log.d("MainActivity", "line: " + line);
                String[] tokens = line.split("\t\t");
                if (tokens.length < 2) {
                    continue;
                }
                String username = tokens[0];
                String password = tokens[1];

                if (input_username.equals(username) && input_password.equals(password)) {
                    found = true;
                    Toast toast = Toast.makeText(ctx, successfulSignIn, duration);
                    toast.show();

                    Intent intent = new Intent(this, OptionsActivity.class);
                    intent.putExtra("username", username);

                    startActivity(intent);
                }
            }
            if (!found) {
                Log.d("MainActivity", "No username found");

                Toast toast = Toast.makeText(ctx, failSignIn, duration);
                toast.show();
            }
        } catch (IOException e) {
            Log.e("MainActivity", "Can't read account_names file");
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                Log.e("MainActivity", "Can't close account_names file");
            }
        }
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private class ValMenuTask extends AsyncTask<Context, Integer, Void> {
        protected Void doInBackground(Context... ctxs) {
            Context ctx = ctxs[0];
            Format formatter = new SimpleDateFormat("yyyy-MM-dd");
            OutputStreamWriter writer = null;
            SharedPreferences sharedPref = ctx.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
//            while(!stopValMenuTask) {

            Date date = Calendar.getInstance().getTime();
            String dateStr = formatter.format(date);

            String savedDate = sharedPref.getString(getString(R.string.saved_date_key), null);
            Log.d("MainActivity", "savedDate: " + savedDate);

            try {
                writer = new OutputStreamWriter(openFileOutput(SAVED_MENUS_FILE,
                        Context.MODE_PRIVATE));

                Document doc = Jsoup.connect(DINING_URL).get();

                Elements breakfastMenu = doc.select("div#dining-menu-" + dateStr + "-Breakfast-menu-listing")
                        .first().children();
                Elements lunchMenu = doc.select("div#dining-menu-" + dateStr + "-Lunch-menu-listing")
                        .first().children();
                Elements dinnerMenu = doc.select("div#dining-menu-" + dateStr + "-Dinner-menu-listing")
                        .first().children();

                for (Element e : breakfastMenu) {
                    Log.d("MainActivity", e.html());
                    if (e.className().equals("dining-course-name")) {
                        writer.write("Breakfast, " + e.html() + ", ");
                    } else {
                        writer.write(e.html() + "\n");
                    }
                }

                for (Element e : lunchMenu) {
                    if (e.className().equals("dining-course-name")) {
                        writer.write("Lunch, " + e.html() + ", ");
                    } else {
                        writer.write(e.html() + "\n");
                    }
                }

                for (Element e : dinnerMenu) {
                    if (e.className().equals("dining-course-name")) {
                        writer.write("Dinner, " + e.html() + ", ");
                    } else {
                        writer.write(e.html() + "\n");
                    }
                }

            } catch (IOException e) {
                Log.e("MainActivity", "Error with JSOUP");
                e.printStackTrace();
            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    Log.e("MainActivity", "Can't close val_menus file after writing");
                }
                editor.putString(getString(R.string.saved_date_key), dateStr);
                editor.commit();
            }
//                }
//            }
            return null;
        }
    }
}
