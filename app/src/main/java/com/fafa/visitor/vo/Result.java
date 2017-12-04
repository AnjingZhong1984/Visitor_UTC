package com.fafa.visitor.vo;

import java.util.Map;

/**
 * Created by Zhou Bing
 * <p>
 * 2017/10/15.
 */
public class Result {

    private Boolean success;
    private Map<String,Object> resultValues;

    public Result() {
    }

    public Result(Boolean success,Map<String, Object> resultValues) {
        this.resultValues = resultValues;
        this.success = success;
    }

    public void setResultValues(Map<String, Object> resultValues) {
        this.resultValues = resultValues;
    }

    public Map<String, Object> getResultValues() {
        return resultValues;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return success;
    }

}
