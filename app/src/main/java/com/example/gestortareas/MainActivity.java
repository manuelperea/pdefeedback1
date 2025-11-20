package com.example.gestortareas;
// Asegúrate de tener todas estas importaciones
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private TaskDbHelper dbHelper;
    private TaskAdapter taskAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Configurar la Toolbar (ActionBar)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. Inicializar Base de Datos y RecyclerView
        dbHelper = new TaskDbHelper(this); // Inicializa el helper de BD
        recyclerView = findViewById(R.id.recyclerview_tasks);

        // 3. Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicialmente, cargamos los datos
        Cursor cursor = dbHelper.readAllTasks();
        taskAdapter = new TaskAdapter(this, cursor);
        recyclerView.setAdapter(taskAdapter);

        // 4. Configurar FAB para ir a TaskEditActivity (Creación)
        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TaskEditActivity.class);
                startActivity(intent);
            }
        });
    }


    // Se llama cuando la Activity vuelve a primer plano (e.g., después de guardar una tarea).
    @Override
    protected void onResume() {
        super.onResume();
        refreshTaskList();
    }

    // Carga nuevos datos de la DB e intercambia el Cursor en el Adapter.
    private void refreshTaskList() {
        Cursor newCursor = dbHelper.readAllTasks();
        taskAdapter.swapCursor(newCursor);
    }

    // MÉTODOS DEL MENÚ DE OPCIONES
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // Asegúrate de crear res/menu/menu_main.xml
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

    // ACERCA DE
    private void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.about_title);
        alertDialog.setMessage(R.string.about_message);

        alertDialog.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }
}