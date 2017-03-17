package assignment.cs342.thomas.menuapp;

import android.graphics.Bitmap;

/**
 * Created by tlaminator on 3/16/17.
 */

public class MyImage {
    private String title;
    private String date;
    private String description;
    private Bitmap imgBitmap;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Bitmap getImgBitmap() { return imgBitmap; }
    public void setImgBitmap(Bitmap imgBitmap) { this.imgBitmap = imgBitmap; }
}
