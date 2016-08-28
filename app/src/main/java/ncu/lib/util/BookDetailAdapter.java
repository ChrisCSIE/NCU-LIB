package ncu.lib.util;

import java.util.ArrayList;

import ncu.lib.util.EntryItem;
import ncu.lib.util.Item;
import ncu.lib.util.SectionItem;
import ncu.lib.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by chenli-han on 2014/8/14.
 */
public class BookDetailAdapter extends ArrayAdapter<Item> {
    private ArrayList<Item> items;
    private LayoutInflater inflater;

    public BookDetailAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
        this.items = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        int SectionFontSize = 20;
//        int EntryFontSize = 18;

        final Item item = items.get(position);
        if(item != null) {
            if(item.isSection()) {
                SectionItem sectionItem = (SectionItem) item;
                view = inflater.inflate(R.layout.item_section_header, null);

                view.setOnClickListener(null);
                //view.setOnLongClickListener(null);
                //view.setLongClickable(false);

                final TextView sectionView = (TextView) view.findViewById(R.id.item_section_header);
                sectionView.setTextSize(SectionFontSize);
                sectionView.setText(sectionItem.getTitle());

            } else {
                EntryItem entryItem = (EntryItem) item;
                view = inflater.inflate(R.layout.item_entry, null);

                final TextView title = (TextView) view.findViewById(R.id.item_entry_title);
//                title.setTextSize(EntryFontSize);
                if(title != null)
                    title.setText(entryItem.getTitle());
            }
        }

        return view;
    }
}
