package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.DSyncBase;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;

public class MsgManager {

    public static final int ID_DHuman = 124;
    private MessageUnpacker unpacker;

    public byte[] packMessage(DHuman dHuman) {
        try {
            MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
            packer.packInt(ID_DHuman);
            dHuman.writeTo(packer);
            packer.close();
            return packer.toByteArray();
        } catch (IOException ignored) {
        }
        return null;
    }

    public static DSyncBase tryParseStruct(byte[] bytes) {
            return parseStruct(bytes);
        return null;
    }

    public static DSyncBase parseStruct(byte[] bytes) throws IOException {
        MessageUnpacker _unpacker = null;
        DSyncBase _result = null;
        try {
            _unpacker = MessagePack.newDefaultUnpacker(bytes);
            switch(_unpacker.unpackInt()) {
                case ID_DHuman: {
                    _result = new DHuman();
                    _result.readFrom(_unpacker);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (_unpacker != null) {
                _unpacker.close();
            }
        }
        return _result;
    }


}
