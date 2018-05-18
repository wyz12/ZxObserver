package com.zxwl.zxobserver;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by sks on 2018/5/4.
 */

public class SunnyGLRender implements GLSurfaceView.Renderer {

    public float mAngle;
    float one = 0.5f;
    private FloatBuffer triggerBuffer2 = BufferUtil.floatToBuffer(new float[]{
            0, one, 0, //上顶点
            -one, -one, 0, //左下点
            one, -one, 0,}); //右下点
    private FloatBuffer triggerBuffer1 = BufferUtil.floatToBuffer(new float[]{
            0, one, 0, //上顶点
            -one, -one, 0, //左下点
            one, -one, 0,}); //右下点
    private float[] mTriangleArray = {
            // X, Y, Z 这是一个等边三角形
            -0.5f, -0.25f, 0,
            0.5f, -0.25f, 0,
            0.0f, 0.559016994f, 0};

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        //GLES30:为OpenGL ES2.0版本,相应的
        //GLES30:OpenGL ES3.0
        //黑色背景
        gl.glClearColor(0.0f, 0f, 1f, 0.5f);
        //gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        // 启用顶点数组（否则glDrawArrays不起作用）
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //mProgram = GLES30.glCreateProgram();
        //Log.e("zcxgl", "Could not link program: mProgram = "+mProgram);
        Log.e("zcxgl", "onSurfaceChanged");
        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION); // 设置当前矩阵为投影矩阵
        gl.glLoadIdentity(); // 重置矩阵为初始值
        //gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7); // 根据长宽比设置投影矩阵
        gl.glFrustumf(-ratio, ratio, -1, 1, 5, 6);

    }

    private FloatBuffer colorBuffer2 = BufferUtil.floatToBuffer(new float[]{
            one, 0, 0, one,
            0, one, 0, one,
            0, 0, one, one,
    });

    @Override
    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        Log.d("zcxgl", "onDrawFramew");
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);

        /************ 启用MODELVIEW模式，并使用GLU.gluLookAt()来设置视点 ***************/
        // 设置当前矩阵为模型视图模式
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity(); // reset the matrix to its default state
        // 设置视点
        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        /*****************************************/

        long time = SystemClock.uptimeMillis() % 4000L;
        mAngle = 0.090f * ((int) time);
        // 重置当前的模型观察矩阵
        gl.glLoadIdentity();
        // 移动绘图原点的坐标与上面的语句连用就相当于设置新的绘图远点坐标，
        //gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);//后面的1-10是指图像的1-10层，
        // 图像所处层次越大，在屏幕上显示就越小。默认为（0，0，1),
        // 左移 1.5 单位，并移入屏幕 6.0。
        gl.glTranslatef(0f, 0.0f, -5.0f);

        gl.glRotatef(mAngle, 0.0f, 0.0f, 1.0f);
        //启用平滑着色
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);//
        //gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);//可以直接设置绘图的单调颜色
        // 设置三角形点
        // gl.glVertexPointer(3, GL10.GL_FIXED, 0, triggerBuffer);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triggerBuffer2);
        //设置平滑着色的颜色矩阵
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer2);//都是一维矩阵，因此第一个参数就是表示一个颜色的长度表示
        //绘制
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
        // 关闭颜色平滑着色设置
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        //gl.glFinish();

    }

    static class BufferUtil {
        public static FloatBuffer mBuffer;

        public static FloatBuffer floatToBuffer(float[] a) {
            // 先初始化buffer，数组的长度*4，因为一个float占4个字节
            ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 4);
            // 数组排序用nativeOrder
            mbb.order(ByteOrder.nativeOrder());
            mBuffer = mbb.asFloatBuffer();
            mBuffer.put(a);
            mBuffer.position(0);
            return mBuffer;
        }
    }
}
