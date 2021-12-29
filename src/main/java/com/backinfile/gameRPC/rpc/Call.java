package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.serialize.ISerializable;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;

import static com.backinfile.gameRPC.rpc.ConstRPC.RPC_TYPE_CALL;
import static com.backinfile.gameRPC.rpc.ConstRPC.RPC_TYPE_CALL_RETURN;

public class Call implements ISerializable {
    public long id;
    public CallPoint to;
    public CallPoint from;

    public int type;
    public int method;
    public Object[] args = null;
    public int code = 0;
    public boolean fromClient = false;


    public Call() {
    }

    public static Call newCall(long id, CallPoint from, CallPoint to, int method, Object[] args) {
        Call call = new Call();
        call.id = id;
        call.from = from;
        call.to = to;
        call.args = args;
        call.method = method;
        call.type = RPC_TYPE_CALL;
        return call;
    }

    public Call newCallReturn(Object[] args) {
        Call callReturn = new Call();
        callReturn.id = id;
        callReturn.from = to.copy();
        callReturn.to = from.copy();
        callReturn.args = args;
        callReturn.type = RPC_TYPE_CALL_RETURN;
        return callReturn;
    }

    public Call newErrorReturn(int code) {
        Call callReturn = new Call();
        callReturn.from = to.copy();
        callReturn.to = from.copy();
        callReturn.id = id;
        callReturn.code = code;
        callReturn.type = RPC_TYPE_CALL_RETURN;
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
        out.write(fromClient);
    }

    @Override
    public void readFrom(InputStream in) {
        id = in.read();
        to = in.read();
        from = in.read();
        type = in.read();
        method = in.read();
        args = in.read();
        code = in.read();
        fromClient = in.read();
    }

    public static class LocalCall extends Call {
        public boolean fromClient = false;
        public Object clientVar = null;

        @Override
        public void readFrom(InputStream in) {
            super.readFrom(in);
            fromClient = in.read();
            clientVar = in.read();
        }

        @Override
        public void writeTo(OutputStream out) {
            super.writeTo(out);
            out.write(fromClient);
            out.write(clientVar);
        }
    }
}
