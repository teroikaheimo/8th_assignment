package com.example.a8th_assingment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.a8th_assingment.room.EntityListItem;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter {
    private ArrayList<EntityListItem> rowItems;

    public CustomAdapter(@NonNull Context context, ArrayList<EntityListItem> entityListItems) {
        super(context, 0, entityListItems);
        this.rowItems = entityListItems;
    }

    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        // Get the item to be displayed
        final EntityListItem rowItem = rowItems.get(position);

        // IF null then inflate the layout. Otherwise just update layout contents.
        if (convertView == null) {
            // Get the custom layout and inflate it
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Get the customLayout components
        TextView textOwner = convertView.findViewById(R.id.textViewOwner);
        TextView textLicense = convertView.findViewById(R.id.textViewLicense);
        ImageView mainImage = convertView.findViewById(R.id.imageViewItemImage);

        // Set content
        textOwner.setText(rowItem.owner);
        textLicense.setText(rowItem.license);
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeByteArray(rowItem.image, 0, rowItem.image.length);
        mainImage.setImageBitmap(bitmap);
        return convertView;
    }

    public void setListItems(ArrayList<EntityListItem> listItems) {
        rowItems.clear();
        rowItems.addAll(listItems);
        notifyDataSetChanged();
    }

    public void addItem(ArrayList<EntityListItem> listItems) {
        rowItems.addAll(listItems);
        notifyDataSetChanged();
    }
}
