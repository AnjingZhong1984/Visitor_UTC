package com.fafa.visitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.fafa.visitor.util.HttpUtil;
import com.fafa.visitor.vo.Result;
import org.apache.http.conn.HttpHostConnectException;

import java.text.SimpleDateFormat;
import java.util.*;

public class VisitorRegistActivity extends Activity {

    private EditText visitEnglishSurname;//来访人英文姓
    private EditText visitEnglishFirstName;//来访人英文名
    private EditText visitChineseSurname;//来访人中文姓
    private EditText visitChineseFirstName;//来访人中文名
    private EditText visitMobile;//来访人电话号码
    private EditText visitEmail;//来访人邮箱
    private Spinner visitNationality;//来访人国籍
    private EditText visitCompany;//来访人公司
    private Spinner visitBusiness;//来访人Business
    private Spinner visitLocation;//来访人Location
    private EditText audienceEmail;//被访人邮箱
    private EditText reason;//来访事由
    private EditText name;


    private TextView visitEnglishSurnameDesc;
    private TextView visitEnglishFirstNameDesc;
    private TextView visitMobileDesc;
    private TextView visitEmailDesc;
    private TextView visitNationalityDesc;
    private TextView reasonDesc;
    private TextView visitCompanyDesc;
    private TextView nameDesc;

    private TextView register;//登记
    private TextView back;//返回

    private RelativeLayout loading;

    private final int SHOW_MESSAGE = 0;//显示消息
    private final int LOADING_SHOW = 1;//显示加载
    private final int LOADING_HIDE = 2;//显示加载
    private final int FORM_ClEAR = 3;//清空表单
    private final int SHOW_MESSAGE_LONG = 4;//显示消息

    private final SimpleDateFormat LONG_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final String SERVERPATH = "http://10.176.16.16:6160";//UTC new
    //private final String SERVERPATH = "http://172.28.167.6:6160";//UTC old
    //private final String SERVERPATH = "http://demo.fafa.com.cn:6160";//fafaDemo
    //private final String SERVERPATH = "http://192.168.1.109:6160";//localhost

    private String protocol = "";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_MESSAGE:
                    Toast.makeText(VisitorRegistActivity.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_MESSAGE_LONG:
                    Toast.makeText(VisitorRegistActivity.this,msg.obj.toString(),Toast.LENGTH_LONG).show();
                    break;
                case LOADING_SHOW:
                    loading.setVisibility(View.VISIBLE);
                    break;
                case LOADING_HIDE:
                    loading.setVisibility(View.INVISIBLE);
                    break;
                case FORM_ClEAR:
                    visitEnglishSurname.setText("");
                    visitEnglishFirstName.setText("");
                    visitChineseSurname.setText("");
                    visitChineseFirstName.setText("");
                    visitMobile.setText("");
                    visitEmail.setText("");
                    visitCompany.setText("");
                    audienceEmail.setText("");
                    reason.setText("");
                    name.setText("");
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_regist);
        
