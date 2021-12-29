package com.backinfile.gameRPC.parser;

import com.backinfile.gameRPC.Log;
import com.backinfile.support.StreamUtils;
import com.backinfile.support.Utils;

import java.util.*;

public class SyntaxWorker {
    private final List<Token> tokens = new ArrayList<>();
    private static final String DS_STRUCT = "struct";
    private static final String DS_ENUM = "enum";
    private static final String DS_SERV = "service";
    private final List<Token> lastCommentTokens = new ArrayList<>();
    private final Result result = new Result();

    private final HashSet<String> requireDefinedType = new HashSet<>();

    public static class Result {
        public boolean hasError = false;
        public String errorStr = "";
        public Map<String, DSyncStruct> userDefineStructMap = new HashMap<>(); // 自定义类
        public Map<String, String> properties = new HashMap<>(); // 自定义属性
        public Map<String, DSyncService> serviceMap = new HashMap<>(); // 自定义服务
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
        if (!worker.result.hasError) {
            for (String name : worker.requireDefinedType) {
                if (!worker.result.userDefineStructMap.containsKey(name)) {
                    worker.result.hasError = true;
                    worker.result.errorStr = Utils.format("type {} not define!", name);
                    break;
                }
            }
        }

        return worker.result;
    }

    private void parseRoot() {
        // 第一遍，找到所有自定义struct
        index = 0;
        while (index < tokens.size()) {
            if (catchComment()) {
                continue;
            }

            if (test(DS_STRUCT)) {
                parseStruct();
            } else if (test(DS_ENUM)) {
                parseEnum();
            } else if (test(DS_SERV)) {
                parseService();
            } else { // 不是枚举不是结构体， 是自定义变量
                parseProperty();
            }
        }
    }

    // 记录一个注释
    // 如果下一行是空行，清空所有暂存的注释
    private boolean catchComment() {
        if (test(TokenType.Comment)) {
            Token token = match(TokenType.Comment);
            lastCommentTokens.add(token);

            if (index < tokens.size()) {
                Token nextToken = getToken();
                if (nextToken != null) {
                    if (nextToken.lineno != token.lineno + 1) {
                        lastCommentTokens.clear();
                    }
                }
            }
            return true;
        }
        return false;
    }

    private List<String> extractComments() {
        List<String> comments = StreamUtils.map(lastCommentTokens, token -> token.value);
        lastCommentTokens.clear();
        return comments;
    }

    private void parseService() {
        match(DS_SERV);
        Token nameToken = match(TokenType.Name);
        String name = nameToken.value;
        match(TokenType.LBrace);

        DSyncService service = new DSyncService();
        service.name = name;
        result.serviceMap.put(name, service);
        service.comments.addAll(extractComments());

        while (catchComment()) ;
        while (test("rpc")) { // rpc 开始
            next();
            DSyncService.DSyncRPC rpc = new DSyncService.DSyncRPC();
            service.rpcList.add(rpc);
            rpc.comments.addAll(extractComments());
            Token rpcNameToken = match(TokenType.Name);
            rpc.name = rpcNameToken.value;
            match(TokenType.LRoundBracket); // rpc参数开始

            if (test("client")) {
                next();
                rpc.clientVar = parseFiled();
                if (test(TokenType.Comma)) {
                    next();
                }
            }
            for (int i = 0; i < 10 && !test(TokenType.RRoundBracket); i++) {
                rpc.callParams.add(parseFiled());
                if (test(TokenType.Comma)) {
                    next();
                }
            }
            match(TokenType.RRoundBracket); // rpc参数结束

            if (test("returns")) { // rpc返回开始
                next();
                match(TokenType.LRoundBracket);
                for (int i = 0; i < 10 && !test(TokenType.RRoundBracket); i++) {
                    rpc.returnParams.add(parseFiled());
                    if (test(TokenType.Comma)) {
                        next();
                    }
                }
                match(TokenType.RRoundBracket); // rpc返回结束
            }
            match(TokenType.Semicolon);
            while (catchComment()) ;
        } // rpc结束
        match(TokenType.RBrace);
    }

