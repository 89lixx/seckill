package com.lxx.seckill.common.webSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author: lixiuxiang3
 * @Date: 2023/7/21 14:58
 * @Version: 1.0
 */
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {
    private final static Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收userId
    private String userId="";

    /**
     * 连接建立成功调用的方法
     * */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        webSocketSet.add(this); //加入set中
        addOnlineCount();
        log.info("有新窗口开始监听:"+userId+",当前在线人数为" + getOnlineCount());
        this.userId = userId;

        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }


    /**
     * 收到客户端消息后调用的方法
     * @param message:
     * @param session:
     * @return void
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口"+userId+"的信息："+message);
        //群发消息
        for (WebSocketServer server : webSocketSet) {
            try {
                server.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     * @param message:
     * @return void
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     * @param message:
     * @param userId:
     * @return void
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) {
        log.info("推送消息到窗口"+userId+"，推送内容:"+message);
        for (WebSocketServer server : webSocketSet) {
            try {
                if(userId == null) {
                    server.sendMessage(message);
                } else if (server.userId.equals(userId)) {
                    server.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
