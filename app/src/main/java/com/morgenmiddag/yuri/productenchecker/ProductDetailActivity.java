package com.morgenmiddag.yuri.productenchecker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ProductDetailActivity extends AppCompatActivity {

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

            ImageLoader.getInstance().displayImage(productData.getString("productImage"), productImageView);
            productNameTextView.setText(productData.getString("productName"));
            productDescriptionTextView.setText(productData.getString("productDescription"));
            productPriceTextView.setText("€" + productData.getString("productPrice"));
        }
    }
}
