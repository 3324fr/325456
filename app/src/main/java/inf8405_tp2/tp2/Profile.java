package inf8405_tp2.tp2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;


/**
 * Created by 422234 on 2017-02-17.
 */

public class Profile {

    public String name_;
    public Bitmap picture_;


    public Profile(String name, Bitmap picture) {
        name_ = name;
        picture_ = picture;
    }

    public Profile(String name) {
        name_ = name;
    }


    public void save(Context context) {

        DatabaseHelper helper = new DatabaseHelper(context);

        SQLiteDatabase db = helper.getWritableDatabase();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture_.compress(Bitmap.CompressFormat.PNG, 0, stream);

        // Create insert entries
        ContentValues values = new ContentValues();
        values.put(ContractSQLite.ProfileEntry.COLUMN_NAME_TITLE, name_);
        values.put(ContractSQLite.ProfileEntry.COLUMN_NAME_PICTURE, stream.toByteArray());
        long newRowId = db.insert(ContractSQLite.ProfileEntry.TABLE_NAME, null, values);

        db.close();

    }

    public static Profile get(Context context, String name) {

        DatabaseHelper helper = new DatabaseHelper(context);

        SQLiteDatabase db = helper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ContractSQLite.ProfileEntry._ID,
                ContractSQLite.ProfileEntry.COLUMN_NAME_TITLE,
                ContractSQLite.ProfileEntry.COLUMN_NAME_PICTURE
        };

        // Filter results WHERE
        String selection = ContractSQLite.ProfileEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = {name};


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
        if( cursor != null && cursor.moveToFirst() ){
            byte[] bitmapbytes = cursor.getBlob(2);
            Bitmap bitmap = null;
            if(bitmapbytes != null)
                bitmap = BitmapFactory.decodeByteArray(bitmapbytes, 0, bitmapbytes.length);
            profile = new Profile(cursor.getString(1), bitmap);
        }

        db.close();
        return profile;
    }

}

