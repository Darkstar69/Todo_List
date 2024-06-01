package com.example.mynote;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ToDoListDatabase";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE ToDoList (id INTEGER PRIMARY KEY AUTOINCREMENT, todo TEXT, completed BOOLEAN DEFAULT false)");
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error creating database: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS ToDoList");
            onCreate(db);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error upgrading database: " + e.getMessage());
        }
    }
}
