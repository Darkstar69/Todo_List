package com.example.mynote;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageButton addBtn;
    DatabaseHelper databaseHelper;
    ArrayList<TodoData> todos;
    TodoAdapter todoAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.todoListRecycler);
        toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("Todo List");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitle("Manage your to do list");
        addBtn = findViewById(R.id.addTodoBtn);
        todos = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        addBtn.setOnClickListener(v -> addTodoPopup());
        showTodos();
        todoAdapter = new TodoAdapter(this, todos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(todoAdapter);
    }

    private void addTodoPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_add_todo);
        Objects.requireNonNull(dialog.getWindow()).setElevation(10.0f);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialouge_box_custom);
        dialog.setCancelable(false);
        dialog.show();
        Button saveBtn = dialog.findViewById(R.id.saveTodo);
        ImageButton closeBtn = dialog.findViewById(R.id.cancelPopup);
        EditText todoTitle = dialog.findViewById(R.id.enterTitle);
        EditText todoText = dialog.findViewById(R.id.enterTodo);
        saveBtn.setOnClickListener(v -> saveTodo(todoTitle, todoText, dialog));
        closeBtn.setOnClickListener(v -> dialog.dismiss());
    }

    private void showTodos() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM ToDoList", null);
        while (cursor.moveToNext()) {
            int todoId = cursor.getInt(0);
            String title = cursor.getString(1);
            String todo = cursor.getString(2);
            boolean completed = cursor.getInt(3) == 1;
            todos.add(new TodoData(todoId, title, todo, completed));
        }
        cursor.close();
        db.close();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void saveTodo(EditText todoTitle, EditText todoText, Dialog dialog) {
        String title = todoTitle.getText().toString().trim();
        String todoBody = todoText.getText().toString().trim();
        if (title.isEmpty()) {
            showToast("Please enter a todo");
        } else {
            todos.clear();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.execSQL("INSERT INTO ToDoList (title, todo, completed) VALUES (?, ?, ?)", new Object[]{title, todoBody, false});
            db.close();
            showToast("Todo Added");
            dialog.dismiss();
            showTodos();
            todoAdapter.notifyDataSetChanged();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
        private final Context context;
        private final ArrayList<TodoData> todos;

        public TodoAdapter(Context context, ArrayList<TodoData> todos) {
            this.context = context;
            this.todos = todos;
        }

        @NonNull
        @Override
        public TodoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.each_todo, parent, false);
            assert view != null;
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TodoAdapter.ViewHolder holder, int position) {
            boolean completed = todos.get(position).isCompleted();
            TodoData todo = todos.get(position);
            String todoTitle = todo.getTitle();
            String todoText = todo.getTodo();
            if (completed) {
                holder.todoTitle.setPaintFlags(holder.todoTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.todoTitle.setPaintFlags(holder.todoTitle.getPaintFlags() & ~(Paint.STRIKE_THRU_TEXT_FLAG));
            }
            holder.todoText.setText(todoText);
            holder.todoTitle.setText(todoTitle);
            int todoId = todo.getId();
            holder.deleteBtn.setOnClickListener(v -> showDeletePopup(todoId, position));
            holder.checkBox.setChecked(completed);
            holder.checkBox.setOnCheckedChangeListener((v, isChecked) -> markAsCompleted(todoId, holder.todoTitle, isChecked));
            holder.todoBody.setOnLongClickListener(v -> {
                showEditPopup(todoId, todoTitle, todoText);
                return true;
            });
        }

        private void showEditPopup(int todoId, String todoTitle, String todoText) {
            editTodoDialog(todoId, todoTitle, todoText);
        }

        private void editTodoDialog(int todoId, String todoTitle, String todoText) {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.popup_add_todo);
            Objects.requireNonNull(dialog.getWindow()).setElevation(10.0f);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialouge_box_custom);
            dialog.setCancelable(false);
            dialog.show();
            Button saveBtn = dialog.findViewById(R.id.saveTodo);
            ImageButton closeBtn = dialog.findViewById(R.id.cancelPopup);
            EditText title = dialog.findViewById(R.id.enterTitle);
            EditText todo = dialog.findViewById(R.id.enterTodo);
            title.setText(todoTitle);
            todo.setText(todoText);
            saveBtn.setOnClickListener(v -> updateTodo(todoId, title, todo, dialog));
            closeBtn.setOnClickListener(v -> dialog.dismiss());
        }

        @SuppressLint("NotifyDataSetChanged")
        private void updateTodo(int todoId, EditText todoTitle, EditText todoText, Dialog dialog) {
            String title = todoTitle.getText().toString().trim();
            String todoBody = todoText.getText().toString().trim();
            if (title.isEmpty()) {
                showToast("Todo cannot be empty");
            } else {
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                db.execSQL("UPDATE ToDoList SET title = ?, todo = ? WHERE id = ?", new Object[]{title, todoBody, todoId});
                db.close();
                showToast("Todo Updated");
                dialog.dismiss();
                todos.clear();
                showTodos();
                notifyDataSetChanged();
            }
        }


        private void showDeletePopup(int todoId, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Todo");
            builder.setMessage("Are you sure you want to delete this todo?");
            builder.setPositiveButton("Yes", (dialog, which) -> deleteTodo(todoId, position));
            builder.setNegativeButton("No", null);
            builder.setCancelable(false);
            builder.show();
        }

        private void markAsCompleted(int todoId, TextView todoTitle, boolean isChecked) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.execSQL("UPDATE ToDoList SET completed = ? WHERE id = ?", new Object[]{isChecked, todoId});
            db.close();
            for (TodoData todo : todos) {
                if (todo.getId() == todoId) {
                    if (isChecked) {
                        todoTitle.setPaintFlags(todoTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        todoTitle.setPaintFlags(todoTitle.getPaintFlags() & ~(Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void deleteTodo(int todoId, int position) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.execSQL("DELETE FROM ToDoList WHERE id =?", new Object[]{todoId});
            db.close();
            Iterator<TodoData> iterator = todos.iterator();
            while (iterator.hasNext()) {
                TodoData todo = iterator.next();
                if (todo.getId() == todoId) {
                    iterator.remove();
                    break;
                }
            }
            notifyItemRemoved(position);
            showToast("Todo Deleted");
        }

        @Override
        public int getItemCount() {
            return todos.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView todoTitle, todoText;
            ImageButton deleteBtn;
            CheckBox checkBox;
            LinearLayout todoBody;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                todoTitle = itemView.findViewById(R.id.todoTitle);
                todoText = itemView.findViewById(R.id.todoText);
                deleteBtn = itemView.findViewById(R.id.deleteButton);
                checkBox = itemView.findViewById(R.id.checkCompleted);
                todoBody = itemView.findViewById(R.id.todoBody);
            }
        }
    }
}