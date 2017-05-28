package com.sebas.popularmovies.interfaces;

import com.sebas.popularmovies.model.MoviesListType;

/**
 * Created by Sebas on 5/25/17.
 */

public interface RequestDelegate {
    void beforeRequest(MoviesListType type);
    void onRequestSuccess(MoviesListType type, Object data);
    void onRequestError(MoviesListType type);
}
