package com.fafa.visitor.vo;

import java.util.Map;

/**
 * Created by ZhouBing on 2017/7/21.
 */
public class WSResult {

    private int resultCode;
    private Map<String,Object> resultValues;


    public WSResult() {
    }

    public WSResult(int resultCode, Map<String, Object> resultValues) {
        this.resultCode = resultCode;
        this.resultValues = resultValues;
    }

    public int getResultCode() {
        return resultCode;
    }

    public Map<String, Object> getResultValues() {
        return resultValues;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setResultValues(Map<String, Object> resultValues) {
        this.resultValues = resultValues;
    }
}
