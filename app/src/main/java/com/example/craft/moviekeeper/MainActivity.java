package com.example.craft.moviekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.craft.moviekeeper.rest.Movie;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements  MoviePosterGridFragment.OnMovieSelectedListener {

    static final String TAG = MainActivity.class.getSimpleName();

    private static final String KEY_MOVIE = "movie";

    private static final String TAG_DETAIL_FRAGMENT = "fragment_details";

    boolean mIsFavorite = false;

    boolean mTwoPaneMode = false;

    // remember the selected movie
    Movie mSelectedMovie;

    ListView mDrawerList;
    ArrayAdapter<String> mDrawerAdapter;
    @Nullable
    @Bind(R.id.fab_favorite)
    FloatingActionButton mFavoriteFab;

    private void addDrawerItems() {
        String[] movieLists = {"Popular", "Highest Rated", "Favorites"};
        mDrawerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, movieLists);
        mDrawerList.setAdapter(mDrawerAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mDrawerList = (ListView) findViewById(R.id.navList);
        addDrawerItems();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }


        if (savedInstanceState != null) {
            mSelectedMovie = savedInstanceState.getParcelable(KEY_MOVIE);
            mIsFavorite = MovieFavorites.isFavoriteMovie(this, mSelectedMovie.getId());
        }

        if (findViewById(R.id.content_split) != null) {
            mTwoPaneMode = false;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_MOVIE, mSelectedMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSelectedMovie != null && findViewById(R.id.movie_detail_title) != null) {
            onMovieSelected(mSelectedMovie, false, null);
        }
    }

    @Override
    public void onMovieSelected(Movie movie, boolean onClick, View srcView) {

        mSelectedMovie = movie;
        mIsFavorite = mSelectedMovie == null ? false : MovieFavorites.isFavoriteMovie(this, mSelectedMovie.getId());

        if (mTwoPaneMode) {
            MovieDetailFragment fragment = (MovieDetailFragment)
                  getSupportFragmentManager().findFragmentById(R.id.fragment_detail);

            if (fragment != null & movie == null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(fragment).commit();
            } else if (fragment == null || fragment.mMovie.getId() != mSelectedMovie.getId()) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(MovieDetailFragment.KEY_MOVIE, movie);
                fragment = MovieDetailFragment.newInstance(movie);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (srcView != null) {
                    Log.d(TAG, "Fragment with transition??");
                    transaction.addSharedElement(srcView, getResources().getString(R.string.transition_poster));
                }
                transaction.replace(R.id.fragment_detail, fragment, TAG_DETAIL_FRAGMENT).commit();
                mFavoriteFab.setImageResource(MovieFavorites.getImageResourceId(mIsFavorite));
            }

            String title = movie == null ? "" : movie.getTitle();
            TextView titleView = (TextView) findViewById(R.id.movie_detail_title);
            titleView.setText(title);
            mFavoriteFab.show();

        } else if (onClick) {
            onMovieClicked(movie, true, srcView);

        }
    }

    public void onMovieClicked(Movie movie, boolean onClick, View srcView) {
        Log.d(TAG, "Start Activity with transition??");
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.KEY_MOVIE, movie);

        ActivityOptionsCompat options = ActivityOptionsCompat.
              makeSceneTransitionAnimation(this, srcView, getResources().getString(R.string.transition_poster));
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    @Nullable @OnClick(R.id.fab_favorite) void onFavoriteClicked() {
        toggleFavorite();
    }

    private void toggleFavorite() {
        mIsFavorite = !mIsFavorite;
        mFavoriteFab.setImageResource(MovieFavorites.getImageResourceId(mIsFavorite));
        if (mSelectedMovie != null) {
            MovieFavorites.updateFavorite(this, mIsFavorite, mSelectedMovie);
        }
        // refresh after movie unfavorited
        if (mTwoPaneMode && !mIsFavorite) {
            MoviePosterGridFragment gridFragment = (MoviePosterGridFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_grid);
            gridFragment.removeMovie(mSelectedMovie);
        }
    }
}
