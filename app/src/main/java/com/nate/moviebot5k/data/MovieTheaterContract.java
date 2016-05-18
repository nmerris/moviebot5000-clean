package com.nate.moviebot5k.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.nate.moviebot5k.ActivitySingleFragment;

/**
 * Defines the table and column names for movie_theater SQLite database.
 *
 * Created by Nathan Merris on 5/4/2016.
 */
public class MovieTheaterContract {
    private static final String LOGTAG = ActivitySingleFragment.N8LOG + "MovieThetrContrct";

    public static final String CONTENT_AUTHORITY = "com.nate.moviebot5k";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_GENRES = "genres";
    public static final String PATH_CERTS = "certifications";






    // private but not final: private to protect it from malevolent code goblins outside this class,
    // not final because MoviesEntry and FavoritesEntry extend it
    private static class MoviesEntryColumns {

        // populated in FragmentMovieGrid from /discover/movie themoviedb API endpoint
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_GENRE_ID1 = "genre_id1";
        public static final String COLUMN_GENRE_NAME1 = "genre_name1";
        public static final String COLUMN_GENRE_ID2 = "genre_id2";
        public static final String COLUMN_GENRE_NAME2 = "genre_name2";
        public static final String COLUMN_GENRE_ID3 = "genre_id3";
        public static final String COLUMN_GENRE_NAME3 = "genre_name3";
        public static final String COLUMN_GENRE_ID4 = "genre_id4";
        public static final String COLUMN_GENRE_NAME4 = "genre_name4";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_HAS_VIDEO = "has_video";
        public static final String COLUMN_VOTE_AVG = "vote_average";

        // populated in MovieDetailFragment from /movie/id themoviedb API endpoint
        public static final String COLUMN_BUDGET = "budget";
        public static final String COLUMN_REVENUE = "revenue";
        public static final String COLUMN_RUNTIME = "runtime";

        // populated in MovieDetailFragment from /movie/id/reviews API endpoint
        public static final String COLUMN_REVIEW_AUTHOR1 = "review_author1";
        public static final String COLUMN_REVIEW_CONTENT1 = "review_content1";
        public static final String COLUMN_REVIEW_AUTHOR2 = "review_author2";
        public static final String COLUMN_REVIEW_CONTENT2 = "review_content2";
        public static final String COLUMN_REVIEW_AUTHOR3 = "review_author3";
        public static final String COLUMN_REVIEW_CONTENT3 = "review_content3";
        public static final String COLUMN_REVIEW_AUTHOR4 = "review_author4";
        public static final String COLUMN_REVIEW_CONTENT4 = "review_content4";

        // populated in FragmentMovieGrid from /movie/id/videos API endpoint
        public static final String COLUMN_VIDEO_KEY1 = "video_key1";
        public static final String COLUMN_VIDEO_NAME1 = "video_name1";
        public static final String COLUMN_VIDEO_SITE1 = "video_site1";
        public static final String COLUMN_VIDEO_SIZE1 = "video_size1";
        public static final String COLUMN_VIDEO_TYPE1 = "video_type1";
        public static final String COLUMN_VIDEO_KEY2 = "video_key2";
        public static final String COLUMN_VIDEO_NAME2 = "video_name2";
        public static final String COLUMN_VIDEO_SITE2 = "video_site2";
        public static final String COLUMN_VIDEO_SIZE2 = "video_size2";
        public static final String COLUMN_VIDEO_TYPE2 = "video_type2";
        public static final String COLUMN_VIDEO_KEY3 = "video_key3";
        public static final String COLUMN_VIDEO_NAME3 = "video_name3";
        public static final String COLUMN_VIDEO_SITE3 = "video_site3";
        public static final String COLUMN_VIDEO_SIZE3 = "video_size3";
        public static final String COLUMN_VIDEO_TYPE3 = "video_type3";
        public static final String COLUMN_VIDEO_KEY4 = "video_key4";
        public static final String COLUMN_VIDEO_NAME4 = "video_name4";
        public static final String COLUMN_VIDEO_SITE4 = "video_site4";
        public static final String COLUMN_VIDEO_SIZE4 = "video_size4";
        public static final String COLUMN_VIDEO_TYPE4 = "video_type4";
    }

    
    /**
     * Defines the table contents that FragmentMovieGrid and MovieDetailFragment will access when
     * hosted by ActivityHome and MovieDetailPagerActivity (in phone mode).
     * Basically this will be the table that is used when user is NOT viewing their favorites.
     * It will only ever hold one set of data, and will be written over completely every time a
     * FetchMoviesTask returns at least 1 movie.
     */
    public static final class MoviesEntry extends MoviesEntryColumns implements BaseColumns {

