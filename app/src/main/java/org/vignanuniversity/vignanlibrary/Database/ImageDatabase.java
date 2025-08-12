package org.vignanuniversity.vignanlibrary.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ImageDatabase extends SQLiteOpenHelper {
    public ImageDatabase(@Nullable Context context) {
        super(context, "imageDatabase.db", null, 1);
    }

    private static final String TABLE_CREATE = "CREATE TABLE images (id INTEGER PRIMARY KEY, image BLOB);";
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS images");
    }

    public void truncateTable() {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("DELETE FROM images");
    }

    public Boolean saveImageToDatabase(byte[] imageBytes) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("image", imageBytes);
        long result = DB.insert("images", null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

}
