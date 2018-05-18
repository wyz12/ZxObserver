package com.zxwl.zxobserver;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sks on 2018/5/3.
 */
public class MyService extends Service {

    private static final String TAG = "MainService";

    ConstraintLayout toucherLayout;
    WindowManager.LayoutParams params;
    WindowManager windowManager;
    private boolean isclick;
    ImageView imageButton1;

    //状态栏高度.
    int statusBarHeight = -1;
    private int x;
    private long endTime;
    private long startTime;
    private TextView shesemm;
    private TextView mysemm;

    //不与Activity进行绑定.
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i(TAG,"MainService Created");
        createToucher();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createToucher()
    {
        //赋值WindowManager&LayoutParam.
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;

        //设置悬浮窗口长宽数据.
        params.width = 300;
        params.height = 300;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        toucherLayout = (ConstraintLayout) inflater.inflate(R.layout.toucherlayout,null);
        //添加toucherlayout
        windowManager.addView(toucherLayout,params);


        //主动计算出当前View的宽高信息.
        toucherLayout.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);

        //用于检测状态栏高度.
        int resourceId = getResources().getIdentifier("status_bar_height","dimen","android");
        if (resourceId > 0)
        {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Log.i(TAG,"状态栏高度为:" + statusBarHeight);

        //浮动窗口按钮.
        imageButton1 = (ImageView) toucherLayout.findViewById(R.id.imageButton1);
        shesemm = (TextView) toucherLayout.findViewById(R.id.shesemm);
        mysemm = (TextView) toucherLayout.findViewById(R.id.mysemm);
//        Glide.with(MyService.this).load("http://61.164.108.157:8080/uploads/gif/20180503/1525335989611.gif").into(imageButton1);
        imageButton1.setOnClickListener(new View.OnClickListener() {
                        long[] hints = new long[2];
            @Override
            public void onClick(View v) {

                    Toast.makeText(MyService.this, "你好", Toast.LENGTH_SHORT).show();

                initSpeech(MyService.this);

//                    Intent intent = new Intent(MyService.this, Main2Activity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    MyService.this.startActivity(intent);


                Log.i(TAG,"点击了");
//                System.arraycopy(hints,1,hints,0,hints.length -1);
//                hints[hints.length -1] = SystemClock.uptimeMillis();
//                if (SystemClock.uptimeMillis() - hints[0] >= 700)
//                {
//                    Log.i(TAG,"要执行");
//                    Toast.makeText(MyService.this,"连续点击两次以退出",Toast.LENGTH_SHORT).show();
//                }else
//                {
//                    Log.i(TAG,"即将关闭");
//                    stopSelf();
//                }
            }
        });

        imageButton1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                params.x = (int) event.getRawX() - 150;
                params.y = (int) event.getRawY() - 150 - statusBarHeight;
                windowManager.updateViewLayout(toucherLayout,params);
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        isclick = false;//当按下的时候设置isclick为false，具体原因看后边的讲解
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isclick = true;//当按钮被移动的时候设置isclick为true
                        break;
                    case MotionEvent.ACTION_UP:
                        endTime = System.currentTimeMillis();
                        //当从点击到弹起小于半秒的时候,则判断为点击,如果超过则不响应点击事件
                        if ((endTime - startTime) > 0.1 * 1000L) {
                            isclick = true;
                        } else {
                            isclick = false;
                        }
                        break;
                }
                return isclick;
            }



        });
    }

    @Override
    public void onDestroy()
    {
        if (imageButton1 != null)
        {
            windowManager.removeView(toucherLayout);
        }
        super.onDestroy();
    }



    public void initSpeech(final Context context) {
        //1.创建RecognizerDialog对象

        RecognizerDialog mDialog = new RecognizerDialog(context, null);
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                if (!isLast) {
                    //解析语音
                    final String result = parseVoice(recognizerResult.getResultString());

                    Log.e("TTTTTT",result);
                    mysemm.setText(result);
                    if(result==null){
                        return;
                    }



                    RequestBody body = new FormBody.Builder()
                            .add("key","71ddab791d8f4289998eda33fe938318")
                            .add("info",result)
                            .add("userid",1+"")
                            .build();

                    OkHttp.getInstance().sendPost("http://www.tuling123.com/openapi/api", body, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String string = response.body().string();
                            shesemm.setText(string);

                        }
                    });
                }
            }

            @Override
            public void onError(SpeechError speechError) {

            }
        });
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    /**
     * 解析语音json
     */
    public String parseVoice(String resultString) {
        Gson gson = new Gson();
        Main2Activity.Voice voiceBean = gson.fromJson(resultString, Main2Activity.Voice.class);

        StringBuffer sb = new StringBuffer();
        ArrayList<Main2Activity.Voice.WSBean> ws = voiceBean.ws;
        for (Main2Activity.Voice.WSBean wsBean : ws) {
            String word = wsBean.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }

    /**
     * 语音对象封装
     */
    public class Voice {

        public ArrayList<Main2Activity.Voice.WSBean> ws;

        public class WSBean {
            public ArrayList<Main2Activity.Voice.CWBean> cw;
        }

        public class CWBean {
            public String w;
        }
    }
}
