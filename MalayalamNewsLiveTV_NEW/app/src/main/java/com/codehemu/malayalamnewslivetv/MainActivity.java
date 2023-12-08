package com.codehemu.malayalamnewslivetv;


import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codehemu.malayalamnewslivetv.adopters.ChannelAdopters;
import com.codehemu.malayalamnewslivetv.models.Channel;
import com.codehemu.malayalamnewslivetv.models.Common;
import com.codehemu.malayalamnewslivetv.models.InAppUpdate;
import com.codehemu.malayalamnewslivetv.services.ChannelDataService;
import com.codehemu.malayalamnewslivetv.services.ShortDataAsync;
import com.codehemu.malayalamnewslivetv.services.ShortDesAsync;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    AdView adView, adView1,adView2;
    LinearLayout linearLayout;
    public static final String TAG = "TAG";
    RecyclerView newsChannelList,newsChannelList2,newsChannelList3;
    ChannelAdopters newsChannelAdopters,newsChannelAdopters2,newsChannelAdopters3;
    List<Channel> newsChannels,newsChannels2,newsChannels3;
    ChannelDataService service;
    SwipeRefreshLayout mSwipeRefreshLayout;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    CardView cardView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    String appsName, packageName;
    ReviewManager manager;
    ReviewInfo reviewInfo;
    TextView more_bengali,more_hindi,email_click;
    Button ePaper,englishNews,topNews,RateBtn,aboutBtn,shareBtn,setting,moreApp;
    private InAppUpdate inAppUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        service = new ChannelDataService(this);

        inAppUpdate = new InAppUpdate(MainActivity.this);
        inAppUpdate.checkForAppUpdate();

        this.appsName = getApplication().getString(R.string.app_name);
        this.packageName = getApplication().getPackageName();
        cardView = findViewById(R.id.InternetAlert);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        getListActivity1(getString(R.string.Bengali_news_json));
        getListActivity2("no",getString(R.string.Bengali_news_json));
        getListActivity3("no",getString(R.string.Hindi_news_json));

        MobileAds.initialize(this, initializationStatus -> {});
        adView = findViewById(R.id.adView);
        adView1 = findViewById(R.id.adView1);
        adView2 = findViewById(R.id.adView2);

        RefreshLayout();
        RequestReviewInfo();
        moreButton();

    }

    private void moreButton() {
        more_bengali = findViewById(R.id.more_bengali);
        more_hindi = findViewById( R.id.moreHindi);


        more_bengali.setOnClickListener(v -> openListingActivity("bengaliNews"));
        more_hindi.setOnClickListener(v -> openListingActivity("hindiNews"));

        ePaper = findViewById(R.id.ePaper);

        ePaper.setOnClickListener(v -> openListingActivity("bengaliPaper"));
        englishNews = findViewById(R.id.englishBtn);

        englishNews.setOnClickListener(v -> openListingActivity("EnglishNews"));
        topNews = findViewById(R.id.topNews);
        topNews.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ShortActivity.class)));

        RateBtn = findViewById(R.id.rateBtn);
        RateBtn.setOnClickListener(v -> LinkRateUs());
        aboutBtn = findViewById(R.id.aboutAppBtn);
        aboutBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AboutActivity.class)));
        shareBtn = findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(v -> LinkShareApp());

        moreApp = findViewById(R.id.moreApp);
        moreApp.setOnClickListener(v -> openLink("https://play.google.com/store/apps/dev?id=7464231534566513633"));

        setting = findViewById(R.id.setting);
        setting.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = getLayoutInflater().inflate(R.layout.dialog_rss,null);
            builder.setIcon(R.drawable.shorts);
            builder.setTitle(R.string.short_categories);
            Spinner spinner = view.findViewById(R.id.spinner);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item,MainActivity.this.getResources().getStringArray(R.array.rssList));
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);
            Button button = view.findViewById(R.id.save);
            Button button1 = view.findViewById(R.id.back);
            SharedPreferences sharedPreferences = getSharedPreferences("BigBengaliJson",Context.MODE_PRIVATE);
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
                    Toast.makeText(MainActivity.this, spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    if (Common.isConnectToInternet(MainActivity.this)) {
                        new ShortDataAsync(MainActivity.this).execute();
                        try {
                            Thread.sleep(2000);
                            new ShortDesAsync(MainActivity.this).execute();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    mDialog.dismiss();
                }

            });

            button1.setOnClickListener(v12 -> mDialog.dismiss());
        });

    }

    private void openListingActivity(String activity) {
        startActivity(new Intent(MainActivity.this, ListingActivity.class).
                putExtra("activity",activity));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.rateHeart) {
            RateMe();
        }
        if (item.getItemId() == R.id.shorts) {
            SharedPreferences getShared = getSharedPreferences("shorts", MODE_PRIVATE);
            String JsonValueEdit = getShared.getString("edit","noValue");
            if(!JsonValueEdit.equals("noValue")){
                startActivity(new Intent(MainActivity.this, ShortActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }



    private void RateMe(){
        if (reviewInfo != null){
            Task<Void> flow = manager.launchReviewFlow(this,reviewInfo);

            flow.addOnCompleteListener(task -> {
            });
        }

    }

    private void RequestReviewInfo(){
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();

        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                reviewInfo = task.getResult();
            }else {
                Toast.makeText(this, "Not Review", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        if (item.getItemId() == R.id.contain) {
            openListingActivity("containing");
        }

        if (item.getItemId() == R.id.policy) {
            startActivity(new Intent(MainActivity.this, WebActivity.class).
                    putExtra("title","Privacy Policy")
                    .putExtra("url",getString(R.string.policy_url)));
        }
        if (item.getItemId() == R.id.disclaimer) {
            final Dialog dialog = new Dialog(MainActivity.this); // Context, this, etc.
            dialog.setContentView(R.layout.activity_disclaimer);
            linearLayout = dialog.findViewById(R.id.dismiss);
            linearLayout.setOnClickListener(v -> dialog.cancel());
            email_click = dialog.findViewById(R.id.email_click);

            email_click.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String emailID = getString(R.string.my_email);
                String AppNAME = getString(R.string.app_name);
                Uri data = Uri.parse("mailto:"
                        + emailID
                        + "?subject=" +AppNAME+ " Feedback" + "&body=" + AppNAME);
                intent.setData(data);
                startActivity(intent);
            });

            dialog.show();
        }

        if (item.getItemId() == R.id.share) {
            LinkShareApp();
        }

        if (item.getItemId() == R.id.rate) {
            LinkRateUs();
        }
        if (item.getItemId() == R.id.more) {
            openLink("https://play.google.com/store/apps/dev?id=7464231534566513633");
        }
        if (item.getItemId() == R.id.about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        }
        if (item.getItemId() == R.id.website) {
            openLink("https://www.codehemu.com/");
        }
        if (item.getItemId() == R.id.fb) {
            openLink("https://www.facebook.com/codehemu/");
        }
        if (item.getItemId() == R.id.yt) {
            openLink("https://www.youtube.com/c/HemantaGayen");
        }
        return false;
    }

    private void LinkShareApp() {
        Intent share = new Intent("android.intent.action.SEND");
        share.setType("text/plain");
        share.putExtra("android.intent.extra.SUBJECT", MainActivity.this.appsName);
        String APP_Download_URL = "https://play.google.com/store/apps/details?id=" + MainActivity.this.packageName;
        share.putExtra("android.intent.extra.TEXT", MainActivity.this.appsName + getString(R.string.download_it) + APP_Download_URL);
        MainActivity.this.startActivity(Intent.createChooser(share, getString(R.string.share_it)));
    }

    private void LinkRateUs() {
        try {
            Intent intent2 = new Intent("android.intent.action.VIEW");
            intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + MainActivity.this.packageName));
            MainActivity.this.startActivity(intent2);
        }
        catch (Exception e){
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("market://details?id=" + MainActivity.this.packageName));
            MainActivity.this.startActivity(intent);
        }
    }

    public void openLink(String url) {
        Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(linkOpen);
    }

    private void RefreshLayout() {
        mSwipeRefreshLayout = findViewById(R.id.refresh_app);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            getListActivity1(getString(R.string.Bengali_news_json));
            getListActivity2("yes",getString(R.string.Bengali_news_json));
            getListActivity3("yes",getString(R.string.Hindi_news_json));
        });
    }

    public class NetworkChangeListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!Common.isConnectToInternet(context)) {
                cardView.setVisibility(View.VISIBLE);
            } else {
                cardView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void getListActivity1(String url) {
        newsChannelList = findViewById(R.id.SliderList_1);
        newsChannelList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        newsChannels = new ArrayList<>();
        newsChannelAdopters = new ChannelAdopters(this, newsChannels, "big"){
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
                super.onBindViewHolder(holder, position);
            }

        };
        newsChannelList.setAdapter(newsChannelAdopters);

        SharedPreferences getShared = getSharedPreferences("BengaliJson", MODE_PRIVATE);
        String JsonValue = getShared.getString("str","noValue");



        if (JsonValue.equals("noValue")) {
            service.getChannelData( url, new ChannelDataService.OnDataResponse() {
                @Override
                public Void onError(String error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                    return null;
                }
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(JSONArray response) {
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("BengaliJson",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("str",response.toString());
                    editor.apply();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject channelData = response.getJSONObject(i);
                            if (!channelData.getString("big_thumbnail").isEmpty()){
                                Channel c = new Channel();
                                c.setId(channelData.getInt("id"));
                                c.setName(channelData.getString("name"));
                                c.setDescription(channelData.getString("description"));
                                c.setLive_url(channelData.getString("live_url"));
                                c.setThumbnail(channelData.getString("big_thumbnail"));
                                c.setFacebook(channelData.getString("facebook"));
                                c.setYoutube(channelData.getString("youtube"));
                                c.setWebsite(channelData.getString("website"));
                                c.setCategory(channelData.getString("category"));
                                c.setLiveTvLink(channelData.getString("liveTvLink"));
                                c.setContact(channelData.getString("contact"));
                                newsChannels.add(c);
                                newsChannelAdopters.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
        }else {
            try {
                JSONArray jsonArray = new JSONArray(JsonValue);
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject channelData = jsonArray.getJSONObject(i);
                        if (!channelData.getString("big_thumbnail").isEmpty()){
                            Channel c = new Channel();
                            c.setId(channelData.getInt("id"));
                            c.setName(channelData.getString("name"));
                            c.setDescription(channelData.getString("description"));
                            c.setLive_url(channelData.getString("live_url"));
                            c.setThumbnail(channelData.getString("big_thumbnail"));
                            c.setFacebook(channelData.getString("facebook"));
                            c.setYoutube(channelData.getString("youtube"));
                            c.setWebsite(channelData.getString("website"));
                            c.setCategory(channelData.getString("category"));
                            c.setLiveTvLink(channelData.getString("liveTvLink"));
                            c.setContact(channelData.getString("contact"));
                            newsChannels.add(c);
                            newsChannelAdopters.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void getListActivity2(String refresh, String url) {
        newsChannelList2 = findViewById(R.id.SliderList_2);
        newsChannelList2.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        newsChannels2 = new ArrayList<>();
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.preparing_loading);
        dialog.show();
        newsChannelAdopters2 = new ChannelAdopters(this, newsChannels2, "small"){
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                AdRequest adRequest1 = new AdRequest.Builder().build();
                dialog.cancel();
                adView1.loadAd(adRequest1);
                if (Common.isConnectToInternet(MainActivity.this)) {
                    new ShortDesAsync(MainActivity.this).execute();
                }
                super.onBindViewHolder(holder, position);
            }
        };
        newsChannelList2.setAdapter(newsChannelAdopters2);

        SharedPreferences getShared = getSharedPreferences("BengaliJson", MODE_PRIVATE);
        String JsonValue = getShared.getString("str","noValue");
        if (refresh.equals("yes")){
            service.getChannelData( url, new ChannelDataService.OnDataResponse() {
                @Override
                public Void onError(String error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                    return null;
                }
                @Override
                public void onResponse(JSONArray response) {
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("BengaliJson",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("str",response.toString());
                    editor.apply();
                }
            });
        }

        if (JsonValue.equals("noValue")) {
            service.getChannelData( url, new ChannelDataService.OnDataResponse() {
                @Override
                public Void onError(String error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                    return null;
                }
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(JSONArray response) {
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("BengaliJson",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("str",response.toString());
                    editor.apply();
                    for (int i = 0; i < 8; i++) {
                        try {
                            JSONObject channelData = response.getJSONObject(i);
                            Channel c = new Channel();
                            c.setId(channelData.getInt("id"));
                            c.setName(channelData.getString("name"));
                            c.setDescription(channelData.getString("description"));
                            c.setLive_url(channelData.getString("live_url"));
                            c.setThumbnail(channelData.getString("thumbnail"));
                            c.setFacebook(channelData.getString("facebook"));
                            c.setYoutube(channelData.getString("youtube"));
                            c.setWebsite(channelData.getString("website"));
                            c.setCategory(channelData.getString("category"));
                            c.setLiveTvLink(channelData.getString("liveTvLink"));
                            c.setContact(channelData.getString("contact"));
                            newsChannels2.add(c);
                            newsChannelAdopters2.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
        }else {
            try {
                JSONArray jsonArray = new JSONArray(JsonValue);
                for (int i = 0; i < 8; i++) {
                    try {
                        JSONObject channelData = jsonArray.getJSONObject(i);
                        Channel c = new Channel();
                        c.setId(channelData.getInt("id"));
                        c.setName(channelData.getString("name"));
                        c.setDescription(channelData.getString("description"));
                        c.setLive_url(channelData.getString("live_url"));
                        c.setThumbnail(channelData.getString("thumbnail"));
                        c.setFacebook(channelData.getString("facebook"));
                        c.setYoutube(channelData.getString("youtube"));
                        c.setWebsite(channelData.getString("website"));
                        c.setCategory(channelData.getString("category"));
                        c.setLiveTvLink(channelData.getString("liveTvLink"));
                        c.setContact(channelData.getString("contact"));
                        newsChannels2.add(c);
                        newsChannelAdopters2.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void getListActivity3(String refresh, String url) {
        newsChannelList3 = findViewById(R.id.SliderList_3);
        newsChannelList3.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        newsChannels3 = new ArrayList<>();
        newsChannelAdopters3 = new ChannelAdopters(this, newsChannels3, "small"){
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                AdRequest adRequest2 = new AdRequest.Builder().build();
                adView2.loadAd(adRequest2);
                super.onBindViewHolder(holder, position);
            }
        };
        newsChannelList3.setAdapter(newsChannelAdopters3);

        SharedPreferences getShared = getSharedPreferences("HindiJson", MODE_PRIVATE);
        String JsonValue = getShared.getString("str","noValue");
        if (refresh.equals("yes")){
            service.getChannelData( url, new ChannelDataService.OnDataResponse() {
                @Override
                public Void onError(String error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                    return null;
                }
                @Override
                public void onResponse(JSONArray response) {
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("HindiJson",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("str",response.toString());
                    editor.apply();
                }
            });
        }
        if (JsonValue.equals("noValue")) {
            service.getChannelData( url, new ChannelDataService.OnDataResponse() {
                @Override
                public Void onError(String error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                    return null;
                }
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(JSONArray response) {
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("HindiJson",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("str",response.toString());
                    editor.apply();
                    for (int i = 0; i < 8; i++) {
                        try {
                            JSONObject channelData = response.getJSONObject(i);
                            Channel c = new Channel();
                            c.setId(channelData.getInt("id"));
                            c.setName(channelData.getString("name"));
                            c.setDescription(channelData.getString("description"));
                            c.setLive_url(channelData.getString("live_url"));
                            c.setThumbnail(channelData.getString("thumbnail"));
                            c.setFacebook(channelData.getString("facebook"));
                            c.setYoutube(channelData.getString("youtube"));
                            c.setWebsite(channelData.getString("website"));
                            c.setCategory(channelData.getString("category"));
                            c.setLiveTvLink(channelData.getString("liveTvLink"));
                            c.setContact(channelData.getString("contact"));
                            newsChannels3.add(c);
                            newsChannelAdopters3.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
        }else {
            try {
                JSONArray jsonArray = new JSONArray(JsonValue);
                for (int i = 0; i < 8; i++) {
                    try {
                        JSONObject channelData = jsonArray.getJSONObject(i);
                        Channel c = new Channel();
                        c.setId(channelData.getInt("id"));
                        c.setName(channelData.getString("name"));
                        c.setDescription(channelData.getString("description"));
                        c.setLive_url(channelData.getString("live_url"));
                        c.setThumbnail(channelData.getString("thumbnail"));
                        c.setFacebook(channelData.getString("facebook"));
                        c.setYoutube(channelData.getString("youtube"));
                        c.setWebsite(channelData.getString("website"));
                        c.setCategory(channelData.getString("category"));
                        c.setLiveTvLink(channelData.getString("liveTvLink"));
                        c.setContact(channelData.getString("contact"));
                        newsChannels3.add(c);
                        newsChannelAdopters3.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inAppUpdate.onActivityResult(requestCode, resultCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        inAppUpdate.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inAppUpdate.onDestroy();
    }

}