package com.backinfile.gameRPC.pack;

import com.backinfile.gameRPC.DSyncBase;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

import java.io.IOException;

public class PackManager {
    public static byte[] pack(DSyncBase base) {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        try {
            packer.packInt(base.getType());
            base.writeTo(packer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                packer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return packer.toByteArray();
    }
}
