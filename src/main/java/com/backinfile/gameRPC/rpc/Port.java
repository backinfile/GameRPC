package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.support.func.Action0;
import com.backinfile.gameRPC.support.func.Action1;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class Port implements Delayed {

    private Node node;

    private String portId;

    private Terminal terminal;

    /**
     * 用于检查是否需要心跳操作，（在额外的唤醒中不执行心跳）
     */
    public volatile boolean pulsed = false;

    // 上次执行时间
    protected long time = 0;
    private boolean isInited = false;

    // 两次执行时间的时间差距
    private long deltaTime = 0;
    private long lastPulsePerSecondTime = 0L;

    // 执行频率（每秒执行几次)
    private int HZ = 33;

    private static final ThreadLocal<Port> curPort = new ThreadLocal<>();
    private final ConcurrentLinkedQueue<Action0> postActionList = new ConcurrentLinkedQueue<>();

    public Port(String portId) {
        this.portId = portId;
    }

    public static Port getCurrentPort() {
        return curPort.get();
    }

    public void setNode(Node node) {
        this.node = node;
        terminal = new Terminal(this, node);
    }

    public abstract void startup();

    public abstract void pulse();

    /**
     * 注意每帧先执行pulse再执行pulsePerSec
     */
    public abstract void pulsePerSec();

    public abstract void handleRequest(int requestKey, Params param);

    public void checkInit() {
        if (!isInited) {
            startup();
            isInited = true;
        }
    }

    public void caseRunOnce() {
        // 设置port时间
        long newTime = node.getTime();
        this.deltaTime = time > 0 ? newTime - time : 0;
        this.time = newTime;
        // 设置当前port
        curPort.set(this);
        // 检查有没有失效的listen
        terminal.checkCallReturnTimeout();
        // 处理rpc
        terminal.executeInCall();
        // 子类的心跳
        pulse();
        // 子类每秒心跳
        if (time - lastPulsePerSecondTime >= Time2.SEC) {
            lastPulsePerSecondTime = time;
            pulsePerSec();
        }
        // 执行post函数
        while (!postActionList.isEmpty()) {
            Action0 action = postActionList.poll();
            try {
                action.invoke();
            } catch (Exception e) {
                Log.core.error("error in post action", e);
            }
        }
        // 设置当前port
        curPort.set(null);
    }

    /**
     * 临时唤醒来执行rpc调用
     */
    public void caseAwakeUp() {
        curPort.set(this);
        terminal.executeInCall();
        curPort.set(null);
    }

    void addCall(Call call) {
        terminal.addCall(call);
    }

    public void returns(Object... values) {
        terminal.returns(values);
    }

    public void returns(Call call, Object... results) {
        terminal.returns(call, results);
    }

    public void listen(Action1<IResult> consumer, Object... contexts) {
        terminal.listenLastOutCall(consumer, contexts);
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    /**
     * 设置每秒执行次数
     */
    protected void setHZ(int HZ) {
        this.HZ = HZ;
    }

    public Node getNode() {
        return node;
    }

    public long getTime() {
        if (time > 0)
            return time;
        return System.currentTimeMillis();
    }

    public long getDeltaTime() {
        return deltaTime;
    }

    /**
     * 将一个函数推迟到心跳结束执行
     */
    public void post(Action0 action) {
        postActionList.add(action);
    }

    /**
     * 距离下次执行的时间
     */
    public long getDelay(TimeUnit unit) {
        return time + (1000 / HZ) - node.getTime();
    }

    public int compareTo(Delayed o) {
        Port port = (Port) o;
        return Long.compare(time + (1000 / HZ), port.time + (1000 / port.HZ));
    }

    public Terminal getTerminal() {
        return terminal;
    }
}
