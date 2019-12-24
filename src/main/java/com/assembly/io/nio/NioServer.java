package com.assembly.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName NioServer
 * @Description TODO
 * @Author liulei33
 * @Time 2019/12/23 20:35
 */
public class NioServer {
    /**
     * Nio需要准备两个组件
     * 1、大堂经理
     * 2、缓冲区
     */
    private Selector selector;
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    private int port = 8080;

    public NioServer(int port) throws IOException {
        this.port = port;
        //初始化serversocket
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        //注册ip、端口
        socketChannel.bind(new InetSocketAddress(this.port));
        //Nio为了兼容Bio，Nio模型默认采用阻塞模式
        socketChannel.configureBlocking(false);
        //大堂经理准备就绪
        selector = Selector.open();
        //开始营业
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void listen() throws IOException {
        System.out.println("listen on : " + port);
        //轮询
        while (true){
            /**
             * NIO的非阻塞体现在这里
             * 1、当客户端连接时会收到一个accept的key
             * 2、服务端将该key设置为readable
             * 3、服务端等待所有客户端请求（非阻塞）
             * 4、刚连接的客户端flush数据后服务端收到readable的请求进行处理
             *
             *
             * 个人理解
             * 将连接和数据发送flush隔离开来，BIO连接上后就必须等待数据准备、发送，
             * 而NIO连接上后，可以处理其他的连接请求，各客户端连接后数据准备完毕flush后，
             * 服务端再进行处理，服务端对连接设置了状态用来处理
             */
            int selectNum = selector.select();//没有连接时阻塞进程
            System.out.println("selectNum " + selectNum);
            //获取客户集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            //同步体现在此，每次只能拿一个key进行处理，每次只能处理一种状态
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                //业务处理
                work(key);
            }
        }
    }

    public void work(SelectionKey key) throws IOException {
        if(key.isAcceptable()){
            ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();
            SocketChannel accept = socketChannel.accept();
            accept.configureBlocking(false);
            accept.register(selector, SelectionKey.OP_READ);
        }else if(key.isReadable()){
            SocketChannel socketChannel = (SocketChannel)key.channel();
            int len = socketChannel.read(buffer);
            if(len > 0){
                buffer.flip();
                String msg = new String(buffer.array(),0,len);
                socketChannel.register(selector, SelectionKey.OP_WRITE);
                key.attach("reply:" + msg);
                System.out.println("receive msg :" + msg);
            }
        }else if(key.isWritable()){
            SocketChannel socketChannel = (SocketChannel)key.channel();
            socketChannel.write(ByteBuffer.wrap(key.attachment().toString().getBytes()));
            socketChannel.close();
        }
    }

    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer(8888);
        nioServer.listen();
    }
}
