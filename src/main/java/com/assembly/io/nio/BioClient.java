package com.assembly.io.nio;

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
        os.close();
    }

    public static void main(String[] args) throws IOException {
        BioClient bioClient = new BioClient("localhost", 8888);
        String msg = UUID.randomUUID().toString();
        bioClient.sendMsg(msg.getBytes());
    }
}
