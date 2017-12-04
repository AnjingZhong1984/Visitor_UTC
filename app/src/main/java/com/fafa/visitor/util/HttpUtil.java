package com.fafa.visitor.util;

import com.fafa.visitor.vo.Result;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhou Bing
 * <p>
 * 2017/10/15.
 */
public class HttpUtil {


    public static Result post(final Map<String, String> params, String url) {

        List<NameValuePair> list = new ArrayList<NameValuePair>();      //封装请求体参数
        if ((params != null) && !params.isEmpty()) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
        }
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");    //对请求体参数进行URL编码
            HttpPost httpPost = new HttpPost(url);           //创建一个POST方式的HttpRequest对象
            httpPost.setEntity(entity);                       //设置POST方式的请求体
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);                      //执行POST请求
            int reponseCode = httpResponse.getStatusLine().getStatusCode();            //获得服务器的响应码
            if (reponseCode == HttpStatus.SC_OK) {
                String resultData = EntityUtils.toString(httpResponse.getEntity());    //获得服务器的响应内容

                Result result = JSNO2Result.cast(resultData);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Result get(String url) {

        HttpGet httpGet = new HttpGet(url);                           //创建一个GET方式的HttpRequest对象
        DefaultHttpClient httpClient = new DefaultHttpClient();        //创建一个默认的HTTP客户端
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);               //执行GET方式的HTTP请求
            int reponseCode = httpResponse.getStatusLine().getStatusCode();        //获得服务器的响应码
            if (reponseCode == HttpStatus.SC_OK) {
                String resultData = EntityUtils.toString(httpResponse.getEntity());
                Result result = JSNO2Result.cast(resultData);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
