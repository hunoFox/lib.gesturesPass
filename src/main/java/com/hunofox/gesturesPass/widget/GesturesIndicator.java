package com.hunofox.gesturesPass.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.hunofox.gesturesPass.R;

import java.lang.ref.WeakReference;

/**
 * 项目名称：GesturesPass
 * 项目作者：胡玉君
 * 创建日期：2017/12/8 15:01.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class GesturesIndicator extends View {

    private final int CLEAR_GESTURES = 1;

    private final IndicatorHandler handler;
    private final Drawable indicatorNormal;
    private final Drawable indicatorSelected;

    private final int indicatorWidth;
    private final int indicatorHeight;

    private String inputGesturesPass;

    public GesturesIndicator(Context context) {
        this(context, null);
    }

    public GesturesIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        handler = new IndicatorHandler(this);

        indicatorNormal = context.getApplicationContext().getResources().getDrawable(R.drawable.indicator_node_normal);
        indicatorSelected = context.getApplicationContext().getResources().getDrawable(R.drawable.indicator_node_selected);

        indicatorWidth = indicatorNormal.getIntrinsicWidth();
        indicatorHeight = indicatorNormal.getIntrinsicHeight();

        indicatorNormal.setBounds(0, 0, indicatorWidth, indicatorHeight);
        indicatorSelected.setBounds(0, 0, indicatorWidth, indicatorHeight);
    }

    /**
     * 设置当前手势密码
     *
     * @param gesturesPass      输入的手势密码
     */
    public void setInputPassword(String gesturesPass){
        this.inputGesturesPass = gesturesPass;
        invalidate();
    }

    /**
     * 清除手势密码
     *
     * @param delayedTime       单位：毫秒
     */
    public void clearGesturesDelayed(long delayedTime){
        handler.sendEmptyMessageDelayed(CLEAR_GESTURES, delayedTime);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(3*indicatorHeight+indicatorHeight/2,
                3*indicatorWidth+indicatorWidth/2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                int i1 = j*(indicatorHeight + indicatorHeight/4);
                int i2 = i*(indicatorWidth + indicatorWidth/4);
                canvas.save();
                canvas.translate(i1, i2);

                String curNum = String.valueOf(3*i + j + 1);
                if(inputGesturesPass != null && inputGesturesPass.trim().length() > 0){
                    if(!inputGesturesPass.contains(curNum)){
                        indicatorNormal.draw(canvas);
                    }else{
                        indicatorSelected.draw(canvas);
                    }
                }else{
                    indicatorNormal.draw(canvas);
                }
                canvas.restore();
            }
        }
    }

    private static final class IndicatorHandler extends Handler {

        private final WeakReference<GesturesIndicator> reference;

        public IndicatorHandler(GesturesIndicator indicator) {
            reference = new WeakReference<>(indicator);
        }

        @Override
        public void handleMessage(Message msg) {
            if(msg.what == reference.get().CLEAR_GESTURES){
                reference.get().setInputPassword(null);
            }
        }
    }
}
