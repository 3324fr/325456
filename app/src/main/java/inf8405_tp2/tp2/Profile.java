package inf8405_tp2.tp2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.renderscript.Sampler;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 422234 on 2017-02-17.
 */

public class Profile {

    public  String name_;
    public String group_;
    public byte[] picture_;

    public Profile(String name, String group, byte[] picture) {
        name_ = name;
        group_ = group;
        picture_ = picture;
    }

    public void david(SQLiteDatabase db){
        // Create insert entries

        ContentValues values = new ContentValues();
        values.put(ContractSQLite.ProfileEntry.COLUMN_NAME_TITLE, name_);
    values.put(ContractSQLite.ProfileEntry.COLUMN_NAME_SUBTITLE, group_);
   values.put(ContractSQLite.ProfileEntry.COLUMN_NAME_PICTURE, picture_);
        long newRowId = db.insert(ContractSQLite.ProfileEntry.TABLE_NAME, null, values);

    }

    public static Profile gourde(SQLiteDatabase db, String name, String group){


        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
               ContractSQLite.ProfileEntry._ID,
                ContractSQLite.ProfileEntry.COLUMN_NAME_TITLE,
             ContractSQLite.ProfileEntry.COLUMN_NAME_SUBTITLE,
           ContractSQLite.ProfileEntry.COLUMN_NAME_PICTURE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = ContractSQLite.ProfileEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { name };


        Cursor cursor = db.query(
                ContractSQLite.ProfileEntry.TABLE_NAME,   // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                        // The sort order
        );

        Profile profile = null;
        if (cursor != null) {
            cursor.moveToFirst();
            profile = new Profile(cursor.getString(1), cursor.getString(2), cursor.getBlob(3));
        }



        return profile;
    }

}

