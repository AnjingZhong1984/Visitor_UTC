package com.fafa.visitor.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.*;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Zhou Bing
 * <p>
 * 2017/10/19.
 */
public class UsbPrintUtil {

    private Context context;
    private UsbDevice device;
    private static UsbManager mUsbManager;
    private static PendingIntent mPermissionIntent;
    private static UsbDeviceConnection mConnection;
    private static  UsbEndpoint mEndpointIntr;

    public UsbPrintUtil(Context context){
        this.context = context;
    }

    /**
     * 打印设备是否打开成功
     * @return
     */
    private boolean openUsbDevice() {
        //before open usb device
        //should try to get usb permission
        tryGetUsbPermission();

        UsbInterface intf = null;
        UsbEndpoint ep = null;

        int InterfaceCount = device.getInterfaceCount();
        int j;

        for (j = 0; j < InterfaceCount; j++) {
            int i;

            intf = device.getInterface(j);
            Log.i("test", "接口是:" + j + "类是:" + intf.getInterfaceClass());
            if (intf.getInterfaceClass() == 7) {
                int UsbEndpointCount = intf.getEndpointCount();
                for (i = 0; i < UsbEndpointCount; i++) {
                    ep = intf.getEndpoint(i);
                    Log.i("test", "端点是:" + i + "方向是:" + ep.getDirection() + "类型是:" + ep.getType());
                    if (ep.getDirection() == 0 && ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        Log.i("test", "接口是:" + j + "端点是:" + i);
                        break;
                    }
                }
                if (i != UsbEndpointCount) {
                    break;
                }
            }
        }
        if (j == InterfaceCount) {
            Log.i("test", "没有打印机接口");
            return false;
        }

        mEndpointIntr = ep;
        UsbDeviceConnection connection = mUsbManager.openDevice(device);

        if (connection != null && connection.claimInterface(intf, true)) {
            Log.i("test", "打开成功！ ");
            mConnection = connection;
            return true;
        } else {
            Log.i("test", "打开失败！ ");
            mConnection = null;
            return false;
        }
    }

    public void print(final Map<String,String> parameters){
        boolean flag =  openUsbDevice();
        usbPrint(parameters);
        close();
        if(!flag){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    openUsbDevice();
                    usbPrint(parameters);
                    close();
                }
            },5000);
        }
    }

    private void usbPrint(Map<String,String> parameters){
        sendCommand(mEndpointIntr,mConnection,PrinterCmdUtils.init_printer());
        StringBuffer data = new StringBuffer();
        data.append("{D1000,0900,0550|}");//设置纸张尺寸，标签间隔距离、标签宽度、标签高度
        data.append("{C|}");//清除缓存区数据
        data.append("{U2;0030|}");//向后走纸到打印位置
        data.append("{AX;+000,+000,+00|}");//定位打印原点x
        data.append("{AY;+00,0|}");//定位打印原点y
        data.append("{PC001;0900,0300,15,15,r,22,B|}");//打印数据格式
        data.append("{RC001; 来访时间(Time)     "+parameters.get("date")+"|}");//打印数据
        data.append("{PC002;0900,0220,15,15,r,22,B|}");//打印数据格式
        data.append("{RC002; 访客姓名(Name)     "+parameters.get("visitEnglishFirstName")+" "+parameters.get("visitEnglishSurname")+"|}");//打印数据
        data.append("{PC003;0900,0140,15,15,r,22,B|}");//打印数据格式
        data.append("{RC003; 被访对象(Employee) "+parameters.get("name")+"|}");//打印数据
        data.append("{PC004;0900,0060,15,15,r,22,B|}");//打印数据格式
        data.append("{RC004; 凭证号(Pin)        "+parameters.get("pinCode")+"|}");//打印数据
        data.append("{XS;I,0001,0000C1010|}");//打印设置

        try {
             sendCommand(mEndpointIntr,mConnection,data.toString().getBytes("GB2312"));
            context.unregisterReceiver(mUsbPermissionActionReceiver);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String Char(int asciiCode)
    {
        StringBuffer s = new StringBuffer();
        s.append((char)asciiCode);
        return  s.toString();
    }

    private void sendCommand(UsbEndpoint mEndpointIntr,UsbDeviceConnection mConnection,byte[] content){
        synchronized (this) {
            int len = -1;
            if (mConnection != null) {
                len = mConnection.bulkTransfer(mEndpointIntr, content, content.length, 10000);
            }

            if (len < 0) {
                System.out.println("发送失败！ " + len);
            } else {
                System.out.println("发送" + len + "字节数据");
            }
        }
    }
    private void close(){

        if (mConnection != null) {
            mConnection.close();
            mConnection = null;
        }
        if(mPermissionIntent !=null ){
            mPermissionIntent.cancel();
            mPermissionIntent = null;
        }
        if(mEndpointIntr !=null){
            mEndpointIntr = null;
        }
        mUsbManager = null;
        System.out.println("关闭");
    }

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private void tryGetUsbPermission() {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(mUsbPermissionActionReceiver, filter);

        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);

        //here do emulation to ask all connected usb device for permission
        Collection<UsbDevice> collections = mUsbManager.getDeviceList().values();
        for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
            if(usbDevice.getVendorId()==2214){
                device = usbDevice;
            }
            System.out.println(usbDevice.getVendorId()+" if has permission "+mUsbManager.hasPermission(usbDevice));
            //add some conditional check if necessary
            //if(isWeCaredUsbDevice(usbDevice)){
            if (mUsbManager.hasPermission(usbDevice)) {
                //if has already got permission, just goto connect it
                //that means: user has choose yes for your previously popup window asking for grant perssion for this usb device
                //and also choose option: not ask again
                afterGetUsbPermission(usbDevice);
            } else {
                //this line will let android popup window, ask user whether to allow this app to have permission to operate this usb device
                mUsbManager.requestPermission(usbDevice, mPermissionIntent);
            }
            //}
        }
    }


    private void afterGetUsbPermission(UsbDevice usbDevice) {
        //call method to set up device communication
        System.out.println(String.valueOf("Got permission for usb device: " + usbDevice));
        System.out.println(String.valueOf("Found USB device: VID=" + usbDevice.getVendorId() + " PID=" + usbDevice.getProductId()));

        doYourOpenUsbDevice(usbDevice);
    }

    private void doYourOpenUsbDevice(UsbDevice usbDevice) {
        //now follow line will NOT show: User has not given permission to device UsbDevice
        UsbDeviceConnection connection = mUsbManager.openDevice(usbDevice);
        //add your operation code here

        System.out.println(usbDevice.getVendorId()+"~~~~~~~~~~~~~");
    }

    private final BroadcastReceiver mUsbPermissionActionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        //user choose YES for your previously popup window asking for grant perssion for this usb device
                        if (null != usbDevice) {
                            afterGetUsbPermission(usbDevice);
                        }
                    } else {
                        //user choose NO for your previously popup window asking for grant perssion for this usb device
                        System.out.println(String.valueOf("Permission denied for device" + usbDevice));
                    }
                }
            }
        }
    };
}
