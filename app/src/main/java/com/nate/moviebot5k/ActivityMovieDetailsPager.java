package com.nate.moviebot5k;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.nate.moviebot5k.data.MovieTheaterContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Nathan Merris on 5/16/2016.
 */
public class ActivityMovieDetailsPager extends AppCompatActivity
        /*implements FragmentMovieDetails.Callbacks*/{
    private final String LOGTAG = ActivitySingleFragment.N8LOG + "MovDetPager";

    @Bind(R.id.activity_movie_detail_view_pager) ViewPager mViewPager;

    private SharedPreferences mSharedPrefs;
//    private Cursor mCursor;
    private int mNumMovies;
    ArrayList<Integer> mMovieIds = new ArrayList<>();



//    @Override
//    protected int getLayoutResourceId() {
//        return R.layout.activity_movie_details_pager;
//    }
//
//    @Override
//    protected Fragment createFragment() {
//
//        int currentlySelectedMovieId = PreferenceManager.getDefaultSharedPreferences(this)
//                .getInt(getString(R.string.key_currently_selected_movie_id), 0);
//
//        // create a new FMD and pass false to tell it not to use the favorites table
//        return FragmentMovieDetails.newInstance(false, currentlySelectedMovieId);
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: get rid of some of these variables that are only used once
        setContentView(R.layout.activity_movie_details_pager);
        ButterKnife.bind(this);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        FragmentManager fragmentManager = getSupportFragmentManager();

        // store the id of the last clicked movie back in FragmentMovieGrid
        int currSelectedMovieId = mSharedPrefs.getInt(getString(R.string.key_currently_selected_movie_id), 0);
        Log.i(LOGTAG," &&&&&&&&&&&&&&&&&&&&&& currSelectedMovieId in sharedPrefs before moving pager is: " + currSelectedMovieId);

        // get the current list of movieIds, these are the ids of the movies in the gridview
        mMovieIds = Utility.getMovieIdList(this);
        mNumMovies = mMovieIds.size();


        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Log.i(LOGTAG, "    FragmentPager position in getItem is: " + position);
                mMovieIds.get(position);

                int movieId = mMovieIds.get(position);
                Log.i(LOGTAG, "      and about to load new detail frag with movieId: " + movieId);


                return FragmentMovieDetails.newInstance(false, movieId);
            }

            @Override
            public int getCount() {
                return mNumMovies;
            }

        });

        // set the viewpager to start on the movie that was clicked back in FragmentMovieGrid
        for(int i = 0; i < mNumMovies; i++) {
            if(mMovieIds.get(i) == currSelectedMovieId) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    } // end onCreate


    //    @Override
//    public void onFavoriteRemoved(int movieId) {
//        Log.i(LOGTAG, "entered onFavoriteRemoved");
//
//    }


}