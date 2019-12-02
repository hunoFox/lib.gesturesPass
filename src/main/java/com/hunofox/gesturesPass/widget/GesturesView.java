package com.hunofox.gesturesPass.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.hunofox.gesturesPass.R;
import com.hunofox.gesturesPass.beans.GesturePointBean;
import com.hunofox.gesturesPass.interfaces.GesturesCallback;
import com.hunofox.gesturesPass.GesturesConst;
import com.hunofox.gesturesPass.utils.GesturesUiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：GesturesPass
 * 项目作者：胡玉君
 * 创建日期：2017/12/8 11:07.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class GesturesView extends ViewGroup {

    private List<GesturePointBean> pointList = new ArrayList<>();
    private final GesturesLine gesturesLine;

    public final Animation invalidateAnim;

    /**
     * 第一步：初始化构造器
     *
     * @param context       Activity
     * @param callback      手势密码回调
     */
    public GesturesView(Context context, GesturesCallback callback) {
        super(context);
        invalidateAnim = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.shake);

        float screenWidth = GesturesUiUtils.getDeviceWidth(context);
        float radius = GesturesUiUtils.dip2px(GesturesConst.radius, context.getApplicationContext());
        float blockWidth = GesturesConst.blockWidth<=0?screenWidth/3-2*radius:GesturesUiUtils.dip2px(GesturesConst.blockWidth, context.getApplicationContext());
        float lrtb = screenWidth/2 - 3*radius - blockWidth;
        for (int i = 0; i < 9; i++) {
            ImageView image = new ImageView(context);
            image.setImageResource(R.drawable.point_state_normal);
            this.addView(image);
            invalidate();
            // 第几行
            int row = i / 3;
            // 第几列
            int col = i % 3;

            // 定义点的每个属性
            int leftX = (int)(lrtb + col*(blockWidth+2*radius));
            int topY = (int)(lrtb + row*(radius*2+blockWidth));
            int rightX = (int)(lrtb + 2*radius + col*(blockWidth+2*radius));
            int bottomY = (int)(lrtb + 2*radius + row*(blockWidth+2*radius));

            GesturePointBean p = new GesturePointBean(leftX, rightX, topY, bottomY, image,i+1);
            pointList.add(p);
        }
        gesturesLine = new GesturesLine(context, pointList, callback);
    }

    /**
     * 第二步：将该ViewGroup添加至容器中
     *
     * @param parent        父布局，容器
     */
    public void setParentView(ViewGroup parent){
        // 得到屏幕的宽度
        int width = GesturesUiUtils.getDeviceWidth(parent.getContext().getApplicationContext());
        LayoutParams layoutParams = new LayoutParams(width, width);
        this.setLayoutParams(layoutParams);
        gesturesLine.setLayoutParams(layoutParams);
        parent.addView(gesturesLine);
        parent.addView(this);
    }

    /**
     * 设置是否可绘制手势密码
     *
     * @param enable        true为可绘制
     */
    public void setGesturesEnable(boolean enable){
        gesturesLine.setDrawEnable(enable);
    }

    /**
     * 清除手势密码绘制
     *
     * @param delayedTime   单位：毫秒
     */
    public void clearGesturesDelayed(long delayedTime){
        gesturesLine.clearDrawLineState(delayedTime);
    }

    @Override
    protected void onLayout(boolean flag, int l, int t, int r, int b) {
        for(GesturePointBean point:pointList){
            View v = getChildAt(point.getNum() - 1);
            v.layout(point.getLeftX(), point.getTopY(), point.getRightX(), point.getBottomY());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 遍历设置每个子view的大小
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
