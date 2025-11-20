package com.example.gestortareas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.gestortareas.TaskContract.TaskEntry;

public class TaskEditActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "TASK_ID";

    private EditText editTitle;
    private EditText editDescription;
    private TaskDbHelper dbHelper;
    private long mTaskId = -1; // -1 indica que es una tarea NUEVA

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        dbHelper = new TaskDbHelper(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Lógica para modo CREACIÓN o EDICIÓN
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            mTaskId = intent.getLongExtra(EXTRA_TASK_ID, -1);

            if (mTaskId != -1) {
                loadTaskData(mTaskId); // Carga datos existentes
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Editar Tarea");
                }
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Nueva Tarea");
            }
        }
    }

    /** Manejador de clic para el botón Guardar/save. (Definido en XML) */
    public void saveTask(View view) {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "El título no puede estar vacío.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mTaskId == -1) {
            insertNewTask(title, description);
        } else {
            updateExistingTask(mTaskId, title, description);
        }
    }

    // --- Lógica de Lectura y Carga de Datos (READ) ---
    private void loadTaskData(long taskId) {
        Cursor cursor = dbHelper.readTaskById(taskId);

        if (cursor != null && cursor.moveToFirst()) {
            // Se usa getColumnIndex para evitar la excepción getColumnIndexOrThrow si el cursor está mal
            int titleIndex = cursor.getColumnIndex(TaskEntry.COLUMN_NAME_TITLE);
            int descIndex = cursor.getColumnIndex(TaskEntry.COLUMN_NAME_DESCRIPTION);

            String title = (titleIndex != -1) ? cursor.getString(titleIndex) : "";
            String description = (descIndex != -1) ? cursor.getString(descIndex) : "";

            editTitle.setText(title);
            editDescription.setText(description);

            cursor.close();
        }
    }

    // --- Lógica de Inserción (CREATE) ---
    private void insertNewTask(String title, String description) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_TITLE, title);
        values.put(TaskEntry.COLUMN_NAME_DESCRIPTION, description);
        values.put(TaskEntry.COLUMN_NAME_IS_COMPLETED, 0);

        long newRowId = db.insert(TaskEntry.TABLE_NAME, null, values);
        db.close();

        if (newRowId != -1) {
            Toast.makeText(this, "Tarea registrada con éxito.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al registrar la tarea.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Lógica de Actualización (UPDATE) ---
    private void updateExistingTask(long taskId, String title, String description) {
        int rowsAffected = dbHelper.updateTask(taskId, title, description);

        if (rowsAffected > 0) {
            Toast.makeText(this, "Tarea modificada con éxito.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al modificar la tarea.", Toast.LENGTH_SHORT).show();
        }
    }
}