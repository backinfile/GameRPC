package com.backinfile.support;

import com.backinfile.support.func.Action1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 处理消息，产生数据的终端
 *
 * @param <In>  需要处理的数据，在内部维护一个queue
 * @param <Out> 产生的数据，可通过listener获取
 */
public abstract class Terminal<In, Out> {
    private LinkedList<In> msgQueue = new LinkedList<In>();
    private List<Action1<Out>> outputListeners = new ArrayList<>();

    /**
     * 输入消息
     */
    public final void putMsg(In msg) {
        msgQueue.addLast(msg);
    }

    /**
     * 监听产出的消息
     */
    public final void addOutputListener(Action1<Out> listener) {
        outputListeners.add(listener);
    }

    /**
     * 获取输入的消息
     */
    protected final In pollMsg() {
        return msgQueue.pollFirst();
    }

    protected final boolean hasMsg() {
        return !msgQueue.isEmpty();
    }

    /**
     * 产出消息
     */
    protected final void outputMsg(Out out) {
        for (Action1<Out> listener : outputListeners) {
            listener.invoke(out);
        }
    }
}
