package com.morgenmiddag.yuri.productenchecker;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ProductDetailActivity extends AppCompatActivity {

    private Double lat, lon;
    private String shopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Bundle productData = getIntent().getExtras();
        if(productData != null) {
            TextView productNameTextView = findViewById(R.id.productNameTextView);
            TextView productDescriptionTextView = findViewById(R.id.productDescriptionTextView);
            TextView productPriceTextView = findViewById(R.id.productPriceTextView);
            ImageView productImageView = findViewById(R.id.productImageView);

            lat = productData.getDouble("productLat");
            lon = productData.getDouble("productLon");
            shopName = productData.getString("shopName");

            ImageLoader.getInstance().displayImage(productData.getString("productImage"), productImageView);
            productNameTextView.setText(productData.getString("productName"));
            productDescriptionTextView.setText(productData.getString("productDescription"));
            productPriceTextView.setText("€" + productData.getString("productPrice"));
        }
    }

    public void MakeMapIntent(View view) {
        Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lon + "?q="+ lat + "," + lon +"("+ shopName +")");

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
