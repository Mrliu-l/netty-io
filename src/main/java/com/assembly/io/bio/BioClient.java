package com.assembly.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * @ClassName BioClient
 * @Description TODO
 * @Author liulei33
 * @Time 2019/12/23 19:43
 */
public class BioClient {
    private static Socket client;

    public BioClient(String ip, int port) throws IOException {
        client = new Socket(ip,port);
    }

    public void sendMsg(byte[] msg) throws IOException {
        OutputStream os = client.getOutputStream();

        os.write(msg);
        os.flush();
//        os.close();
    }

    public static void main(String[] args) throws IOException {
        BioClient bioClient = new BioClient("localhost", 8888);
        String msg = UUID.randomUUID().toString();
        bioClient.sendMsg(msg.getBytes());

        //客户端只能发送或收取一条消息
        //在客户端inputstream或outstream进行close时，会关闭socket通道，所以正常使用情况下，客户端只能收或发一次，除非不close流操作
        InputStream is = client.getInputStream();
        byte[] rtnMsg = new byte[2048];
        int len = is.read(rtnMsg);
        msg = new String(rtnMsg,0,len);


        System.out.println("服务端返回msg：" + msg);
    }
}
