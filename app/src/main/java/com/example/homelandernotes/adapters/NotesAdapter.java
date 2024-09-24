package com.example.homelandernotes.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homelandernotes.R;
import com.example.homelandernotes.entities.note;
import com.example.homelandernotes.listeners.NotesListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>{
    private List<note> notes;
    private NotesListener notesListener;
    private Timer timer;
    private List<note> notesSource;

    public NotesAdapter(List<note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
        notesSource = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    notesListener.onNoteClicked(notes.get(currentPosition), currentPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutNote;
        RoundedImageView imageNote;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle=itemView.findViewById(R.id.textSubitle);
            textDateTime=itemView.findViewById(R.id.textDateTime);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
        }

        void setNote(note nt) {
            textTitle.setText(nt.getTitle());
            if (nt.getSubtitle().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            }else {
                textSubtitle.setText(nt.getSubtitle());
            }
            textDateTime.setText(nt.getDateTime());
            GradientDrawable gradientDrawable =(GradientDrawable) layoutNote.getBackground();
            if (nt.getColor() != null){
                gradientDrawable.setColor(Color.parseColor(nt.getColor()));
            }else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }
            if (nt.getImagePath() != null) {
                File imgFile = new File(nt.getImagePath());
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(nt.getImagePath());
                    if (bitmap != null) {
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                    } else {
                        Log.e("ImageLoad", "Failed to decode bitmap from file.");
                        imageNote.setVisibility(View.GONE);
                    }
                } else {
                    Log.e("ImageLoad", "Image file does not exist at path: " + nt.getImagePath());
                    imageNote.setVisibility(View.GONE);
                }
            } else {
                Log.e("ImageLoad", "Image path is null.");
                imageNote.setVisibility(View.GONE);
            }
        }
    }
    public void searchNotes(final String searchKeyword) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    notes = notesSource;
                } else {
                    ArrayList<note> temp = new ArrayList<>();
                    for (note nt : notesSource) {
                        if (nt.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || nt.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || nt.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(nt);
                        }
                    }
                    notes = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer() {
        if (timer != null){
            timer.cancel();
        }
    }

}
