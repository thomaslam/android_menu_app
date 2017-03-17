package assignment.cs342.thomas.menuapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tlaminator on 3/16/17.
 */

public class MyImageAdapter extends RecyclerView.Adapter<MyImageAdapter.ViewHolder> {
    private ArrayList<MyImage> galleryList;
    private Context context;

    public MyImageAdapter(Context context, ArrayList<MyImage> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
    }

    @Override
    public MyImageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.img_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyImageAdapter.ViewHolder viewHolder, int i) {
        viewHolder.title.setText(galleryList.get(i).getTitle());
        viewHolder.img.setImageBitmap(galleryList.get(i).getImgBitmap());
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private ImageView img;
        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.img_title);
            img = (ImageView)  view.findViewById(R.id.img_pic);
        }
    }
}