        // "content://com.nate.moviebot5k/movies"
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES).build();
        public static final String CONTENT_TYPE = ContentResolver
                .CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver
                .CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String TABLE_NAME = "movies";

        
        // returns a movie Uri (a single record in this table) given a MOVIE id
        // MovieTheaterProvider's uri matcher will know what to do if any of it's db related
        // methods receive a call with a uri as described below, which would look like:
        // "content://com.nate.moviebot5k/movies/[movieId]"
        // used by MovieDetailFragment to get a cursor that points to a movie with id = movieId
        // NOTE: movieId is not the same as the primary key, which is just _id
        public static Uri buildMovieUriFromMovieId(long movieId) {
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }
        
    }


    /**
     * Defines the table contents that FragmentMovieGrid and MovieDetailFragment will access when
     * hosted by FavoritesActivity and FavoritesPagerActivity (in phone mode).
     * Basically this will be the table that is used when user is viewing their favorites, which
     * can be done with or without an internet connection.  The data in this table is identical to
     * MoviesEntry except that it has 2 additional columns to hold the file paths for the poster
     * and backdrop images, since they are stored locally.  The records in this table are inserted
     * or deleted one at a time.
     */
    public static final class FavoritesEntry extends MoviesEntryColumns implements BaseColumns {

        // "content://com.nate.moviebot5k/favorites"
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES).build();
        public static final String CONTENT_TYPE = ContentResolver
                .CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver
                .CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String TABLE_NAME = "favorites";


        // 2 additional columns needed to store local poster and backdrop image file paths
        // so that the favorites table can be accessed while offline
        public static final String COLUMN_POSTER_FILE_PATH = "poster_file_path";
        public static final String COLUMN_BACKDROP_FILE_PATH = "backdrop_file_path";


        // returns a favorte Uri (a single record in this table) given a MOVIE id
        // TODO: this is not going to work, needs to use .appendQueryParameter
        // .withAppendedId will only work for the primary key _id, I think
        public static Uri buildFavoriteUriFromMovieId(long movieId) {
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }

    }


    /**
     * Defines the table contents that are used to populate the Genres movie filter spinner.
     * The data is updated once each time the app is started from dead, in StartupActivity.
     */
    public static final class GenresEntry implements BaseColumns {
        
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_GENRES).build();
        public static final String CONTENT_TYPE = ContentResolver
                .CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver
                .CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRES;
        public static final String TABLE_NAME = "genres";

        public static final String COLUMN_GENRE_ID = "genre_id";
        public static final String COLUMN_GENRE_NAME = "genre_name";


        // should not need any build methods.. genres table is only ever wiped out completely and
        // then written over with a bulk insert

//        public static Uri buildGenreUriFromGenreName(String genreName) {
//            return CONTENT_URI.buildUpon()
//                    .appendQueryParameter(COLUMN_GENRE_NAME, genreName).build();
//        }

    }


    /**
     * Defines the table contents that are used to populate the Certifications movie filter spinner.
     * The data is updated once each time the app is started from dead, in StartupActivity.
     */
    public static final class CertsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CERTS).build();
        public static final String CONTENT_TYPE = ContentResolver
                .CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CERTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver
                .CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CERTS;
        public static final String TABLE_NAME = "certifications";

        public static final String COLUMN_CERT_ORDER = "certification_order";
        public static final String COLUMN_CERT_NAME = "certification_name";
        public static final String COLUMN_CERT_MEANING = "certification_meaning";

        // should not need any build methods, same reason as GenresEntry

    }
    
}
