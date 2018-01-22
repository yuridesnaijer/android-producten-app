package com.morgenmiddag.yuri.productenchecker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

        // Go to details view on item click
        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ProductModel listItem = (ProductModel) productsListView.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra("productName", listItem.getName());
                intent.putExtra("productDescription", listItem.getDescription());
                intent.putExtra("productImage", listItem.getImage());
                intent.putExtra("productPrice", listItem.getPrice().toString());
                intent.putExtra("shopName", listItem.getShop().getName());
                intent.putExtra("productLat", listItem.getShop().getLatitude());
                intent.putExtra("productLon", listItem.getShop().getLongitude());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.products_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_non_food_products){
            Toast.makeText(this, "non_food", Toast.LENGTH_SHORT).show();
            new RequestManager().execute("https://docent.cmi.hro.nl/bootb/service/products/nonfood");
        }
        if(item.getItemId() == R.id.action_food_products){
            Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
            new RequestManager().execute("https://docent.cmi.hro.nl/bootb/service/products/food");
        }
        if(item.getItemId() == R.id.action_all_products){
            Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
            new RequestManager().execute("https://docent.cmi.hro.nl/bootb/service/products");
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

                    JSONObject location = shop.getJSONObject("location");

                    shopModel.setLatitude(location.getDouble("latitude"));
                    shopModel.setLongitude(location.getDouble("longitude"));

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