        initView();
        initEvent();
    }

    private void initEvent() {

        //换行跳到下一个EditView
        /*visitName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_UNSPECIFIED){
                    visitCompany.requestFocus();
                }
                return true;
            }
        });*/
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visitEnglishSurname.getText()!=null&&!"".equals(visitEnglishSurname.getText().toString().trim())
                        &&visitEnglishFirstName.getText()!=null&&!"".equals(visitEnglishFirstName.getText().toString().trim())
                        &&visitMobile.getText()!=null&&!"".equals(visitMobile.getText().toString().trim())
                        &&visitEmail.getText()!=null&&!"".equals(visitEmail.getText().toString().trim())
                        &&reason.getText()!=null&&!"".equals(reason.getText().toString().trim())
                        && name.getText()!=null&&!"".equals(name.getText().toString().trim())
                        &&visitCompany.getText()!=null&&!"".equals(visitCompany.getText().toString().trim())
                        ){

                    AlertDialog.Builder builder = new AlertDialog.Builder(VisitorRegistActivity.this);

                    TextView tv = new TextView(VisitorRegistActivity.this);
                    tv.setPadding(20,20,20,20);
                    tv.setText(protocol);
                    ScrollView sv = new ScrollView(VisitorRegistActivity.this);
                    sv.addView(tv);
                    builder.setView(sv);
                    builder.setNegativeButton("Cancel(取消)", null);
                    builder.setPositiveButton("Agree(同意)", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.sendEmptyMessage(LOADING_SHOW);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final Map<String, String> params = new HashMap<String, String>();

                                    params.put("visitEnglishSurname",visitEnglishSurname.getText().toString());//来访人英文姓
                                    params.put("visitEnglishFirstName",visitEnglishFirstName.getText().toString());//来访人英文名
                                    params.put("visitChineseSurname",visitChineseSurname.getText().toString());//来访人中文姓
                                    params.put("visitChineseFirstName",visitChineseFirstName.getText().toString());//来访人中文名
                                    params.put("visitMobile",visitMobile.getText().toString());//来访人电话号码
                                    params.put("visitEmail",visitEmail.getText().toString());//来访人邮箱
                                    params.put("visitNationality",(String)visitNationality.getSelectedItem());//来访人国籍
                                    params.put("visitCompany",visitCompany.getText().toString());//来访人公司
                                    params.put("visitBusiness",(String)visitBusiness.getSelectedItem());//来访人Business
                                    params.put("visitLocation",(String)visitLocation.getSelectedItem());//来访人Location
                                    params.put("audienceEmail",audienceEmail.getText().toString());//被访人邮箱
                                    params.put("reason",reason.getText().toString());//来访事由
                                    params.put("name",name.getText().toString());

                                    params.put("visitTime",LONG_DATE.format(new Date()));//来访时间
                                    params.put("status","0");//是否进入，离开  0未进入,1进入,2已出来
                                    params.put("blacklist","1");//是否在白名单 1为在


                                    try{
                                        Result result = HttpUtil.post(params,SERVERPATH+"/fengqi/reserve/save");
                                        if(result!=null&&result.getSuccess()){
                                            Message message = new Message();
                                            message.what = SHOW_MESSAGE;
                                            message.obj = "预约成功";
                                            handler.sendMessage(message);

                                            handler.sendEmptyMessage(FORM_ClEAR);

                                        }else{
                                            Message message = new Message();
                                            message.what = SHOW_MESSAGE;
                                            message.obj = "请求异常";
                                            if(!result.getSuccess()&&result.getResultValues().get("message")!=null){
                                                message.obj = result.getResultValues().get("message").toString();
                                            }
                                            handler.sendMessage(message);
                                        }
                                        handler.sendEmptyMessage(LOADING_HIDE);
                                    }catch (Exception e){
                                        Message message = new Message();
                                        message.what = SHOW_MESSAGE_LONG;
                                        message.obj = "服务器访问异常";
                                        handler.sendMessage(message);
                                        handler.sendEmptyMessage(LOADING_HIDE);
                                    }
                                }
                            }).start();//这个start()方法不要忘记了
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    Message message = new Message();
                    message.what = SHOW_MESSAGE;
                    message.obj = "必填项不能为空";
                    handler.sendMessage(message);
                    return;
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        //visitName = (EditText) findViewById(R.id.visitName);

        visitEnglishSurname = (EditText) findViewById(R.id.visitEnglishSurname);//来访人英文姓
        visitEnglishFirstName = (EditText) findViewById(R.id.visitEnglishFirstName);//来访人英文名
        visitChineseSurname = (EditText) findViewById(R.id.visitChineseSurname);//来访人中文姓
        visitChineseFirstName = (EditText) findViewById(R.id.visitChineseFirstName);//来访人中文名
        visitMobile = (EditText) findViewById(R.id.visitMobile);//来访人电话号码
        visitEmail = (EditText) findViewById(R.id.visitEmail);//来访人邮箱
        visitNationality = (Spinner) findViewById(R.id.visitNationality);//来访人国籍
        visitCompany = (EditText) findViewById(R.id.visitCompany);//来访人公司
        visitBusiness = (Spinner) findViewById(R.id.visitBusiness);//来访人Business
        visitLocation = (Spinner) findViewById(R.id.visitLocation);//来访人Location
        audienceEmail = (EditText) findViewById(R.id.audienceEmail);//被访人邮箱
        reason = (EditText) findViewById(R.id.reason);//来访事由
        name = (EditText) findViewById(R.id.name);

        visitEnglishSurnameDesc = (TextView) findViewById(R.id.visitEnglishSurnameDesc);
        visitEnglishFirstNameDesc = (TextView) findViewById(R.id.visitEnglishFirstNameDesc);
        visitMobileDesc = (TextView) findViewById(R.id.visitMobileDesc);
        visitEmailDesc = (TextView) findViewById(R.id.visitEmailDesc);
        reasonDesc = (TextView) findViewById(R.id.reasonDesc);
        nameDesc = (TextView) findViewById(R.id.namelDesc);
        visitCompanyDesc = (TextView) findViewById(R.id.visitCompanyDesc);
        visitNationalityDesc = (TextView) findViewById(R.id.visitNationalityDesc);

        //必填项
        visitEnglishSurnameDesc.setText(Html.fromHtml(visitEnglishSurnameDesc.getText()+"<font color=\"#ff0000\">*</font>"));
        visitEnglishFirstNameDesc.setText(Html.fromHtml(visitEnglishFirstNameDesc.getText()+"<font color=\"#ff0000\">*</font>"));
        visitMobileDesc.setText(Html.fromHtml(visitMobileDesc.getText()+"<font color=\"#ff0000\">*</font>"));
        visitEmailDesc.setText(Html.fromHtml(visitEmailDesc.getText()+"<font color=\"#ff0000\">*</font>"));
        reasonDesc.setText(Html.fromHtml(reasonDesc.getText()+"<font color=\"#ff0000\">*</font>"));
        nameDesc.setText(Html.fromHtml(nameDesc.getText()+"<font color=\"#ff0000\">*</font>"));
        visitCompanyDesc.setText(Html.fromHtml(visitCompanyDesc.getText()+"<font color=\"#ff0000\">*</font>"));
        visitNationalityDesc.setText(Html.fromHtml(visitNationalityDesc.getText()+"<font color=\"#ff0000\">*</font>"));

        visitEnglishSurname.clearFocus();
        visitEnglishFirstName.clearFocus();
        visitChineseSurname.clearFocus();
        visitChineseFirstName.clearFocus();
        visitMobile.clearFocus();
        visitEmail.clearFocus();
        visitNationality.clearFocus();
        visitCompany.clearFocus();
        visitBusiness.clearFocus();
        visitLocation.clearFocus();
        audienceEmail.clearFocus();
        reason.clearFocus();
        name.clearFocus();

        register = (TextView) findViewById(R.id.register);
        back = (TextView) findViewById(R.id.back);

        loading = (RelativeLayout) findViewById(R.id.loading);


        //todo : 从服务器获取
        /*List<String> list = new ArrayList<String>();
        list.add("AAA");
        list.add("BBB");
        list.add("CCC");
        list.add("DDD");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,list);
        visitNationality.setAdapter(adapter);*/



        new Thread(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> params = new HashMap<String, String>();
                try{
                    System.out.println(SERVERPATH+"/fengqi/reserve/protocol?code=protocol");
                    Result result = HttpUtil.get(SERVERPATH+"/fengqi/reserve/protocol?code=protocol");
                    if(result!=null&&result.getSuccess()){

                        if(result.getResultValues().get("content")!=null){
                            protocol = result.getResultValues().get("content").toString();
                        }
                    }else{
                        Message message = new Message();
                        message.what = SHOW_MESSAGE_LONG;
                        message.obj = "无法获取协议";
                        handler.sendMessage(message);
                    }
                    handler.sendEmptyMessage(LOADING_HIDE);
                }catch (Exception e){
                    Message message = new Message();
                    message.what = SHOW_MESSAGE_LONG;
                    message.obj = "服务器访问异常";
                    handler.sendMessage(message);
                    handler.sendEmptyMessage(LOADING_HIDE);
                }
            }
        }).start();//这个start()方法不要忘记了

    }
}
