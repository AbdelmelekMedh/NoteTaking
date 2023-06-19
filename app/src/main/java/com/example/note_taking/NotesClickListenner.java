package com.example.note_taking;

import androidx.cardview.widget.CardView;

import com.example.note_taking.models.Notes;

public interface NotesClickListenner {

    void onClick(Notes notes);
    void onLongClick(Notes notes, CardView cardView);
}
