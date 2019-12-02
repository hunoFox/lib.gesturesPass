package com.hunofox.gesturesPass.widget;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import com.hunofox.gesturesPass.beans.GesturePointBean;
import com.hunofox.gesturesPass.interfaces.GesturesCallback;
import com.hunofox.gesturesPass.GesturesConst;
import com.hunofox.gesturesPass.utils.GesturesUiUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：GesturesPass
 * 项目作者：胡玉君
 * 创建日期：2017/12/7 19:42.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：手势密码连接线
 * ----------------------------------------------------------------------------------------------------
 */
public class GesturesLine extends View {

    private final List<GesturePointBean> pointList;//9个点的集合
    //画线的集合
    private final List<Pair<GesturePointBean, GesturePointBean>> lines = new ArrayList<>();
    //需要被自动选中的点
    private final Map<String, GesturePointBean> autoCheckPoint = new HashMap<>();
    private StringBuilder password;//用户绘制的密码

    private final GesturesCallback callback;
    private final WeakReference<Context> reference;
    private final GestureHandler handler;

    private final Paint paint = new Paint(Paint.DITHER_FLAG);//抗抖动画笔
    private final Canvas canvas = new Canvas();
    private final Bitmap bitmap;

    private boolean isDrawFinished = true;
    private boolean isDrawEnable = true;

