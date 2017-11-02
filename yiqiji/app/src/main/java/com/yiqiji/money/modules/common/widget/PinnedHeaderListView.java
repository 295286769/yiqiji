package com.yiqiji.money.modules.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 支持ListView置顶功能
 */
public class PinnedHeaderListView extends ListView {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================
    private int start_x;
    private int start_y;

    private View mHeaderView;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private boolean mDrawFlag = true;
    private PinnedHeaderAdapter mPinnedHeaderAdapter;
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 0;

    private Context mContext;
    private int mMaxYOverscrollDistance;
    private boolean isSetData = false;// 是否正在onmesure

    // ===========================================================
    // Constructors
    // ===========================================================

    public boolean isSetData() {
        return isSetData;
    }

    public PinnedHeaderListView(Context context) {
        super(context);
        mContext = context;
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;

        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;

        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;

        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    /**
     * 设置置顶的Header View
     *
     * @param pHeader
     */
    public void setPinnedHeader(View pHeader) {
        mHeaderView = pHeader;

        requestLayout();
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        mPinnedHeaderAdapter = (PinnedHeaderAdapter) adapter;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // 三个覆写方法负责在当前窗口显示inflate创建的Header View

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                   int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        // This is where the magic happens, we have replaced the incoming
        // maxOverScrollY with our own custom variable mMaxYOverscrollDistance;

        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
                mMaxYOverscrollDistance, isTouchEvent);
    }

    private int position;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        isSetData = true;
        position++;

        if (null != mHeaderView) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);

            mMeasuredWidth = mHeaderView.getMeasuredWidth();
            mMeasuredHeight = mHeaderView.getMeasuredHeight();

        }
        // if (listHeight > 0) {
        // setMeasuredDimension(getMeasuredWidth(), listHeight);
        // }
    }

    private int positionposition;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        isSetData = false;
        positionposition++;
        if (null != mHeaderView) {
            mHeaderView.layout(0, 0, mMeasuredWidth, mMeasuredHeight);
            controlPinnedHeader(getFirstVisiblePosition());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (null != mHeaderView && mDrawFlag) {
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * HeaderView三种状态的具体处理
     *
     * @param position
     */
    public void controlPinnedHeader(int position) {
        if (null == mHeaderView) {
            return;
        }

        int pinnedHeaderState = mPinnedHeaderAdapter.getPinnedHeaderState(position);
        switch (pinnedHeaderState) {
            case PinnedHeaderAdapter.PINNED_HEADER_GONE:
                mDrawFlag = false;
                break;

            case PinnedHeaderAdapter.PINNED_HEADER_VISIBLE:
                mPinnedHeaderAdapter.configurePinnedHeader(mHeaderView, position, 0);
                mDrawFlag = true;
                mHeaderView.layout(0, 0, mMeasuredWidth, mMeasuredHeight);
                break;

            case PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP:
                mPinnedHeaderAdapter.configurePinnedHeader(mHeaderView, position, 0);
                mDrawFlag = true;

                // 移动位置
                View topItem = getChildAt(0);

                if (null != topItem) {
                    int bottom = topItem.getBottom();
                    int height = mHeaderView.getHeight();

                    int y;
                    if (bottom < height) {
                        y = bottom - height;
                    } else {
                        y = 0;
                    }

                    if (mHeaderView.getTop() != y) {
                        mHeaderView.layout(0, y, mMeasuredWidth, mMeasuredHeight + y);
                    }

                }
                break;
        }

    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public interface PinnedHeaderAdapter {

        public static final int PINNED_HEADER_GONE = 0;

        public static final int PINNED_HEADER_VISIBLE = 1;

        public static final int PINNED_HEADER_PUSHED_UP = 2;

        int getPinnedHeaderState(int position);

        void configurePinnedHeader(View headerView, int position, int alpaha);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        switch (arg0.getAction()) {
            case MotionEvent.ACTION_DOWN:
                start_x = (int) arg0.getRawX();
                start_y = (int) arg0.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int delet_x = (int) (arg0.getRawX() - start_x);
                int delet_y = (int) (arg0.getRawY() - start_y);
                if (Math.abs(delet_x) > 50) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;

            default:
                break;
        }
        return super.onInterceptTouchEvent(arg0);
    }

    // @Override
    // public boolean onTouchEvent(MotionEvent ev) {
    // switch (ev.getAction()) {
    // case MotionEvent.ACTION_DOWN:
    // dletY = ev.getRawY();
    // deletX = ev.getRawX();
    // break;
    //
    // case MotionEvent.ACTION_UP:
    // float y = ev.getRawY() - dletY;
    // float x = Math.abs(ev.getRawX() - deletX);
    // if (y > 0 && x < 50) {
    // if (getFirstVisiblePosition() == 0) {
    // getParent().requestDisallowInterceptTouchEvent(false);
    // return false;
    // } else {
    // getParent().requestDisallowInterceptTouchEvent(true);
    // return true;
    // }
    //
    // } else if (y < 0 && x < 50) {
    // getParent().requestDisallowInterceptTouchEvent(true);
    // return true;
    //
    // } else {
    // getParent().requestDisallowInterceptTouchEvent(false);
    // }
    // default:
    // break;
    // }
    // return super.onTouchEvent(ev);
    // }

    // private float dletY = 0;
    // private float deletX = 0;
    //
    // @Override
    // public boolean dispatchTouchEvent(MotionEvent ev) {
    // switch (ev.getAction()) {
    // case MotionEvent.ACTION_DOWN:
    // dletY = ev.getRawY();
    // deletX = ev.getRawX();
    // break;
    //
    // case MotionEvent.ACTION_MOVE:
    // float y = ev.getRawY() - dletY;
    // float x = Math.abs(ev.getRawX() - deletX);
    // if (y > 0 && x < 50) {
    // if (getFirstVisiblePosition() == 0) {
    // getParent().requestDisallowInterceptTouchEvent(false);
    // return false;
    // } else {
    // getParent().requestDisallowInterceptTouchEvent(true);
    // return true;
    // }
    //
    // } else if (y < 0 && x < 50) {
    // getParent().requestDisallowInterceptTouchEvent(true);
    // return true;
    //
    // } else {
    // getParent().requestDisallowInterceptTouchEvent(false);
    // }
    // default:
    // break;
    // }
    // return super.dispatchTouchEvent(ev);
    // }

}
