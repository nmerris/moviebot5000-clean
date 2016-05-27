package com.nate.moviebot5k;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.nate.moviebot5k.api_fetching.GenresAndCertsFetcher;
import com.nate.moviebot5k.data.MovieTheaterContract;

/**
 * Initializes this app the first time it is installed on device, and detects if internet is
 * available.  If internet is available, launches Intent to ActivityHome, if not it will check to
 * see if user has at least one favorite, and then asks user via an AlertDialog if they would like
 * to view their favorites.  If so, launches and Intent to FavoritesActivity.
 */
public class StartupActivity extends AppCompatActivity {
    private static final String LOGTAG = ActivitySingleFragment.N8LOG + "StartupActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOGTAG, "entered onCreate");

        setContentView(R.layout.activity_startup);
        TextView messageTextView = (TextView) findViewById(R.id.problem_message);

        initializeSharedPrefs();

        clearCreditsVideosReviewsTables();

        // go fetch a new list of genres and certs in a background thread, the app will then either continue
        // on to ActivityHome if successful, or the user will be presented with a choice to view
        // their favorites (if they have any), or the app will just show a msg saying that it needs
        // to have internet connection to work (and that they have not favorites)
        new FetchGenresAndCertsTask(this, messageTextView).execute();
    }


    // NOTE: old movies records are deleted every time new movies are fetched in MoviesFetcher
    // but for these tables, just clean out the non favorites records each time app starts from dead
    private void clearCreditsVideosReviewsTables() {
        Log.i(LOGTAG, "about to clear out credits, videos, and reviews tables but only NON favorites records");
        String[] selectionArgs = new String[]{ "false" };

        getContentResolver().delete(MovieTheaterContract.CreditsEntry.CONTENT_URI,
                MovieTheaterContract.CreditsEntry.COLUMN_IS_FAVORITE + " = ?", selectionArgs);

        getContentResolver().delete(MovieTheaterContract.VideosEntry.CONTENT_URI,
                MovieTheaterContract.VideosEntry.COLUMN_IS_FAVORITE + " = ?", selectionArgs);

        getContentResolver().delete(MovieTheaterContract.ReviewsEntry.CONTENT_URI,
                MovieTheaterContract.ReviewsEntry.COLUMN_IS_FAVORITE + " = ?", selectionArgs);
    }


    private class FetchGenresAndCertsTask extends AsyncTask<Void, Void, Integer> {

        // async task needs it's own Context it can hold on to, in case of orientation change while
        // do in background is running
        Context context;
        TextView messageTV;

        private FetchGenresAndCertsTask(Context c, TextView message) {
            context = c;
            messageTV = message;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Log.i(LOGTAG, "just entered FetchGenresAndCertsTask.doInBackground");

            // fetch a new list of certs from themoviedb, the certifications table will be updated
            new GenresAndCertsFetcher(context).fetchAvailableCertifications();

            // I'm choosing to use genres as the task that returns the number of items fetched,
            // could be either, it's arbitrary, the genres table will also be updated
            return new GenresAndCertsFetcher(context).fetchAvailableGenres();
        }

        @Override
        protected void onPostExecute(Integer numGenresFetched) {
            Log.i(LOGTAG,"  in FetchGenresAndCertsTask.onPostExecute, numGenresFetched was: " + numGenresFetched);

            // if at least 10 genres was fetched, the assumption here is that it was successful
            // so go ahead and launch ActivityHome
            if (numGenresFetched > 10) { // 10 is arbitrary
                Log.i(LOGTAG, "    since there were at least 10 genres fetched, connection to" +
                        " themoviedb must be ok, so about to launch intent to ActivityHome");

                Intent intent = new Intent(context, ActivityHome.class);
                startActivity(intent);
                finish();

            }
            // no items were returned, so check if user has any favorites saved
            // first get a cursor that points to the favorites table, projection does not matter,
            // so just arbitrarily use movie_id
            else {
                Cursor cursor = getContentResolver().query(
                        MovieTheaterContract.MoviesEntry.CONTENT_URI,
                        new String[]{MovieTheaterContract.MoviesEntry.COLUMN_MOVIE_ID},
                        MovieTheaterContract.MoviesEntry.COLUMN_IS_FAVORITE + " = ?",
                        new String[]{ "true" },
                        null);

                if(cursor == null) {
                    Log.e(LOGTAG, "    Woah there buddy, somehow a Cursor was null, this should never happen!");
                    messageTV.setText("There appears to be a database problem.  Try restarting the" +
                            " app and if that doesn't work try reinstalling.");
                }
                else {
                    try {
                        if (cursor.moveToFirst()) {
                            // user has at least one favorite saved locally
                            Log.i(LOGTAG, "    no connection to themoviedb, BUT user has at least one favorite" +
                                    " saved, so about to launch an intent to FavoritesActivity");

                            Intent intent = new Intent(context, ActivityFavorites.class);
                            TaskStackBuilder.create(context).addNextIntentWithParentStack(intent).startActivities();
                            finish();

                        } else {
                            // not much can be done at this point, no connection to themoviedb AND
                            // user has no favorites saved, so just need to show them an appropriate msg

                            Log.i(LOGTAG, "    NOTHING can be done at this point," +
                                    " no connection to themoviedb and no favorites saved");
                            messageTV.setText("Unfortunately there was a connection problem.  If you had" +
                                    " any favorites saved, you would be able to view them now even without" +
                                    " an internet connection, but it appears you do not have any.  Please" +
                                    " try again when a connection is available and consider adding some favorites!");
                        }
                    } finally {
                        cursor.close();
                    }
                }
            } // end else when numGenres fetched was < 10
        } // end onPostExecute
    } // end AsyncTask



    // initialize all sharedPrefs, need this to happen the first time app is installed
    // or if user clears the app data, they will either ALL exist, or NONE will exist
    private void initializeSharedPrefs() {
        Log.i(LOGTAG, "entered initializeSharedPrefs, will report if they do not exist yet");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // if any single sharedPrefs exists, then they all do and have already been initialized
        if(!sharedPreferences.contains(getString(R.string.key_movie_filter_year))) {
            Log.i(LOGTAG, "  sharedPrefs are being created for the first time, writing defaults...");

            // TODO: prob won't end up using num_favorites, easier to just check db each time
            editor.putInt(getString(R.string.key_num_favorites), 0);

            editor.putString(getString(R.string.key_movie_filter_sortby_value),
                    getString(R.string.default_movie_filter_sortby_value));

            editor.putString(getString(R.string.key_movie_filter_year),
                    getString(R.string.default_movie_filter_year));

            editor.putString(getString(R.string.key_movie_filter_cert),
                    getString(R.string.default_movie_filter_cert));

            editor.putString(getString(R.string.key_movie_filter_genre_id),
                    getString(R.string.default_movie_filter_genre_id));

            editor.putString(getString(R.string.key_favorites_sortby_value),
                    getString(R.string.default_favorites_sortby_value));

            editor.putInt(getString(R.string.key_currently_selected_movie_id),
                    getResources().getInteger(R.integer.default_currently_selected_movie_id));

            editor.putInt(getString(R.string.key_currently_selected_favorite_id),
                    getResources().getInteger(R.integer.default_currently_selected_favorite_id));

//            editor.putBoolean(getString(R.string.key_fetch_new_movies), true);

            // all spinners start at zeroth position
            editor.putInt(getString(R.string.key_movie_filter_year_spinner_position), 0);
            editor.putInt(getString(R.string.key_movie_filter_sortby_spinner_position), 0);
            editor.putInt(getString(R.string.key_movie_filter_cert_spinner_position), 0);
            editor.putInt(getString(R.string.key_movie_filter_genre_spinner_position), 0);
            editor.putInt(getString(R.string.key_favorites_sortby_spinner_position), 0);

//            editor.commit();
        }

        // and it's always a good idea to fetch new movies when the app starts from dead
        // because the movies in themoviedb database may have changed since app was last used
        editor.putBoolean(getString(R.string.key_fetch_new_movies), true);
        editor.commit();

    }

}
