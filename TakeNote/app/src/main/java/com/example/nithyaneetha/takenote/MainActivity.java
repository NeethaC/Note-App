 package com.example.nithyaneetha.takenote;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

 public class MainActivity extends AppCompatActivity {
    private NoteViewModel noteViewModel;
    public static final int ADD_NOTE = 1;
     public static final int EDIT_NOTE = 2;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

         FloatingActionButton buttonAddNote = findViewById(R.id.buttonadd);
         buttonAddNote.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(MainActivity.this, AddEditNote.class);
                 startActivityForResult(intent, ADD_NOTE);
             }
         });


         noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>(){

            @Override
            public void onChanged(@Nullable List<Note> notes) {
                    adapter.setNotes(notes);
                }
             });

         new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                 ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
             @Override
             public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                 return false;
             }

             @Override
             public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                 noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                 Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
             }
         }).attachToRecyclerView(recyclerView);

         adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
             @Override
             public void onItemClick(Note note) {
                 Intent intent = new Intent(MainActivity.this, AddEditNote.class);
                 intent.putExtra(AddEditNote.EXTRA_ID, note.getId());
                 intent.putExtra(AddEditNote.EXTRA_TITLE, note.getTitle());
                 intent.putExtra(AddEditNote.EXTRA_DESCRIPTION, note.getDescription());
                 startActivityForResult(intent, EDIT_NOTE);
             }
         });

        }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == ADD_NOTE && resultCode == RESULT_OK) {
             String title = data.getStringExtra(AddEditNote.EXTRA_TITLE);
             String description = data.getStringExtra(AddEditNote.EXTRA_DESCRIPTION);

             Note note = new Note(title, description);
             noteViewModel.insert(note);

             Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show();
         }
         else if (requestCode == EDIT_NOTE && resultCode == RESULT_OK) {
             int id = data.getIntExtra(AddEditNote.EXTRA_ID, -1);

             if (id == -1) {
                 Toast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT).show();
                 return;
             }

             String title = data.getStringExtra(AddEditNote.EXTRA_TITLE);
             String description = data.getStringExtra(AddEditNote.EXTRA_DESCRIPTION);
             Note note = new Note(title, description);
             note.setId(id);
             noteViewModel.update(note);

             Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
         }
         else {
             Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
         }
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         MenuInflater menuInflater = getMenuInflater();
         menuInflater.inflate(R.menu.mainmenu, menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             case R.id.delete:
                 noteViewModel.deleteAllNotes();
                 Toast.makeText(this, "All Notes Deleted..", Toast.LENGTH_SHORT).show();
                 return true;
             default:
                 return super.onOptionsItemSelected(item);
         }
     }

 }

