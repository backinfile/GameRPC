package com.backinfile.gameRPC.parser;

import java.util.ArrayList;
import java.util.Arrays;
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

	public static enum DSyncStructType {
		Int("Int", "int", "int32", "Integer"), // int
		Long("Long", "long", "int64"), // long
		String("String", "string", "str"), // string
		Double("Double", "Float", "double", "float"), // float
		Boolen("Boolean", "boolean", "bool"), // boolean
		Enum("enum"), UserDefine;

		private List<String> names = new ArrayList<>();

		private DSyncStructType(String... names) {
			this.names.addAll(Arrays.asList(names));
		}

		public static DSyncStructType match(String matchName) {
			for (var type : DSyncStructType.values()) {
				for (var typeName : type.names) {
					if (typeName.equals(matchName)) {
						return type;
					}
				}
			}
			return UserDefine;
		}
	}

	public static class DSyncVariable {
		public String name;
		public DSyncStructType type;
		public String typeName;
		public boolean isArray = false;
		public String comment = "";

		public DSyncVariable() {
		}

		public DSyncVariable(String name, DSyncStructType type, boolean isArray) {
			this.name = name;
			this.type = type;
			this.isArray = isArray;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}

		public String getTypeName() {
			if (isArray) {
				return "List<" + getLargeTypeName() + ">";
			}
			String typeName = "";
			switch (type) {
			case Boolen:
				typeName = "boolean";
				break;
			case Double:
				typeName = "double";
				break;
			case Int:
				typeName = "int";
				break;
			case Long:
				typeName = "long";
				break;
			case String:
				typeName = "String";
				break;
			case Enum:
			case UserDefine:
				typeName = this.typeName;
				break;
			default:
				break;
			}
			return typeName;
		}

		public String getSingleTypeName() {
			String typeName = "";
			switch (type) {
			case Boolen:
				typeName = "boolean";
				break;
			case Double:
				typeName = "double";
				break;
			case Int:
				typeName = "int";
				break;
			case Long:
				typeName = "long";
				break;
			case String:
				typeName = "String";
				break;
			case Enum:
			case UserDefine:
				typeName = this.typeName;
				break;
			default:
				break;
			}
			return typeName;
		}

		public String getLargeTypeName() {
			String typeName = "";
			switch (type) {
			case Boolen:
				typeName = "Boolean";
				break;
			case Double:
				typeName = "Double";
				break;
			case Int:
				typeName = "Integer";
				break;
			case Long:
				typeName = "Long";
				break;
			case String:
				typeName = "String";
				break;
			case Enum:
			case UserDefine:
				typeName = this.typeName;
				break;
			default:
				break;
			}
			return typeName;
		}

		public String getJSONLongTypeName() {
			String typeName = "";
			switch (type) {
			case Boolen:
				typeName = "BooleanValue";
				break;
			case Int:
				typeName = "IntValue";
				break;
			case Long:
				typeName = "LongValue";
				break;
			case Double:
				typeName = "DoubleValue";
				break;
			case String:
				typeName = "String";
				break;
			case Enum:
			case UserDefine:
				typeName = this.typeName;
				break;
			default:
				break;
			}
			return typeName;
		}

		public String getDefaultValue() {
			if (isArray) {
				return "new ArrayList<>()";
			}
			switch (type) {
			case Boolen:
				return "false";
			case Int:
				return "0";
			case Long:
				return "0";
			case Double:
				return "0f";
			case String:
				return "\"\"";
			case Enum:
				return "null"; // 临时使用
			case UserDefine:
				return "null";
			default:
				break;
			}
			return "null";
		}

		public boolean isEqualType() {
			if (isArray) {
				return true;
			}
			return type == DSyncStructType.UserDefine || type == DSyncStructType.Enum || type == DSyncStructType.String;
		}

	}

}
