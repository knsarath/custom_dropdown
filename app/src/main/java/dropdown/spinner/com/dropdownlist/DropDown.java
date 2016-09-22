package dropdown.spinner.com.dropdownlist;

import android.content.Context;
import android.content.res.TypedArray;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DropDown<T> extends TextView implements View.OnClickListener {
    private static final String TAG = DropDown.class.getSimpleName();
    private static final int HEIGHT = 0;
    private static final int WIDTH = 1;
    private List<T> mListItems = new ArrayList<>();
    private T mSelectedItem;
    private PopupWindow popupWindow;
    private ListView mListView;
    private int mPopupHeight = 500;
    private int backgroundColor = Color.WHITE;
    private LinearLayout mDropdownContainer;
    private TextView mDropdowHeader;
    private static final int sPaddingLeft = 8;
    private static final int sPaddingRight = 8;
    private String mHintText = "Select an item";


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
        int[] attrsArray = new int[]{android.R.attr.textAppearanceListItemSmall,
                android.R.attr.listPreferredItemHeightSmall,
                android.R.attr.listPreferredItemPaddingLeft};
        TypedArray ta = getContext().obtainStyledAttributes(attrs, attrsArray);
        int textAppearanceIndex = 0;
        int minHeightIndex = 1;
        int paddingIndex = 2;
        int textAppearance = ta.getResourceId(textAppearanceIndex, -1);
        setGravity(Gravity.CENTER_VERTICAL);
        setMinHeight(ta.getDimensionPixelSize(minHeightIndex, -1));
        final int padding = ta.getDimensionPixelSize(paddingIndex, -1);
        setPadding(padding, 0, 0, 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextAppearance(textAppearance);
        } else {
            setTextAppearance(getContext(), textAppearance);
        }


        ta.recycle();
        int screenHeight = getScreenDimension(HEIGHT);
        mPopupHeight = screenHeight / 3;
        setText(mHintText);

        Drawable arrowDrawable = ContextCompat.getDrawable(getContext(), R.drawable.arrow);


        arrowDrawable.setBounds(0, 0, 80, 80);
        setCompoundDrawables(null, null, arrowDrawable, null);
        setKeyListener(null);
        setOnClickListener(this);
        mDropdownContainer = new LinearLayout(getContext());
        mDropdownContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mDropdownContainer.setOrientation(LinearLayout.VERTICAL);
        mDropdowHeader = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_1, null);


       /*TypedArray ta = getContext().obtainStyledAttributes(attrs, attrsArray);
        int padding = ta.getDimensionPixelSize(0, 8);
        ta.recycle();

        mDropdowHeader.setPadding(padding, padding, 24, padding);*/

        mDropdowHeader.setCompoundDrawables(null, null, arrowDrawable, null);
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

        mDropdowHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                collapse();
            }
        });
        mDropdowHeader.setText(mHintText);
        mDropdowHeader.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mListView.setLayoutParams(params);
        mDropdownContainer.addView(mDropdowHeader);
        mDropdownContainer.addView(mListView);
        popupWindow = new PopupWindow(getContext());
        popupWindow.setContentView(mDropdownContainer);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(16);
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.rounded_listview_background));
        } else {
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.rounded_listview_background));
        }

        if (backgroundColor != Color.WHITE) { // default color is white
            setBackgroundColor(backgroundColor);
        }

    }

    private int getScreenDimension(int what) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        return (what == HEIGHT) ? displaymetrics.heightPixels : (what == WIDTH) ? displaymetrics.widthPixels : 0;
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
            if (mPopupHeight > totalHeight) {
                mPopupHeight = totalHeight;
            }
            mDropdowHeader.setText(mHintText);
            final int[] location = new int[2];
            getLocationInWindow(location);

            int hintTextHeight = mDropdowHeader.getMeasuredHeight();
            popupWindow.setHeight(mPopupHeight + hintTextHeight);
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
