package com.example.sourabh.seafood;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.ClarifaiRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class Detection_Activity extends AppCompatActivity {

    ClarifaiClient client;
    byte[] Data;
    Toolbar toolbar;
    String[] country = { "Food", "Logo", "Travel", "NSFW"  };

    ImageView imageView;
    ExpandableHeightGridView expandableHeightGridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_);

        imageView = (ImageView)findViewById(R.id.imageView);
        expandableHeightGridView = (ExpandableHeightGridView)findViewById(R.id.detection_list);
        expandableHeightGridView.setExpanded(true);
        expandableHeightGridView.setNumColumns(1);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Spinner spin = (Spinner) findViewById(R.id.spinner1);
        //spin.setOnItemSelectedListener();

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 67);


            }
        });
        client =         new ClarifaiBuilder("6z4RyMU9hVtJNGcrCI9cGLzujeRKLA4qqKRgk9-7", "yBKLs3O3BXemIm69ebZlWO0LDOmh5Xal7wYMlNk7").buildSync();

        expandableHeightGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("inside_list",""+expandableHeightGridView.getAdapter().getItem(position));

                startActivity(new Intent(Detection_Activity.this,MainActivity.class).putExtra("food_name",(String)expandableHeightGridView.getAdapter().getItem(position)));
            }
        });



    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 67 && resultCode == Activity.RESULT_OK) {
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Data = byteArray;
            imageView.setImageBitmap(bmp);
            Provide_Image(Data);

        }
    }


    public void Provide_Image(byte[] data)
    {
        Get_Image_Details(data);

    }

    public void Get_Image_Details( byte[] data)
    {


        final ProgressDialog progressDialog = new ProgressDialog(Detection_Activity.this, AlertDialog.THEME_TRADITIONAL);
        progressDialog.setIndeterminate(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressDialog.create();
            progressDialog.show();
        }

        client.getDefaultModels().foodModel() // You can also do Clarifai.getModelByID("id") to get custom models
                .predict()
                .withInputs(
                        ClarifaiInput.forImage(ClarifaiImage.of(data))
                )

                .executeAsync(new ClarifaiRequest.OnSuccess<List<ClarifaiOutput<Concept>>>() {
                    @Override
                    public void onClarifaiResponseSuccess(List<ClarifaiOutput<Concept>> clarifaiOutputs) {

                        progressDialog.cancel();
                        Populate_List(clarifaiOutputs);


                    }
                });
/*        for(int i=0;i<predictionResults.size();i++)
        {
            Log.d("inside_suggestions", ""+predictionResults.get(i).data().get(i).name());
        }
        */


    }


    public void Populate_List(List<ClarifaiOutput<Concept>> clarifaiOutputs)
    {
        final ArrayList<String> foods = new ArrayList<String>();

        for(int i=0;i<clarifaiOutputs.size();i++)
        {
            Log.d("inside_output",""+clarifaiOutputs+" ");
            for(int j=0;j<clarifaiOutputs.get(i).data().size();j++)
            {
                foods.add(""+clarifaiOutputs.get(i).data().get(j).name());

            }

        }


        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                ArrayAdapter arrayAdapter = new ArrayAdapter<String>(Detection_Activity.this, android.R.layout.simple_list_item_1, foods);
                expandableHeightGridView.setAdapter(arrayAdapter);
                Log.d("array_size",""+foods.size());            }
        });

    }
















}
