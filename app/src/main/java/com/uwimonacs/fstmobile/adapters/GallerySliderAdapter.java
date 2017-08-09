package com.uwimonacs.fstmobile.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uwimonacs.fstmobile.R;

import java.util.ArrayList;


/**
 * Created by Akinyele on 8/4/2017.
 */

public class GallerySliderAdapter extends PagerAdapter {

    private final Context context;
    private final ArrayList<String> Urls;


    public GallerySliderAdapter(Context context, ArrayList<String> Urls){
        this.context = context;
        this.Urls = Urls;
    }


    @Override
    public int getCount() {
        return Urls.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {

        String Url = "https://" + Urls.get(position);

        View imageLayout =LayoutInflater.from(context).inflate(R.layout.layout_slidingimages, view, false);

        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.sliderimage);

        Picasso.with(context)
                .load(Url)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(imageView);

        view.addView(imageLayout,0);
        return imageLayout;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }


}
