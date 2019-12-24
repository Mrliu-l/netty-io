package com.assembly.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

/**
 * @ClassName BioServer
 * @Description TODO
 * @Author liulei33
 * @Time 2019/12/23 18:21
 */
public class BioServer {

    private ServerSocket serverSocket;

    public BioServer(int port){
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("服务启动成功:" + serverSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() throws IOException {
        while (true){
            Socket accept = serverSocket.accept();
            InputStream is = accept.getInputStream();
            byte[] buff = new byte[2048];
            int len = is.read(buff);
            if(len > 0){
                String msg = new String(buff,0,len);
                System.out.println("服务的收到：" + msg);
            }

            OutputStream os = accept.getOutputStream();
            String rtnMsg = UUID.randomUUID().toString();
            os.write(rtnMsg.getBytes());
            os.flush();
        }
    }

    public static void main(String[] args) throws IOException {
        BioServer bioServer = new BioServer(8888);
        bioServer.listen();
    }
}
