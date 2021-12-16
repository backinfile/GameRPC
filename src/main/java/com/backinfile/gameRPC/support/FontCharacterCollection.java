package com.backinfile.gameRPC.support;

import java.util.HashSet;

public class FontCharacterCollection {
	private HashSet<Character> characters = new HashSet<Character>();

	public void put(String str) {
		for (int i = 0; i < str.length(); i++) {
			characters.add(str.charAt(i));
		}
	}

	public String getAll() {
		StringBuilder sb = new StringBuilder();
		for (Character ch : characters) {
			sb.append(ch);
		}
		return sb.toString();
	}
}