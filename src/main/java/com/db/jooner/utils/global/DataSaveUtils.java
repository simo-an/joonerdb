package com.db.jooner.utils.global;

import com.alibaba.fastjson.JSONObject;
import com.db.jooner.common.ConfigInfo;
import com.db.jooner.object.DataInfo;
import com.db.jooner.object.JoonerObject;
import com.db.jooner.utils.DefaultInfoUtils;
import com.db.jooner.utils.JoonerTypeUtils;
import com.db.jooner.utils.JsonUtils;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.*;
import java.util.*;

/**
 * 〈后台数据持久化工具类〉<br>
 *
 * @author 未绪
 * @time 2018/5/3 20:53
 */
public class DataSaveUtils {
    // 客户端对数据库的操作次数 默认是 0 次
    public static int OPERATION_TIMES = 0;
    // 上一次数据持久化的时间
    public static Date LATE_PERSISTENCE_DATE = null;

    /**
     * 数据持久化的类
     */
public static void dataSave(HashMap<Integer, Integer> saveMap) {
    for (Map.Entry<Integer, Integer> entry : saveMap.entrySet()) {
        int time = entry.getKey();       //时间
        int operation = entry.getValue();     //操作次数
        long timeInterval = (new Date()).getTime() - DataSaveUtils.LATE_PERSISTENCE_DATE.getTime();
        if (timeInterval >= time && DataSaveUtils.OPERATION_TIMES >= operation) {
            doDataSave();
            // 初始化时间
            DataSaveUtils.OPERATION_TIMES = 0;
            DataSaveUtils.LATE_PERSISTENCE_DATE = new Date();
            break;
        }
    }
}

    public static void doDataSave() {
        // 初始化JDB持久化文件
        try {
            DataSaveUtils.initPersistenceFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 数据存储的元素
        HashMap<String, JoonerObject<Object>> joonerObject = DataInfo.getJoonerInstance();
        System.out.println("执行数据持久化");
        for (Map.Entry<String, JoonerObject<Object>> entry : joonerObject.entrySet()) {
            String key = entry.getKey();
            JoonerObject<Object> valueObject = entry.getValue();
            switch (valueObject.getType()) {
                case JoonerTypeUtils.JOONER_TYPE_STR:
                    //持久化 STR 类型
                    DataSaveUtils.persistenceStr(key, valueObject.getObj().toString());
                    break;
                case JoonerTypeUtils.JOONER_TYPE_LIST:
                    //持久化LIST类型
                    DataSaveUtils.persistenceList(key, (List) valueObject.getObj());
                    break;
                case JoonerTypeUtils.JOONER_TYPE_SET:
                    //持久化SET类型
                    DataSaveUtils.persistenceSet(key, (Set) valueObject.getObj());
                    break;
                case JoonerTypeUtils.JOONER_TYPE_MAP:
                    //持久化MAP类型
                    DataSaveUtils.persistenceMap(key, (Map) valueObject.getObj());
                    break;
                case JoonerTypeUtils.JOONER_TYPE_JSON:
                    //持久化JSON类型
                    DataSaveUtils.persistenceJson(key, valueObject.getObj());
                    break;
                default:
                    break;
            }
            if (valueObject.getTtl() != DefaultInfoUtils.DEFAULT_TTL) {
                DataSaveUtils.persistenceTTL(key, valueObject.getTtl());
            }
        }
    }

    /**
     * 持久化 STR 类型数据
     */
    private static void persistenceStr(String key, String value) {
        String command = JoonerKeyUtils.STR_SET + " " + key + " \"" + value + "\"";

        persistenceToJdb(command);
    }

    /**
     * 持久化 LIST 类型数据
     */
    private static void persistenceList(String key, List valueList) {
        String command = JoonerKeyUtils.LISTL_PUSH + " " + key;
        for (int i = 0; i < valueList.size(); i++) {
            command += " " + "\"" + valueList.get(i) + "\"";
        }

        persistenceToJdb(command);
    }

    /**
     * 持久化 SET 类型数据
     */
    private static void persistenceSet(String key, Set valueSet) {
        String command = JoonerKeyUtils.SET_ADD + " " + key;
        for (Object value : valueSet) {
            command += " \"" + value + "\"";
        }
        persistenceToJdb(command);
    }

    /**
     * 持久化 MAP 类型数据
     */
    private static void persistenceMap(String key, Map valueMap) {
        String command = JoonerKeyUtils.MAP_PUT + " " + key;
        for (Object object : valueMap.entrySet()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) object;
            command += " \"" + entry.getKey() + "\" \"" + entry.getValue() + "\"";
        }
        persistenceToJdb(command);
    }

    /**
     * 持久化 JSON 类型数据
     */
    private static void persistenceJson(String key, Object valueJson) {
        String command = JoonerKeyUtils.JSON_SET + " " + key + " ";
        Map valueMap = (Map) valueJson;
        JSONObject jsonObject = new JSONObject(valueMap);
        command += jsonObject.toString();
        persistenceToJdb(command);
    }

    /**
     * 设置相应的过期时间
     *
     * @param key
     * @param time 过期时间 单位 秒
     */
    private static void persistenceTTL(String key, long time) {
        time -= (new Date().getTime());
        String command = JoonerKeyUtils.GLOBAL_SETTTL + " " + key + " " + (time / 1000);
        persistenceToJdb(command);
    }

    /**
     * 将相关的命令持久化到JDB文件中去
     *
     * @param command
     */
    private static void persistenceToJdb(String command) {
        System.out.println(command);
        File rdb = new File(ConfigInfo.dbFileName);
        if (!rdb.exists()) {      // 如果RDB文件不存在，则创建一个新的文件
            try {
                rdb.createNewFile();//创建新文件
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 执行存储操作
        try {
            FileWriter fileWriter = new FileWriter(rdb, true);
            fileWriter.write(command + '\n');
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initPersistenceFile() throws IOException {
        File rdb = new File(ConfigInfo.dbFileName);
        rdb.delete();
        rdb.createNewFile();//创建新文件
    }

    /**
     * 保存指定内容的数据到持久化文件中去
     *
     * @param content
     */
    public static void doDataSave(String content) throws IOException {
        System.out.println(ConfigInfo.dbFileName + ":" + content);
        File rdb = new File(ConfigInfo.dbFileName);
        if (!rdb.exists()) {      // 如果RDB文件不存在，则创建一个新的文件
            try {
                rdb.createNewFile();//创建新文件
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 执行存储操作
        FileWriter fileWriter = new FileWriter(rdb, true);
        fileWriter.write(content + '\n');
        fileWriter.flush();
        fileWriter.close();
    }

    public static String getDBFileContent() throws IOException {
        File rdb = new File(ConfigInfo.dbFileName);
        if (!rdb.exists()) {      // 如果RDB文件不存在，则创建一个新的文件
            try {
                rdb.createNewFile();//创建新文件
            } catch (IOException e) {
                e.printStackTrace();
            }
            return DefaultInfoUtils.DEFAULT_EMPTY;
        }
        FileReader reader = new FileReader(rdb);//定义一个fileReader对象，用来初始化BufferedReader
        BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
        StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
        String tmp;
        while ((tmp = bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
            sb.append(tmp + "\n");//将读取的字符串添加换行符后累加存放在缓存中
        }
        bReader.close();
        String content = sb.toString();
        return content;
    }

    public static void deleteDBFile() throws IOException {
        File rdb = new File(ConfigInfo.dbFileName);
        if (rdb.exists()) {
            rdb.delete();
        }
    }
}
