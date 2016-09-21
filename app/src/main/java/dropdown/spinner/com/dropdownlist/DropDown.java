package dropdown.spinner.com.dropdownlist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

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
    private LinearLayout linearLayout;
    private TextView headerTextView;
    private static final int sPaddingLeft = 8;
    private static final int sPaddingRight = 8;


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
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        popupHeight = height / 3;

        Drawable arrowDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ms__arrow);

        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        layoutParams.setMargins(sPaddingLeft, 0, sPaddingRight, 0);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;

        arrowDrawable.setBounds(0, 0, 80, 80);
        setCompoundDrawables(null, null, arrowDrawable, null);
        setKeyListener(null);
        setOnClickListener(this);
        linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        headerTextView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_1, null);
        final LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(sPaddingLeft, 0, sPaddingRight, 0);
        headerTextView.setLayoutParams(layoutParams1);

        headerTextView.setCompoundDrawables(null, null, arrowDrawable, null);
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
        linearLayout.addView(headerTextView);
        linearLayout.addView(mListView);
        popupWindow = new PopupWindow(getContext());
        popupWindow.setContentView(linearLayout);
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
            headerTextView.setText("Select an item");
            final int[] location = new int[2];
            getLocationInWindow(location);
            popupWindow.setHeight(popupHeight);
            popupWindow.setWidth(this.getWidth());
            popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, location[0], location[1]);
        }

    }

    public T getSelectedItem() {
        return mSelectedItem;
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
