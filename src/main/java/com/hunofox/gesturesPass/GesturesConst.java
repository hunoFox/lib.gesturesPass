package com.hunofox.gesturesPass;

/**
 * 项目名称：GesturesPass
 * 项目作者：胡玉君
 * 创建日期：2017/12/8 10:14.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class GesturesConst {

    //默认手势连接线的颜色
    public static final String COLOR_GESTURES_LINES = "#028ce5";

    //手势密码错误时连接线的颜色
    public static final String COLOR_GESTURES_LINES_WRONG = "#e80606";

    //默认最少连接点的数量
    public static final int MIN_GESTURES_COUNT = 4;

    //输入不符合要求的手势密码后，默认1500毫秒后清理
    public static final long TIME_CLEAR_LINE_DELAYED = 1500;

    //默认手势密码圆圈半径，单位dp
    public static final float radius = 30;

    //默认两个手势密码圆圈之前的距离，单位dp，若<=0则按屏幕宽度均分
    public static final float blockWidth = 50;

}
