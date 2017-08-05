package zrsApps.safenights;

/**
 * Created by nanditakannapadi on 4/26/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TimelineAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;

    public TimelineAdapter(Context context, ArrayList<String> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.timeline, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textView1);
        textView.setText(values.get(position));
        if((parent.getHeight()/getCount()) > 150) {
            rowView.setMinimumHeight(parent.getHeight()/getCount());
        }
        else {
            rowView.setMinimumHeight(150);
        }

        return rowView;
    }
}