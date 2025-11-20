package com.example.gestortareas;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gestortareas.TaskContract.TaskEntry;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Extender de RecyclerView.Adapter e implementar el ViewHolder
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    /**
     * Constructor que recibe el Contexto de la Activity y el Cursor con los datos.
     */
    public TaskAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    /**
     * 1. Crea nuevos ViewHolders (y sus Vistas) para el RecyclerView.
     * Llamado cuando el RecyclerView necesita un nuevo ViewHolder.
     */
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout del elemento individual de la lista
        View view = LayoutInflater.from(mContext).inflate(R.layout.task_list_item, parent, false);
        return new TaskViewHolder(view);
    }

    /**
     * 2. Reemplaza el contenido de una vista.
     * Llamado por el LayoutManager cuando necesita mostrar datos en una posición.
     */
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return; // No debe suceder
        }

        // Obtener los datos del Cursor para la posición actual
        long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(TaskEntry._ID));
        String title = mCursor.getString(mCursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
        int isCompleted = mCursor.getInt(mCursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_IS_COMPLETED));

        // Asignar datos a las Vistas del ViewHolder
        holder.titleText.setText(title);
        holder.checkBox.setChecked(isCompleted == 1);
        holder.itemView.setTag(id); // Almacena el ID de la tarea en la vista para su posterior uso

        // Lógica de interfaz de usuario para tareas completadas
        if (isCompleted == 1) {
            // Tachar el texto (strikethrough)
            holder.titleText.setPaintFlags(holder.titleText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            // Quitar el tachado
            holder.titleText.setPaintFlags(holder.titleText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // TODO: En el Paso 5, asignaremos OnClickListeners a CheckBox y ImageView.
    }

    /**
     * 3. Devuelve el número total de elementos.
     */
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    /**
     * Permite intercambiar el Cursor por uno nuevo (e.g., después de añadir o completar una tarea).
     */
    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged(); // Notifica a la vista que los datos han cambiado
        }
    }

    /**
     * Clase interna: Define el patrón ViewHolder (cache de vistas).
     * Requisito: Debe extender de RecyclerView.ViewHolder.
     */
    public class TaskViewHolder extends RecyclerView.ViewHolder {
        // Declaración de las vistas
        public final CheckBox checkBox;
        public final TextView titleText;
        public final ImageView editIcon;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            // Localización de las vistas
            checkBox = itemView.findViewById(R.id.task_checkbox);
            titleText = itemView.findViewById(R.id.task_title);
            editIcon = itemView.findViewById(R.id.task_edit_icon);
        }
    }
}