package com.sebas.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sebas on 5/27/17.
 */

public class Response<T> {

    @SerializedName("results")
    private List<T> results = null;

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

}
