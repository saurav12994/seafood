package com.example.sourabh.seafood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mProgressMore, mProgressSearch;
    private FatSecretSearch mFatSecretSearch;
    private FatSecretGet mFatSecretGet;
    TextView fats, carbs, cals, protien,foodname,serve;
    String brand;
    ProgressDialog pd;


    private ArrayList<Item> mItem;
    String tag;
    private static String TAG="MainActivity";
    private float[] yData = {25.3f, 10.6f, 66.76f, 44.32f, 46.01f, 16.89f, 23.9f};
    private String[] xData = {"Calories", "Carbohydrates" , "Protein" , "Fats"};
    PieChart pieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFatSecretSearch = new FatSecretSearch(); // method.search
        mFatSecretGet = new FatSecretGet();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tag = getIntent().getStringExtra("food_name");
        //button = (Button) findViewById(R.id.button);

        mItem = new ArrayList<>();
        pieChart = (PieChart) findViewById(R.id.idPieChart);
Typeface typeface=Typeface.createFromAsset(getApplicationContext().getAssets(),"sansandroid.otf");
//cals=(TextView)findViewById(R.id.cal);
//cals.setTypeface(typeface);
  //      carbs=(TextView)findViewById(R.id.car);
    ///    carbs.setTypeface(typeface);
       /// protien=(TextView)findViewById(R.id.pr);
        //protien.setTypeface(typeface);
       // fats=(TextView)findViewById(R.id.fat);
       // fats.setTypeface(typeface);
        pieChart.setRotationEnabled(true);
        foodname=(TextView)findViewById(R.id.foodname);
        serve=(TextView)findViewById(R.id.serving);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.BLUE);
        //pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Super Cool Chart");
        pieChart.setCenterTextSize(10);
        foodname.setText(tag);

        searchFood(tag,1);

      /*  fats = (TextView)findViewById(R.id.fat);
        cals = (TextView)findViewById(R.id.calorie);
        protien = (TextView)findViewById(R.id.protien);
        carbs = (TextView)findViewById(R.id.carbs);*/

    }


    private void searchFood(final String item, final int page_num) {
        new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {
                Log.d("Works","Starting to work");
            }

            @Override
            protected String doInBackground(String... arg0) {
                JSONObject food = mFatSecretSearch.searchFood(item, page_num);
                JSONArray FOODS_ARRAY;
                try {
                    if (food != null) {
                        FOODS_ARRAY = food.getJSONArray("food");
                        if (FOODS_ARRAY != null) {
                            for (int i = 0; i < 1; i++) {
                                JSONObject food_items = FOODS_ARRAY.optJSONObject(i);
                                String food_name = food_items.getString("food_name");
                                String food_description = food_items.getString("food_description");
                                String[] row = food_description.split("-");
                                String id = food_items.getString("food_type");
                                if (id.equals("Brand")) {
                                    brand = food_items.getString("brand_name");
                                }
                                if (id.equals("Generic")) {
                                    brand = "Generic";
                                }
                                String food_id = food_items.getString("food_id");
                                mItem.add(new Item(food_name, row[1].substring(1),
                                        "" + brand, food_id));
                            }
                        }
                    }
                } catch (JSONException exception) {
                    return "Error";
                }
                return "";
            }

            @Override
            protected void onPostExecute(String result) {
                xyz();

                super.onPostExecute(result);
                if (result.equals("Error"))
                    Toast.makeText(getApplicationContext(), "No Items Containing Your Search", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    public void xyz(){

        Log.d("okok"," " +mItem);
        getFood(Long.parseLong(mItem.get(0).getID()));
    }



    private void getFood(final long id) {
        new AsyncTask<String, JSONObject, JSONObject>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("loading");
                pd.show();
            }

            @Override
            protected JSONObject doInBackground(String... arg0) {

                JSONObject foodGet = mFatSecretGet.getFood(id);
                try {
                    if (foodGet != null) {
                        String food_name = foodGet.getString("food_name");
                        JSONObject servings = foodGet.getJSONObject("servings");

                        JSONObject serving = servings.getJSONObject("serving");
                        String calories = serving.getString("calories");
                        String carbohydrate = serving.getString("carbohydrate");
                        String protein = serving.getString("protein");
                        String fat = serving.getString("fat");
                        String serving_description = serving.getString("serving_description");


                        Log.e("serving_description", serving_description);
                        /**
                         * Displays results in the LogCat
                         */
                        Log.e("food_name", food_name);
                        Log.e("calories", calories);
                        Log.e("carbohydrate", carbohydrate);
                        Log.e("protein", protein);
                        Log.e("fat", fat);



                        //  Display_Details(foodGet);
                        //cals.setText("Carbohydrates : "+carbohydrate);
                    }

                } catch (JSONException exception) {
                    return foodGet;
                }
                return foodGet;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                super.onPostExecute(result);
                if (pd != null)
                {
                    pd.dismiss();
                }
                try {
                    Display_Details(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (result.equals("Error"))
                    Toast.makeText(getApplicationContext(), "No Items Containing Your Search", Toast.LENGTH_SHORT).show();

            }
        }.execute();
    }


    public void Display_Details(final JSONObject data) throws JSONException {



        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, "addDataSet started");
                ArrayList<PieEntry> yEntrys = new ArrayList<>();
                ArrayList<String> xEntrys = new ArrayList<>();
                for(int i = 1; i < xData.length; i++){
                    xEntrys.add(xData[i]);
                }
                String food_name = null;
                try {
                    food_name = data.getString("food_name");
                    JSONObject servings = data.getJSONObject("servings");
                    JSONObject serving = servings.getJSONObject("serving");
                    String calories = serving.getString("calories");
                    String carbohydrate = serving.getString("carbohydrate");
                    String protein = serving.getString("protein");
                    String fat = serving.getString("fat");
                    String serving_description = serving.getString("serving_description");
                    Log.e("serving_description", serving_description);
                    /**
                     * Displays results in the LogCat
                     */
                serve.setText(serving_description);
                    Log.e("food_name", food_name);
                    Log.e("calories", calories);
                    Log.e("carbohydrate", carbohydrate);
                    Log.e("protein", protein);
                    Log.e("fat", fat);
                    yEntrys.add(new PieEntry(Float.parseFloat(calories),0));
                    yEntrys.add(new PieEntry(Float.parseFloat(carbohydrate),1));
                    yEntrys.add(new PieEntry(Float.parseFloat(protein),2));
                    yEntrys.add(new PieEntry(Float.parseFloat(fat),3));
                   /* fats.setText("Fats : "+fat);
                    cals.setText("Calories : "+calories);
                    protien.setText("Protien : "+protein);
                    carbs.setText("Carbohydrates : "+carbohydrate);*/
                    PieDataSet pieDataSet = new PieDataSet(yEntrys, " ");
                    pieDataSet.setSliceSpace(2);
                    pieDataSet.setValueTextSize(12);

                    //add colors to dataset
                    ArrayList<Integer> colors = new ArrayList<>();
                    colors.add(Color.GRAY);
                    colors.add(Color.BLUE);
                    colors.add(Color.RED);
                    colors.add(Color.GREEN);


                    pieDataSet.setColors(colors);

                    //add legend to chart
                    Legend legend = pieChart.getLegend();
                    legend.setForm(Legend.LegendForm.CIRCLE);
                    legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

                    //create pie data object


                    PieData pieData = new PieData(pieDataSet);
                    pieChart.setData(pieData);
                    pieChart.invalidate();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"Conectivity error ",Toast.LENGTH_LONG).show();
                }



            }
        });


    }
}