package com.morgenmiddag.yuri.productenchecker;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    /**
     * Gets called when an instance is created. Used to setup a toolbar, requestmanager for http requests and an imageLoader for better image handling.
     * Also setup a click listener for listItems.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup a toolbar with buttons for selecting product type.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check for an active internet connection.
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
            // Send a request to the webservice in this method.
            new RequestManager().execute("https://docent.cmi.hro.nl/bootb/service/products");
        } else {
            Toast.makeText(this, "@string/error_no_internet_connection", Toast.LENGTH_SHORT).show();
        }

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
                // Intent to go to the details view
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                // Add the necessary data
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

    /**
     * Make sure the menu is filled.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.products_menu, menu);
        return true;
    }

    /**
     * Click handler for menu options.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get non food products
        if(item.getItemId() == R.id.action_non_food_products){
            new RequestManager().execute("https://docent.cmi.hro.nl/bootb/service/products/nonfood");
        }
        // Get food products
        if(item.getItemId() == R.id.action_food_products){
            new RequestManager().execute("https://docent.cmi.hro.nl/bootb/service/products/food");
        }
        // Get all products
        if(item.getItemId() == R.id.action_all_products){
            new RequestManager().execute("https://docent.cmi.hro.nl/bootb/service/products");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Asynchronously fetch the data from the webservice.
     */
    public class RequestManager extends AsyncTask<String, String, String> {

        /**
         * Method gets executed in the background, networking cannot be done on the main thread.
         * @param urls
         * @return
         */
        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            // try to setup url connection.
            try{
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Get the data.
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                // Add the data to a stringBuffer.
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Always close connection and reader.
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

        /**
         * Method gets executed after data is fetched.
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Parse the JSON result
            List<ProductModel> productList = parseJSON(result);

            // Fill the listView using a custom adapter
            ProductAdapter adapter = new ProductAdapter(getApplicationContext(), R.layout.row, productList);
            productsListView.setAdapter(adapter);
        }

        /**
         * Method for parsing JSON data.
         * @param result
         * @return
         */
        private List<ProductModel> parseJSON(String result) {
            // Use a list of models to represent the data.
            List<ProductModel> productList = new ArrayList<>();

            try {
                // Make JSON object and get the main array called products
                JSONObject jsonObject = new JSONObject(result);
                JSONArray products = jsonObject.getJSONArray("products");

                // Loop through all items in the products array
                for(int i = 0; i < products.length(); i++) {
                    // for each item create a model
                    ProductModel productModel = new ProductModel();
                    JSONObject product = products.getJSONObject(i);
                    // Set the data from the JSON object in the model
                    productModel.setName(product.getString("name"));
                    productModel.setDescription(product.getString("description"));
                    productModel.setPrice((float) product.getDouble("price"));
                    productModel.setImage(product.getString("image"));
                    // Parse the data for the shop object and create a Shop model for it.
                    ProductModel.Shop shopModel = new ProductModel.Shop();
                    JSONObject shop = product.getJSONObject("shop");
                    JSONObject location = shop.getJSONObject("location");
                    // Fill the Shop model and add it to the Product model.
                    shopModel.setName(shop.getString("name"));
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
