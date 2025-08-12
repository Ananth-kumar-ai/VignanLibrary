package org.vignanuniversity.vignanlibrary.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.vignanuniversity.vignanlibrary.StudentData;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE student_details (" +
                "regno VARCHAR(20) PRIMARY KEY," +
                "name VARCHAR(100)," +
                "gender CHAR(1)," +
                "branch VARCHAR(100)," +
                "branchcode VARCHAR(10)," +
                "course VARCHAR(50)," +
                "coursecode VARCHAR(10)," +
                "semester INT," +
                "section VARCHAR(10)," +
                "studentmobile VARCHAR(20)," +
                "studentemailid VARCHAR(100)," +
                "parentemailid VARCHAR(100)," +
                "fathermobile VARCHAR(20)" +
                ");";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop Table if exists student_details");
    }

    public void truncateTable() {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("DELETE FROM student_details");
    }

    public Boolean insertData(String regno, ArrayList<StudentData> studentDetails){

        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for(StudentData i:studentDetails){
            contentValues.put("regno",i.getRegno());
            contentValues.put("name",i.getName());
            contentValues.put("gender",i.getGender());
            contentValues.put("branch",i.getBranch());
            contentValues.put("branchcode",i.getBranchcode());
            contentValues.put("course",i.getCourse());
            contentValues.put("coursecode",i.getCoursecode());
            contentValues.put("semester",i.getSemester());
            contentValues.put("section",i.getSection());
            contentValues.put("studentmobile",i.getStudentmobile());
            contentValues.put("studentemailid",i.getStudentemailid());
            contentValues.put("parentemailid",i.getParentemailid());
            contentValues.put("fathermobile",i.getFathermobile());
        }



        long result=DB.insert("student_details", null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }


    public Boolean updateuserdata(String regno,ArrayList<StudentData> studentDetails)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for(StudentData i:studentDetails){
            contentValues.put("regno",i.getRegno());
            contentValues.put("name",i.getName());
            contentValues.put("gender",i.getGender());
            contentValues.put("branch",i.getBranch());
            contentValues.put("branchcode",i.getBranchcode());
            contentValues.put("course",i.getCourse());
            contentValues.put("coursecode",i.getCoursecode());
            contentValues.put("semester",i.getSemester());
            contentValues.put("section",i.getSection());
            contentValues.put("studentmobile",i.getStudentmobile());
            contentValues.put("studentemailid",i.getStudentemailid());
            contentValues.put("parentemailid",i.getParentemailid());
            contentValues.put("fathermobile",i.getFathermobile());
        }




        Cursor cursor = DB.rawQuery("Select * from student_details where regno = ?", new String[]{regno});
        if (cursor.getCount() > 0) {
            long result = DB.update("student_details", contentValues, "regno=?", new String[]{regno});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public Cursor getReg_no ()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from student_details", null);
        return cursor;
    }

}
