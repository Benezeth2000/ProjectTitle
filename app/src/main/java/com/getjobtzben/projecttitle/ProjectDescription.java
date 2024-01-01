package com.getjobtzben.projecttitle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class ProjectDescription extends AppCompatActivity {

    TextView projectTitle, code, custCode, learnMore, desc;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_description);

        projectTitle = findViewById(R.id.project_title);
        code = findViewById(R.id.code);
        custCode = findViewById(R.id.cust_sourceCode);
        learnMore = findViewById(R.id.learnMore);
        desc = findViewById(R.id.Description);
        imageView = findViewById(R.id.project_images);

        Intent intent = getIntent();

        String projectTilte = intent.getStringExtra("projecTitle");
        String priceCode = intent.getStringExtra("priceCode");
        String priceCustCode = intent.getStringExtra("priceCustCode");
        String projDesc = intent.getStringExtra("projDesc");
        String image = intent.getStringExtra("image");

        projectTitle.setText(projectTilte);
        code.setText(priceCode);
        custCode.setText(priceCustCode);
        desc.setText(projDesc);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Cache the original image and resized versions

        Glide.with(ProjectDescription.this)
                .load(image)
                .apply(requestOptions)
                .centerCrop()
                .into(imageView);

    }
}