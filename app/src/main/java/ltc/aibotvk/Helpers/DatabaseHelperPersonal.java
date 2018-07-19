package ltc.aibotvk.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 29.06.2018.
 */

public class DatabaseHelperPersonal extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Personal.db";
    public static final String TABLE_NAME = "personal_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "SENTENCE";
    public static final String COL_3 = "ANSWER";

    public DatabaseHelperPersonal(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,SENTENCE TEXT,ANSWER TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String sentence,String answer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, sentence);
        contentValues.put(COL_3, answer);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public boolean updateData(String id,String sentence,String answer, String oldSent, String oldAns) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,sentence);
        contentValues.put(COL_3,answer);
        db.execSQL("UPDATE "+TABLE_NAME+" SET (SENTENCE, ANSWER) = ('"+sentence+"', '"+answer+"') WHERE SENTENCE = '"+oldSent+"' AND ANSWER = '"+oldAns+"'");

        //db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id, String oldSent, String oldAns) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE SENTENCE = '"+oldSent+"' AND ANSWER = '"+oldAns+"'");
        //return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
        return 1;
    }

}