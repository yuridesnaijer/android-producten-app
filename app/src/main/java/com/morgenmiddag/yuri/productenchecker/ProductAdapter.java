package com.morgenmiddag.yuri.productenchecker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.morgenmiddag.yuri.productenchecker.models.ProductModel;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ProductAdapter extends ArrayAdapter {

    private List<ProductModel> productModelList;
    private int _resource;
    private LayoutInflater inflater;

    public ProductAdapter(@NonNull Context context, int resource, @NonNull List<ProductModel> objects) {
        super(context, resource, objects);
        productModelList = objects;
        _resource = resource;
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Only inflate if convertView is null.
        if (convertView == null)
        {
            convertView = inflater.inflate(_resource, null);
        }

        // Get the views
        ImageView productImage = convertView.findViewById(R.id.productImage);
        TextView productName = convertView.findViewById(R.id.productName);
        TextView productDescription = convertView.findViewById(R.id.productDescription);
        TextView productPrice = convertView.findViewById(R.id.productPrice);
        TextView productShopName = convertView.findViewById(R.id.productShopName);

        // Use the imageloader to set the image and set the data.
        ImageLoader.getInstance().displayImage(productModelList.get(position).getImage(), productImage);
        productName.setText(productModelList.get(position).getName());
        productDescription.setText(productModelList.get(position).getDescription());
        productPrice.setText(productModelList.get(position).getPrice().toString());
        productShopName.setText(productModelList.get(position).getShop().getName());
        return convertView;
    }
}