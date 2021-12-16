package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.DSyncBase;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;

public class MsgManager {

    public static final int ID_DHuman = 124;
    private MessageUnpacker unpacker;

    public static byte[] packMessage(DHuman dHuman) {
        try {
            return _packMessage(dHuman);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] _packMessage(DHuman dHuman) throws IOException {
        MessageBufferPacker _packer = MessagePack.newDefaultBufferPacker();
        _packer.packInt(ID_DHuman);
        dHuman.writeTo(_packer);
        _packer.close();
        return _packer.toByteArray();
    }

    public static DSyncBase parseStruct(byte[] bytes) {
        try {
            return _parseStruct(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static DSyncBase _parseStruct(byte[] bytes) throws IOException {
        MessageUnpacker _unpacker = MessagePack.newDefaultUnpacker(bytes);
        switch (_unpacker.unpackInt()) {
            case ID_DHuman: {
                DHuman dHuman = new DHuman();
                dHuman.readFrom(_unpacker);
                _unpacker.close();
                return dHuman;
            }
            default: {
                return null;
            }
        }
    }


}
