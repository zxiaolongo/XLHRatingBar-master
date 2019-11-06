package com.example.xlhratingbar_lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * you can use it in xml or java code.
 *
 *
 *
 * @author xingliuhua
 */
public class XLHRatingBar extends LinearLayout {
    private int mNumStars = 5;// 共有几个星星
    private float mRating;
    private String mRatingViewClassName = "com.example.xlhratingbar_lib.SimpleRatingView";
    private boolean mIndicator;
    private boolean mShowHalf;
    private ArrayList<IRatingView> mIRatingViews = new ArrayList<>();

    public XLHRatingBar(Context context) {
        super(context);
        initData(context, null);
        initView();
    }

    public XLHRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs);

        initView();
    }


    private void initData(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.XLHRatingBar);

            mNumStars = typedArray.getInt(R.styleable.XLHRatingBar_numStars, 5);
            mRating = typedArray.getFloat(R.styleable.XLHRatingBar_rating, 0f);
            mRatingViewClassName = typedArray.getString(R.styleable.XLHRatingBar_ratingViewClass);
            mIndicator = typedArray.getBoolean(R.styleable.XLHRatingBar_indicator, false);
            mShowHalf = typedArray.getBoolean(R.styleable.XLHRatingBar_showHalf,true);
            if(TextUtils.isEmpty(mRatingViewClassName)){
                mRatingViewClassName = "com.example.xlhratingbar_lib.SimpleRatingView";
            }
        }
        try {
            mIRatingViews.clear();
            for (int i = 0; i < mNumStars; i++) {
                Class<?> netErrorClass = Class.forName(mRatingViewClassName);
                IRatingView mIRatingView = (IRatingView) netErrorClass.newInstance();
                mIRatingViews.add(mIRatingView);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void initView() {

        removeAllViews();


        for (int i = 0; i < mNumStars; i++) {
            IRatingView ratingView = mIRatingViews.get(i);
            ViewGroup viewGroup = ratingView.getRatingView(getContext(), i, mNumStars);

            addView(viewGroup);

            final int finalI = i + 1;
            viewGroup.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mIndicator){
                        return true;
                    }
                    if (!isEnabled()) {
                        return false;
                    }
                    if (event.getAction() != MotionEvent.ACTION_UP) {
                        return true;
                    }

                    if (getOrientation() == LinearLayout.HORIZONTAL) {
                        if (mShowHalf && event.getX() < v.getWidth() / 2f) {
                            mRating = finalI - 0.5f;
                        } else {
                            mRating = finalI;
                        }
                    } else {
                        if (mShowHalf && event.getY() < v.getHeight() / 2f) {
                            mRating = finalI - 0.5f;
                        } else {
                            mRating = finalI;
                        }
                    }
                    resetRatingViewRes();
                    if (mOnRatingChangeListener != null) {
                        mOnRatingChangeListener.onChange(mRating, mNumStars);
                    }
                    return true;
                }
            });
        }
        resetRatingViewRes();
    }

    public int getNumStars() {
        return mNumStars;
    }

    public void setNumStars(int numStars) {
        mIRatingViews.clear();
        this.mNumStars = numStars;
        try {
            mIRatingViews.clear();
            for (int i = 0; i < mNumStars; i++) {
                Class<?> netErrorClass = Class.forName(mRatingViewClassName);
                IRatingView mIRatingView = (IRatingView) netErrorClass.newInstance();
                mIRatingViews.add(mIRatingView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initView();
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {

        if (rating < 0 || rating > mNumStars) {
            return;
        }
        this.mRating = rating;

        initView();
    }

    public String getRatingViewClassName() {
        return mRatingViewClassName;
    }

    public void setRatingViewClassName(String ratingViewClassName) {
        mRatingViewClassName = ratingViewClassName;
        mIRatingViews.clear();
        try {
            mIRatingViews.clear();
            for (int i = 0; i < mNumStars; i++) {
                Class<?> netErrorClass = Class.forName(mRatingViewClassName);
                IRatingView mIRatingView = (IRatingView) netErrorClass.newInstance();
                mIRatingViews.add(mIRatingView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initView();
    }

    /**
     *  can not change rating by screen
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    private void resetRatingViewRes() {
        for (int i = 0; i < mNumStars; i++) {
            int state = IRatingView.STATE_NONE;
            if (mRating - i <= 0) {
                state = IRatingView.STATE_NONE;
            } else if (mRating - i == 0.5) {
                state = IRatingView.STATE_HALF;
            } else if (mRating - i >= 0.5) {
                state = IRatingView.STATE_FULL;
            }
            IRatingView ratingView = mIRatingViews.get(i);
            ratingView.setCurrentState(state, i, mNumStars);
        }
    }

    /**
     * you can get rating change event
     */
    private OnRatingChangeListener mOnRatingChangeListener;

    public OnRatingChangeListener getOnRatingChangeListener() {
        return mOnRatingChangeListener;
    }

    public void setOnRatingChangeListener(OnRatingChangeListener onRatingChangeListener) {
        mOnRatingChangeListener = onRatingChangeListener;
    }

    public interface OnRatingChangeListener {
        /**
         *
         * @param rating from 0
         * @param numStars star item sum count
         */
        void onChange(float rating, int numStars);
    }
}
