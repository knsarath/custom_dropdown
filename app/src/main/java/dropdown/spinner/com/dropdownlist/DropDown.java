package dropdown.spinner.com.dropdownlist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

public class DropDown<T> extends EditText implements View.OnClickListener {
    private static final String TAG = DropDown.class.getSimpleName();
    private List<T> mListItems = new ArrayList<>();
    private T mSelectedItem;
    private PopupWindow popupWindow;
    private ListView mListView;
    private int popupHeight = 500;
    private int backgroundColor = Color.WHITE;

    public interface ItemClickListener<T> {
        void onItemSelected(DropDown dropDown, T selectedItem);
    }

    private ItemClickListener<T> mItemClickListener;

    public DropDown(Context context) {
        super(context);
        init(null);
    }

    public DropDown(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    public void setItemClickListener(ItemClickListener<T> itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    private void init(AttributeSet attrs) {
        Drawable img = getContext().getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp);
        img.setBounds(0, 0, 80, 80);
        setCompoundDrawables(null, null, img, null);
        setKeyListener(null);
        setOnClickListener(this);
        mListView = new ListView(getContext());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                T selectedItem = mListItems.get(position);
                setText(selectedItem.toString());
                mSelectedItem = selectedItem;
                if (mItemClickListener != null) {
                    mItemClickListener.onItemSelected(DropDown.this, selectedItem);
                }
                collapse();
            }
        });

        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mListView.setLayoutParams(params);
        popupWindow = new PopupWindow(getContext());
        popupWindow.setContentView(mListView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(16);
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ms__drawable));
        } else {
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ms__drop_down_shadow));
        }

        if (backgroundColor != Color.WHITE) { // default color is white
            setBackgroundColor(backgroundColor);
        }

    }

    private void collapse() {
        popupWindow.dismiss();
    }

    public void setItems(List<T> items) {
        mListItems = items;
        setDropDownList(mListItems);
    }

    @Override
    public void onClick(View view) {
        if (mListView.getAdapter().getCount() > 0) {
            final int count = mListView.getAdapter().getCount();
            View itemview = mListView.getAdapter().getView(0, null, mListView);
            int totalHeight = 0;
            for (int i = 0; i < count; i++) {
                itemview.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                totalHeight += itemview.getMeasuredHeight();
                Log.w("HEIGHT" + i, String.valueOf(totalHeight));
            }
            if (popupHeight > totalHeight) {
                popupHeight = totalHeight;
            }

            final int[] location = new int[2];
            getLocationInWindow(location);
            popupWindow.setHeight(popupHeight);
            popupWindow.setWidth(this.getWidth());
            popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, location[0], location[1]);
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    private void setDropDownList(List<T> list) {
        ArrayAdapter<T> adapter = new ArrayAdapter<T>(getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        mListView.setAdapter(adapter);

    }
}
