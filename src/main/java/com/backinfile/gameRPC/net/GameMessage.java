package com.backinfile.gameRPC.net;


import com.backinfile.gameRPC.support.Utils;

public class GameMessage {
    private final String message;

    public GameMessage(String message) {
        this.message = message;
    }

    public static GameMessage buildGameMessage(byte[] bytes, int offset, int len) {
        if (len < 8 || bytes.length < 8)
            return null;
//		int byteSize = Utils.bytes2Int(bytes, 0);
//		int msgHash = Utils.bytes2Int(bytes, 4);
        String message = new String(bytes, 8, len - 8);
        return new GameMessage(message);
    }

    public byte[] getBytes() {
        byte[] byteArray = message.getBytes();
        byte[] contentBytes = new byte[byteArray.length + 8];
        Utils.int2bytes(byteArray.length + 8, contentBytes, 0);
        Utils.int2bytes(Utils.getHashCode(message), contentBytes, 4);
        System.arraycopy(byteArray, 0, contentBytes, 8, byteArray.length);
        return contentBytes;
    }

    public String getMessage() {
        return message;
    }
}
