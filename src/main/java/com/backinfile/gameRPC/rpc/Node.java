package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.net.Server;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;
import com.backinfile.gameRPC.support.Utils;
import com.backinfile.gameRPC.support.func.Action0;

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
    private static final int THREAD_NUM = 3;
    private final ConcurrentLinkedQueue<Action0> postActionList = new ConcurrentLinkedQueue<>();
    private final String nodeId;

    private final ArrayList<RemoteNode> remoteNodeList = new ArrayList<>();

    private Server server = null;


    public Node(String nodeId) {
        this.nodeId = nodeId;
        Instance = this;
    }

    public static Node getInstance() {
        return Instance;
    }

    public void startServer(int port) {
        server = new Server(port);
        server.start();
    }

    public void connectServer(String ip, int port) {
    }

    public void startUp() {
        String name = nodeId;
        Thread.currentThread().setName("Node-" + name);
        dispatchThreads = new DispatchThreads(("Node-" + name) + "DispatchThread", THREAD_NUM, null, this::dispatchRun, null);
        dispatchThreads.start();
    }


    public void abort() {
        Log.core.info("node {} 中断开始.....", nodeId);
        dispatchThreads.abortSync();
        Log.core.info("node {} 中断完成", nodeId);
    }

    public void addPort(Port port) {
        post(() -> {
            port.setNode(this);
            portsWaitForRun.add(port);
            allPorts.put(port.getId(), port);
        });
    }

    private void dispatchRun() {
        Port port = portsWaitForRun.poll();
        if (port == null) {
            reSchedule(THREAD_NUM);
            Utils.sleep(1);
            return;
        }
        pulsePort(port);
        while (postActionList.isEmpty()) {
            try {
                postActionList.poll().invoke();
            } catch (Exception e) {
                Log.core.error(e, "error in invoke postAction");
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

    public static CallPoint getCurCallPoint() {
        CallPoint callPoint = new CallPoint();
        callPoint.portID = Port.getCurrentPort().getId();
        return callPoint;
    }

    public Port getPort(String portId) {
        return allPorts.get(portId);
    }

    public void handleSendCall(Call call) {
        if (call.to.nodeID.equals(nodeId)) {
            Port port = getPort(call.to.portID);
            if (port == null) {
                Log.core.error("此call发送到未知port(" + call.to.portID + ")，已忽略", new SysException(""));
                return;
            }

            port.addCall(serializeCall(call));
            awake(port);
        } else {

        }
    }

    private Call serializeCall(Call call) {
        OutputStream outputStream = new OutputStream();
        outputStream.write(call);
        byte[] bytes = outputStream.getBytes();
        InputStream inputStream = new InputStream(bytes);
        Call newCall = inputStream.read();
        outputStream.close();
        inputStream.close();
        return newCall;
    }

    public void post(Action0 action0) {
        postActionList.add(action0);
    }

    public String getId() {
        return nodeId;
    }
}
