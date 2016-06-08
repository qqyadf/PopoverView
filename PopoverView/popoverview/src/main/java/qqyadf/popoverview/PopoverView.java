package qqyadf.popoverview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

/**
 * Created by Yeah on 2016-06-08.
 */

public class PopoverView extends RelativeLayout implements View.OnTouchListener {
    private boolean DEBUG = true;
    private String TAG = "PopoverView";

    //Popover 位置
    public final static int PopoverPositionUndefine    = 0x0;
    public final static int PopoverPositionUp           = 0x01;
    public final static int PopoverPositionDown         = 0x02;
    public final static int PopoverPositionLeft         = 0x03;
    public final static int PopoverPositionRight        = 0x04;

    public SizeClass mArrowSize;//arrow的Size
    public int mCornerRadius;//popover的Corner radius
    public int mSideEdge;//距离containerView 的最小边距
    public boolean mAnimationSpring;//是否显示动画
    public int mAnimationTime;//动画显示的时间， 单位ms
    public boolean mBlackOverlay;//popover背景模糊效果是否打开
    public boolean mOutsideTouchCancel;//点击显示区域以外，popover是否消失
    public int mBackgroundColor;
    public PopoverViewDelegate delegate;//PopoverView代理

    private RelativeLayout mPopoverView;
    private ViewGroup mContainerView;
    private boolean isAnimating;
    private Point mAtPoint;

    public PopoverView(Context context) {
        super(context);
        init();
    }

    public PopoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mArrowSize = new SizeClass(Misc.dpToPx(11, getResources()), Misc.dpToPx(9, getResources()));
        mCornerRadius = Misc.dpToPx(7, getResources());
        mAnimationSpring = true;
        mAnimationTime = 200;//ms
        mSideEdge = Misc.dpToPx(10, getResources());
        mOutsideTouchCancel = true;
        mBlackOverlay = true;
        mBackgroundColor = Color.WHITE;
        mAtPoint = new Point();

