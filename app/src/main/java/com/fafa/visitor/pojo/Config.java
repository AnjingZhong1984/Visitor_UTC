package com.fafa.visitor.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZhouBing on 2017/7/26.
 *
 * SQLite  --  T_CONFIG
 *
 * 配置项
 */
public class Config {


    /**
     * 基础数据更新时间
     * T_CARDINFO_UPDATE_DATE
     * T_DATELIMITRULEINFO_UPDATE_DATE
     * T_POSINfO_UPDATE_DATE
     * T_USERINFO_UPDATE_DATE
     * T_VIPGROUPINFO_UPDATE_DATE
     * T_VIPINFO_UPDATE_DATE
     */

    private Map<String,String> properties = new HashMap<String,String>();

    public Map<String, String> getProperties() {
        return properties;
    }
}
