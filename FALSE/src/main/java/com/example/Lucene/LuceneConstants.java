package com.example.Lucene;

import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;

public class LuceneConstants {

    // File System Constants
    public static final String ROOT_DIRECTORY = "./SilverLuceneProjectAppData";
    public static final String DATA_DIRECTORY = "./SilverLuceneProjectAppData/Data";
    public static final String SETTINGS_DIRECTORY = "./SilverLuceneProjectAppData/Data/settings";
    public static final String SETTINGS_FILE_DIRECTORY = "./SilverLuceneProjectAppData/Data/settings/LuceneSettings.conf";
    public static final String INDEX_DIRECTORY = "./SilverLuceneProjectAppData/Index";
    public static final String SONG_INDEX_DIRECTORY = "./SilverLuceneProjectAppData/Index/albums";
    public static final String ALBUM_INDEX_DIRECTORY = "./SilverLuceneProjectAppData/Index/songs";

    // Argument Constants
    public static final String ALBUM_DATATYPE = "Albums";
    public static final String SONG_DATATYPE = "Songs";
    public static final String LYRICS_DATATYPE = "Lyrics";

    // Field Constants
    public static final String GENERAL = "General";
    public static final String SONG_NAME = "Name";
    public static final String SONG_ARTIST = "Artist";
    public static final String SONG_LINK = "Link";
    public static final String SONG_LYRICS = "Lyrics";
    public static final String ALBUM_NAME = "Name";
    public static final String ALBUM_ARTIST = "Artist";
    public static final String ALBUM_TYPE = "Type";
    public static final String ALBUM_YEAR = "Year";

    public static boolean INDEX_DEBUG = true;
    public static boolean SEARCH_DEBUG = true;
    public static boolean LYRICS_DEBUG = false;
}
