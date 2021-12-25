package com.backinfile.gameRPC.net;


import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.Call;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;
import com.backinfile.gameRPC.support.Utils;

/**
 * 用于包装socket中传递的消息
 */
public class GameMessage {
    private static final int CHECK_CODE = Utils.getHashCode("call");
    private Call call;

    private GameMessage(Call call) {
        this.call = call;
    }

    public static GameMessage build(Call call) {
        return new GameMessage(call);
    }

    public static GameMessage build(byte[] bytes, int offset, int len) {
        if (len < 8 || bytes.length < 8)
            return null;
        int byteSize = Utils.bytes2Int(bytes, 0);
        int msgHash = Utils.bytes2Int(bytes, 4);
        if (msgHash != CHECK_CODE) {
            Log.core.error("hash code not match in buildGameMessage");
            return null;
        }
        InputStream in = new InputStream(bytes, 8, len - 8);
        Call call = in.read();
        in.close();
        if (call == null) {
            Log.core.error("read call from stream error");
            return null;
        }
        return new GameMessage(call);
    }

    public byte[] getBytes() {
        OutputStream out = new OutputStream();
        out.write(call);
        out.close();
        byte[] byteArray = out.getBytes();
        byte[] contentBytes = new byte[byteArray.length + 8];
        Utils.int2bytes(byteArray.length + 8, contentBytes, 0);
        Utils.int2bytes(CHECK_CODE, contentBytes, 4);
        System.arraycopy(byteArray, 0, contentBytes, 8, byteArray.length);
        return contentBytes;
    }

    public Call getMessage() {
        return call;
    }
}
