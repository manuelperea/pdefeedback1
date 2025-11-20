package com.example.gestortareas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gestortareas.TaskContract.TaskEntry;

public class TaskDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TaskOrganizer.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                    TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                    TaskEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    TaskEntry.COLUMN_NAME_IS_COMPLETED + " INTEGER DEFAULT 0)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    // --- LECTURA DE TODAS LAS TAREAS (READ ALL) ---
    public Cursor readAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = { TaskEntry._ID, TaskEntry.COLUMN_NAME_TITLE, TaskEntry.COLUMN_NAME_IS_COMPLETED };
        String sortOrder = TaskEntry.COLUMN_NAME_IS_COMPLETED + " ASC";

        Cursor cursor = db.query(TaskEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
        return cursor;
    }

    // --- LECTURA POR ID (READ BY ID - para la Edición) ---
    public Cursor readTaskById(long taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                TaskEntry._ID, TaskEntry.COLUMN_NAME_TITLE, TaskEntry.COLUMN_NAME_DESCRIPTION, TaskEntry.COLUMN_NAME_IS_COMPLETED
        };
        String selection = TaskEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(taskId) };

        Cursor cursor = db.query(TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        return cursor;
    }

    // --- ACTUALIZACIÓN COMPLETA (UPDATE - para la Edición) ---
    public int updateTask(long taskId, String newTitle, String newDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_TITLE, newTitle);
        values.put(TaskEntry.COLUMN_NAME_DESCRIPTION, newDescription);

        String selection = TaskEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(taskId) };

        int count = db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        return count;
    }

    // --- ACTUALIZACIÓN DE ESTADO (UPDATE - para el CheckBox) ---
    public int updateTaskCompletion(long taskId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_IS_COMPLETED, isCompleted ? 1 : 0);

        String selection = TaskEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(taskId) };

        int count = db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        return count;
    }

    // --- ELIMINACIÓN (DELETE) ---
    public int deleteTask(long taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = TaskEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(taskId) };
        int deletedRows = db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows;
    }
}