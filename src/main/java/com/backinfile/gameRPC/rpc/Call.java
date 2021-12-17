package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.DSyncBase;
import com.backinfile.gameRPC.pack.PackManager;
import com.backinfile.gameRPC.serialize.ISerializable;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;

import static com.backinfile.gameRPC.rpc.ConstRPC.RPC_TYPE_CALL_RETURN;
import static com.backinfile.gameRPC.rpc.ConstRPC.RPC_TYPE_CALL_RETURN_ERROR;

public class Call implements ISerializable {
    public long id;
    public CallPoint to;
    public CallPoint from;

    public int type;
    public int method;
    public Object[] args;
    public int code;


    public Call() {
    }

    public Call newCallReturn(DSyncBase base) {
        Call callReturn = new Call();
        callReturn.id = id;
        callReturn.from = to;
        callReturn.to = from;
        callReturn.args = PackManager.pack(base);
        callReturn.type = RPC_TYPE_CALL_RETURN;
        return callReturn;
    }

    public Call newErrorReturn(int code) {
        Call callReturn = new Call();
        callReturn.from = to;
        callReturn.to = from;
        callReturn.id = id;
        callReturn.code = code;
        callReturn.type = RPC_TYPE_CALL_RETURN_ERROR;
        return callReturn;
    }

    @Override
    public void writeTo(OutputStream out) {
        out.write(id);
        out.write(to);
        out.write(from);
        out.write(type);
        out.write(method);
        out.write(args);
        out.write(code);
    }

    @Override
    public void readFrom(InputStream in) {

    }
}
