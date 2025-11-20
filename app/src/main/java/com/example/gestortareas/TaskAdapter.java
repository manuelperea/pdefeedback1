package com.example.gestortareas;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestortareas.TaskContract.TaskEntry;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    // INTERFAZ DE CALLBACK: Define los eventos que la Activity debe escuchar
    public interface TaskActionListener {
        void onTaskCompletionChanged(long taskId, boolean isCompleted); // Evento de CheckBox
        void onTaskDeleted(long taskId); // Evento de Pulsación Larga (Eliminar)
    }

    private final Context mContext;
    private Cursor mCursor;
    private final TaskActionListener mListener;

    /** Constructor: ahora requiere la Activity como listener. */
    public TaskAdapter(Context context, Cursor cursor, TaskActionListener listener) {
        this.mContext = context;
        this.mCursor = cursor;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.task_list_item, parent, false);
        // Pasa el listener al ViewHolder
        return new TaskViewHolder(view, mListener, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(TaskEntry._ID));
        String title = mCursor.getString(mCursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
        int isCompleted = mCursor.getInt(mCursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_IS_COMPLETED));

        holder.titleText.setText(title);
        holder.itemView.setTag(id);

        // --- LÓGICA DE CHECKBOX (EVENTO DE USUARIO) ---
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(isCompleted == 1);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mListener != null) {
                // Notifica a la Activity que el estado ha cambiado
                mListener.onTaskCompletionChanged(id, isChecked);
            }
        });

        // --- LÓGICA DE ESTILO (Tachado) ---
        if (isCompleted == 1) {
            holder.titleText.setPaintFlags(holder.titleText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.titleText.setPaintFlags(holder.titleText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // --- LÓGICA DE CLIC EN EL ÍCONO DE EDICIÓN ---
        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long taskId = (long) holder.itemView.getTag();
                Intent intent = new Intent(mContext, TaskEditActivity.class);
                intent.putExtra(TaskEditActivity.EXTRA_TASK_ID, taskId);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    // Clase interna: Implementa OnLongClickListener para el evento de eliminar
    public static class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public final CheckBox checkBox;
        public final TextView titleText;
        public final ImageView editIcon;
        private final TaskActionListener listener;

        public TaskViewHolder(@NonNull View itemView, TaskActionListener listener, Context context) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.task_checkbox);
            titleText = itemView.findViewById(R.id.task_title);
            editIcon = itemView.findViewById(R.id.task_edit_icon);

            this.listener = listener;
            itemView.setOnLongClickListener(this); // Asigna el OnLongClickListener a toda la fila
        }

        @Override
        public boolean onLongClick(View v) {
            long taskId = (long) v.getTag();
            if (listener != null) {
                listener.onTaskDeleted(taskId); // Dispara el evento de eliminación
                return true;
            }
            return false;
        }
    }
}