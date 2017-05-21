package com.apps.eric.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Eric on 5/21/2017.
 */

public class MyPreferences {
    private static final String PREF_SEARCH_QUERY = "searchQuery";

    public static String getStoredQuery(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY, null);
    }

    public static void setStoredQuery(Context context,String query){
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply();
    }
}
