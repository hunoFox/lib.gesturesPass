package com.hunofox.gesturesPass.beans;

import android.widget.ImageView;
import com.hunofox.gesturesPass.R;

/**
 * 项目名称：GesturesPass
 * 项目作者：胡玉君
 * 创建日期：2017/12/7 18:21.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class GesturePointBean {

    public static final int POINT_STATE_NORMAL = 0;//未选中状态
    public static final int POINT_STATE_SELECTED = 1;//选中状态
    public static final int POINT_STATE_WRONG = 2;//错误状态

    //该Point上下左右的坐标
    private int leftX;
    private int rightX;
    private int topY;
    private int bottomY;

    //该Point中心点的坐标
    private int centerX;
    private int centerY;

    private ImageView imageView;

    /**
     * 当前Point的状态(未选中、选中、错误状态)
     *
     * 取值POINT_STATE_NORMAL(未选中)、POINT_STATE_SELECTED(选中)、POINT_STATE_WRONG(错误)
     */
    private int pointState;

    /**
     * 该Point代表的数字
     *
     * 取值范围 1~9
     */
    private int num;

    public GesturePointBean(int leftX, int rightX, int topY, int bottomY, ImageView imageView, int num) {
        this.leftX = leftX;
        this.rightX = rightX;
        this.topY = topY;
        this.bottomY = bottomY;
        this.imageView = imageView;
        this.num = num;

        this.pointState = POINT_STATE_NORMAL;//默认初始值：未选中
        this.centerX = (leftX + rightX) / 2;
        this.centerY = (topY + bottomY) / 2;
    }

    public int getLeftX() {
        return leftX;
    }

    public void setLeftX(int leftX) {
        this.leftX = leftX;
    }

    public int getRightX() {
        return rightX;
    }

    public void setRightX(int rightX) {
        this.rightX = rightX;
    }

    public int getTopY() {
        return topY;
    }

    public void setTopY(int topY) {
        this.topY = topY;
    }

    public int getBottomY() {
        return bottomY;
    }

    public void setBottomY(int bottomY) {
        this.bottomY = bottomY;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public int getPointState() {
        return pointState;
    }

    public void setPointState(int pointState) {
        this.pointState = pointState;
        switch (pointState) {
            case POINT_STATE_NORMAL:
                this.imageView.setImageResource(R.drawable.point_state_normal);
                break;
            case POINT_STATE_SELECTED:
                this.imageView.setImageResource(R.drawable.point_state_pressed);
                break;
            case POINT_STATE_WRONG:
                this.imageView.setImageResource(R.drawable.point_state_wrong);
                break;
            default:
                break;
        }
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    /**
     * 重写hashCode的目的是为了保证在不同位置的Point返回不同的hashCode
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + bottomY;
        result = prime * result + ((imageView == null) ? 0 : imageView.hashCode());
        result = prime * result + leftX;
        result = prime * result + rightX;
        result = prime * result + topY;
        return result;
    }

    /**
     * 重写该方法是为了保证不同位置的Point返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GesturePointBean other = (GesturePointBean) obj;
        if (bottomY != other.bottomY)
            return false;
        if (imageView == null) {
            if (other.imageView != null)
                return false;
        } else if (!imageView.equals(other.imageView))
            return false;
        if (leftX != other.leftX)
            return false;
        if (rightX != other.rightX)
            return false;
        if (topY != other.topY)
            return false;
        return true;
    }
}
