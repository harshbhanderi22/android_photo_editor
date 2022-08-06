package com.harshbhanderi.photoeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class result extends AppCompatActivity {
Button save,backedit;
ImageView fimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        getSupportActionBar().hide();

        save=findViewById(R.id.save);
        fimage=findViewById(R.id.finalimage);
        backedit=findViewById(R.id.backedit);
        Intent intent=getIntent();
        fimage.setImageURI(getIntent().getData());
        String path=intent.getDataString();
        backedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(result.this, path, Toast.LENGTH_SHORT).show();

            }
        });
    }
}