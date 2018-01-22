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

/**
 * Class for adding the ProductModels to the ListView
 */
public class ProductAdapter extends ArrayAdapter {

    private List<ProductModel> productModelList;
    private int resource;
    private LayoutInflater inflater;

    /**
     * Constructor for the Adapter class
     * @param context
     * @param _resource
     * @param objects
     */
    public ProductAdapter(@NonNull Context context, int _resource, @NonNull List<ProductModel> objects) {
        super(context, _resource, objects);
        // Set the Fields for later use.
        productModelList = objects;
        resource = _resource;
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Method to get the view. Used for getting the textviews and setting their data.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Use the viewHolder pattern to save memory when finding views.
        ViewHolder viewHolder;

        // Only inflate and get views if convertView is null.
        if (convertView == null)
        {
            convertView = inflater.inflate(resource, null);
            viewHolder = new ViewHolder();
            // Get the views
            viewHolder.productImage = convertView.findViewById(R.id.productImage);
            viewHolder.productName = convertView.findViewById(R.id.productName);
            viewHolder.productDescription = convertView.findViewById(R.id.productDescription);
            viewHolder.productPrice = convertView.findViewById(R.id.productPrice);
            viewHolder.productShopName = convertView.findViewById(R.id.productShopName);
            convertView.setTag(viewHolder);
        } else {
            // Get the views from the viewHolder.
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Use the imageloader to set the image and set the data.
        ImageLoader.getInstance().displayImage(productModelList.get(position).getImage(), viewHolder.productImage);
        viewHolder.productName.setText(productModelList.get(position).getName());
        viewHolder.productDescription.setText(productModelList.get(position).getDescription());
        viewHolder.productPrice.setText("€" +productModelList.get(position).getPrice().toString());
        viewHolder.productShopName.setText(productModelList.get(position).getShop().getName());
        return convertView;
    }

    // Use the viewHolder pattern to save memory when finding views.
    private class ViewHolder {
        private ImageView productImage;
        private TextView productName;
        private TextView productDescription;
        private TextView productPrice;
        private TextView productShopName;
    }
}