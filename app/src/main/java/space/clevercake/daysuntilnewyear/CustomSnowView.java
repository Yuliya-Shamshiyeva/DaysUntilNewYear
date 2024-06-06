package space.clevercake.daysuntilnewyear;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import space.clevercake.daysuntilnewyear.BobbleBean;
import space.clevercake.daysuntilnewyear.ColorUtil;
public class CustomSnowView extends View {
    public CustomSnowView(Context context) {
        this(context, null);
    }

    public CustomSnowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    //brush
    Paint mPaint;

    //Collection of save points
    List<BobbleBean> mBobbleBeanList;

    public CustomSnowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();

        mBobbleBeanList = new ArrayList<>();
    }
    //First measurement
    //The default View size
    private int mDefaultWidth = dp2px(100);
    private int mDefaultHeight = dp2px(100);

    //The size of the measured View is the size of the canvas
    private int mMeasureWidth = 0;
    private int mMeasureHeight = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //Get measurement and calculation related content
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        if (widthSpecMode == MeasureSpec.EXACTLY) {
            //When specMode = EXACTLY, the exact value mode, that is, when we specify a specific size for the View in the layout file
            mMeasureWidth = widthSpecSize;
        } else {
            //Specify the default size
            mMeasureWidth = mDefaultWidth;
            if (widthSpecMode == MeasureSpec.AT_MOST) {
                mMeasureWidth = Math.min(mMeasureWidth, widthSpecSize);
            }
        }

        //Measure and calculate the height of View
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            //When specMode = EXACTLY, the exact value mode, that is, when we specify a specific size for the View in the layout file
            mMeasureHeight = heightSpecSize;
        } else {
            //Specify the default size
            mMeasureHeight = mDefaultHeight;
            if (heightSpecMode == MeasureSpec.AT_MOST) {
                mMeasureHeight = Math.min(mMeasureHeight, heightSpecSize);
            }
        }
        mMeasureHeight = mMeasureHeight - getPaddingBottom() - getPaddingTop();
        mMeasureWidth = mMeasureWidth - getPaddingLeft() - getPaddingBottom();
        //Re-measure
        setMeasuredDimension(mMeasureWidth, mMeasureHeight);
    }
    //Calculation of a dp to pixel
    private int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
    //Create a point here
    Random mRandom = new Random();

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        for (int i = 0; i < mMeasureWidth / 3; i++) {

            BobbleBean lBobbleBean = new BobbleBean();

            //Generate location information randomly
            //The value range is 0 ~ mMeasureWidth
            int x = mRandom.nextInt(mMeasureWidth);
            int y = mRandom.nextInt(mMeasureHeight);

            //Draw the location used
            lBobbleBean.postion = new Point(x, y);
            //Reset position
            lBobbleBean.origin = new Point(x, 0);
            //Random radius 1 ~ 4
            lBobbleBean.radius = mRandom.nextFloat() * 3 + dp2px(1);
            //Random speed 3 ~ 6
            lBobbleBean.speed = 1 + mRandom.nextInt(3);
            //White with random transparency
            lBobbleBean.color = ColorUtil.randomWhiteColor();
            mBobbleBeanList.add(lBobbleBean);
        }

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Recalculate position when drawing
        for (BobbleBean lBobbleBean : mBobbleBeanList) {

            Point lPostion = lBobbleBean.postion;
            //Increase the offset in the vertical direction
            lPostion.y+=lBobbleBean.speed;

            //Slightly offset in the x-axis direction
            float randValue = mRandom.nextFloat() *2 -0.5f;
            lPostion.x+=randValue;

            //Boundary control
            if(lPostion.y>mMeasureHeight){
                lPostion.y = 0;
            }
        }

        //Draw all these points first

        for (BobbleBean lBobbleBean : mBobbleBeanList) {
            //Modify the color of the brush
            mPaint.setColor(lBobbleBean.color);
            //draw
            // Parameter 1 2 Dot position
            // parameter three radius
            // Parameters four brushes
            canvas.drawCircle(lBobbleBean.postion.x, lBobbleBean.postion.y, lBobbleBean.radius, mPaint);
        }

        //Cyclic refresh every 10 milliseconds
        postInvalidateDelayed(10L);

    }
}