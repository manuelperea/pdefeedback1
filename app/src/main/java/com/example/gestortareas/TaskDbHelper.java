package com.example.gestortareas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gestortareas.TaskContract.TaskEntry;

public class TaskDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TaskOrganizer.db";

    // Sentencia SQL para la creación de la tabla.
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                    TaskEntry._ID + " INTEGER PRIMARY KEY," + // Proporcionado por BaseColumns
                    TaskEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                    TaskEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    TaskEntry.COLUMN_NAME_IS_COMPLETED + " INTEGER DEFAULT 0)";

    // Sentencia SQL para eliminar la tabla (en caso de actualización).
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;

    // Constructor
    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Se llama la primera vez que se accede a la base de datos.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    // Se llama si la versión de la base de datos ha cambiado.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // La política más simple es descartar los datos y empezar de nuevo.
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

}
