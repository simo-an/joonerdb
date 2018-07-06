package com.db.jooner.common;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 〈配置文件信息〉<br>
 *
 * @author 未绪
 * @time 2018/2/12 21:16
 */
public class ConfigInfo {

    //数据库文件路径
    public static String dbFileName;

    //每隔多少时间有多少次操作就进行一次数据的持久化
    public static HashMap<Integer, Integer> dataSave;

    //主机的IP
    public static String hostIp;

    //主机端口
    public static String hostPort;

    //过期键的定时检测时间
    public static int checkExpiredTime;

    //当前数据库的角色 MASTER SALVE
    public static String role;

    //如果当前数据库的角色是SLAVE，则配置该项 - 当前从库的主库
    public static String masterIp;    // 所属主机的IP
    public static String masterPort;    // 所属主机的端口rm

    public String getMasterIp() {
        return masterIp;
    }

    public void setMasterIp(String masterIp) {
        ConfigInfo.masterIp = masterIp;
    }

    public String getMasterPort() {
        return masterPort;
    }

    public void setMasterPort(String masterPort) {
        ConfigInfo.masterPort = masterPort;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        ConfigInfo.role = role;
    }

    public int getCheckExpiredTime() {
        return checkExpiredTime;
    }

    public void setCheckExpiredTime(int checkExpiredTime) {
        ConfigInfo.checkExpiredTime = checkExpiredTime;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }


    public String getDbFileName() {
        return dbFileName;
    }

    public void setDbFileName(String dbFileName) {
        this.dbFileName = dbFileName;
    }

    public HashMap<Integer, Integer> getDataSave() {
        return dataSave;
    }

    public void setDataSave(String[] datas) {
        if (this.dataSave == null) {
            this.dataSave = new HashMap<>();
        }
        this.dataSave.put(Integer.parseInt(datas[1]), Integer.parseInt(datas[2]));
    }



}
