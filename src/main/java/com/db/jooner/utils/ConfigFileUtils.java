package com.db.jooner.utils;

import com.db.jooner.common.ConfigInfo;

import java.io.*;

/**
 * 〈配置文件工具类〉<br>
 *
 * @author 未绪
 * @time 2018/2/12 14:23
 */
public class ConfigFileUtils {

    private static String CONFIG_FILE_PATH = "src/main/res/jooner.conf";

    /**
     * <加载配置文件></>
     *
     * @author 未绪
     */
    public static boolean loadConfigFile(String configFilePath, ConfigInfo configInfo) throws IOException {
        FileInputStream fis = new FileInputStream("".equals(configFilePath) ? CONFIG_FILE_PATH : configFilePath);

        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"), 512);

        String line;   //保存配置文件的一行内容
        String[] configArray;   //将一行配置内容按照空格拆分成数组保存

        for (line = reader.readLine(); line != null; line = reader.readLine()) {
            //修剪字符串
            line = line.trim().replaceAll("\\s+", " ");
            if (isConfigContent(line)) {
                configArray = line.split(" ");
                setConfigInfo(configArray, configInfo);
            }
        }

        return true;
    }

    private static boolean isConfigContent(String line) {

        if ("".equals(line) || line.startsWith("#")) {
            return false;
        }

        return true;
    }

    /**
     * <将配置文件中欧个提取出来的数组加载到相应的配置文件的对象中去></>
     *
     * @author 未绪
     */
    public static void setConfigInfo(String[] configArray, ConfigInfo configInfo) {
        if (configInfo == null) {
            configInfo = new ConfigInfo();
        }
        switch (configArray[0]) {
            case ConfigInfoUtils.DB_FILE_NAME:
                configInfo.setDbFileName(configArray[1]);
                break;
            case ConfigInfoUtils.DATA_SAVE:
                configInfo.setDataSave( configArray);
                break;
            case ConfigInfoUtils.DB_HOST_IP:
                configInfo.setHostIp(configArray[1]);
                break;
            case ConfigInfoUtils.DB_HOST_PORT:
                configInfo.setHostPort(configArray[1]);
                DefaultInfoUtils.DEFAULT_HOST_PORT = Integer.parseInt(configArray[1]);
                break;
            case ConfigInfoUtils.EXPIRE_CHECK_TIME:
                configInfo.setCheckExpiredTime(Integer.parseInt(configArray[1]));
                break;
            case ConfigInfoUtils.SERVER_ROLE:
                configInfo.setRole(configArray[1]);
                break;
            case ConfigInfoUtils.SLAVE_OF:
                configInfo.setMasterIp(configArray[1]);
                configInfo.setMasterPort(configArray[2]);
                break;
            default:
                break;
        }
    }
}
