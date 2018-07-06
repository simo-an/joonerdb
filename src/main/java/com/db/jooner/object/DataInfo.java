package com.db.jooner.object;

import com.db.jooner.common.ClientInfo;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 〈全局的数据储存容器〉<br>
 *
 * @author 未绪
 * @time 2018/3/17 9:48
 */
public class DataInfo {
    private static HashMap<String, JoonerObject<Object>> joonerObjectHashMap = new HashMap<>();
    private static HashSet<ClientInfo> clientInfoHashSet = new HashSet<>();

    public static HashMap<String, JoonerObject<Object>> getJoonerInstance() {
        if (joonerObjectHashMap == null) {
            joonerObjectHashMap = new HashMap<>();
        }
        return joonerObjectHashMap;
    }

    public static HashSet<ClientInfo> getClientInfoInstance() {
        if (clientInfoHashSet == null) {
            clientInfoHashSet = new HashSet<>();
        }
        return clientInfoHashSet;
    }
}
