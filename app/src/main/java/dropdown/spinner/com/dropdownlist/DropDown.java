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
    private static final int HINT_TEXT_INDEX = -1;
    private List<T> mListItems = new ArrayList<>();
    private T mSelectedItem;
    private PopupWindow mPopupWindow;
    private ListView mListView;
    private int mPopupHeight = 500;
    private int mBackgroundColor = Color.WHITE;
    private LinearLayout mDropdownContainer;
    private TextView mDropdownHeader;
    private String mHintText = "Select an item";
    private boolean mShowArrow = true;
    private int mArrowColor = Color.BLACK;
    private Drawable mArrowDrawable;
    private int mSelectedIndex = -1;
    private ArrayAdapter<T> adapter;
    private Printable<T> mWhatToPrint = null;
    private boolean mFirstItemSelected = false;
    private Paint mPaint;
    private int mLineColor = 0xFF848484;
    private int mHintTextColor = 0x70000000;
    private int mTextColor = Color.BLACK;


    public interface ItemClickListener<T> {
        void onItemSelected(DropDown dropDown, T selectedItem);

        void onNothingSelected();
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
                int[] defaultAttrSet = {
                        android.R.attr.hint
                };
                TypedArray customStyledAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.DropDownAttrs);
                TypedArray defaultStyleAttrs = getContext().obtainStyledAttributes(attrs, defaultAttrSet);
                final String hint = defaultStyleAttrs.getString(0);
                mHintText = (hint == null) ? mHintText : hint;
                mTextColor = customStyledAttrs.getColor(R.styleable.DropDownAttrs_textColor, mTextColor);
                mHintTextColor = customStyledAttrs.getColor(R.styleable.DropDownAttrs_hintTextColor, mHintTextColor);
                mFirstItemSelected = customStyledAttrs.getBoolean(R.styleable.DropDownAttrs_firstItemSelected, false);
                mLineColor = customStyledAttrs.getColor(R.styleable.DropDownAttrs_bottomLineColor, mLineColor);
                mShowArrow = customStyledAttrs.getBoolean(R.styleable.DropDownAttrs_showArrow, true);
                customStyledAttrs.recycle();
                defaultStyleAttrs.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        initBottomLinePaint();
        setStyle(attrs, this);
        mArrowDrawable = ContextCompat.getDrawable(getContext(), R.drawable.arrow).mutate();
        setCompoundDrawables(null, null, mArrowDrawable, null);
        setKeyListener(null);
        setOnClickListener(this);
        mDropdownContainer = new LinearLayout(getContext());
        mDropdownContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mDropdownContainer.setOrientation(LinearLayout.VERTICAL);
        setDropDownHeader();
        if (mShowArrow) {
            mArrowDrawable = ContextCompat.getDrawable(getContext(), R.drawable.arrow).mutate();
            mArrowDrawable.setColorFilter(mArrowColor, PorterDuff.Mode.SRC_IN);
            setCompoundDrawablesWithIntrinsicBounds(null, null, mArrowDrawable, null);
        }
        setupListView();
        mDropdownContainer.addView(mDropdownHeader);
        final View view = new View(getContext());
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        view.setBackgroundColor(0x70000000);
        view.setAlpha(0.3f);
        mDropdownContainer.addView(view);
        mDropdownContainer.addView(mListView);
        setupPopupWindow();
        setSelectedIndex(HINT_TEXT_INDEX);
    }


    /**
     * Paint for bottom line
     */
    private void initBottomLinePaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        mPaint.setAlpha(200);
        mPaint.setColor(mLineColor);// line color
    }

    private void setupPopupWindow() {
        mPopupWindow = new PopupWindow(getContext());
        mPopupWindow.setContentView(mDropdownContainer);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPopupWindow.setElevation(16);
        }
        mPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.rounded_listview_background));
        if (mBackgroundColor != Color.WHITE) { // default color is white
            setBackgroundColor(mBackgroundColor);
        }
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation);

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
        mDropdownHeader = (TextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
        setStyle(null, mDropdownHeader);
        mDropdownHeader.setTextColor(mHintTextColor);
        if (mShowArrow) {
            ObjectAnimator animator = ObjectAnimator.ofInt(mArrowDrawable, "level", 0, 10000);
            animator.start();
            mDropdownHeader.setCompoundDrawablesWithIntrinsicBounds(null, null, mArrowDrawable, null);
        }
        mDropdownHeader.setText(mHintText);
        mDropdownHeader.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        mDropdownHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onNothingSelected();
                }
                setSelectedIndex(HINT_TEXT_INDEX);
                collapse();
            }
        });
    }

    private void computePopupHeight() {
        int screenHeight = getScreenDimension(HEIGHT);
        mPopupHeight = screenHeight / 3;

    }

    private void setStyle(AttributeSet attrs, TextView textView) {
        int[] attrsArray = new int[]{android.R.attr.textAppearanceListItemSmall,
                android.R.attr.listPreferredItemHeightSmall,
                android.R.attr.listPreferredItemPaddingStart};
        TypedArray ta = getContext().obtainStyledAttributes(attrs, attrsArray);
        int textAppearanceIndex = 0;
        int minHeightIndex = 1;
        int paddingLeftIndex = 2;
        int textAppearance = ta.getResourceId(textAppearanceIndex, -1);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setMinHeight(ta.getDimensionPixelSize(minHeightIndex, 0));
        final int paddingStart = ta.getDimensionPixelSize(paddingLeftIndex, 0);
        textView.setPadding(paddingStart, 0, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(textAppearance);
        } else {
            textView.setTextAppearance(getContext(), textAppearance);
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
        mPopupWindow.dismiss();
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
        mSelectedIndex = (item != null && mListItems.contains(item)) ? mListItems.indexOf(item) : HINT_TEXT_INDEX;
        setTextColor((mSelectedIndex == HINT_TEXT_INDEX) ? mHintTextColor : mTextColor);
        if (mSelectedIndex == HINT_TEXT_INDEX) {
            setText(mHintText);
        } else if (mSelectedItem != null) {
            final String text = (mWhatToPrint != null) ? mWhatToPrint.getPrintable(mSelectedItem) : mSelectedItem.toString();
            setText(text);
        }
    }

    private void setSelectedIndex(int index) {
        mSelectedIndex = index;
        final T item = (index != HINT_TEXT_INDEX) ? adapter.getItem(mSelectedIndex) : null;
        setSelectedItem(item);
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
            mPopupWindow.setHeight(mPopupHeight + hintTextHeight);
            mPopupWindow.setWidth(this.getWidth());
            mPopupWindow.showAtLocation(this, Gravity.NO_GRAVITY, location[0], location[1]);
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
                viewHolder.itemView.setTextColor(mTextColor);
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
        if (mPopupWindow != null) {
            bundle.putBoolean(KEY_IS_POPUP_SHOWING, mPopupWindow.isShowing());
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
            setSelectedIndex(bundle.getInt(KEY_SELECTED_INDEX));
            if (adapter != null) {
                setSelectedItem(adapter.getItem(mSelectedIndex));
            }
            if (bundle.getBoolean(KEY_IS_POPUP_SHOWING)) {
                if (mPopupWindow != null) {
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