        mPopoverView = new RelativeLayout(getContext());
    }

    public Bitmap createArrowBitmap(Point arrowPoint, int popoverPosition, Rect popoverViewRect) {
        Bitmap arrowBmp = Bitmap.createBitmap(popoverViewRect.width(), popoverViewRect.height(), Bitmap.Config.ARGB_8888);// noticeIconBmp是最终需要的画字的bitmap
        Canvas arrowCanvas = new Canvas(arrowBmp);
        Paint arrowPaint = new Paint();
        arrowPaint.setAntiAlias(true);
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPaint.setColor(mBackgroundColor);
        Path path = new Path();

        RectF rectF = new RectF();
        Point relativeArrowPoint = new Point(arrowPoint.x-popoverViewRect.left, arrowPoint.y-popoverViewRect.top);//arrow的相对PopoverView坐标
        SizeClass popoverViewSize = new SizeClass(popoverViewRect.width(), popoverViewRect.height());
        switch (popoverPosition) {
            case PopoverPositionUp:
                path.moveTo(relativeArrowPoint.x, relativeArrowPoint.y);
                path.lineTo((float) (relativeArrowPoint.x-0.5*mArrowSize.width), relativeArrowPoint.y-mArrowSize.height);
                path.lineTo(mCornerRadius, popoverViewRect.height()-mArrowSize.height);
                rectF.set(0, popoverViewSize.height-mArrowSize.height-2*mCornerRadius, 2*mCornerRadius, popoverViewSize.height-mArrowSize.height);
                path.arcTo(rectF, 90, 90);
                path.lineTo(0, mCornerRadius);
                rectF.set(0, 0, 2*mCornerRadius, 2*mCornerRadius);
                path.arcTo(rectF, 180, 90);
                path.lineTo(popoverViewSize.width-mCornerRadius, 0);
                rectF.set(popoverViewSize.width-2*mCornerRadius, 0, popoverViewSize.width, 2*mCornerRadius);
                path.arcTo(rectF, 270, 90);
                path.lineTo(popoverViewSize.width, popoverViewSize.height-mArrowSize.height-mCornerRadius);
                rectF.set(popoverViewSize.width-2*mCornerRadius, popoverViewSize.height-mArrowSize.height-2*mCornerRadius, popoverViewSize.width, popoverViewSize.height-mArrowSize.height);
                path.arcTo(rectF, 0, 90);
                path.lineTo((float) (relativeArrowPoint.x+0.5*mArrowSize.width), popoverViewSize.height-mArrowSize.height);
                path.lineTo(relativeArrowPoint.x, relativeArrowPoint.y);
                break;
            case PopoverPositionDown:
                path.moveTo(relativeArrowPoint.x, relativeArrowPoint.y);
                path.lineTo((float) (relativeArrowPoint.x+0.5*mArrowSize.width), mArrowSize.height);
                path.lineTo(popoverViewSize.width-mCornerRadius, mArrowSize.height);
                rectF.set(popoverViewSize.width-2*mCornerRadius, mArrowSize.height, popoverViewSize.width, mArrowSize.height+2*mCornerRadius);
                path.arcTo(rectF, 270, 90);
                path.lineTo(popoverViewSize.width, popoverViewSize.height-mCornerRadius);
                rectF.set(popoverViewSize.width-2*mCornerRadius, popoverViewSize.height-2*mCornerRadius, popoverViewSize.width, popoverViewSize.height);
                path.arcTo(rectF, 0, 90);
                path.lineTo(mCornerRadius, popoverViewSize.height);
                rectF.set(0, popoverViewSize.height-2*mCornerRadius, 2*mCornerRadius, popoverViewSize.height);
                path.arcTo(rectF, 90, 90);
                path.lineTo(0, mCornerRadius+mArrowSize.height);
                rectF.set(0, mArrowSize.height, 2*mCornerRadius, mArrowSize.height+2*mCornerRadius);
                path.arcTo(rectF, 180, 90);
                path.lineTo((float) (relativeArrowPoint.x-0.5*mArrowSize.width), mArrowSize.height);
                path.lineTo(relativeArrowPoint.x, relativeArrowPoint.y);
                break;
            case PopoverPositionLeft:
                path.moveTo(relativeArrowPoint.x, relativeArrowPoint.y);
                path.lineTo(relativeArrowPoint.x-mArrowSize.width, (float) (relativeArrowPoint.y+0.5*mArrowSize.height));
                path.lineTo(popoverViewSize.width-mArrowSize.width, popoverViewSize.height-mCornerRadius);
                rectF.set(popoverViewSize.width-mArrowSize.width-2*mCornerRadius, popoverViewSize.height-2*mCornerRadius, popoverViewSize.width-mArrowSize.width, popoverViewSize.height);
                path.arcTo(rectF, 0, 90);
                path.lineTo(mCornerRadius, popoverViewSize.height);
                rectF.set(0, popoverViewSize.height-2*mCornerRadius, 2*mCornerRadius, popoverViewSize.height);
                path.arcTo(rectF, 90, 90);
                path.lineTo(0, mCornerRadius);
                rectF.set(0, 0, 2*mCornerRadius, 2*mCornerRadius);
                path.arcTo(rectF, 180, 90);
                path.lineTo(popoverViewSize.width-mArrowSize.width-mCornerRadius, 0);
                rectF.set(popoverViewSize.width-mArrowSize.width-2*mCornerRadius, 0, popoverViewSize.width-mArrowSize.width, 2*mCornerRadius);
                path.arcTo(rectF,270, 90);
                path.lineTo(popoverViewSize.width-mArrowSize.width, (float) (relativeArrowPoint.y-0.5*mArrowSize.height));
                path.lineTo(relativeArrowPoint.x, relativeArrowPoint.y);
                break;
            case PopoverPositionRight:
                path.moveTo(relativeArrowPoint.x, relativeArrowPoint.y);
                path.lineTo(mArrowSize.width, (float) (relativeArrowPoint.y-0.5*mArrowSize.height));
                path.lineTo(mArrowSize.width, mCornerRadius);
                rectF.set(mArrowSize.width, 0, mArrowSize.width+2*mCornerRadius, 2*mCornerRadius);
                path.arcTo(rectF, 180, 90);
                path.lineTo(popoverViewSize.width-mCornerRadius, 0);
                rectF.set(popoverViewSize.width-2*mCornerRadius, 0, popoverViewSize.width, 2*mCornerRadius);
                path.arcTo(rectF, 270, 90);
                path.lineTo(popoverViewSize.width, popoverViewSize.height-mCornerRadius);
                rectF.set(popoverViewSize.width-2*mCornerRadius, popoverViewSize.height-2*mCornerRadius, popoverViewSize.width, popoverViewSize.height);
                path.arcTo(rectF, 0, 90);
                path.lineTo(mArrowSize.width+mCornerRadius, popoverViewSize.height);
                rectF.set(mArrowSize.width, popoverViewSize.height-2*mCornerRadius, mArrowSize.width+2*mCornerRadius, popoverViewSize.height);
                path.arcTo(rectF, 90, 90);
                path.lineTo(mArrowSize.width, (float) (relativeArrowPoint.y+0.5*mArrowSize.height));
                path.lineTo(relativeArrowPoint.x, relativeArrowPoint.y);
                break;
            default:
                break;
        }
        arrowCanvas.drawPath(path, arrowPaint);

        return arrowBmp;
    }

    private Rect getPopoverRect(Point arrowPoint, SizeClass contentSize, SizeClass containerSize, int popoverPosition) {
        Rect rect = new Rect();
        if (popoverPosition==PopoverPositionUp || popoverPosition==PopoverPositionDown) {
            rect.left = (int) (arrowPoint.x-0.5*contentSize.width);
            rect.right = rect.left+contentSize.width;
            if (popoverPosition==PopoverPositionUp) {
                rect.top = arrowPoint.y-contentSize.height-mArrowSize.height;
                rect.bottom = arrowPoint.y;
            } else  {
                rect.top = arrowPoint.y;
                rect.bottom = arrowPoint.y+contentSize.height+mArrowSize.height;
            }

            int sideEdge = 0;
            if (contentSize.width < containerSize.width) {
                sideEdge = mSideEdge;
            }

            int outerSideEdge = rect.right-containerSize.width;
            if (outerSideEdge > 0) {
                rect.left -= (outerSideEdge+sideEdge);
            } else {
                if (rect.left < 0) {
                    rect.left += Math.abs(rect.left)+sideEdge;
                }
            }
            rect.right = rect.left+contentSize.width;
        } else {
            rect.top = (int) (arrowPoint.y-0.5*contentSize.height);
            rect.bottom = rect.top+contentSize.height;
            if (popoverPosition==PopoverPositionLeft) {
                rect.left = arrowPoint.x-mArrowSize.width-contentSize.width;
                rect.right = arrowPoint.x;
            } else {
                rect.left = arrowPoint.x;
                rect.right = arrowPoint.x+mArrowSize.width+contentSize.width;
            }

            int sideEdge = 0;
            if (contentSize.height < containerSize.height) {
                sideEdge = mSideEdge;
            }

            int outerEdge = rect.bottom-containerSize.height;
            if (outerEdge > 0) {
                rect.top -= (outerEdge+sideEdge);
            } else  {
                if (rect.top < 0) {
                    rect.top += Math.abs(rect.top)+sideEdge;
                }
            }
            rect.bottom = rect.top+contentSize.height;
        }

        return rect;
    }

    private Point getContentAbsolutePoint(int popoverPosition) {
        Point point = new Point();
        switch (popoverPosition) {
            case PopoverPositionUp:
                point.x = 0;
                point.y = 0;
                break;
            case PopoverPositionDown:
                point.x = 0;
                point.y = mArrowSize.height;
                break;
            case PopoverPositionLeft:
                point.x = 0;
                point.y = 0;
                break;
            case PopoverPositionRight:
                point.x = mArrowSize.width;
                point.y = 0;
                break;
            default:
                point.x = 0;
                point.y = 0;
                break;
        }

        return point;
    }

    public void showAtPoint(Point atPoint, int popoverPosition, SizeClass contentSize, View contentView, ViewGroup containerView) {
        mContainerView = containerView;
        Rect containerViewRect = getFrameForView(containerView);
        if (DEBUG) {
            Log.d(TAG, "showAtPoint.atPoint:"+atPoint.x+","+atPoint.y);
            Log.d(TAG, "showAtPoint.popoverPosition:"+popoverPosition);
            Log.d(TAG, "showAtPoint.contentSize:"+contentSize.width+","+contentSize.height);
            Log.d(TAG, "showAtPoint.containerViewRect:"+containerViewRect.left+","+containerViewRect.top+","+containerViewRect.right
                    +","+containerViewRect.bottom);
        }

        if ((contentSize.width<=0 || contentSize.height<=0)
                || (containerViewRect.width()<=0 || containerViewRect.height()<=0)) {
            return;
        }

        if (mOutsideTouchCancel) {
            setOnTouchListener(this);
        }
        if (mBlackOverlay) {
            setBackgroundColor(0x8c000000);//背景模糊
        }

        if (mPopoverView==null) {
            mPopoverView = new PopoverView(getContext());
        }

        RelativeLayout.LayoutParams contentLp = new RelativeLayout.LayoutParams(contentSize.width, contentSize.height);
        Point contentPoint = getContentAbsolutePoint(popoverPosition);
        contentLp.leftMargin = contentPoint.x;
        contentLp.topMargin = contentPoint.y;
        mPopoverView.addView(contentView, contentLp);

        Rect popoverRect = getPopoverRect(atPoint, contentSize, new SizeClass(containerViewRect.width(), containerViewRect.height()), popoverPosition);
        Bitmap bmp = createArrowBitmap(atPoint, popoverPosition, popoverRect);
        mPopoverView.setBackgroundDrawable(new BitmapDrawable(bmp));
        RelativeLayout.LayoutParams popoverLp = new RelativeLayout.LayoutParams(popoverRect.width(), popoverRect.height());
        popoverLp.leftMargin = popoverRect.left;
        popoverLp.topMargin = popoverRect.top;
        addView(mPopoverView, popoverLp);

        ViewGroup.LayoutParams insertParams =  new  android.view.ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainerView.addView(this, insertParams);

        //If we don't want animation, just tell the delegate
        if (!mAnimationSpring){
            //Tell delegate we did show
            if (delegate != null)
                delegate.popoverViewDidShow(this);
        } else{
            //Continue only if we are not animating
            mAtPoint.set(atPoint.x-popoverRect.left, atPoint.y-popoverRect.top);
            if (!isAnimating){
                //Create alpha animation, with its listener
                ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1, mAtPoint.x, mAtPoint.y);
                animation.setDuration(mAnimationTime);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //Nothing to do here
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        //Nothing to do here
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //End animation
                        isAnimating = false;
                        //Tell delegate we did show
                        if (delegate != null)
                            delegate.popoverViewDidShow(PopoverView.this);
                    }
                });

                //Start animation
                isAnimating = true;
                mPopoverView.startAnimation(animation);
            }
        }
    }

    public void showAtView(View atView, int popoverPosition, SizeClass contentSize, View contentView, ViewGroup containerView) {
        Rect atViewRect = getFrameForView(atView);//atView的绝对坐标
        Rect containerViewRect = getFrameForView(containerView);//containerView的绝对坐标
        if (DEBUG) {
            Log.d(TAG, "showAtView.atViewRect:"+atViewRect.left+","+atViewRect.top+","+atViewRect.right+","+atViewRect.bottom);
            Log.d(TAG, "showAtView.popoverPosition:"+popoverPosition);
            Log.d(TAG, "showAtView.contentSize:"+contentSize.width+","+contentSize.height);
            Log.d(TAG, "showAtView.containerViewRect:"+containerViewRect.left+","+containerViewRect.top+","+containerViewRect.right
                    +","+containerViewRect.bottom);
        }

        if ((contentSize.width<=0 || contentSize.height<=0)
                || (containerViewRect.width()<=0 || containerViewRect.height()<=0)) {
            return;
        }

        if (popoverPosition == PopoverPositionUndefine) {
            boolean upCanContain = (atViewRect.top-containerViewRect.top) >= contentSize.height + mArrowSize.height;
            boolean downCanContain = containerViewRect.height()-(atViewRect.bottom-containerViewRect.top)-mArrowSize.height >= contentSize.height;
            boolean leftCanContain = (atViewRect.left-containerViewRect.left) >= contentSize.width + mArrowSize.width;
            boolean rightCanContain = containerViewRect.width()-(atViewRect.right-containerViewRect.left)-mArrowSize.width >= contentSize.width;
            if (upCanContain) {
                popoverPosition = PopoverPositionUp;
            } else if (downCanContain) {
                popoverPosition = PopoverPositionDown;
            } else if (leftCanContain) {
                popoverPosition = PopoverPositionLeft;
            } else if (rightCanContain) {
                popoverPosition = PopoverPositionRight;
            }
        }

        Point atPoint = new Point();
        switch (popoverPosition) {
            case PopoverPositionUp:
                atPoint.x = atViewRect.centerX()-containerViewRect.left;
                atPoint.y = atViewRect.top-containerViewRect.top;
                break;
            case PopoverPositionDown:
                atPoint.x = atViewRect.centerX()-containerViewRect.left;
                atPoint.y = atViewRect.bottom-containerViewRect.top;
                break;
            case PopoverPositionLeft:
                atPoint.x = atViewRect.left-containerViewRect.left;
                atPoint.y = atViewRect.centerY()-containerViewRect.top;
                break;
            case PopoverPositionRight:
                atPoint.x = atViewRect.right-containerViewRect.left;
                atPoint.y = atViewRect.centerY()-containerViewRect.top;
                break;
            default:
                return;
        }

        showAtPoint(atPoint, popoverPosition, contentSize, contentView, containerView);
    }

    public void showAtView(View atView, SizeClass contentSize, View contentView, ViewGroup containerView) {
        showAtView(atView, PopoverPositionUndefine, contentSize, contentView, containerView);
    }

    public void dismissView() {
        if (mPopoverView==null)
            return;

        //Tell delegate we will dismiss
        if (delegate != null)
            delegate.popoverViewWillDismiss(PopoverView.this);

        //If we don't want animation
        if (!mAnimationSpring){
            //Just remove views
            mPopoverView.removeAllViews();
            removeAllViews();
            mContainerView.removeView(this);
            //Tell delegate we did dismiss
            if (delegate != null)
                delegate.popoverViewDidDismiss(PopoverView.this);
        }
        else{
            //Continue only if there is not an animation in progress
            if (!isAnimating){
                //Create alpha animation, with its listener
                ScaleAnimation animation = new ScaleAnimation(1, 0, 1, 0, mAtPoint.x, mAtPoint.y);
                animation.setDuration(mAnimationTime);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //Nothing to do here
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        //Nothing to do here
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //Remove the view
                        mPopoverView.removeAllViews();
                        removeAllViews();
                        mContainerView.removeView(PopoverView.this);
                        //End animation
                        isAnimating = false;
                        //Tell delegate we did dismiss
                        if (delegate != null)
                            delegate.popoverViewDidDismiss(PopoverView.this);
                    }
                });

                //Start animation
                isAnimating = true;
                mPopoverView.startAnimation(animation);
            }

        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (!isAnimating && view.equals(this)) {
            dismissView();
        }
        return true;
    }

    /**
     * Get the Rect frame for a view (relative to the Window of the application)
     * @param v The view to get the rect from
     * @return The rect of the view, relative to the application window
     */
    public static Rect getFrameForView(View v){
        int location [] = new int [2];
        v.getLocationOnScreen(location);
        Rect viewRect = new Rect(location[0], location[1], location[0]+v.getWidth(), location[1]+v.getHeight());
        return viewRect;
    }
    /**
     * Interface to get information from the popover view. Use setDelegate to have access to this methods
     */
    public static interface PopoverViewDelegate{
        public void popoverViewWillShow(PopoverView view);
        public void popoverViewDidShow(PopoverView view);
        public void popoverViewWillDismiss(PopoverView view);
        public void popoverViewDidDismiss(PopoverView view);
    }
}
