package com.backinfile.gameRPC;

import com.backinfile.gameRPC.support.log.LogManager;
import com.backinfile.gameRPC.support.log.Logger;

public class Log {
    public static Logger parser = LogManager.getLogger("gameRPC.serialize");
    public static Logger core = LogManager.getLogger("gameRPC.core");
    public static Logger net = LogManager.getLogger("gameRPC.net");
    public static Logger server = LogManager.getLogger("gameRPC.server");
    public static Logger client = LogManager.getLogger("gameRPC.client");
    public static Logger serialize = LogManager.getLogger("gameRPC.serialize");
    public static Logger gen = LogManager.getLogger("gameRPC.gen");
}