    public GesturesLine(Context context, List<GesturePointBean> pointList, GesturesCallback callback) {
        super(context);

        handler = new GestureHandler(this);

        this.pointList = pointList;
        autoCheckPoint.put("1,3", getGesturePointByNum(2));
        autoCheckPoint.put("1,7", getGesturePointByNum(4));
        autoCheckPoint.put("1,9", getGesturePointByNum(5));
        autoCheckPoint.put("2,8", getGesturePointByNum(5));
        autoCheckPoint.put("3,7", getGesturePointByNum(5));
        autoCheckPoint.put("3,9", getGesturePointByNum(6));
        autoCheckPoint.put("4,6", getGesturePointByNum(5));
        autoCheckPoint.put("7,9", getGesturePointByNum(8));

        this.callback = callback;
        reference = new WeakReference<>(context);

        bitmap = Bitmap.createBitmap(GesturesUiUtils.getDeviceWidth(context.getApplicationContext()),
                GesturesUiUtils.getDeviceWidth(context.getApplicationContext()),
                Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        paint.setStyle(Paint.Style.STROKE);// 设置非填充
        paint.setStrokeWidth(5);// 笔宽5像素
        paint.setAntiAlias(true);// 不显示锯齿

        password = new StringBuilder();
    }

    /**
     * 清除屏幕上绘制的手势密码
     *
     * @param delay     延迟delay毫秒执行
     */
    public void clearDrawLineState(long delay){
        isDrawFinished = false;
        handler.sendEmptyMessageDelayed(handler.CLEAR_LINES, delay);
    }

    /**
     * 设置是否可绘制
     *
     * @param isDrawEnable      true为可绘制
     */
    public void setDrawEnable(boolean isDrawEnable){
        this.isDrawEnable = isDrawEnable;
    }

    /**
     * 绘制手势密码错误的线
     */
    public void drawErrorPath(){
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        paint.setColor(Color.parseColor(GesturesConst.COLOR_GESTURES_LINES_WRONG));
        if(lines.size() <= 0){
            if(currentPoint != null){
                currentPoint.setPointState(GesturePointBean.POINT_STATE_WRONG);
            }
        }else{
            for (Pair<GesturePointBean, GesturePointBean> pair : lines) {
                pair.first.setPointState(GesturePointBean.POINT_STATE_WRONG);
                pair.second.setPointState(GesturePointBean.POINT_STATE_WRONG);
                canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
                        pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
            }
        }
        invalidate();
    }

    /**
     * 绘制最终的手势密码(用于清理没有连接点的线)
     */
    private void drawFinishedPath(){
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        paint.setColor(Color.parseColor(GesturesConst.COLOR_GESTURES_LINES));
        if(lines.size() <= 0){
            if(currentPoint != null){
                currentPoint.setPointState(GesturePointBean.POINT_STATE_SELECTED);
            }
        }else{
            for (Pair<GesturePointBean, GesturePointBean> pair : lines) {
                pair.first.setPointState(GesturePointBean.POINT_STATE_SELECTED);
                pair.second.setPointState(GesturePointBean.POINT_STATE_SELECTED);
                canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
                        pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    private int moveX,moveY;//手指移动的位置
    private GesturePointBean currentPoint;//当前点的位置
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isDrawEnable || !isDrawFinished) return true;//当前不允许绘制手势
        paint.setColor(Color.parseColor(GesturesConst.COLOR_GESTURES_LINES));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveX = (int) event.getX();
                moveY = (int) event.getY();

                //判断当前点击的位置处于哪个点
                currentPoint = getPointAt(moveX, moveY);
                if(currentPoint != null){
                    //当前手指在点上
                    currentPoint.setPointState(GesturePointBean.POINT_STATE_SELECTED);//被选中状态
                    password.append(currentPoint.getNum());
                }
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                clearScreenAndDrawLines();
                
                GesturePointBean pointAt = getPointAt((int)event.getX(), (int)event.getY());
                if(currentPoint == null && pointAt == null){
                    //说明此时手指处于点与点之间，且按下时也未选中点
                    return true;
                }
                if(currentPoint == null){
                    //说明此时手指选中了点，且按下时未选中点
                    currentPoint = pointAt;
                    currentPoint.setPointState(GesturePointBean.POINT_STATE_SELECTED);
                    password.append(currentPoint.getNum());
                }
                
                //此时的currentPoint不再为null
                if(pointAt == null || currentPoint.equals(pointAt) || pointAt.getPointState() == GesturePointBean.POINT_STATE_SELECTED){
                    //此时在两点之间 || 与当前点一致 || 按下的点已被选中
                    canvas.drawLine(currentPoint.getCenterX(), currentPoint.getCenterY(), event.getX(), event.getY(), paint);
                }else{
                    //此时手指已经移动到下一个未被选中的点
                    canvas.drawLine(currentPoint.getCenterX(), currentPoint.getCenterY(), event.getX(), event.getY(), paint);
                    pointAt.setPointState(GesturePointBean.POINT_STATE_SELECTED);
                    
                    //判断两点之间是否有点需要被选中
                    int startNum = currentPoint.getNum();
                    int endNum = pointAt.getNum();
                    String key = null;
                    if (startNum < endNum) {
                        key = startNum + "," + endNum;
                    } else {
                        key = endNum + "," + startNum;
                    }
                    GesturePointBean between = autoCheckPoint.get(key);
                    if(between != null && between.getPointState() != GesturePointBean.POINT_STATE_SELECTED){
                        between.setPointState(GesturePointBean.POINT_STATE_SELECTED);

                        //有中间点且未被选中
                        Pair<GesturePointBean, GesturePointBean> pairCurrentAndBetween = new Pair<>(currentPoint, between);
                        lines.add(pairCurrentAndBetween);
                        password.append(between.getNum());

                        Pair<GesturePointBean, GesturePointBean> pairBetweenAndPointAt = new Pair<>(between, pointAt);
                        lines.add(pairBetweenAndPointAt);
                        password.append(pointAt.getNum());
                    }else{
                        //无中间点，直接连接两条线即可
                        Pair<GesturePointBean, GesturePointBean> pair = new Pair<>(currentPoint, pointAt);
                        lines.add(pair);
                        password.append(pointAt.getNum());
                    }
                    currentPoint = pointAt;
                }
                invalidate();
                if(this.callback != null){
                    callback.onGesturesInput(password.toString());
                }
                break;
            case MotionEvent.ACTION_UP:
                if(this.callback != null){
                    if(password.length() == 0){
                        //没有点任何一个点
                        return true;
                    }else if(password.length() < GesturesConst.MIN_GESTURES_COUNT){
                        //手势密码不合法
                        drawErrorPath();
                        callback.onGesturesInvalidate(password.toString());
                        clearDrawLineState(GesturesConst.TIME_CLEAR_LINE_DELAYED);
                    }else{
                        drawFinishedPath();
                        callback.onGesturesFinished(password.toString());
                    }
                }
                break;
        }
        return true;
    }

    //清除屏幕上的线，绘制集合中的线
    private void clearScreenAndDrawLines() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for(Pair<GesturePointBean, GesturePointBean> pair:lines){
            canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
                    pair.second.getCenterX(), pair.second.getCenterY(),
                    paint
            );
        }
    }

    //判断当前点击的位置处于哪个点
    private GesturePointBean getPointAt(int moveX, int moveY) {
        for(GesturePointBean point:pointList){
            //如果当前手指在该点的左侧，或在该点的右侧，则不是当前点击的点
            if(point.getLeftX() > moveX || point.getRightX() < moveX){
                continue;
            }

            //若当前手指在该点的上部，或在该点的下部，则不是当前点击的点
            if(point.getTopY() > moveY || point.getBottomY() < moveY){
                continue;
            }

            //此时为当前点击的点
            return point;
        }
        return null;
    }

    //通过对当前点的数字获取当前点的实例
    private GesturePointBean getGesturePointByNum(int num) {
        for (GesturePointBean point : pointList) {
            if (point.getNum() == num) {
                return point;
            }
        }
        return null;
    }

    private static class GestureHandler extends Handler {

        private final int CLEAR_LINES = 1;//清除绘制的线

        private final WeakReference<GesturesLine> reference;
        public GestureHandler(GesturesLine gesturesLine) {
            reference = new WeakReference<>(gesturesLine);
        }

        @Override
        public void handleMessage(Message msg) {
            GesturesLine gesturesLine = reference.get();
            if(gesturesLine != null){
                switch (msg.what) {
                    case CLEAR_LINES:
                        gesturesLine.password = new StringBuilder();
                        gesturesLine.lines.clear();
                        gesturesLine.clearScreenAndDrawLines();
                        for(GesturePointBean point:gesturesLine.pointList){
                            point.setPointState(GesturePointBean.POINT_STATE_NORMAL);
                        }
                        gesturesLine.invalidate();
                        gesturesLine.isDrawFinished = true;
                        break;
                }
            }
        }
    }
}
