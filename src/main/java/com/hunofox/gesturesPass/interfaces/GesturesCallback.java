package com.hunofox.gesturesPass.interfaces;

/**
 * 项目名称：GesturesPass
 * 项目作者：胡玉君
 * 创建日期：2017/12/7 19:44.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public interface GesturesCallback {


    //绘制完成
    void onGesturesFinished(String inputPassword);

    //绘制不合法，如实际连接点的个数不满足最少连接点的个数
    void onGesturesInvalidate(String inputPassword);

    //正在绘制
    void onGesturesInput(String inputPassword);

}
