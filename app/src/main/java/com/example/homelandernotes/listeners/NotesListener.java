package com.example.homelandernotes.listeners;

import com.example.homelandernotes.entities.note;

public interface NotesListener {
    void onNoteClicked(note nt, int position);
}
