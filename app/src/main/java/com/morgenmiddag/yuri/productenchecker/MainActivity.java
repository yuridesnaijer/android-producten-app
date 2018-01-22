package com.morgenmiddag.yuri.productenchecker;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.morgenmiddag.yuri.productenchecker.models.ProductModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static List<ProductModel> shopData;
    private ListView productsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup a toolbar with buttons for selecting product type
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Send a request to the webservice in this method.
        new RequestManager().execute("https://docent.cmi.hro.nl/bootb/service/products");

        // Setup the image loader for better image loading and caching to save data.
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        // Setup the list view for later use.
        productsListView = findViewById(R.id.productsListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.products_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_non_food){
            Toast.makeText(this, "non_food", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId() == R.id.action_setting){
            Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Asynchronously fetch the data from the webservice.
     */
    public class RequestManager extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try{
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null){
                    connection.disconnect();
                }

                try{
                    if(reader != null){
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            List<ProductModel> productList = parseJSON(result);
            ProductAdapter adapter = new ProductAdapter(getApplicationContext(), R.layout.row, productList);
            productsListView.setAdapter(adapter);
        }

        private List<ProductModel> parseJSON(String result) {

            List<ProductModel> productList = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray products = jsonObject.getJSONArray("products");

                for(int i = 0; i < products.length(); i++) {
                    ProductModel productModel = new ProductModel();
                    JSONObject product = products.getJSONObject(i);

                    productModel.setName(product.getString("name"));
                    productModel.setDescription(product.getString("description"));
                    productModel.setPrice((float) product.getDouble("price"));
                    productModel.setImage(product.getString("image"));

                    // Parse the data for the shop.
                    ProductModel.Shop shopModel = new ProductModel.Shop();
                    JSONObject shop = product.getJSONObject("shop");
                    shopModel.setName(shop.getString("name"));
                    // shopModel.setLocation(); TODO: get location data.

                    productModel.setShop(shopModel);
                    // Add the object to the list
                    productList.add(productModel);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return productList;
        }
    }
}
