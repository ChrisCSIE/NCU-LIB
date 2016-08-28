package ncu.lib.util;

import android.view.View;
import android.widget.CheckedTextView;

import ncu.lib.R;

/**
 * Created by chenli-han on 15/1/22.
 */
public class BookItem {

    private String bookname;
    private String status;
    private String itemId;
    private boolean isChecked;
    private View.OnClickListener onClickListener;

    public BookItem(String bookname, String status, String itemid, final boolean isChecked) {
        this.bookname = bookname;
        this.status = status;
        this.itemId = itemid;
        this.isChecked = isChecked;
        this.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckedTextView checkTextView = (CheckedTextView) view.findViewById(R.id.requested_item);

                if(checkTextView != null && checkTextView.isInTouchMode()) {
                    setChecked(!isChecked());
                    checkTextView.toggle();
                }
            }
        };
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }


    public String getItemid() {
        return itemId;
    }

    public void setItemid(String itemid) {
        this.itemId = itemid;
    }
}
