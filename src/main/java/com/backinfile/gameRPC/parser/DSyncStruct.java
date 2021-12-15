package com.backinfile.gameRPC.parser;

import java.util.ArrayList;
import java.util.List;

public class DSyncStruct {
	private DSyncStructType type;
	private String typeName;
	private List<DSyncVariable> children = new ArrayList<>();
	private List<String> comments = new ArrayList<>();
	private String defaultValue; // for enum

	public DSyncStruct(DSyncStructType type) {
		this.type = type;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}

	public DSyncStructType getType() {
		return type;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void addVariable(DSyncVariable variable) {
		children.add(variable);
	}

	public List<DSyncVariable> getChildren() {
		return children;
	}

	public void addComments(List<String> comments) {
		this.comments.addAll(comments);
	}

	public List<String> getComments() {
		return comments;
	}

}
