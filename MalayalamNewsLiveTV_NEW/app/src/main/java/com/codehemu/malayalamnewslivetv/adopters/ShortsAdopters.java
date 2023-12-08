package com.codehemu.malayalamnewslivetv.adopters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import com.codehemu.malayalamnewslivetv.R;

import com.codehemu.malayalamnewslivetv.WebActivity;
import com.codehemu.malayalamnewslivetv.models.Common;
import com.codehemu.malayalamnewslivetv.services.ShortDataAsync;
import com.codehemu.malayalamnewslivetv.services.ShortDesAsync;

import java.util.ArrayList;

public class ShortsAdopters extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> titles;
    ArrayList<String> des;
    ArrayList<String> images;
    ArrayList<String> links;

    public ShortsAdopters(Context context,  ArrayList<String> title, ArrayList<String> desc, ArrayList<String> image, ArrayList<String> link) {
        this.context = context;
        this.titles = title;
        this.des = desc;
        this.images = image;
        this.links = link;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View itemView = layoutInflater.inflate(R.layout.item_short,container,false);
        ImageView imageView  = itemView.findViewById(R.id.imageView);
        ImageView imageView1  = itemView.findViewById(R.id.imageView2);
        TextView textView =itemView.findViewById(R.id.headline);
        TextView textView1 =itemView.findViewById(R.id.desc);
        TextView textView2 =itemView.findViewById(R.id.textView6);

        ImageView imageView4  = itemView.findViewById(R.id.imageView4);
        imageView4.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = layoutInflater.inflate(R.layout.dialog_rss,null);
            builder.setIcon(R.drawable.shorts);
            builder.setTitle(R.string.short_categories);
            Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,context.getResources().getStringArray(R.array.rssList));
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);
            Button button = (Button) view.findViewById(R.id.save);
            Button button1 = (Button) view.findViewById(R.id.back);
            SharedPreferences sharedPreferences = context.getSharedPreferences("BigBengaliJson",Context.MODE_PRIVATE);
            String rssPosition = sharedPreferences.getString("rss","noValue");
            if (!rssPosition.equals("noValue")){
                spinner.setSelection(Integer.parseInt(rssPosition));
            }
            builder.setView(view);
            AlertDialog mDialog =  builder.create();
            mDialog.show();

            button.setOnClickListener(v1 -> {
                if (spinner.getSelectedItemPosition()!=0){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("rss",String.valueOf(spinner.getSelectedItemPosition()));
                    editor.apply();
                    Toast.makeText(context, spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    if (Common.isConnectToInternet(context)) {
                        new ShortDataAsync(context).execute();
                        try {
                            Thread.sleep(2000);
                            new ShortDesAsync(context).execute();

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    mDialog.dismiss();
                }
            });
            button1.setOnClickListener(v12 -> mDialog.dismiss());

        });

        textView.setText(titles.get(position));
        textView1.setText(des.get(position) + ".....");

        Glide.with(context).load(images.get(position)).centerCrop().into(imageView);
        Glide.with(context).load(images.get(position)).centerCrop().override(12,12).into(imageView1);

        textView2.setOnClickListener(v -> {
            Intent t = new Intent(v.getContext(), WebActivity.class);
            t.putExtra("title","Short");
            t.putExtra("url",links.get(position));
            v.getContext().startActivity(t);
        });

        container.addView(itemView);

        return itemView;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

}
