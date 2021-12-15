package com.backinfile.gameRPC.parser;

import com.backinfile.gameRPC.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SyntaxWorker {
	private final Map<String, DSyncStruct> userDefineStructMap = new HashMap<String, DSyncStruct>();
	private final List<Token> tokens = new ArrayList<Token>();
	private static final String DS_MSG = "message";
	private static final String DS_STRUCT = "struct";
	private static final String DS_ENUM = "enum";
	private final Set<String> messageStructs = new HashSet<>();
	private final List<Token> lastCommentTokens = new ArrayList<>();

	public static class Result {
		public boolean hasError = false;
		public String errorStr = "";
		public Map<String, DSyncStruct> userDefineStructMap = new HashMap<String, DSyncStruct>();
		public Set<String> messageStructs = new HashSet<String>();
	}

	private int index = 0;

	private SyntaxWorker() {
	}

	public static Result parse(List<Token> tokens) {
		Result result = new Result();
		try {
			SyntaxWorker worker = new SyntaxWorker();
			worker.tokens.addAll(tokens);
			worker.parseRoot();
			result.userDefineStructMap.putAll(worker.getUserDefineStructMap());
			result.messageStructs.addAll(worker.getMessageStructs());
		} catch (Exception e) {
			result.hasError = true;
			result.errorStr = e.getMessage();
		}
		return result;

	}

	public Map<String, DSyncStruct> getUserDefineStructMap() {
		return userDefineStructMap;
	}

	public Set<String> getMessageStructs() {
		return messageStructs;
	}

	private void parseRoot() {
		// 第一遍，找到所有自定义struct
		index = 0;
		while (index < tokens.size()) {
			if (test(TokenType.Comment)) {
				var token = match(TokenType.Comment);
				lastCommentTokens.add(token);

				var nextToken = getToken();
				if (nextToken != null) {
					if (nextToken.lineno != token.lineno + 1) {
						lastCommentTokens.clear();
					}
				}
				continue;
			}

			if (test(TokenType.Name, DS_MSG)) {
				next();
				parseStruct(true);
			} else if (test(TokenType.Name, DS_STRUCT)) {
				next();
				parseStruct(false);
			} else if (test(TokenType.Name, DS_ENUM)) {
				next();
				parseEnum();
			}
		}

	}

	private void parseStruct(boolean isMsg) {
		var nameToken = match(TokenType.Name);
		String typeName = nameToken.value;
		match(TokenType.LBrace);
		var struct = new DSyncStruct(DSyncStructType.UserDefine);
		struct.setTypeName(typeName);
		struct.addComments(lastCommentTokens.stream().map(t -> t.value).collect(Collectors.toList()));
		lastCommentTokens.clear();

		while (!test(TokenType.RBrace)) {
			parseFiled(struct);
		}
		match(TokenType.RBrace);

		if (userDefineStructMap.containsKey(typeName)) {
			Log.parser.warn("duplicate struct:{}!", typeName);
		}

		userDefineStructMap.put(typeName, struct);
		if (isMsg) {
			messageStructs.add(typeName);
		}
	}

	private void parseFiled(DSyncStruct struct) {
		boolean isArray = false;
		var typeToken = match(TokenType.Name);
		var varType = DSyncStructType.match(typeToken.value);
		if (test(TokenType.LSquareBracket)) {
			match(TokenType.LSquareBracket);
			match(TokenType.RSquareBracket);
			isArray = true;
		}
		var nameToken = match(TokenType.Name);
		var variable = new DSyncVariable(nameToken.value, varType, isArray);
		if (varType == DSyncStructType.UserDefine) {
			variable.setTypeName(typeToken.value);
		}
		struct.addVariable(variable);
		var semToken = match(TokenType.Semicolon);

		if (test(TokenType.Comment)) {
			var commentToken = getToken();
			if (semToken.lineno == commentToken.lineno) {
				variable.comment = commentToken.value;
				next();
			}
		}
	}

	private void parseEnum() {
		var nameToken = match(TokenType.Name);
		String typeName = nameToken.value;
		match(TokenType.LBrace);
		var struct = new DSyncStruct(DSyncStructType.Enum);
		struct.setTypeName(typeName);
		struct.addComments(lastCommentTokens.stream().map(t -> t.value).collect(Collectors.toList()));
		lastCommentTokens.clear();

		boolean defaultValue = true;
		while (!test(TokenType.RBrace)) {
			parseEnumField(struct, defaultValue);
			defaultValue = false;
		}
		match(TokenType.RBrace);
		userDefineStructMap.put(typeName, struct);
	}

	private void parseEnumField(DSyncStruct struct, boolean defaultValue) {
		var nameToken = match(TokenType.Name);
		var variable = new DSyncVariable(nameToken.value, DSyncStructType.UserDefine, false);
		struct.addVariable(variable);
		Token endToken = match(TokenType.Semicolon, TokenType.Comma);

		if (defaultValue) {
			struct.setDefaultValue(nameToken.value);
		}

		if (test(TokenType.Comment)) {
			var commentToken = getToken();
			if (endToken.lineno == commentToken.lineno) {
				variable.comment = commentToken.value;
				next();
			}
		}
	}

	private Token getToken() {
		if (index >= tokens.size()) {
			var token = tokens.get(tokens.size() - 1);
			throw new ParserError("语法错误  第" + token.lineno + "行。");
		}
		return tokens.get(index);
	}

	private void next() {
		index++;
	}

	private boolean test(TokenType tokenType) {
		if (index >= tokens.size()) {
			return false;
		}
		return getToken().type == tokenType;
	}

	private boolean test(TokenType tokenType, String name) {
		if (index >= tokens.size()) {
			return false;
		}
		Token token = getToken();
		return token.type == tokenType && token.value.equals(name);
	}

	private Token match(TokenType... tokenTypes) {
		if (index >= tokens.size()) {
			var token = tokens.get(tokens.size() - 1);
			throw new ParserError("语法错误 不能匹配" + tokenTypes[0].name() + " 第" + token.lineno + "行。");
		}
		boolean match = false;
		var token = tokens.get(index);
		for (var type : tokenTypes) {
			if (type == token.type) {
				match = true;
				break;
			}
		}
		if (!match) {
			throw new ParserError("语法错误 不能匹配" + tokenTypes[0].name() + " 第" + token.lineno + "行。");
		}
		index++;
		return token;
	}

	private Token match(TokenType tokenType) {
		if (index >= tokens.size()) {
			var token = tokens.get(tokens.size() - 1);
			throw new ParserError("语法错误 不能匹配" + tokenType.name() + " 第" + token.lineno + "行。");
		}
		var token = tokens.get(index);
		if (token.type != tokenType) {
			throw new ParserError("语法错误 不能匹配" + tokenType.name() + " 第" + token.lineno + "行。");
		}
		index++;
		return token;
	}
}
