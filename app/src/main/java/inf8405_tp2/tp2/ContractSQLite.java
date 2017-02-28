package inf8405_tp2.tp2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


/**
 * Created by 422234 on 2017-02-17.
 */

public final class ContractSQLite {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ContractSQLite() {}

    /* Inner class that defines the table contents */
    public static class ProfileEntry implements BaseColumns {
        public static final String TABLE_NAME = "profile";
        public static final String COLUMN_NAME_TITLE = "name";
        public static final String COLUMN_NAME_SUBTITLE = "clan";
        public static final String COLUMN_NAME_PICTURE = "photo";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ProfileEntry.TABLE_NAME + " (" +
                    ProfileEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ProfileEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                    ProfileEntry.COLUMN_NAME_SUBTITLE + " TEXT NOT NULL," +
                    ProfileEntry.COLUMN_NAME_PICTURE + " BLOB) ";



    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ProfileEntry.TABLE_NAME;


}
