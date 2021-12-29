package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.gen.service.AbstractLoginService;
import com.backinfile.gameRPC.net.Connection;
import com.backinfile.gameRPC.net.GameMessage;
import com.backinfile.gameRPC.rpc.server.LoginService;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;
import com.backinfile.support.Utils;
import com.backinfile.support.func.Action0;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;

/**
 * 管理Port，连接远程Node
 * 一个程序只启一个Node
 */
public class Node {
    public static Node Instance = null;

    private final ConcurrentLinkedQueue<Port> portsWaitForRun = new ConcurrentLinkedQueue<>();
    private final DelayQueue<Port> portsWaitForReschedule = new DelayQueue<>();
    private final ConcurrentHashMap<String, Port> allPorts = new ConcurrentHashMap<>();
    private DispatchThreads dispatchThreads;
    private DispatchThreads mainThread;
    private static final int THREAD_NUM = 3;
    private final ConcurrentLinkedQueue<Action0> postActionList = new ConcurrentLinkedQueue<>();
    private final String nodeId;

    private final ArrayList<RemoteNode> remoteNodeList = new ArrayList<>();

    public Node(String nodeId) {
        this.nodeId = nodeId;
        Instance = this;
    }

    public static Node getInstance() {
        return Instance;
    }

    /**
     * 连接远程服务器
     */
    public void connectServer(String nodeId, String ip, int port) {
        addRemoteNode(new RemoteNode(nodeId, ip, port));
    }

    public void addRemoteNode(RemoteNode remoteNode) {
        post(() -> {
            remoteNodeList.add(remoteNode);
            remoteNode.start();
        });
    }

    public void clearRemoteNode(RemoteNode remoteNode) {
        post(() -> {
            if (remoteNode.isAlive()) {
                remoteNode.close();
            }
            remoteNodeList.remove(remoteNode);
            Log.core.info("远程node {} 已清理", remoteNode.getId());
        });
    }

    public void startUp() {
        String name = nodeId;
        Thread.currentThread().setName("Node-" + name);
        dispatchThreads = new DispatchThreads(("Node-" + name) + "-DispatchThread", THREAD_NUM,
                null, this::dispatchRun, null);
        dispatchThreads.start();

        mainThread = new DispatchThreads(("Node-" + name) + "-MainDispatchThread", 1, null, this::pulse, null);
        mainThread.start();

        Log.core.info("=============== node {} 启动 ===============", nodeId);
    }


    public void abort() {
        Log.core.info("node {} 中断开始.....", nodeId);
        dispatchThreads.abort();
        mainThread.abort();
    }

    public void join() {
        while (!dispatchThreads.isAborted() || !mainThread.isAborted()) {
            Utils.sleep(100);
        }
        Log.core.info("=============== node {} 关闭 ===============", nodeId);
    }

    public void addPort(Port port) {
        post(() -> {
            Log.core.info("add port {}", port.getClass().getSimpleName());
            port.setNode(this);
            allPorts.put(port.getId(), port);
            port.startup();
            portsWaitForRun.add(port);
        });
    }

    private void dispatchRun() {
        // pulse port
        Port port = portsWaitForRun.poll();
        if (port == null) {
            reSchedule(THREAD_NUM);
            Utils.sleep(1);
        } else {
            pulsePort(port);
        }
    }

    private void pulse() {
        // pulse remoteNode
        for (RemoteNode remoteNode : remoteNodeList) {
            if (remoteNode.isDisconnected()) {
                clearRemoteNode(remoteNode);
            } else {
                remoteNode.pulse();
            }
        }

        // pulse post action
        while (true) {
            Action0 action0 = postActionList.poll();
            if (action0 == null) {
                break;
            }
            try {
                action0.invoke();
            } catch (Exception e) {
                Log.core.error("error in invoke postAction", e);
            }
        }
    }

    private void pulsePort(Port port) {
        if (!port.pulsed) {
            port.caseRunOnce();
            port.pulsed = true;
        } else {
            port.caseAwakeUp();
        }
        portsWaitForReschedule.add(port);
    }

    // 立即唤醒一个port
    private void awake(Port port) {
        if (portsWaitForReschedule.remove(port)) {
            portsWaitForRun.add(port);
        }
    }

    // 将已经被执行过的port重新放入执行队列
    private void reSchedule(int num) {
        for (int i = 0; i < num; i++) {
            Port port = portsWaitForReschedule.poll();
            if (port == null) {
                break;
            }
            port.pulsed = false;
            portsWaitForRun.add(port);
        }
    }

    public long getTime() {
        return System.currentTimeMillis();
    }

    public Port getPort(String portId) {
        return allPorts.get(portId);
    }

    /**
     * 转发所有经由此node的call
     * 如果call发送至此node，直接推送到相应port中；
     * 如果发送至其他node，通过remoteNode推送。
     * 此方法线程安全
     */
    public void handleCall(Call call) {
        // 发送到此node的消息
        if (call.to.nodeID.equals(nodeId)) {
            Port port = getPort(call.to.portID);
            if (port == null) {
                Log.core.error("此call发送到未知port(" + call.to.portID + ")，已忽略", new SysException(""));
                return;
            }
            port.getTerminal().addCall(serializeCall(call));
            awake(port);
            return;
        }
        // 需要发送到服务器的消息
        {
            RemoteNode remoteNode = getRemoteNode(call.to.nodeID);
            if (remoteNode != null) {
                remoteNode.sendMessage(call);
                return;
            }
        }
        // 需要发送到客户端的消息
        Port port = getPort(AbstractLoginService.PORT_ID_PREFIX);
        if (port instanceof LoginService) {
            String token = call.to.nodeID;
            Connection connection = ((LoginService) port).getConnections().get(token);
            if (connection != null && connection.isAlive()) {
                connection.sendGameMessage(GameMessage.build(call));
                return;
            }
        }

        Log.core.warn("unhandled call to {},{}", call.to.nodeID, call.to.portID);
    }

    private RemoteNode getRemoteNode(String nodeId) {
        for (var remoteNode : remoteNodeList) {
            if (remoteNode.getId().equals(nodeId)) {
                return remoteNode;
            }
        }
        return null;
    }

    /**
     * 序列化一遍call，避免在不同线程修改相同的对象
     */
    public static Call serializeCall(Call call) {
        OutputStream outputStream = new OutputStream();
        outputStream.write(call);
        byte[] bytes = outputStream.getBytes();
        InputStream inputStream = new InputStream(bytes);
        Call newCall = inputStream.read();
        outputStream.close();
        inputStream.close();
        return newCall;
    }

    /**
     * 在node线程中执行action
     */
    public void post(Action0 action0) {
        postActionList.add(action0);
    }

    public String getId() {
        return nodeId;
    }

}
