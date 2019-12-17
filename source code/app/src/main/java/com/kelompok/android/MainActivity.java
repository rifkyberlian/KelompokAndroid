package com.kelompok.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.kelompok.android.adapters.NavigationAdapter;
import com.kelompok.android.fragments.LiveTvFragment;
import com.kelompok.android.fragments.MoviesFragment;
import com.kelompok.android.fragments.TvSeriesFragment;
import com.kelompok.android.models.NavigationModel;
import com.kelompok.android.nav_fragments.CountryFragment;
import com.kelompok.android.nav_fragments.FavoriteFragment;
import com.kelompok.android.nav_fragments.GenreFragment;
import com.kelompok.android.nav_fragments.MainHomeFragment;
import com.kelompok.android.utils.SpacingItemDecoration;
import com.kelompok.android.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private NavigationAdapter mAdapter;
    private List<NavigationModel> list =new ArrayList<>();
    private NavigationView navigationView;
    private String[] navItemImage;

    private String[] navItemName2;
    private String[] navItemImage2;
    private boolean status=false;

    private SharedPreferences preferences;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //---analytics-----------
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "main_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //----dark mode----------
        preferences=getSharedPreferences("push",MODE_PRIVATE);
//        if (preferences.getBoolean("dark",false)){
//            AppCompatDelegate
//                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        }else {
//            AppCompatDelegate
//                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }




        //----init---------------------------
        navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);


        //----navDrawer------------------------
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        navigationView.setNavigationItemSelectedListener(this);


        //----fetch array------------
        String[] navItemName = getResources().getStringArray(R.array.nav_item_name);
        navItemImage=getResources().getStringArray(R.array.nav_item_image);
        navItemImage2=getResources().getStringArray(R.array.nav_item_image_2);


        navItemName2=getResources().getStringArray(R.array.nav_item_name_2);




        //----navigation view items---------------------
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 15), true));
        recyclerView.setHasFixedSize(true);


        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        status = prefs.getBoolean("status",false);

        if (status){
            for (int i = 0; i< navItemName.length; i++){
                NavigationModel models =new NavigationModel(navItemImage[i], navItemName[i]);
                list.add(models);
            }
        }else {
            for (int i=0;i<navItemName2.length;i++){
                NavigationModel models =new NavigationModel(navItemImage2[i],navItemName2[i]);
                list.add(models);
            }
        }







        //set data and list adapter
        mAdapter = new NavigationAdapter(this, list);
        recyclerView.setAdapter(mAdapter);


        final NavigationAdapter.OriginalViewHolder[] viewHolder = {null};

        mAdapter.setOnItemClickListener(new NavigationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, NavigationModel obj, int position, NavigationAdapter.OriginalViewHolder holder) {

                Log.e("POSITION OF NAV:::", String.valueOf(position));

                //----action for click items nav---------------------

                if (position==0){
                    loadFragment(new MainHomeFragment());
                }
                else if (position==1){
                    loadFragment(new MoviesFragment());
                }
                else if (position==2){
                    loadFragment(new LiveTvFragment());
                }
                else if (position==3){
                    loadFragment(new TvSeriesFragment());
                }
                else if (position==4){
                    loadFragment(new GenreFragment());
                }
                else if (position==5){
                    loadFragment(new CountryFragment());
                }
                else {


                    if (status){

                        if (position==6){
                            Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                            startActivity(intent);
                        }
                        else if (position==7){
                            loadFragment(new FavoriteFragment());
                        }
                        else if (position==8){
                            new AlertDialog.Builder(MainActivity.this).setMessage("Are you sure to logout ?")
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                                            editor.putBoolean("status",false);
                                            editor.apply();

                                            Intent intent=new Intent(MainActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).create().show();
                        }
                        else if (position==9){
                            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
                            startActivity(intent);
                        }
                    }else {
                        if (position==6){
                            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                        else if (position==7){
                            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
                            startActivity(intent);
                        }
                    }

                }




                //----behaviour of bg nav items-----------------
                if (!obj.getTitle().equals("Settings") && !obj.getTitle().equals("Login") && !obj.getTitle().equals("Sign Out")){

                    if (preferences.getBoolean("dark",false)){
                        mAdapter.chanColor(viewHolder[0],position,R.color.nav_bg);
                    }else {
                        mAdapter.chanColor(viewHolder[0],position,R.color.white);
                    }


                    holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    holder.name.setTextColor(getResources().getColor(R.color.white));
                    viewHolder[0] =holder;
                }

                mDrawerLayout.closeDrawers();
            }
        });

        //----external method call--------------
        loadFragment(new MainHomeFragment());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        return true;
    }


    private boolean loadFragment(Fragment fragment){

        if (fragment!=null){

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();

            return true;
        }
        return false;

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_search:

                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {

                        Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                        intent.putExtra("q",s);
                        startActivity(intent);

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });

                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
        }else {

            new AlertDialog.Builder(MainActivity.this).setMessage("Do you want to exit ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();

        }
    }

    //----nav menu item click---------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // set item as selected to persist highlight
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();


        return true;
    }
}
