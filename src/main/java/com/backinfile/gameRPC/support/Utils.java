package com.backinfile.gameRPC.support;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;
import java.util.*;

public class Utils {
    public static final String UTF8 = "utf-8";
    private static final Random RANDOM = new Random();
    public static final String NUMBER = "1234567890";
    public static final String LETTER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LETTER_AND_NUMBER = LETTER + NUMBER;
    public static final int TOKEN_LENGTH = 16;

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.equals("");
    }

    public static <T> ArrayList<T> subList(List<T> list, int fromIndex, int toIndex) {
        return new ArrayList<T>(list.subList(fromIndex, toIndex));
    }

    public static int indexOf(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (value == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int[] str2IntArray(String str) {
        String[] strs = str.split(",");
        int[] result = new int[strs.length];
        for (int i = 0; i < strs.length; i++) {
            result[i] = Integer.parseInt(strs[i]);
        }
        return result;
    }

    public static void setRndSeed(long seed) {
        RANDOM.setSeed(seed);
    }

    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    public static double nextDouble() {
        return RANDOM.nextDouble();
    }

    public static int nextInt(int b) {
        return nextInt(0, b);
    }

    public static int nextInt(int a, int b) {
        return RANDOM.nextInt(b - a) + a;
    }

    public static double nextDouble(double a, double b) {
        return RANDOM.nextDouble() * (b - a) + a;
    }

    public static <T> T randItem(List<T> list) {
        int index = nextInt(list.size());
        return list.get(index);
    }

    public static String getBlankString(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public static String getRandomToken() {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            int index = rnd.nextInt(LETTER_AND_NUMBER.length());
            sb.append(LETTER_AND_NUMBER.charAt(index));
        }
        return sb.toString();
    }

    public static String getRandomNumber(int n) {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int index = rnd.nextInt(NUMBER.length());
            sb.append(NUMBER.charAt(index));
        }
        return sb.toString();
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter, false));
        stringWriter.flush();
        return stringWriter.getBuffer().toString();
    }

    public static void int2bytes(int num, byte[] bytes, int offset) {
        for (int i = 0; i < 4; i++) {
            bytes[offset + i] = (byte) (num >>> (24 - i * 8));
        }
    }

    public static int bytes2Int(byte[] bytes, int offset) {
        int num = 0;
        for (int i = offset; i < offset + 4; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xFF);
        }
        return num;
    }

    public static int getHashCode(String str) {
        int h = 0;
        for (int i = 0; i < str.length(); i++) {
            h = 31 * h + str.charAt(i);
        }
        return h;
    }

    public static String format(String pattern, Object... arguments) {
        String[] argStrings = new String[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            argStrings[i] = arguments[i].toString();
        }

        StringBuilder sb = new StringBuilder();
        int defaultIndex = 0;
        int length = pattern.length();
        for (int i = 0; i < length; i++) {
            char curChar = pattern.charAt(i);
            if (curChar == '{') {
                boolean hasNumber = false;
                boolean stoped = false;
                int number = 0;
                for (int j = i + 1; j < length; j++) {
                    char nextChar = pattern.charAt(j);
                    if (Character.isDigit(nextChar)) {
                        hasNumber = true;
                        number *= 10;
                        number += nextChar - ('0');
                    } else if (nextChar == '}') {
                        stoped = true;
                        i = j;
                        break;
                    }
                }
                if (stoped) {
                    if (hasNumber) {
                        sb.append(argStrings[number]);
                    } else {
                        sb.append(argStrings[defaultIndex]);
                    }
                    defaultIndex++;
                } else {
                    sb.append(curChar);
                }
            } else {
                sb.append(curChar);
            }
        }

        return sb.toString();
    }

    public static boolean contains(String[] args, String string) {
        for (String str : args) {
            if (str.equals(string)) {
                return true;
            }
        }
        return false;
    }

    private static long idMax = 1;

    public static long applyId() {
        return idMax++;
    }

    public static void resetId() {
        idMax = 1;
    }

    public static void readExit() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String msg = scanner.next();
            if (msg.equals("exit")) {
                break;
            }
        }
    }

    /**
     * 将驼峰式变量名转化为下划线式
     * roomService -> room_service
     */
    public static String convertVarName(String name) {
        List<Integer> sPointList = new ArrayList<>();
        for (int index = 0; index < name.length() - 1; index++) {
            char first = name.charAt(index);
            char next = name.charAt(index + 1);
            if (Character.isUpperCase(first) && Character.isLowerCase(next)) {
                sPointList.add(index);
            }
        }
        sPointList.add(name.length());

        StringJoiner sj = new StringJoiner("_");
        int begin = 0;
        for (var end : sPointList) {
            sj.add(name.substring(begin, end).toLowerCase());
            begin = end;
        }
        return sj.toString();
    }

    // test
    public static void main(String[] args) {
        System.out.println(Utils.format("{} say {}, {0}", "ww", "hi"));
        for (var str : Arrays.asList("roomService", "room", "RPC", "RPCService", "convertVarName")) {
            System.out.println(str + " -> " + convertVarName(str));
        }
    }

}
