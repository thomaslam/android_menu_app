package assignment.cs342.thomas.menuapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    public final static String DINING_URL = "https://www.amherst.edu/campuslife/housing-dining/dining/menu";
    public final static String SAVED_MENUS_FILE = "val_menus";
    private boolean stopValMenuTask = false;
    // TODO: change URL to vega server
    public final static String OWN_SERVER_URL = "http://148.85.253.123:15675";
    public final static String VEGA_SERVER_URL = "http://148.85.77.39:15675";
    public final static String REMUS_SERVER_URL = "http://148.85.1.64:15675";
    private final static int COMPRESSION_QUALITY = 100;

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

    private String getStringFromBitmap(Bitmap bitmapPicture) {
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public void sendPicToServer(View v) {
        ImageView mImageView = (ImageView) findViewById(R.id.profile_img);
        final BitmapDrawable bitmapDrawable = (BitmapDrawable) mImageView.getDrawable();

        if (bitmapDrawable != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Picture Details");

            alertDialogBuilder
                    .setTitle("Picture Details")
                    .setView(getLayoutInflater().inflate(R.layout.dialog_send_pic, null))
                    .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText usernameET = (EditText) ((AlertDialog) dialog).
                                    findViewById(R.id.pic_username);
                            EditText descriptionET = (EditText) ((AlertDialog) dialog).
                                    findViewById(R.id.pic_description);
                            String username = usernameET.getText().toString();
                            String description = descriptionET.getText().toString();

                            // JSON object containing image bitmap
                            JSONObject imageJSON = new JSONObject();
                            try {
                                imageJSON.put("username", username);
                                imageJSON.put("description", description);
                                Format formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                                Date date = Calendar.getInstance().getTime();
                                String dateStr = formatter.format(date);
                                imageJSON.put("date", dateStr);
                                imageJSON.put("image",
                                        getStringFromBitmap(bitmapDrawable.getBitmap()));

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                new SendPicTask().execute(imageJSON);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            Toast toast = Toast.makeText(this, "Take a pic first before sending to server",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void goToMapActivity(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void viewGallery(View view) {
        Intent intent = new Intent(this, GalleryActivity.class);
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
                Log.d(TAG, "line: " + line);
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
                Log.d(TAG, "No username found");

                Toast toast = Toast.makeText(ctx, failSignIn, duration);
                toast.show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Can't read account_names file");
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                Log.e(TAG, "Can't close account_names file");
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
            Log.d(TAG, "savedDate: " + savedDate);

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
                    Log.d(TAG, e.html());
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
                Log.e(TAG, "Error with JSOUP");
                e.printStackTrace();
            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    Log.e(TAG, "Can't close val_menus file after writing");
                }
                editor.putString(getString(R.string.saved_date_key), dateStr);
                editor.commit();
            }
//                }
//            }
            return null;
        }
    }

    private class SendPicTask extends AsyncTask<JSONObject, Void, Integer> {
        protected Integer doInBackground(JSONObject... objs) {
            HttpURLConnection urlConnection = null;
            int status = -1;
            try {
                Log.d(TAG, "in doInBackground of SendPicTask");
                JSONObject jsonObject = objs[0];

                // TODO: remus or vega server?
                URL url = new URL(REMUS_SERVER_URL);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");

                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonObject.toString().getBytes("UTF-8"));
                os.close();

                // Connecting to url
                urlConnection.connect();

                status = urlConnection.getResponseCode();
                Log.d(TAG, "Response code: " + status);
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return status;
        }

        protected void onPostExecute(Integer status) {
            if (status == 200 || status == 201) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Successfully sent pic to server", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Fail to send pic to server", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
