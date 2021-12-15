package com.backinfile.gameRPC.rpc;

import java.util.Scanner;

public class ShutdownThread extends Thread {
	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		while (!scanner.next().equals("exit")) {
		}
		scanner.close();
		shutdown();
	}

	public void shutdown() {
		if (Node.getInstance() != null) {
			Node.getInstance().abort();
		}
	}
}
