package com.fafa.visitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.InputType;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.fafa.visitor.util.*;
import com.fafa.visitor.vo.Result;

import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {

    private final int BARCODE_CLEAR = 0;//清除barCode
    private final int ClOSE_SOFT_KEY_BOARD = 1;//关闭软盘
    private final int SHOW_RESPONSE = 2;//显示response
    private final int BARCODE_VISIBLE = 3;//barCode 可见
    private final int BARCODE_INVISIBLE = 4;//barCode 不可见
    private final int SHOW_MESSAGE_LONG = 5;//显示消息

    private final SimpleDateFormat LONG_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final String SERVERPATH = "http://172.28.167.6:6160";//UTC
//    private final String SERVERPATH = "http://demo.fafa.com.cn:6160";//fafaDemo
//    private final String SERVERPATH = "http://192.168.1.109:6160";//localhost
    private TextView register;
    private TextView pinInput;
    private EditText barCode;

    private String lastBarCode = "";//上一次扫描的二维码


    //Usb打印机
    private UsbPrintUtil usbPrintUtil = new UsbPrintUtil(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BARCODE_CLEAR:
                    barCode.setText("");
                    System.out.println("barCode 清空");
                    break;
                case ClOSE_SOFT_KEY_BOARD:
                    break;
                case SHOW_RESPONSE:
                    Toast.makeText(MainActivity.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_MESSAGE_LONG:
                    Toast.makeText(MainActivity.this,msg.obj.toString(),Toast.LENGTH_LONG).show();
                    break;
                case BARCODE_VISIBLE:
                    barCode.setVisibility(View.VISIBLE);
                    break;
                case BARCODE_INVISIBLE:
                    barCode.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };

    private void initEvent() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MainActivity.this,VisitorRegistActivity.class);
                startActivity(intent);

            }
        });

        pinInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handler.sendEmptyMessage(BARCODE_INVISIBLE);

                View view = View.inflate(MainActivity.this,R.layout.alert_dialog_layout,null);
                RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.alert_dialog_container);
                final EditText editText = new EditText(MainActivity.this);
                editText.setWidth(200);
                editText.setTextSize(30);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                relativeLayout.addView(editText);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                TextView tv = new TextView(MainActivity.this);
                tv.setText("请输入邀请码");    //内容
                tv.setTextSize(34);//字体大小
                tv.setPadding(30, 20, 10, 10);//位置
                tv.setTextColor(Color.parseColor("#000000"));//颜色
                builder.setCustomTitle(tv);//不是setTitle()
                builder.setView(view);
                builder.setNegativeButton("Cancel(取消)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.sendEmptyMessage(BARCODE_VISIBLE);
                    }
                });
                builder.setPositiveButton("Submit(确认)", null);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(34);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(34);

                WindowManager m = getWindowManager();
                Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
                System.out.println("width:"+d.getWidth());
                System.out.println("height:"+d.getHeight());
                android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
                p.height = (int) (d.getHeight() * 0.7);   //高度设置为屏幕的0.7
                p.width = (int) (d.getWidth() * 0.7);    //宽度设置为屏幕的0.7
                dialog.getWindow().setAttributes(p);     //设置生效
//980518

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //不为空校验
                        if(editText.getText()!=null&&!"".equals(editText.getText().toString().trim())){

                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try{

                                    Result result = HttpUtil.get(SERVERPATH+"/fengqi/reserve/detailByPinCode/"+editText.getText().toString());
                                    if(result!=null&&result.getSuccess()){
                                            try{
                                                Map<String,String> parameters = new HashMap<String,String>();

                                                parameters.put("date",LONG_DATE.format(new Date()));

                                                parameters.put("visitEnglishSurname",result.getResultValues().get("visitEnglishSurname").toString());
                                                parameters.put("visitEnglishFirstName",result.getResultValues().get("visitEnglishFirstName").toString());
                                                parameters.put("visitChineseSurname",result.getResultValues().get("visitChineseSurname").toString());
                                                parameters.put("visitChineseFirstName",result.getResultValues().get("visitChineseFirstName").toString());
                                                parameters.put("visitMobile",result.getResultValues().get("visitMobile").toString());
                                                if(result.getResultValues().get("reason").toString().length()>7){
                                                    parameters.put("reason",result.getResultValues().get("reason").toString().substring(0,7)+"...");
                                                }else{
                                                    parameters.put("reason",result.getResultValues().get("reason").toString());
                                                }
                                                parameters.put("name",result.getResultValues().get("name").toString());
                                                parameters.put("pinCode",result.getResultValues().get("pinCode").toString());

                                                //parameters.put("visitCompany",result.getResultValues().get("visitCompany").toString());

                                                usbPrintUtil.print(parameters);

                                                Message message = new Message();
                                                message.what = SHOW_RESPONSE;
                                                message.obj = "正在打印,请稍等";
                                                handler.sendMessage(message);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                                Message message = new Message();
                                                message.what = SHOW_RESPONSE;
                                                message.obj = "打印设备异常";
                                                handler.sendMessage(message);
                                            }
                                        }else{
                                            Message message = new Message();
                                            message.what = SHOW_RESPONSE;
                                            message.obj = "请求异常";
                                            if(result!=null&&result.getResultValues().get("message")!=null){
                                                message.obj = result.getResultValues().get("message").toString();
                                            }
                                            handler.sendMessage(message);
                                        }
                                    }catch (Exception e){
                                        Message message = new Message();
                                        message.what = SHOW_MESSAGE_LONG;
                                        message.obj = "服务器访问异常";
                                        handler.sendMessage(message);
                                    }
                                    System.out.println();
                                }
                            }).start();
                            handler.sendEmptyMessage(BARCODE_VISIBLE);
                            dialog.dismiss();
                        }else{
                            Message message = new Message();
                            message.what = SHOW_RESPONSE;
                            message.obj = "输入不能为空";
                            handler.sendMessage(message);
                            return;
                        }
                    }
                });
            }
        });
        TimerTask activeTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("我还活着");
            }
        };
        new Timer().schedule(activeTask, 0, 1000*60*5);
    }

    private void initView() {
        register = (TextView) findViewById(R.id.register);
        pinInput = (TextView) findViewById(R.id.pinInput);
        barCode = (EditText) findViewById(R.id.barCode);
    }
}
