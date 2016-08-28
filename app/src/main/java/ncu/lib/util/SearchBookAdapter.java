package ncu.lib.util;

import java.util.ArrayList;

import ncu.lib.R;
import ncu.lib.util.RequestedAdapter.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class SearchBookAdapter extends ArrayAdapter<String> {

	//private Context context;
    private ArrayList<String> books;
    private LayoutInflater inflater;

    public SearchBookAdapter(Context context, ArrayList<String> books) {
        super(context, 0, books);
        //this.context = context;
        this.books = books;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	//int textSize = 5;
    	final String book = books.get(position);
        
        convertView = inflater.inflate(R.layout.simple_list_item, null);
        //convertView.setOnClickListener(null);
        
        final TextView bookTitleView = (TextView) convertView.findViewById(R.id.simple_list_text);
        //bookTitleView.setTextSize(textSize);
        bookTitleView.setText(book);
        
        return convertView;
    }
}
