package com.sebas.popularmovies.network;

import com.sebas.popularmovies.interfaces.RequestDelegate;
import com.sebas.popularmovies.model.Movie;
import com.sebas.popularmovies.model.MovieDetailsResponse;
import com.sebas.popularmovies.model.MoviesListType;
import com.sebas.popularmovies.model.Response;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Sebas on 5/22/17.
 */

public final class MovieService {

    private static final ApiInterface sApiInterface = ApiClient.getClient().create(ApiInterface.class);

    public static void getTopRatedMovies(final RequestDelegate delegate) {
        delegate.beforeRequest(MoviesListType.TopRated);
        sApiInterface.getTopRatedMovies(ApiClient.TMDB_API_KEY).enqueue(new Callback<Response<Movie>>() {
            @Override
            public void onResponse(Call<Response<Movie>> call, retrofit2.Response<Response<Movie>> response) {
                if (response.body() != null && response.body().getResults() != null && response.body().getResults().size() > 0)
                    delegate.onRequestSuccess(MoviesListType.TopRated, response.body().getResults());
                else
                    delegate.onRequestError(MoviesListType.TopRated);
            }

            @Override
            public void onFailure(Call<Response<Movie>> call, Throwable t) {
                delegate.onRequestError(MoviesListType.TopRated);
            }
        });
    }

    public static void getPopularMovies(final RequestDelegate delegate) {
        delegate.beforeRequest(MoviesListType.MostPopular);
        sApiInterface.getPopularMovies(ApiClient.TMDB_API_KEY).enqueue(new Callback<Response<Movie>>() {
            @Override
            public void onResponse(Call<Response<Movie>> call, retrofit2.Response<Response<Movie>> response) {
                if (response.body() != null && response.body().getResults() != null && response.body().getResults().size() > 0)
                    delegate.onRequestSuccess(MoviesListType.MostPopular, response.body().getResults());
                else
                    delegate.onRequestError(MoviesListType.MostPopular);
            }

            @Override
            public void onFailure(Call<Response<Movie>> call, Throwable t) {
                delegate.onRequestError(MoviesListType.MostPopular);
            }
        });
    }

    public static void getMovieDetails(final RequestDelegate delegate, int movieId) {
        delegate.beforeRequest(MoviesListType.Detail);
        sApiInterface.getMovieDetails(movieId,
                ApiClient.TMDB_API_KEY,
                ApiClient.TMDB_APPEND_FIELDS).enqueue(new Callback<MovieDetailsResponse>() {
            @Override
            public void onResponse(Call<MovieDetailsResponse> call, retrofit2.Response<MovieDetailsResponse> response) {
                if (response.body() != null)
                    delegate.onRequestSuccess(MoviesListType.Detail, response.body());
                else
                    delegate.onRequestError(MoviesListType.Detail);
            }

            @Override
            public void onFailure(Call<MovieDetailsResponse> call, Throwable t) {
                delegate.onRequestError(MoviesListType.Detail);
            }
        });
    }

}
