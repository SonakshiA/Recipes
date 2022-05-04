package com.example.recipes;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import java.text.DecimalFormat;

public class Recipe10 extends AppCompatActivity {

    ImageView imageView;
    private static final String SPOONACULAR_REQUEST_URL = "https://api.spoonacular.com/recipes/complexSearch?apiKey=99d792e3beae47d68292366a2534f363&minFat=25&query=pasta";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.last_recipe);

        RecipeAsyncTask task = new RecipeAsyncTask();
        task.execute();

        imageView = (ImageView) findViewById(R.id.image);

        Button previousButton = (Button) findViewById(R.id.previous_button);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Recipe10.this, Recipe9.class);
                startActivity(intent);
            }
        });
    }

    private void updateUI(RecipeClass recipe){
        TextView recipeTitle = (TextView) findViewById(R.id.recipe_name);
        recipeTitle.setText(recipe.getRecipeName());

        TextView fatAmount = (TextView) findViewById(R.id.fat_amount);
        fatAmount.setText(formatFatAmount(recipe.getFatAmount()));

    }

    private String formatFatAmount(double fatAmount){
        DecimalFormat formattedFatAmount = new DecimalFormat("0.0 grams");
        return formattedFatAmount.format(fatAmount);
    }
    private class RecipeAsyncTask extends AsyncTask<URL, Void, RecipeClass>{
        @Override
        protected RecipeClass doInBackground(URL... urls) {
            URL url = createURL(SPOONACULAR_REQUEST_URL);

            String jsonResponse = "";
            try{
                jsonResponse = makeHttpRequest(url);
            }catch(IOException e){
                Log.e("Error fetching JSON",e.getMessage());
            }

            RecipeClass recipe = extractFromJson(jsonResponse);

            return recipe;
        }

        @Override
        protected void onPostExecute(RecipeClass recipe) {
            if (recipe == null){
                return;
            }
            updateUI(recipe);
        }

        private URL createURL(String stringURL){
            URL url = null;
            try{
                url = new URL(stringURL);
            } catch (MalformedURLException e){
                Log.e("Malformed URL",e.getMessage());
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try{
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                if (urlConnection.getResponseCode()==200){
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }
            } catch(IOException e){
                Log.e("JSON not fetched", e.getMessage());
            } finally{
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (inputStream != null){
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException{
            StringBuilder output = new StringBuilder();
            if (inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null){
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private RecipeClass extractFromJson(String recipeJSON){
            try{
                JSONObject baseJsonObject = new JSONObject(recipeJSON);
                JSONArray jsonArray = baseJsonObject.optJSONArray("results");

                JSONObject jsonObject = jsonArray.optJSONObject(9);
                String recipeName = jsonObject.optString("title");
                String imageURL = jsonObject.optString("image");
                JSONObject nutritionObject = jsonObject.optJSONObject("nutrition");
                JSONArray nutrientsArray = nutritionObject.optJSONArray("nutrients");
                JSONObject nutrientsObject = nutrientsArray.optJSONObject(0);
                double fatAmount = nutrientsObject.optDouble("amount");

                LoadImage loadImage = new LoadImage(imageView);
                loadImage.execute(imageURL);
                return new RecipeClass(recipeName,fatAmount);

            } catch (JSONException e){
                Log.e("Unable to extract JSON",e.getMessage());
            }
            return null;
        }

    }
    private class LoadImage extends AsyncTask<String,Void,Bitmap>{
        ImageView imageView;

        public LoadImage(ImageView ivResult){
            this.imageView = ivResult;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            String urlLink = strings[0];
            Bitmap bitmap = null;
            try{
                InputStream inputStream = new java.net.URL(urlLink).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch(IOException e){
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
