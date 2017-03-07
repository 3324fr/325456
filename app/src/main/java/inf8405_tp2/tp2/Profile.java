package inf8405_tp2.tp2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.XmlRes;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 422234 on 2017-02-17.
 */
@IgnoreExtraProperties
public class Profile {

    public String m_name;
    @Exclude
    public Bitmap m_picture;
    @Exclude
    private byte[] bytesPicture;


    public Profile(String name, Bitmap picture) {
        m_name = name;
        m_picture = picture;
    }

    public Profile() {
        m_name = "";
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }
        Profile that = (Profile) other;

        // Custom equality check here.
        return this.m_name.equals(that.m_name);
    }


    @Exclude
    public void save(SQLiteDatabase db,StorageReference pictureRef ) {
        this.save(db);
        pictureRef.child(m_name).putBytes(bytesPicture);

    }
    @Exclude
    public void save(SQLiteDatabase db ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        m_picture.compress(Bitmap.CompressFormat.PNG, 0, stream);

        bytesPicture = stream.toByteArray();

        // Create insert entries
        ContentValues values = new ContentValues();
        values.put(ContractSQLite.ProfileEntry.COLUMN_NAME_TITLE, m_name);
        values.put(ContractSQLite.ProfileEntry.COLUMN_NAME_PICTURE, bytesPicture);
        db.insert(ContractSQLite.ProfileEntry.TABLE_NAME, null, values);

    }
    @Exclude
    public static Profile get(SQLiteDatabase db, String name) {

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

        return profile;
    }

    @Exclude
    public static final List<String> getAllUsername(SQLiteDatabase db){


        Cursor  cursor = db.rawQuery("SELECT DISTINCT " +
                ContractSQLite.ProfileEntry.COLUMN_NAME_TITLE + " FROM " +
                ContractSQLite.ProfileEntry.TABLE_NAME ,null);
        List<String>  list = new ArrayList<>();
        if (cursor .moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                String name = cursor.getString(cursor.getColumnIndex(ContractSQLite.ProfileEntry.COLUMN_NAME_TITLE));
                list.add(name);
                cursor.moveToNext();
            }
        }

        return list;

    }

}

