package com.backinfile.support;

import com.backinfile.support.func.Action1;

import java.util.LinkedList;

public class ActionQueue<Action extends IAction> implements IAlive {
    private final LinkedList<Action> actions = new LinkedList<>();
    private Action curAction = null;
    private Action1<Action> prepare = null;

    public ActionQueue() {
    }

    public ActionQueue(Action1<Action> prepare) {
        this.prepare = prepare;
    }

    @Override
    public void start() {
        clear();
    }

    @Override
    public void update(long timeDelta) {
        if (curAction == null) {
            if (actions.isEmpty()) {
                return;
            }
            curAction = actions.pollFirst();
            if (prepare != null) {
                prepare.invoke(curAction);
            }

            curAction.start();
            if (curAction.isDone()) {
                curAction.dispose();
                curAction = null;
                return;
            }
        }

        if (curAction != null) {
            curAction.update(timeDelta);
            if (curAction.isDone()) {
                curAction.dispose();
                curAction = null;
            }
        }
    }

    @Override
    public void dispose() {
        clear();
    }

    public void clear() {
        curAction = null;
        actions.clear();
    }

    public boolean isEmpty() {
        return curAction == null && actions.isEmpty();
    }

    public void addLast(Action action) {
        actions.addLast(action);
    }

    public void addFirst(Action action) {
        actions.addFirst(action);
    }
}
