package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.support.Utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;

public class Node {
    private final ConcurrentLinkedQueue<Port> portsWaitForRun = new ConcurrentLinkedQueue<>();
    private final DelayQueue<Port> portsWaitForReschedule = new DelayQueue<>();
    private final ConcurrentHashMap<String, Port> allPorts = new ConcurrentHashMap<>();
    private DispatchThreads dispatchThreads;
    private static final int THREAD_NUM = 3;

    public final String nodeId;

    public static Node Instance = null;

    public Node(String nodeId) {
        this.nodeId = nodeId;
        Instance = this;
    }

    public static Node getInstance() {
        return Instance;
    }

    public void startUp() {
        Thread.currentThread().setName("Thread-Node");
        dispatchThreads = new DispatchThreads(THREAD_NUM, this::dispatchRun);
        dispatchThreads.start();
    }


    public void abort() {
        Log.core.info("node 中断中.....");
        dispatchThreads.abortSync();
        Log.core.info("node 中断结束");
    }

    public void addPort(Port port) {
        port.setNode(this);
        portsWaitForRun.add(port);
        allPorts.put(port.getPortId(), port);
    }

    private void dispatchRun() {
        Port port = portsWaitForRun.poll();
        if (port == null) {
            reSchedule(THREAD_NUM);
            Utils.sleep(1);
            return;
        }
        pulsePort(port);
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
        callPoint.portID = Port.getCurrentPort().getPortId();
        return callPoint;
    }

    public Port getPort(String portId) {
        return allPorts.get(portId);
    }

    public void handleSendCall(Call call) {
        Port port = getPort(call.to.portID);
        if (port == null) {
            Log.Core.error("此call发送到未知port(" + call.to.portID + ")，已忽略", new SysException(""));
            return;
        }

        port.addCall(serializeCall(call));
        awake(port);
    }

    private Call serializeCall(Call call) {
        OutputStream outputStream = new OutputStream();
        outputStream.write(call);
        byte[] bytes = outputStream.getBytes();
        InputStream inputStream = new InputStream(bytes);
        Call newCall = inputStream.read();
        return newCall;
    }

}
