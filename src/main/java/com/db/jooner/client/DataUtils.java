package com.db.jooner.client;

/**
 * 〈一句话功能简述〉<br>
 *
 * @author 未绪
 * @time 2018/5/4 10:53
 */
public class DataUtils {
    public static String EXIT = "EXIT";
    public static String QUIT = "QUIT";
    public static boolean isExit(String command){
        if(EXIT.equalsIgnoreCase(command.trim())||QUIT.equalsIgnoreCase(command.trim())){
            return true;
        }
        return false;
    }
}
