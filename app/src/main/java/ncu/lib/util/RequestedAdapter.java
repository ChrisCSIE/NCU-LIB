package ncu.lib.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;

import ncu.lib.R;

/**
 * Created by chenli-han on 14/11/6.
 */
public class RequestedAdapter extends ArrayAdapter {
    private ArrayList<BookItem> books;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;

    public RequestedAdapter(Context context, ArrayList<BookItem> books) {
        super(context, 0, books);
        this.books = books;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.viewHolder = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final BookItem book = books.get(position);
        final String bookname = book.getBookname();
        final String book_status = book.getStatus();

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_requested, null);
            convertView.setOnClickListener(null);

            final CheckedTextView booknameView = (CheckedTextView) convertView.findViewById(R.id.requested_item);
            final TextView bookStatusView = (TextView) convertView.findViewById(R.id.requested_item_status);

            viewHolder = new ViewHolder();
            viewHolder.checkedTextView = booknameView;
            viewHolder.textView = bookStatusView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(viewHolder != null) {
            viewHolder.checkedTextView.setText(bookname);
            viewHolder.checkedTextView.setChecked(book.isChecked());
            viewHolder.checkedTextView.setOnClickListener(book.getOnClickListener());
            viewHolder.textView.setText(book_status);
            viewHolder.textView.setOnClickListener(book.getOnClickListener());
        }

        return convertView;
    }

    static class ViewHolder {
        public TextView textView;
        public CheckedTextView checkedTextView;
    }
}

