package com.sebas.popularmovies.interfaces;

import com.sebas.popularmovies.model.DetailItemType;

/**
 * Created by Sebas on 5/22/17.
 */

public interface DetailItemClickListener {
    void onDetailItemClick(DetailItemType type, int referenceIndex);
}
