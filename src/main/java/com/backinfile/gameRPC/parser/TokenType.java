package com.backinfile.gameRPC.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public enum TokenType {
    LBrace("\\{"), // 左花括号
    RBrace("\\}"), // 右花括号
    LSquareBracket("\\["), // 左方括号
    RSquareBracket("\\]"), // 右方括号
    Semicolon(";"), // 分号
    Comment("// (.*)", "//(.*)"), // 注释
    Comma(","), // 逗号
    Assign("="), // 等于号

    Str("\"([^\"]*)\"", "'([^']*)'"), // 字符串

    Name("(\\w+)"), // 名字
    ;
    private final List<Pattern> patterns = new ArrayList<>();

    TokenType(String... regexList) {
        for (String reg : regexList) {
            patterns.add(Pattern.compile("^" + reg));
        }
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }
}
