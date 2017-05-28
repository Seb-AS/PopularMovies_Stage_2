package com.sebas.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sebas.popularmovies.adapters.MovieDetailsAdapter;
import com.sebas.popularmovies.data.MovieContract;
import com.sebas.popularmovies.interfaces.DetailItemClickListener;
import com.sebas.popularmovies.interfaces.RequestDelegate;
import com.sebas.popularmovies.model.DetailItemType;
import com.sebas.popularmovies.model.Movie;
import com.sebas.popularmovies.model.MovieDetailsResponse;
import com.sebas.popularmovies.model.MoviesListType;
import com.sebas.popularmovies.model.Review;
import com.sebas.popularmovies.model.Video;
import com.sebas.popularmovies.network.MovieService;
import com.sebas.popularmovies.utilities.MovieUtils;

public class DetailActivity extends AppCompatActivity implements
        DetailItemClickListener,
        RequestDelegate {

    public static final String EXTRA_MOVIE = "movie";
    public static final String EXTRA_FAVOURITE = "is_favourite";

    private static final String EXTRA_VIDEOS = "videos";
    private static final String EXTRA_REVIEWS = "reviews";

    private ProgressBar mProgressBar;

    private RecyclerView mDetailList;
    private MovieDetailsAdapter mDetailAdapter;

    private Movie mMovie;
    private boolean mIsFavourite;
    private Video[] mVideos;
    private Review[] mReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mDetailList = (RecyclerView) findViewById(R.id.rv_movie_detail);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDetailList.setLayoutManager(layoutManager);
        mDetailAdapter = new MovieDetailsAdapter(this, this);
        mDetailList.setAdapter(mDetailAdapter);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(EXTRA_REVIEWS))
                mReviews = (Review[]) savedInstanceState.getParcelableArray(EXTRA_REVIEWS);
            if (savedInstanceState.containsKey(EXTRA_VIDEOS))
                mVideos = (Video[]) savedInstanceState.getParcelableArray(EXTRA_VIDEOS);
            if (savedInstanceState.containsKey(EXTRA_FAVOURITE))
                mIsFavourite = savedInstanceState.getBoolean(EXTRA_FAVOURITE);
            if (savedInstanceState.containsKey(EXTRA_MOVIE))
                mMovie = savedInstanceState.getParcelable(EXTRA_MOVIE);

            mDetailAdapter.swapData(mMovie, mIsFavourite, mVideos, mReviews);
            mDetailList.setAdapter(mDetailAdapter);
        } else {
            Intent startingIntent = getIntent();
            if (startingIntent.hasExtra(EXTRA_FAVOURITE)) {
                mIsFavourite = startingIntent.getBooleanExtra(EXTRA_FAVOURITE, false);
            }

            if (startingIntent.hasExtra(EXTRA_MOVIE)) {
                mMovie = startingIntent.getParcelableExtra(EXTRA_MOVIE);

                MovieService.getMovieDetails(this, mMovie.getId());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_MOVIE, mMovie);
        outState.putBoolean(EXTRA_FAVOURITE, mIsFavourite);
        outState.putParcelableArray(EXTRA_VIDEOS, mVideos);
        outState.putParcelableArray(EXTRA_REVIEWS, mReviews);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_action:
                shareFirstMovie();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeStoreFavouriteState() {
        if (mIsFavourite) {
            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(mMovie.getId() + "").build();
            if (getContentResolver().delete(uri, null, null) > 0) {
                mIsFavourite = false;
                mDetailAdapter.swapData(mMovie, mIsFavourite, mVideos, mReviews);
                mDetailList.setAdapter(mDetailAdapter);
            } else {
                Toast.makeText(this, getString(R.string.error_removing_favourite), Toast.LENGTH_LONG).show();
            }
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, mMovie.getPosterUrl());
            contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mMovie.getOriginalTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
            contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, mMovie.getVoteAverage());

            if (getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues) != null) {
                mIsFavourite = true;
                mDetailAdapter.swapData(mMovie, mIsFavourite, mVideos, mReviews);
                mDetailList.setAdapter(mDetailAdapter);
            } else {
                Toast.makeText(this, getString(R.string.error_adding_favourite), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openYoutubeVideo(String id) {
        Intent ytAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MovieUtils.YOUTUBE_APP_URL + id));
        ytAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (ytAppIntent.resolveActivity(getPackageManager()) != null)
            startActivity(ytAppIntent);
        else {
            Intent ytWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MovieUtils.YOUTUBE_BASE_SHORT_URL + id));
            if (ytWebIntent.resolveActivity(getPackageManager()) != null)
                startActivity(ytWebIntent);
        }
    }

    private void shareFirstMovie() {
        if (this.mVideos != null && this.mVideos.length > 0) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, MovieUtils.YOUTUBE_BASE_SHORT_URL + mVideos[0].getKey());
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
        } else {
            Toast.makeText(this, getString(R.string.no_movie_to_share), Toast.LENGTH_LONG).show();
        }
    }

    //region Detail Item Click
    @Override
    public void onDetailItemClick(DetailItemType type, int referenceIndex) {
        switch (type) {
            case Favourite:
                changeStoreFavouriteState();
                break;
            case Video:
                openYoutubeVideo(mVideos[referenceIndex].getKey());
                break;

            default:
                throw new IllegalArgumentException("Unknown item click type.");
        }
    }
    //endregion

    //region Request Delegate
    @Override
    public void beforeRequest(MoviesListType type) {
        mProgressBar.setVisibility(View.VISIBLE);
        mDetailList.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRequestSuccess(MoviesListType type, Object data) {
        mProgressBar.setVisibility(View.INVISIBLE);

        MovieDetailsResponse list = (MovieDetailsResponse) data;
        mVideos = list.getVideos().getResults().toArray(new Video[list.getVideos().getResults().size()]);
        mReviews = list.getReviews().getResults().toArray(new Review[list.getReviews().getResults().size()]);

        mDetailAdapter.swapData(mMovie, mIsFavourite, mVideos, mReviews);
        mDetailList.setAdapter(mDetailAdapter);

        mDetailList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestError(MoviesListType type) {
        mProgressBar.setVisibility(View.INVISIBLE);

        mVideos = null;
        mReviews = null;

        mDetailAdapter.swapData(mMovie, mIsFavourite, mVideos, mReviews);
        mDetailList.setAdapter(mDetailAdapter);

        mDetailList.setVisibility(View.VISIBLE);
    }
    //endregion

}
