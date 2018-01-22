package com.morgenmiddag.yuri.productenchecker;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Class definition for a Product detail view.
 */
public class ProductDetailActivity extends AppCompatActivity {

    private Double lat, lon;
    private String shopName;

    /**
     * Gets called when an instance is created. Used to setup the textviews and their data from the intent.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Bundle productData = getIntent().getExtras();
        if(productData != null) {
            // setup textviews
            TextView productNameTextView = findViewById(R.id.productNameTextView);
            TextView productDescriptionTextView = findViewById(R.id.productDescriptionTextView);
            TextView productPriceTextView = findViewById(R.id.productPriceTextView);
            ImageView productImageView = findViewById(R.id.productImageView);

            // make these available in the MakeMapIntent method.
            lat = productData.getDouble("productLat");
            lon = productData.getDouble("productLon");
            shopName = productData.getString("shopName");

            // Use imageloader to set the image
            ImageLoader.getInstance().displayImage(productData.getString("productImage"), productImageView);
            productNameTextView.setText(productData.getString("productName"));
            productDescriptionTextView.setText(productData.getString("productDescription"));
            productPriceTextView.setText("€" + productData.getString("productPrice"));
        }
    }

    /**
     * Method used for creating a Google Maps intent when the button on screen is tapped
     * @param view
     */
    public void MakeMapIntent(View view) {
        // Create Uri with location and marker data.
        Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lon + "?q="+ lat + "," + lon +"("+ shopName +")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        // Make Google Maps usage explicit.
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
