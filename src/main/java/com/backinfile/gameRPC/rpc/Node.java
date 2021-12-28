package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.net.Server;
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
        post(() -> {
            server = new Server(port);
            server.start();
        });
    }

    /**
     * 连接服务器
     */
    public void connectServer(String ip, int port) {
        RemoteNode.RemoteServer remoteServer = new RemoteNode.RemoteServer(ip, port);
        addRemoteNode(remoteServer);
    }

    public void addRemoteNode(RemoteNode remoteNode) {
        post(() -> {
            remoteNodeList.add(remoteNode);
            remoteNode.start();
        });
    }

    public void startUp() {
        String name = nodeId;
        Thread.currentThread().setName("Node-" + name);
        dispatchThreads = new DispatchThreads(("Node-" + name) + "DispatchThread", THREAD_NUM,
                null, this::dispatchRun, null);
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
        // pulse port
        Port port = portsWaitForRun.poll();
        if (port == null) {
            reSchedule(THREAD_NUM);
            Utils.sleep(1);
            return;
        }
        pulsePort(port);

        // pulse remoteNode
        for (RemoteNode remoteNode : remoteNodeList) {
            remoteNode.pulse();
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
        if (call.to.nodeID.equals(nodeId)) {
            Port port = getPort(call.to.portID);
            if (port == null) {
                Log.core.error("此call发送到未知port(" + call.to.portID + ")，已忽略", new SysException(""));
                return;
            }
            RemoteNode remoteNode = getRemoteNode(call.from.nodeID);
            if (remoteNode instanceof RemoteNode.RemoteClient) {
                call.fromClient = true;
            }
            port.addCall(serializeCall(call));
            awake(port);
        } else {
            RemoteNode remoteNode = getRemoteNode(call.to.nodeID);
            if (remoteNode != null) {
                remoteNode.sendMessage(call);
                Log.core.info("handle call to local node to portId:{}", call.to.portID);
            } else {
                Log.core.error("handle call to missing node nodeId:{} portId:{}", call.to.nodeID, call.to.portID);
            }

        }
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

    public void handleVerify(Call call) {
        post(() -> {
            RemoteNode remoteNode = getRemoteNode(call.from.nodeID);
            remoteNode.verified = true; // TODO 具体校验规则
        });
    }
}
