package com.backinfile.gameRPC.parser;

import com.backinfile.gameRPC.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SyntaxWorker {
    private final List<Token> tokens = new ArrayList<>();
    private static final String DS_STRUCT = "struct";
    private static final String DS_ENUM = "enum";
    private static final String DS_SERV = "service";
    private final List<Token> lastCommentTokens = new ArrayList<>();
    private final Result result = new Result();

    public static class Result {
        public boolean hasError = false;
        public String errorStr = "";
        public Map<String, DSyncStruct> userDefineStructMap = new HashMap<>();
        public Map<String, String> properties = new HashMap<>();
    }

    private int index = 0;

    private SyntaxWorker() {
    }

    public static Result parse(List<Token> tokens) {
        SyntaxWorker worker = new SyntaxWorker();
        try {
            worker.tokens.addAll(tokens);
            worker.parseRoot();
        } catch (Exception e) {
            worker.result.hasError = true;
            worker.result.errorStr = e.getMessage();
        }
        return worker.result;
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

            if (test(DS_STRUCT)) {
                parseStruct();
            } else if (test(DS_ENUM)) {
                parseEnum();
            } else if (test(DS_SERV)) {
                // TODO
            } else { // 不是枚举不是结构体， 是自定义变量
                parseProperty();
            }
        }

    }

    private void parseProperty() {
        var nameToken = match(TokenType.Name);
        match(TokenType.Assign);
        var strToken = match(TokenType.Str);
        result.properties.put(nameToken.value, strToken.value);
    }

    private void parseStruct() {
        match(DS_STRUCT);
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

        if (result.userDefineStructMap.containsKey(typeName)) {
            Log.parser.warn("duplicate struct:{}!", typeName);
        }

        result.userDefineStructMap.put(typeName, struct);
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
        match(DS_ENUM);
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
        result.userDefineStructMap.put(typeName, struct);
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
            throw new ParserException("语法错误  第" + token.lineno + "行。");
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

    private boolean test(String name) {
        if (index >= tokens.size()) {
            return false;
        }
        Token token = getToken();
        return token.type == TokenType.Name && token.value.equals(name);
    }

    private void match(String name) {
        var token = match(TokenType.Name);
        if (!name.equals(token.value)) {
            throw new ParserException("语法错误 不能匹配" + name + " 第" + token.lineno + "行。");
        }
    }

    private Token match(TokenType... tokenTypes) {
        if (index >= tokens.size()) {
            var token = tokens.get(tokens.size() - 1);
            TOKEN_MISS_MATCH(tokenTypes[0], token.lineno);
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
            TOKEN_MISS_MATCH(tokenTypes[0], token.lineno);
        }
        index++;
        return token;
    }

    private Token match(TokenType tokenType) {
        if (index >= tokens.size()) {
            var token = tokens.get(tokens.size() - 1);
            TOKEN_MISS_MATCH(tokenType, token.lineno);
        }
        var token = tokens.get(index);
        if (token.type != tokenType) {
            TOKEN_MISS_MATCH(tokenType, token.lineno);
        }
        index++;
        return token;
    }

    private void TOKEN_MISS_MATCH(TokenType tokenType, int lineno) {
        throw new ParserException("语法错误 不能匹配" + tokenType.name() + " 第" + lineno + "行。");
    }
}
