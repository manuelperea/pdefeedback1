package com.example.gestortareas;

// Importaciones necesarias
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

// IMPLEMENTA LA INTERFAZ DE CALLBACK DEL ADAPTER
public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskActionListener {

    private TaskDbHelper dbHelper;
    private TaskAdapter taskAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new TaskDbHelper(this);
        recyclerView = findViewById(R.id.recyclerview_tasks);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializa el adapter, pasándole 'this' como listener
        Cursor cursor = dbHelper.readAllTasks();
        taskAdapter = new TaskAdapter(this, cursor, this);
        recyclerView.setAdapter(taskAdapter);

        // Configurar FAB para ir a TaskEditActivity
        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TaskEditActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTaskList();
    }

    private void refreshTaskList() {
        Cursor newCursor = dbHelper.readAllTasks();
        taskAdapter.swapCursor(newCursor);
    }

    // --- IMPLEMENTACIÓN DE LA INTERFAZ DE EVENTOS (TaskActionListener) ---

    // 1. Maneja el evento de cambio de estado del CheckBox
    @Override
    public void onTaskCompletionChanged(long taskId, boolean isCompleted) {
        dbHelper.updateTaskCompletion(taskId, isCompleted);
        refreshTaskList(); // Refresca la lista para reordenar/tachar
    }

    // 2. Maneja el evento de pulsación larga (Eliminar)
    @Override
    public void onTaskDeleted(long taskId) {
        showDeleteConfirmationDialog(taskId);
    }

    // --- LÓGICA DEL MENÚ Y DIÁLOGOS ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            showAlert();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.about_title);
        alertDialog.setMessage(R.string.about_message);
        alertDialog.setPositiveButton(R.string.ok_button, (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    // --- DIÁLOGO DE CONFIRMACIÓN DE ELIMINACIÓN ---
    private void showDeleteConfirmationDialog(long taskId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Eliminación");
        builder.setMessage("¿Estás seguro de que deseas eliminar esta tarea?");

        builder.setPositiveButton("Eliminar", (dialog, which) -> {
            int rowsDeleted = dbHelper.deleteTask(taskId);

            if (rowsDeleted > 0) {
                Toast.makeText(MainActivity.this, "Tarea eliminada.", Toast.LENGTH_SHORT).show();
                refreshTaskList();
            } else {
                Toast.makeText(MainActivity.this, "Error al eliminar.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}