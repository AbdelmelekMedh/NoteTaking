package com.example.note_taking;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.note_taking.adapters.NotesListAdapters;
import com.example.note_taking.dataBase.RoomDB;
import com.example.note_taking.models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    RecyclerView recyclerView;
    NotesListAdapters notesListAdapters;
    List<Notes> notes = new ArrayList<>();
    RoomDB database;
    FloatingActionButton fab_add;
    SearchView searchview_home;
    Notes selectedNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycoler_home);
        fab_add = findViewById(R.id.fab_add);
        searchview_home = findViewById(R.id.searchview_home);

        database = RoomDB.getInstance(this);
        notes = database.mainDAO().getAll();

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNotes.class);
                startActivityForResult(intent,101);
            }
        });

        updateRecycler(notes);

        searchview_home.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String newText) {
        List<Notes> filterList = new ArrayList<>();
        for (Notes singleNote : notes){
            if (singleNote.getTitle().toLowerCase().contains(newText.toLowerCase()) ||
                    singleNote.getNote().toLowerCase().contains(newText.toLowerCase())){
                filterList.add(singleNote);
            }
        }
        notesListAdapters.filterList(filterList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            if (resultCode == Activity.RESULT_OK){
                Notes new_note = (Notes) data.getSerializableExtra("note");
                database.mainDAO().insert(new_note);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapters.notifyDataSetChanged();
            }
        }
        else if(requestCode == 102){
            if (resultCode == Activity.RESULT_OK){
                Notes new_note = (Notes) data.getSerializableExtra("note");
                database.mainDAO().update(new_note.getId(),new_note.getTitle(),new_note.getNote());
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapters.notifyDataSetChanged();
            }
        }
    }

    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        notesListAdapters = new NotesListAdapters(MainActivity.this, notes, notesClickListenner);
        recyclerView.setAdapter(notesListAdapters);
    }

    private final NotesClickListenner notesClickListenner = new NotesClickListenner() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this, AddNotes.class);
            intent.putExtra("old_note", notes);
            startActivityForResult(intent, 102);
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {

            selectedNote = notes;
            showPopup(cardView);
        }
    };

    private void showPopup(CardView cardView) {

        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                database.mainDAO().delete(selectedNote);
                notes.remove(selectedNote);
                notesListAdapters.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,"Note Deleted",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }
}