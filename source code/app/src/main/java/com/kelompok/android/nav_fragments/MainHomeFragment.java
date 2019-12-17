package com.kelompok.android.nav_fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.kelompok.android.R;
import com.kelompok.android.fragments.HomeFragment;
import com.kelompok.android.fragments.LiveTvFragment;
import com.kelompok.android.fragments.MoviesFragment;

public class MainHomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private ImageButton btnMovies,btnHome;
    private FloatingActionButton btnLiveTv;
    private Fragment fragment=null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_main_home, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        btnHome=view.findViewById(R.id.home);
        btnLiveTv=view.findViewById(R.id.live_tv);
        btnMovies=view.findViewById(R.id.movies);



        btnLiveTv.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey_40)));
        btnMovies.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
        btnHome.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));

        btnMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment=new MoviesFragment();
                loadFragment(fragment);

                btnMovies.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
                btnHome.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnLiveTv.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey_40)));


            }
        });

        btnLiveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment=new LiveTvFragment();
                loadFragment(fragment);

                btnLiveTv.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                btnMovies.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnHome.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment=new HomeFragment();
                loadFragment(fragment);

                btnMovies.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnHome.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
                btnLiveTv.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey_40)));
            }
        });

//        bottomNavigationView = view.findViewById(R.id.navigation);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Fragment fragment=null;
//                switch (item.getItemId()) {
//                    case R.id.navigation_home:
//                        fragment=new HomeFragment();
//                        break;
//
//                    case R.id.navigation_livetv:
//                        fragment=new LiveTvFragment();
//                        break;
//
//                    case R.id.navigation_movie:
//                        fragment=new MoviesFragment();
//                        break;
//
//                    case R.id.navigation_tvseries:
//                        fragment=new TvSeriesFragment();
//                        break;
//
//                }
//
//                loadFragment(fragment);
//                return true;
//            }
//        });

        loadFragment(new HomeFragment());

    }


    //----load fragment----------------------
    private boolean loadFragment(Fragment fragment){

        if (fragment!=null){

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();

            return true;
        }
        return false;

    }


}