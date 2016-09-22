package dropdown.spinner.com.dropdownlist;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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
    private static final String KEY_SELECTED_INDEX = "selected_index";
    private static final String KEY_IS_POPUP_SHOWING = "is_popup_showing";
    private static final String KEY_SAVED_STATE = "state";
    private List<T> mListItems = new ArrayList<>();
    private T mSelectedItem;
    private PopupWindow popupWindow;
    private ListView mListView;
    private int mPopupHeight = 500;
    private int backgroundColor = Color.WHITE;
    private LinearLayout mDropdownContainer;
    private TextView mDropdownHeader;
    private String mHintText = "Select an item";
    private boolean mHideArrow = false;
    private int mArrowColor = Color.BLACK;
    private Drawable arrowDrawable;
    private int mSelectedIndex = -1;
    private ArrayAdapter<T> adapter;
    private Printable<T> mWhatToPrint = null;
    private boolean mFirstItemSelected = false;
    private Paint mPaint;
    private int mLineColor = 0xFF848484;


    public interface ItemClickListener<T> {
        void onItemSelected(DropDown dropDown, T selectedItem);
    }

    public interface Printable<T> {
        String getPrintable(T t);
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

    public void setWhatToPrint(Printable<T> whatToPrint) {
        mWhatToPrint = whatToPrint;
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            try {
                TypedArray styledAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.DropDownAttrs);
                final String hint = styledAttrs.getString(R.styleable.DropDownAttrs_hintText);
                mHintText = (hint == null) ? mHintText : hint;
                mFirstItemSelected = styledAttrs.getBoolean(R.styleable.DropDownAttrs_firstItemSelected, false);
                mLineColor = styledAttrs.getColor(R.styleable.DropDownAttrs_bottomLineColor, mLineColor);
                backgroundColor = styledAttrs.getColor(R.styleable.DropDownAttrs_backgroundColor, Color.WHITE);
                styledAttrs.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        mPaint.setAlpha(200);
        mPaint.setColor(mLineColor);// line color

        setText(mHintText);
        setStyle(attrs);
        computePopupHeight();
        arrowDrawable = ContextCompat.getDrawable(getContext(), R.drawable.arrow).mutate();
        setCompoundDrawables(null, null, arrowDrawable, null);
        setKeyListener(null);
        setOnClickListener(this);
        mDropdownContainer = new LinearLayout(getContext());
        mDropdownContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mDropdownContainer.setOrientation(LinearLayout.VERTICAL);
        setDropDownHeader();
        if (!mHideArrow) {
            arrowDrawable = ContextCompat.getDrawable(getContext(), R.drawable.arrow).mutate();
            arrowDrawable.setColorFilter(mArrowColor, PorterDuff.Mode.SRC_IN);
            setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDrawable, null);
        }
        setupListView();
        mDropdownContainer.addView(mDropdownHeader);
        mDropdownContainer.addView(mListView);
        setupPopupWindow();
    }

    private void setupPopupWindow() {
        popupWindow = new PopupWindow(getContext());
        popupWindow.setContentView(mDropdownContainer);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(16);
        }
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.rounded_listview_background));
        if (backgroundColor != Color.WHITE) { // default color is white
            setBackgroundColor(backgroundColor);
        }
    }

    private void setupListView() {
        mListView = new ListView(getContext());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                T selectedItem = mListItems.get(position);
                setText(selectedItem.toString());
                setSelectedIndex(position);
                setSelectedItem(selectedItem);
                if (mItemClickListener != null) {
                    mItemClickListener.onItemSelected(DropDown.this, selectedItem);
                }
                collapse();
            }
        });
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mListView.setLayoutParams(params);
    }

    private void setDropDownHeader() {
        mDropdownHeader = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_1, null);
        if (!mHideArrow) {
            ObjectAnimator animator = ObjectAnimator.ofInt(arrowDrawable, "level", 0, 10000);
            animator.start();
            mDropdownHeader.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDrawable, null);
        }
        mDropdownHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                collapse();
            }
        });
        mDropdownHeader.setText(mHintText);
        mDropdownHeader.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    private void computePopupHeight() {
        int screenHeight = getScreenDimension(HEIGHT);
        mPopupHeight = screenHeight / 3;

    }

    private void setStyle(AttributeSet attrs) {
        int[] attrsArray = new int[]{android.R.attr.textAppearanceListItemSmall,
                android.R.attr.listPreferredItemHeightSmall,
                android.R.attr.listPreferredItemPaddingStart};
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
    }

    /**
     * @param what 0 for height , 1 for width
     * @return
     */
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
        if (mListItems != null && mFirstItemSelected && mListItems.size() >= 1) {
            setSelectedIndex(0);
        }
    }

    private void setSelectedItem(T item) {
        mSelectedItem = item;
        if (mSelectedItem != null) {
            final String text = (mWhatToPrint != null) ? mWhatToPrint.getPrintable(item) : item.toString();
            setText(text);
        }
    }

    private void setSelectedIndex(int index) {
        mSelectedIndex = index;
        setSelectedItem(adapter.getItem(mSelectedIndex));
    }

    @Override
    public void onClick(View view) {
        expand();
    }

    private void expand() {
        if (mListView.getAdapter() != null && mListView.getAdapter().getCount() > 0) {
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
            mDropdownHeader.setText(mHintText);
            final int[] location = new int[2];
            getLocationInWindow(location);
            int hintTextHeight = mDropdownHeader.getMeasuredHeight();
            popupWindow.setHeight(mPopupHeight + hintTextHeight);
            popupWindow.setWidth(this.getWidth());
            popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, location[0], location[1]);
        }
    }

    /**
     * returns the selected item , returns null if nothing selected
     *
     * @return
     */
    public T getSelectedItem() {
        return mSelectedItem;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    private void setDropDownList(List<T> list) {
        adapter = new CustomSpinnerAdapter(getContext(), mListItems);
        mListView.setAdapter(adapter);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        computePopupHeight();
    }


    /**
     * List view adapter
     */
    public class CustomSpinnerAdapter extends ArrayAdapter<T> {
        private class ViewHolder {
            private TextView itemView;
        }

        public CustomSpinnerAdapter(Context context, List<T> items) {
            super(context, -1, items);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.itemView = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            T item = getItem(position);
            if (item != null) {
                final String text = (mWhatToPrint == null) ? item.toString() : mWhatToPrint.getPrintable(item);
                viewHolder.itemView.setText(text);
            }
            return convertView;
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SAVED_STATE, super.onSaveInstanceState());
        bundle.putInt(KEY_SELECTED_INDEX, mSelectedIndex);
        if (popupWindow != null) {
            bundle.putBoolean(KEY_IS_POPUP_SHOWING, popupWindow.isShowing());
            collapse();
        } else {
            bundle.putBoolean(KEY_IS_POPUP_SHOWING, false);
        }
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable savedState) {
        if (savedState instanceof Bundle) {
            Bundle bundle = (Bundle) savedState;
            mSelectedIndex = bundle.getInt(KEY_SELECTED_INDEX);
            if (adapter != null) {
                setSelectedItem(adapter.getItem(mSelectedIndex));
            }
            if (bundle.getBoolean(KEY_IS_POPUP_SHOWING)) {
                if (popupWindow != null) {
                    // Post the show request into the looper to avoid bad token exception
                    post(new Runnable() {

                        @Override
                        public void run() {
                            expand();
                        }
                    });
                }
            }
            savedState = bundle.getParcelable(KEY_SAVED_STATE);
        }
        super.onRestoreInstanceState(savedState);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int height = canvas.getHeight() - getHeight() / 6;
        canvas.drawLine(0, height, getWidth(), height, mPaint);
    }
}
