package com.backinfile.gameRPC.struct;

import com.backinfile.gameRPC.DSyncBase;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;

import java.util.Objects;

public class DNodeVerify extends DSyncBase {
    public static final String TYPE_NAME = DNodeVerify.class.getSimpleName();
    public static final int TYPE_ID = Objects.hash(DNodeVerify.class.getSimpleName());

    private String token;

    @Override
    public void writeTo(OutputStream out) {

    }

    @Override
    public void readFrom(InputStream in) {

    }
}