    private void parseProperty() {
        Token nameToken = match(TokenType.Name);
        match(TokenType.Assign);
        Token strToken = match(TokenType.Str);
        result.properties.put(nameToken.value, strToken.value);
    }

    private void parseStruct() {
        match(DS_STRUCT);
        Token nameToken = match(TokenType.Name);
        String typeName = nameToken.value;
        match(TokenType.LBrace);
        DSyncStruct struct = new DSyncStruct(DSyncStructType.UserDefine);
        struct.setTypeName(typeName);
        struct.addComments(extractComments());

        while (!test(TokenType.RBrace)) {
            struct.addVariable(parseFiled());
        }
        match(TokenType.RBrace);

        if (result.userDefineStructMap.containsKey(typeName)) {
            Log.parser.warn("duplicate struct:{}!", typeName);
        }

        result.userDefineStructMap.put(typeName, struct);
    }

    private DSyncVariable parseFiled() {
        boolean isArray = false;
        Token typeToken = match(TokenType.Name);
        DSyncStructType varType = DSyncStructType.match(typeToken.value);
        if (test(TokenType.LSquareBracket)) {
            match(TokenType.LSquareBracket);
            match(TokenType.RSquareBracket);
            isArray = true;
        }
        Token nameToken = match(TokenType.Name);
        DSyncVariable variable = new DSyncVariable(nameToken.value, varType, isArray);
        if (varType == DSyncStructType.UserDefine) {
            variable.setTypeName(typeToken.value);
            requireDefinedType.add(typeToken.value);
        }
        if (test(TokenType.Semicolon)) {
            Token semToken = match(TokenType.Semicolon);
            if (test(TokenType.Comment)) {
                Token commentToken = getToken();
                if (semToken.lineno == commentToken.lineno) {
                    variable.comment = commentToken.value;
                    next();
                }
            }
        }
        return variable;
    }

    private void parseEnum() {
        match(DS_ENUM);
        Token nameToken = match(TokenType.Name);
        String typeName = nameToken.value;
        match(TokenType.LBrace);
        DSyncStruct struct = new DSyncStruct(DSyncStructType.Enum);
        struct.setTypeName(typeName);
        struct.addComments(extractComments());

        boolean defaultValue = true;
        while (!test(TokenType.RBrace)) {
            parseEnumField(struct, defaultValue);
            defaultValue = false;
        }
        match(TokenType.RBrace);
        result.userDefineStructMap.put(typeName, struct);
    }

    private void parseEnumField(DSyncStruct struct, boolean defaultValue) {
        Token nameToken = match(TokenType.Name);
        DSyncVariable variable = new DSyncVariable(nameToken.value, DSyncStructType.UserDefine, false);
        struct.addVariable(variable);
        Token endToken = match(TokenType.Semicolon, TokenType.Comma);

        if (defaultValue) {
            struct.setDefaultValue(nameToken.value);
        }

        if (test(TokenType.Comment)) {
            Token commentToken = getToken();
            if (endToken.lineno == commentToken.lineno) {
                variable.comment = commentToken.value;
                next();
            }
        }
    }

    private Token getToken() {
        if (index >= tokens.size()) {
            Token token = tokens.get(tokens.size() - 1);
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
        Token token = match(TokenType.Name);
        if (!name.equals(token.value)) {
            throw new ParserException("语法错误 不能匹配" + name + " 第" + token.lineno + "行。");
        }
    }

    private Token match(TokenType... tokenTypes) {
        if (index >= tokens.size()) {
            Token token = tokens.get(tokens.size() - 1);
            TOKEN_MISS_MATCH(tokenTypes[0], token.lineno);
        }
        boolean match = false;
        Token token = tokens.get(index);
        for (TokenType type : tokenTypes) {
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
            Token token = tokens.get(tokens.size() - 1);
            TOKEN_MISS_MATCH(tokenType, token.lineno);
        }
        Token token = tokens.get(index);
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
