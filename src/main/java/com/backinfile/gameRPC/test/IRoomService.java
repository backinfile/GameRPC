package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.DSyncBase;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;

public interface IRoomService {


    /**
     * rpc return int as RoomServiceLoginResult;
     */
    void login(long humanId);


    static class RoomServiceLoginResult implements DSyncBase {
        @Override
        public void writeTo(MessagePacker packer) throws IOException {

        }

        @Override
        public void readFrom(MessageUnpacker unpacker) throws IOException {

        }
    }

}
