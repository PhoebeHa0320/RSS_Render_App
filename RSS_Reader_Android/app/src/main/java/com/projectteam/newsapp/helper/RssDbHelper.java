package com.projectteam.newsapp.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RssDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "RSSLibray.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "rss_list";
    private static final String TABLE_FAV = "favourite";
    String[] codeTranslate = {"11","12","13","14","15","16","17","18","19","20","21"};


    public RssDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //optional: this may be weird
        /*add translate collum with default value (something text). When program run, it will automatic
        check language system and update collum title match with language follow system or follow user input
        P/s: this progress can very slow on lowest system (I mean: the old phone)
        I have no choice so i will follow this idea.
        Follow array-list rss title we have some default value:
        1. Breaking News: 11
        2. World: 12
        3. News: 13
        4. Sport: 14
        5. Law: 15
        6. Education: 16
        7. Health: 17
        8. Life Style: 18
        9. Travel: 19
        10. Science: 20
        11. Other: 21
        */
        String table1 = "CREATE TABLE " + TABLE_NAME + " (id INTEGER primary key autoincrement," +
                "title TEXT, name TEXT, url TEXT,date TEXT, translate TEXT)";
        String table2 = "CREATE TABLE " + TABLE_FAV + " (id INTEGER primary key autoincrement," +
                "title TEXT, url TEXT, img TEXT,pubdate TEXT, favstatus text)";
        //default databse rss
        String querry1 = "INSERT INTO " + TABLE_NAME + " (title, name, url, translate) values " +
                "('Tin tức mới', 'VNExpress Home' , 'https://vnexpress.net/rss/tin-moi-nhat.rss', '11')," +
                "('Tin tức mới', 'Tuổi Trẻ Home' , 'https://tuoitre.vn/rss/tin-moi-nhat.rss', '11')," +
                "('Tin tức mới' , 'Thanh Niên Home' , 'https://thanhnien.vn/rss/home.rss', '11')," +
                "('Tin tức mới', 'Báo tin tức' , 'https://baotintuc.vn/tin-moi-nhat.rss', '11')," +
                "('Tin tức mới', 'Báo người lao động' , 'https://nld.com.vn/tin-moi-nhat.rss', '11')";

        //excute querry
        db.execSQL(table1);
        db.execSQL(table2);
        db.execSQL(querry1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropRSSList = "DROP TABLE IF EXISTS " + TABLE_NAME;
        String dropFavList = "DROP TABLE IF EXISTS " + TABLE_FAV;
        db.execSQL(dropRSSList);
        db.execSQL(dropFavList);
        onCreate(db);
    }

    public void insertRSSList(String rss_title, String rss_name, String rss_url, String translate)
    {
        //Khoi tao database
        SQLiteDatabase db = getWritableDatabase();
        //khoi tao cac doi tuong
        ContentValues contentValues = new ContentValues();
        //them cac doi tuong vao database
        contentValues.put("title", rss_title);
        contentValues.put("name", rss_name);
        contentValues.put("url", rss_url);
        contentValues.put("translate", translate);
        //Lenh Insert
        db.insertWithOnConflict(TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        //dong database
        db.close();

    }

    public void updateRSSList(String rss_id, String rss_title, String rss_name, String rss_url, String translate)
    {
        //Khoi tao database
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", rss_title);
        contentValues.put("name", rss_name);
        contentValues.put("url", rss_url);
        contentValues.put("translate", translate);
        //Lenh Update
        db.update(TABLE_NAME,contentValues, "id=?",new String[]{rss_id});
        //dong database
        db.close();
    }

    public void deleteRSSList(String rss_id) {
        SQLiteDatabase db = getWritableDatabase();
        String delQuery = "delete from " + TABLE_NAME + " where id='" + rss_id + "'";
        db.execSQL(delQuery);
        db.close();
    }

    public void truncate()
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql1 = "delete from " + TABLE_NAME;
        String sql2 =  "DELETE FROM sqlite_sequence WHERE name = '" + TABLE_NAME + "'";
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.close();
    }
    //get source array
    public JSONArray getArray()
    {
        SQLiteDatabase db = getReadableDatabase();
        JSONArray jsonArray = new JSONArray();
        String sQuery = "select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(sQuery, null);
        if (cursor.moveToFirst())
        {
            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", cursor.getString(0));
                    jsonObject.put("title", cursor.getString(1));
                    jsonObject.put("name", cursor.getString(2));
                    jsonObject.put("url", cursor.getString(3));
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return  jsonArray;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //insert favourite item
    public void insertFav(String title, String url, String img, String pubdate, String favstatus) {
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("url", url);
        cv.put("img", img);
        cv.put("pubdate", pubdate);
        cv.put("favstatus", favstatus);
        db.insert(TABLE_FAV, null, cv);
        db.close();
    }
    // remove line from database favourite
    public void remove_fav(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + TABLE_FAV + " SET favstatus = 0 WHERE id = " + id + "";
        String del = "delete from " + TABLE_FAV + " where id = " + id + "";
        db.execSQL(del);
        db.execSQL(sql);
        db.close();
    }
    // remove favourite at home fragment
    public void remove_fav_main(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        String del = "delete from " + TABLE_FAV + " where title = '" + title + "'";
        db.execSQL(del);
        db.close();
    }
    public void truncate_fav()
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql1 = "delete from " + TABLE_FAV;
        String sql2 =  "DELETE FROM sqlite_sequence WHERE name = '" + TABLE_FAV + "'";
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.close();
    }
    //for favourite table, this is for check item available in table or not
    //some title name feed from rss may inclue '' can cause statement error, using new way query to prevent this problem
    public Cursor checkfavourite (String title)
    {
        SQLiteDatabase db = getReadableDatabase();
        String[] columnsToReturn = { "title", "favstatus" };
        String selection = "title = ? AND favstatus = ?";
        String[] selectionArgs = { title, "1" }; // matched to "?" in selection
        Cursor result = db.query(TABLE_FAV, columnsToReturn, selection, selectionArgs, null, null, null);
        return result;
    }
    // select all favorite list
    public JSONArray getFavourite()
    {
        SQLiteDatabase db = getReadableDatabase();
        JSONArray jsonFavourite = new JSONArray();
        String sFav = "SELECT * FROM " + TABLE_FAV + " WHERE favstatus = 1 ";
        Cursor cursor = db.rawQuery(sFav, null);
        if (cursor.moveToFirst())
        {
            do {
                JSONObject favObject = new JSONObject();
                try {
                    favObject.put("id", cursor.getString(0));
                    favObject.put("title", cursor.getString(1));
                    favObject.put("url", cursor.getString(2));
                    favObject.put("img", cursor.getString(3));
                    favObject.put("pubdate", cursor.getString(4));
                    jsonFavourite.put(favObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return  jsonFavourite;
    }
    //get first rss in list by default
    public Cursor firstRSSList() {
        SQLiteDatabase db = getReadableDatabase();
        String sQuery = "select * from " + TABLE_NAME + " where id=1";
        Cursor result = db.rawQuery(sQuery, null);
        return result;
    }
    //get name rss from title
    public ArrayList<String> getRecords(String title){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> data= new ArrayList<>();
        try
        {
            Cursor cursor = db.query(TABLE_NAME, new String[]{"name"},"title = '" + title + "'",null , null, null, null);
            String fieldToAdd;
            while(cursor.moveToNext()){
                fieldToAdd=cursor.getString(0);
                data.add(fieldToAdd);
            }
            cursor.close();
            return data;
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        return data;
    }
    //get name rss from title
    public Cursor getURL(String title, String name) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columnsToReturn = { "*" };
        String selection = "title = ? AND name= ?";
        String[] selectionArgs = { title, name }; // matched to "?" in selection
        Cursor result = db.query(TABLE_NAME, columnsToReturn, selection, selectionArgs, null, null, null);
        return result;
    }

    /*Fix the problem about multi-language database*/
    /*Caution: this progress can very slow on old smartphone*/
    public void updateTranslateTitle(String[] titlelist)
    {
        SQLiteDatabase db = getWritableDatabase();
        //now this will update 11 title. So weird
        //11 position follow 11 title
        String update1 = "UPDATE " + TABLE_NAME + " SET title = '" + titlelist[0] + "' WHERE translate = '" + codeTranslate[0] + "'";
        String update2 = "UPDATE " + TABLE_NAME + " SET title = '" + titlelist[1] + "' WHERE translate = '" + codeTranslate[1] + "'";
        String update3 = "UPDATE " + TABLE_NAME + " SET title = '" + titlelist[2] + "' WHERE translate = '" + codeTranslate[2] + "'";
        String update4 = "UPDATE " + TABLE_NAME + " SET title = '" + titlelist[3] + "' WHERE translate = '" + codeTranslate[3] + "'";
        String update5 = "UPDATE " + TABLE_NAME + " SET title = '" + titlelist[4] + "' WHERE translate = '" + codeTranslate[4] + "'";
        String update6 = "UPDATE " + TABLE_NAME + " SET title = '" + titlelist[5] + "' WHERE translate = '" + codeTranslate[5] + "'";
        String update7 = "UPDATE " + TABLE_NAME + " SET title = '" + titlelist[6] + "' WHERE translate = '" + codeTranslate[6] + "'";
        String update8 = "UPDATE " + TABLE_NAME + " SET title = '" + titlelist[7] + "' WHERE translate = '" + codeTranslate[7] + "'";
        String update9 = "UPDATE " + TABLE_NAME + " SET title = '" + titlelist[8] + "' WHERE translate = '" + codeTranslate[8] + "'";
        String update10 = "UPDATE " + TABLE_NAME + " SET title = '" + titlelist[9] + "' WHERE translate = '" + codeTranslate[9] + "'";
        String update11 = "UPDATE " + TABLE_NAME + " SET title = '" + titlelist[10] + "' WHERE translate = '" + codeTranslate[10] + "'";
        db.execSQL(update1);
        db.execSQL(update2);
        db.execSQL(update3);
        db.execSQL(update4);
        db.execSQL(update5);
        db.execSQL(update6);
        db.execSQL(update7);
        db.execSQL(update8);
        db.execSQL(update9);
        db.execSQL(update10);
        db.execSQL(update11);
        db.close();
    }
}
