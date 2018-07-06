package com.db.jooner.client;

import com.db.jooner.utils.DefaultInfoUtils;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * 〈Jooner的客户端〉<br>
 *
 * @author 未绪
 * @time 2018/2/16 12:42
 */
public class JoonerClientMain {

    private Socket socket;

    public JoonerClientMain(String[] args) {
        try {
            socket = new Socket("localhost", Integer.parseInt(args[1]));
            DefaultInfoUtils.DEFAULT_HOST_PORT = Integer.parseInt(args[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect() {

        try {
            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            PrintWriter printWriter = new PrintWriter(outputStreamWriter, true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print(DefaultInfoUtils.DEFAULT_HOST_PORT + "> ");
                String command = scanner.nextLine();    //读取一条命令
                if (DataUtils.isExit(command)) {
                    break;
                }
                sendCmdAndGetResult(command, bufferedReader, printWriter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 向服务器发送数据并获得返回结果
     *
     * @param command
     * @param bufferedReader
     * @param printWriter
     * @throws IOException
     */
    public void sendCmdAndGetResult(String command, BufferedReader bufferedReader, PrintWriter printWriter) throws IOException {
        printWriter.println(command);
        String result = bufferedReader.readLine();
        System.out.println(DefaultInfoUtils.DEFAULT_HOST_PORT + "> " + result);
    }

    public static void main(String[] args) {

        JoonerClientMain client = new JoonerClientMain(args);
        client.connect();
    }

}
