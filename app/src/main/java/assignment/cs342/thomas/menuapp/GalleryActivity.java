package assignment.cs342.thomas.menuapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by tlaminator on 3/16/17.
 */

public class GalleryActivity extends AppCompatActivity {
    private final static String TAG = "GalleryActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.image_gallery);
        new RetrievePicsTask(this, recyclerView).execute();
    }

    private Bitmap getBitmapFromString(String jsonString) {
        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    private class RetrievePicsTask extends AsyncTask<Void, MyImage, Void> {
        private Context ctx;
        private RecyclerView recyclerView;
        private MyImageAdapter adapter;
        ArrayList<MyImage> myImages = new ArrayList<>();

        public RetrievePicsTask(Context ctx, RecyclerView recyclerView) {
            this.ctx = ctx;
            this.recyclerView = recyclerView;
        }

        protected void onPreExecute() {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(
                   ctx,2);
            recyclerView.setLayoutManager(layoutManager);

            adapter = new MyImageAdapter(ctx, myImages);
            recyclerView.setAdapter(adapter);
        }

        protected Void doInBackground(Void... v) {
            HttpURLConnection urlConnection = null;
            try {
                Log.d(TAG, "in doInBackground of RetrievePicsTask");
                URL url = new URL(MainActivity.REMUS_SERVER_URL);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("GET");

                // Connecting to url
                urlConnection.connect();

                int status = urlConnection.getResponseCode();
                Log.d(TAG, "Response code: " + status);

                if (status == 200 || status == 201) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();

                    JSONObject imagesJSON = new JSONObject(sb.toString());
                    Iterator<?> keys = imagesJSON.keys();

                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        JSONObject json = new JSONObject((String) imagesJSON.get(key));
                        String name = json.getString("username");
                        String description = json.getString("description");
                        String date = json.getString("date");
                        String image = json.getString("image");

                        MyImage imgObj = new MyImage();
                        String title = "User: " + name  + "\nDate: " + date +
                                "\nDescription: " + description;
                        imgObj.setTitle(title);
                        imgObj.setDate(date);
                        imgObj.setDescription(description);
                        imgObj.setImgBitmap(getBitmapFromString(image));
                        publishProgress(imgObj);
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }

        protected void onProgressUpdate(MyImage... img) {
            myImages.add(img[0]);
            adapter.notifyDataSetChanged();
        }
    }
}
