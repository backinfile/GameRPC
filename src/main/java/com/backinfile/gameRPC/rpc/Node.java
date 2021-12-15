package com.backinfile.gameRPC.rpc;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;

import com.backinfile.mrpc.serilize.InputStream;
import com.backinfile.mrpc.serilize.OutputStream;
import com.backinfile.mrpc.support.Log;
import com.backinfile.mrpc.utils.ReflectionUtils;
import com.backinfile.mrpc.utils.Utils2;

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

	public void localStartUp(String[] packageNames, String[] types) {
		Set<Class<?>> classes = new HashSet<>();
		for (var packageName : packageNames) {
			classes.addAll(ReflectionUtils.getClassesExtendsClassAndWithAnnotation(packageName, Port.class,
					AutoStartUp.class));
		}
		for (Class<?> clazz : classes) {
			AutoStartUp annotation = clazz.getAnnotation(AutoStartUp.class);
			if (types.length > 0 && !Utils2.intersect(annotation.value(), types)) {
				continue;
			}
			try {
				String portId = clazz.getName();
				Port port = (Port) clazz.getDeclaredConstructor(String.class).newInstance(portId);
				port.checkInit(); // 初始化
				this.addPort(port); // 添加到node
				Log.Core.info("{} start", portId);
			} catch (Exception e) {
				Log.Core.error("create service failed: " + clazz.getName(), e);
			}
		}

		startUp();
	}

	public void abort() {
		Log.Core.info("node 中断中.....");
		dispatchThreads.abortSync();
		Log.Core.info("node 中断结束");
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
			Utils2.sleep(1);
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
