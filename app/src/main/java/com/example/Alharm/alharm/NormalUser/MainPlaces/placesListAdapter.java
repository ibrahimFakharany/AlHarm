package com.example.Alharm.alharm.NormalUser.MainPlaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.Alharm.alharm.R;

import java.util.ArrayList;

public class placesListAdapter extends BaseAdapter implements Filterable {

    ArrayList<String> places;
    ArrayList<String> _Places;
    Context context;

    private ValueFilter valueFilter;

    public placesListAdapter(ArrayList<String> places, Context context) {
        this.places = places;
        _Places = places;
        this.context = context;
        getFilter();
    }

    @Override
    public int getCount() {
        return _Places.size();
    }

    @Override
    public Object getItem(int i) {
        return _Places.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.places_list, null);
        }
        TextView name = (TextView) v.findViewById(R.id.place_name);
        name.setText("" + _Places.get(i));

        return v;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }


    // هذا ال Class يقوم بالبحث عن الاسم الذي ادخلة المستخدم داخل اال list التي تحتوي على كل اسماء الاماكن ثم يعرضها داخل ال ListView
    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<String> filterList = new ArrayList<String>();
                for (int i = 0; i < places.size(); i++) {
                    if ((places.get(i))
                            .contains(constraint.toString().toUpperCase())) {
                        String mPlaces = "";
                        mPlaces = places.get(i);
                        filterList.add(mPlaces);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = places.size();
                results.values = places;
            }
            return results;
        }


        //Invoked in the UI thread to publish the filtering results in the user interface.
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            _Places = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }
}

