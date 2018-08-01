package com.newpath.puremuse.utils;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newpath.puremuse.models.AudioFileModel;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converters {
    @TypeConverter
    public static ArrayList<AudioFileModel> fromString(String value) {
        Type listType = new TypeToken<ArrayList<AudioFileModel>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<AudioFileModel> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

}
