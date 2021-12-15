package com.backinfile.gameRPC.parser;

public class Token {
	public static enum TokenType {
		Name, // 名字
		LBrace, // 左花括号
		RBrace, // 右花括号
		LSquareBracket, // 左方括号
		RSquareBracket, // 右方括号
		Semicolon, // 分号
		Comment, // 注释
		Comma, // 逗号
	}

	public TokenType type;
	public String value;
	public int lineno;

}
