package com.example.homelandernotes.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.homelandernotes.R;
import com.example.homelandernotes.database.NotesDatabase;
import com.example.homelandernotes.entities.note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {
    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;
    private View viewSubtitleIndicator;
    private ImageView imageNote;
    private TextView textWebURL;
    private LinearLayout layoutWebURL;

    private String selectedNoteColor;
    private String selectedImagePath;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;


    private AlertDialog dialogAddURL;
    private note alreadyAvailableNote;

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
        imageNote=findViewById(R.id.imageNote);
        textWebURL=findViewById(R.id.textWebURL);
        layoutWebURL=findViewById(R.id.layoutWebURL);
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
        selectedImagePath = "";
        if (getIntent().getBooleanExtra("isViewOrUpdate", false)){
            alreadyAvailableNote = (note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }
        findViewById(R.id.imageRemoveWebURL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textWebURL.setText(null);
                textWebURL.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.imageRemoveImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(View.GONE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
                selectedImagePath = "";
            }
        });
        initMiscellaneous();
        setSubtitleIndicatorColor();
    }

    private void setViewOrUpdateNote() {
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        inputNoteText.setText((alreadyAvailableNote.getNoteText()));
        textDateTime.setText(alreadyAvailableNote.getDateTime());

        if (alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty()) {
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            selectedImagePath =alreadyAvailableNote.getImagePath();
        }

        if (alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().trim().isEmpty()){
            textWebURL.setText(alreadyAvailableNote.getWebLink());
            layoutWebURL.setVisibility(View.VISIBLE);
        }
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
        nt.setImagePath(selectedImagePath);

        if (layoutWebURL.getVisibility() == View.VISIBLE){
            nt.setWebLink(textWebURL.getText().toString());
        }

        if (alreadyAvailableNote != null){
            nt.setId(alreadyAvailableNote.getId());
        }

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
    private void initMiscellaneous() {
        final LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        if (layoutMiscellaneous == null) {
            Toast.makeText(this, "LayoutMiscellaneous không tìm thấy!", Toast.LENGTH_SHORT).show();
            return; // Kết thúc hàm nếu layout không tồn tại
        }
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);

        View textMiscellaneous = layoutMiscellaneous.findViewById(R.id.textMiscellaneous);
        if (textMiscellaneous != null) {
            textMiscellaneous.setOnClickListener(v -> {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });
        } else {
            Log.e("CreateNoteActivity", "View with ID textMiscellaneous is null");
        }

        final ImageView imageView1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageView2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageView3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageView4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageView5 = layoutMiscellaneous.findViewById(R.id.imageColor5);
        final ImageView imageView6 = layoutMiscellaneous.findViewById(R.id.imageColor6);

        if (layoutMiscellaneous.findViewById(R.id.viewColor1) != null) {
            layoutMiscellaneous.findViewById(R.id.viewColor1).setOnClickListener(v -> {
                selectedNoteColor = "#333333";
                if (imageView1 != null) imageView1.setImageResource(R.drawable.ic_done);
                if (imageView2 != null) imageView2.setImageResource(0);
                if (imageView3 != null) imageView3.setImageResource(0);
                if (imageView4 != null) imageView4.setImageResource(0);
                if (imageView5 != null) imageView5.setImageResource(0);
                if (imageView6 != null) imageView6.setImageResource(0);
                setSubtitleIndicatorColor();
            });
        }

        if (layoutMiscellaneous.findViewById(R.id.viewColor2) != null) {
            layoutMiscellaneous.findViewById(R.id.viewColor2).setOnClickListener(v -> {
                selectedNoteColor = "#FDBE3B";
                if (imageView1 != null) imageView1.setImageResource(0);
                if (imageView2 != null) imageView2.setImageResource(R.drawable.ic_done);
                if (imageView3 != null) imageView3.setImageResource(0);
                if (imageView4 != null) imageView4.setImageResource(0);
                if (imageView5 != null) imageView5.setImageResource(0);
                if (imageView6 != null) imageView6.setImageResource(0);
                setSubtitleIndicatorColor();
            });
        }

        if (layoutMiscellaneous.findViewById(R.id.viewColor3) != null) {
            layoutMiscellaneous.findViewById(R.id.viewColor3).setOnClickListener(v -> {
                selectedNoteColor = "#FF4842";
                if (imageView1 != null) imageView1.setImageResource(0);
                if (imageView2 != null) imageView2.setImageResource(0);
                if (imageView3 != null) imageView3.setImageResource(R.drawable.ic_done);
                if (imageView4 != null) imageView4.setImageResource(0);
                if (imageView5 != null) imageView5.setImageResource(0);
                if (imageView6 != null) imageView6.setImageResource(0);
                setSubtitleIndicatorColor();
            });
        }

        if (layoutMiscellaneous.findViewById(R.id.viewColor4) != null) {
            layoutMiscellaneous.findViewById(R.id.viewColor4).setOnClickListener(v -> {
                selectedNoteColor = "#3253Fc";
                if (imageView1 != null) imageView1.setImageResource(0);
                if (imageView2 != null) imageView2.setImageResource(0);
                if (imageView3 != null) imageView3.setImageResource(0);
                if (imageView4 != null) imageView4.setImageResource(R.drawable.ic_done);
                if (imageView5 != null) imageView5.setImageResource(0);
                if (imageView6 != null) imageView6.setImageResource(0);
                setSubtitleIndicatorColor();
            });
        }

        if (layoutMiscellaneous.findViewById(R.id.viewColor5) != null) {
            layoutMiscellaneous.findViewById(R.id.viewColor5).setOnClickListener(v -> {
                selectedNoteColor = "#000000";
                if (imageView1 != null) imageView1.setImageResource(0);
                if (imageView2 != null) imageView2.setImageResource(0);
                if (imageView3 != null) imageView3.setImageResource(0);
                if (imageView4 != null) imageView4.setImageResource(0);
                if (imageView5 != null) imageView5.setImageResource(R.drawable.ic_done);
                if (imageView6 != null) imageView6.setImageResource(0);
                setSubtitleIndicatorColor();
            });
        }

        if (layoutMiscellaneous.findViewById(R.id.viewColor6) != null) {
            layoutMiscellaneous.findViewById(R.id.viewColor6).setOnClickListener(v -> {
                selectedNoteColor = "#4CAF50";
                if (imageView1 != null) imageView1.setImageResource(0);
                if (imageView2 != null) imageView2.setImageResource(0);
                if (imageView3 != null) imageView3.setImageResource(0);
                if (imageView4 != null) imageView4.setImageResource(0);
                if (imageView5 != null) imageView5.setImageResource(0);
                if (imageView6 != null) imageView6.setImageResource(R.drawable.ic_done);
                setSubtitleIndicatorColor();
            });
        }

        if (alreadyAvailableNote != null && alreadyAvailableNote.getColor() != null && !alreadyAvailableNote.getColor().trim().isEmpty()){
            switch (alreadyAvailableNote.getColor()){
                case "#FDBE3B" :
                    layoutMiscellaneous.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#FF4842" :
                    layoutMiscellaneous.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#3A53Fc" :
                    layoutMiscellaneous.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#000000" :
                    layoutMiscellaneous.findViewById(R.id.viewColor5).performClick();
                    break;
                case "#4CAF50" :
                    layoutMiscellaneous.findViewById(R.id.viewColor6).performClick();
                    break;
            }
        }
        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CreateNoteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                selectImage();
            }
        });

        layoutMiscellaneous.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddURLDialog();
            }
        });

    }


    private void setSubtitleIndicatorColor(){
        GradientDrawable gradientDrawable =(GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void selectImage(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (i.resolveActivity(getPackageManager())!=null){
            startActivityForResult(i, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else {
                Toast.makeText(this, "Cấp quyền không thành công!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==REQUEST_CODE_SELECT_IMAGE && resultCode ==RESULT_OK){
            if (data != null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                        selectedImagePath = getPathFromUri(selectedImageUri);
                    } catch (Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    private String getPathFromUri(Uri contentUri) {
        String filePath = null;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(contentUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex("_data");
                if (index != -1) {
                    filePath = cursor.getString(index);
                }
            } else {
                filePath = contentUri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return filePath;
    }

    private void showAddURLDialog(){
        if(dialogAddURL == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,
                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);
            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null){
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputURL =view.findViewById(R.id.inputURL);
            inputURL.requestFocus();
            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (inputURL.getText().toString().trim().isEmpty()){
                        Toast.makeText(CreateNoteActivity.this, "Enter URL bro!", Toast.LENGTH_SHORT).show();
                    }else if(!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()){
                        Toast.makeText(CreateNoteActivity.this, "Enter Valid URL", Toast.LENGTH_SHORT).show();
                    }else {
                        textWebURL.setText(inputURL.getText().toString());
                        layoutWebURL.setVisibility(View.VISIBLE);
                        dialogAddURL.dismiss();
                    }
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogAddURL.dismiss();
                }
            });
        }
        dialogAddURL.show();
    }
}
