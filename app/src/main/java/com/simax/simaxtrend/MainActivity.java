package com.simax.simaxtrend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.simax.simaxtrend.Adapters.MovieAdapter;
import com.simax.simaxtrend.Utils.NetworkUtils;
import com.simax.simaxtrend.models.Movie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    String popularMoviesURL;
    String topRatedMoviesURL;
    String myApiKey = BuildConfig.API_KEY;

    private MovieAdapter adapter;
    private List<Movie> movieList;
    private SwipeRefreshLayout swipeContainer;

    ArrayList<com.simax.simaxtrend.models.Movie> mPopularList;
    ArrayList<com.simax.simaxtrend.models.Movie> mTopTopRatedList;

    @BindView(R.id.moviesBar)
    ProgressBar mProgressBar;

    @BindView(R.id.mGridView)
    RecyclerView mGridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.INVISIBLE);

        initialize();

        //new FetchMovies().execute();

    }

    public void initialize(){
        movieList = new ArrayList<>();
        int numberOfColumns = 2;
        mGridView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new MovieAdapter(this, movieList);

        //adapter.setClickListener(this);
        mGridView.setItemAnimator(new DefaultItemAnimator());
        mGridView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                initialize();
                Toast.makeText(MainActivity.this, "Movies Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

    }



    //AsyncTask
    @SuppressLint("StaticFieldLeak")
    public class FetchMovies extends AsyncTask<Void,Void, List<String>> {



        @Override
        protected List<String> doInBackground(Void... voids) {

            popularMoviesURL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key="+myApiKey+"&language=en-US";

            topRatedMoviesURL = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key="+myApiKey+"&language=en-US";

            mPopularList = new ArrayList<>();
            mTopTopRatedList = new ArrayList<>();

            try {
                if(NetworkUtils.networkStatus(MainActivity.this)){
                    mPopularList = NetworkUtils.fetchData(popularMoviesURL); //Get popular movies
                    mTopTopRatedList = NetworkUtils.fetchData(topRatedMoviesURL); //Get top rated movies
                }else{
                    Toast.makeText(MainActivity.this,"Please Connect to the Internet",Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
                return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            mProgressBar.setVisibility(View.GONE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.pop_movies) {
            refreshList(mPopularList);
        }
        if (id == R.id.top_movies) {
            refreshList(mTopTopRatedList);
        }
        return super.onOptionsItemSelected(item);
    }
    private void refreshList(ArrayList<Movie> movieList) {
        //MovieAdapter adapter = new MovieAdapter(this, movieList);
        mGridView.invalidate();
        mGridView.setAdapter(adapter);
    }



}
