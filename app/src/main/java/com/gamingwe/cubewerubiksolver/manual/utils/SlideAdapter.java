package com.gamingwe.cubewerubiksolver.manual.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.gamingwe.cubewerubiksolver.R;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;
    private Bitmap ImageBit;

    public SlideAdapter(Context context) {
        this.context = context;
    }
    public int[] slideimage={
            R.drawable.logo,
            R.drawable.logo
    };
    public int [] slideback={
            R.drawable.design,
            R.drawable.design
    };
    public String[] mheading={
            "                                     Welcome",
            "Some rules to know"
    };

    public String[] mdocs={
            "Welcome to our amazing rapid cube solving application",
            "Rules you should know"
    };
    public int[] img={
            R.drawable.twodmovespng,
            R.drawable.notation
    };

    @Override
    public int getCount() {
        return mheading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==(ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.slide_layout,container,false);


        TextView slideHeading=(TextView)view.findViewById(R.id.mHeading);
        ImageView slideDoc=(ImageView)view.findViewById(R.id.mDocs);




        slideHeading.setText(mheading[position]);
        slideDoc.setImageResource(img[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
