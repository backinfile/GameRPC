package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.DSyncBase;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;

public class MessageTypes {
    public static final int ID_DHuman = 124;

    public static DSyncBase unpack(MessageUnpacker _unpacker) throws IOException {
        switch (_unpacker.unpackInt()) {
            case ID_DHuman: {
                DHuman dHuman = new DHuman();
                dHuman.readFrom(_unpacker);
                return dHuman;
            }
            default: {
                return null;
            }
        }
    }
}
