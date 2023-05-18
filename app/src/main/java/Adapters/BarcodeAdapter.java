package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.scanapp.R;

import java.util.ArrayList;
import java.util.List;

import Models.Barcode;

public class BarcodeAdapter extends ArrayAdapter<Barcode> {

    public BarcodeAdapter(Context context, List<Barcode> Barcode) {
        super(context, R.layout.barcode_list_item,Barcode);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Barcode barcode = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.barcode_list_item, parent, false);
            viewHolder.barcode = (TextView) convertView.findViewById(R.id.barcode);
            viewHolder.barname = (TextView) convertView.findViewById(R.id.barname);
            viewHolder.baritemcount = (TextView) convertView.findViewById(R.id.baritemcount);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.barcode.setText(barcode.getBarCode1());
        viewHolder.barname.setText(barcode.getBarName());
        viewHolder.baritemcount.setText(""+barcode.getBarItemcount());
        // Return the completed view to render on screen
        return convertView;
    }

    public static class ViewHolder {
        TextView barcode,barname,baritemcount;
    }
}
