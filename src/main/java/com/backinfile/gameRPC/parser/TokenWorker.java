package com.backinfile.gameRPC.parser;


import com.backinfile.support.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenWorker {
    private int lineno = 0;
    private final List<String> content = new ArrayList<>();
    private final List<Token> tokenCollection = new ArrayList<>();
    private final Result result = new Result();

    private TokenWorker(List<String> content) {
        this.content.addAll(content);
    }

    public static class Result {
        public boolean hasError = false;
        public String errorStr = "";
        public int errorLineno = -1;
        public List<Token> tokens = new ArrayList<>();
    }

    public static Result getTokens(List<String> allLine) {
        TokenWorker tokenWorker = new TokenWorker(allLine);
        tokenWorker.parse();
        return tokenWorker.result;
    }

    public static Result getTokens(String content) {
        String[] split = content.split("\n");
        return getTokens(Arrays.asList(split));
    }

    private void parse() {
        lineno = 0;
        while (lineno < content.size()) {
            lineno++;
            parseLine();
            if (result.hasError) {
                return;
            }
        }
        result.tokens.addAll(tokenCollection);
    }

    private void parseLine() {
        String str = content.get(lineno - 1);
        int lineIndex = 0;
        LINE:
        while (lineIndex < str.length()) {
            char character = str.charAt(lineIndex);
            if (character == ' ' || character == '\t' || character == '\r' || character == '\n') {
                lineIndex++;
                continue;
            }
            String input = str.substring(lineIndex);
            for (TokenType tokenType : TokenType.values()) {
                for (Pattern pattern : tokenType.getPatterns()) {
                    Matcher matcher = pattern.matcher(input);
                    if (matcher.find()) {
//                        Log.core.info("match type:{} {}->{}, content:{}", tokenType, matcher.start(), matcher.end(), input.substring(matcher.start(), matcher.end()));
                        lineIndex += matcher.end() - matcher.start();
                        if (matcher.groupCount() > 0) {
                            pushToken(tokenType, input.substring(matcher.start(1), matcher.end(1)));
                        } else {
                            pushToken(tokenType, "");
                        }
                        continue LINE;
                    }
                }
            }
            result.hasError = true;
            result.errorStr = Utils.format("不能识别的token, 第{}行，第{}列", lineno, lineIndex);
            return;
        }
    }

    private void pushToken(TokenType type) {
        pushToken(type, "");
    }

    private void pushToken(TokenType type, String value) {
        Token token = new Token();
        token.type = type;
        token.lineno = lineno;
        token.value = value;
        tokenCollection.add(token);
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) throws IOException {
        String resourcePath = TokenWorker.class.getClassLoader().getResource("demo.ds").getPath();
        Path path = Paths.get(resourcePath.substring(1));
        List<String> lines = Files.readAllLines(path);
        var tokenResult = TokenWorker.getTokens(lines);
        var result = SyntaxWorker.parse(tokenResult.tokens);
        System.out.println();
    }

}
