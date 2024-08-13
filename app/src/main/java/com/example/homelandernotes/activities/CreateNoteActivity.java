package com.example.homelandernotes.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homelandernotes.R;
import com.example.homelandernotes.database.NotesDatabase;
import com.example.homelandernotes.entities.note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {
    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;
    private View viewSubtitleIndicator;
    private String selectedNoteColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_note);
        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener((v) -> { onBackPressed(); });
        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle=findViewById(R.id.inputNoteSubtitle);
        inputNoteText=findViewById(R.id.inputNote);
        textDateTime=findViewById(R.id.textDateTime);
        viewSubtitleIndicator=findViewById(R.id.viewSubtitleIndicator);
        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );
        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });
        selectedNoteColor = "#333333";
        initMiscellaneous();
        setSubtitleIndicatorColor();

    }
    private void saveNote() {
        if (inputNoteText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note title cant be empty!!!!", Toast.LENGTH_SHORT).show();
            return;
        }else if (inputNoteSubtitle.getText().toString().trim().isEmpty() && inputNoteText.getText().toString().isEmpty()){
            Toast.makeText(this, "Note cant be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        final note nt = new note();
        nt.setTitle(inputNoteTitle.getText().toString());
        nt.setSubtitle(inputNoteSubtitle.getText().toString());
        nt.setNoteText(inputNoteText.getText().toString());
        nt.setDateTime(textDateTime.getText().toString());
        nt.setColor(selectedNoteColor);

        @SuppressLint("StaticFieldLeak")
        class savaNoteTask extends AsyncTask<Void, Void, Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().InsertNote(nt);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new savaNoteTask().execute();
    }
    private void initMiscellaneous(){
        final LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        final ImageView imageView1 =layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageView2 =layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageView3 =layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageView4 =layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageView5 =layoutMiscellaneous.findViewById(R.id.imageColor5);
        layoutMiscellaneous.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor ="#333333";
                imageView1.setImageResource(R.drawable.ic_done);
                imageView2.setImageResource(0);
                imageView3.setImageResource(0);
                imageView4.setImageResource(0);
                imageView5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor ="#FDBE3B";
                imageView1.setImageResource(0);
                imageView2.setImageResource(R.drawable.ic_done);
                imageView3.setImageResource(0);
                imageView4.setImageResource(0);
                imageView5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor ="#FF4842";
                imageView1.setImageResource(0);
                imageView2.setImageResource(0);
                imageView3.setImageResource(R.drawable.ic_done);
                imageView4.setImageResource(0);
                imageView5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor ="#3253Fc";
                imageView1.setImageResource(0);
                imageView2.setImageResource(0);
                imageView3.setImageResource(0);
                imageView4.setImageResource(R.drawable.ic_done);
                imageView5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor ="#000000";
                imageView1.setImageResource(0);
                imageView2.setImageResource(0);
                imageView3.setImageResource(0);
                imageView4.setImageResource(0);
                imageView5.setImageResource(R.drawable.ic_done);
                setSubtitleIndicatorColor();
            }
        });


    }

    private void setSubtitleIndicatorColor(){
        GradientDrawable gradientDrawable =(GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

}