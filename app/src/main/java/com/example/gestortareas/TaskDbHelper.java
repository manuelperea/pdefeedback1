package com.example.gestortareas;

import android.content.Context;
import android.database.Cursor;
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

    // Consulta la BD y devuelve un Cursor con todas las tareas
    public Cursor readAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase(); // Abre la bd en modo lectura

        //  Columnas que queremos consultar
        String[] projection = {
                TaskEntry._ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_IS_COMPLETED
        };

        // Ordenamos por el estado completado para que salgan primero las tareas pendientes
        String sortOrder = TaskEntry.COLUMN_NAME_IS_COMPLETED + " ASC";

        // Consulta SQL para mostrar la lista ordenada por estado
        Cursor cursor = db.query(
                TaskEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        return cursor;
    }

}
