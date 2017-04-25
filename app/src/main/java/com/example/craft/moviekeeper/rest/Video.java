package com.example.craft.moviekeeper.rest;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class Video {


    @SerializedName("id")
    String id;

    @SerializedName("iso_639_1")
    String languageCode;

    @SerializedName("key")
    String key;

    @SerializedName("name")
    String name;

    @SerializedName("site")
    String site;

    @SerializedName("size")
    int size;

    @SerializedName("type")
    String type;

    public String getId() {
        return id;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public boolean isYoutubeTrailer() {
        return (site.toLowerCase().equals("youtube") && type.toLowerCase().equals("trailer"));
    }

    public Uri getYoutubUrl() {
        return Uri.parse("http://www.youtube.com/watch?v=" + getKey());
    }
}
