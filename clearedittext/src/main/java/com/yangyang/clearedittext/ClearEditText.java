package com.yangyang.clearedittext;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;




/**
 * 说明：清除EditText
 * 一束光线：1050189980 2018/8/22
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ClearEditText extends AppCompatEditText implements TextWatcher, OnFocusChangeListener {
    private Drawable mClearDrawable;
    private TextWatcherCallback mTextWatcherCallback = null;
    private OnClearCallback mOnClearCallback;
    private boolean mIsFocused;
    private int mSize = 50;
    
    private OnFocusChangeListener mOnFocusChangeListener;
    
   
    /**
     * 设置图标
     */
    public void setClearImageResource(int resId) {
        if (resId != 0) {
            mClearDrawable = getResources().getDrawable(resId);
        }
    }
    

    /**
     * 设置监听器
     */
    public void setTextWatcherCallback(TextWatcherCallback textWatcherCallback) {
        if (textWatcherCallback != null) {
            mTextWatcherCallback = textWatcherCallback;
        }
    }
    
    public ClearEditText(Context context) {
        this(context, null);
    }
    
    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }
    
    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ClearEditText, defStyle, 0);
        mClearDrawable = typedArray.getDrawable(R.styleable.ClearEditText_drawable_clear);
        mSize = (int) typedArray.getDimension(R.styleable.ClearEditText_drawable_size, mSize);
        typedArray.recycle();
        
        if (mClearDrawable == null) {
            mClearDrawable = getResources().getDrawable(R.drawable.control_ic_clear_delete);
        }
        //  mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        mClearDrawable.setBounds(0, 0, mSize, mSize);
        setClearDrawableVisible(false);
        addTextChangedListener(this);
        super.setOnFocusChangeListener(this);
    }
    
    private void setClearDrawableVisible(boolean isVisible) {
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], isVisible ? mClearDrawable : null,
                getCompoundDrawables()[3]);
    }
    
    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        mOnFocusChangeListener = onFocusChangeListener;
    }
    
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(v, hasFocus);
        }
        mIsFocused = hasFocus;
        if (hasFocus) {
            setClearDrawableVisible(getText().length() > 0);
        } else {
            setClearDrawableVisible(false);
        }
    }
    
    /**
     * 监听Touch事件，通过触摸点的位置，来模拟点击事件
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                //判断触摸点是否在删除图标上
                Rect rect = getCompoundDrawables()[2].getBounds();
                int height = rect.height();
                //删除图标左右边缘
                int leftBound = getWidth() - getTotalPaddingRight();
                int rightBound = getWidth() - getPaddingRight();
                boolean isInnerWidth = x > leftBound && x < rightBound;
                //删除图标上下边缘
                int distance = (getHeight() - height) / 2;
                boolean isInnerHeight = y > distance && y < (distance + height);
                if (isInnerWidth && isInnerHeight) {
                    this.setText("");
                    if (mOnClearCallback != null) {
                        mOnClearCallback.onClear(this);
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }
    
    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (mIsFocused) {
            setClearDrawableVisible(text.length() > 0);
        }
        if (mTextWatcherCallback != null) {
            mTextWatcherCallback.handleTextChanged(text, start, lengthBefore, lengthAfter);
        }
    }
    
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    
    @Override
    public void afterTextChanged(Editable s) {
    }
    
    public interface TextWatcherCallback {
        
      
        void handleTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter);
    }
    

    /**
     * 清除按钮点击回调
     */
    public interface OnClearCallback {
        /**
         * 当点击清除按钮时执该方法
         */
        void onClear(ClearEditText view);
    }
    
    public void setOnClearCallback(OnClearCallback clearCallback) {
        mOnClearCallback = clearCallback;
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mClearDrawable = null;
    }
    
}