package com.backinfile.gameRPC;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
    public static Logger parser = LoggerFactory.getLogger("gameRPC.serialize");
    public static Logger core = LoggerFactory.getLogger("gameRPC.core");
    public static Logger net = LoggerFactory.getLogger("gameRPC.net");
    public static Logger server = LoggerFactory.getLogger("gameRPC.server");
    public static Logger client = LoggerFactory.getLogger("gameRPC.client");
    public static Logger serialize = LoggerFactory.getLogger("gameRPC.serialize");
    public static Logger gen = LoggerFactory.getLogger("gameRPC.gen");
    public static Logger game = LoggerFactory.getLogger("gameRPC.game");
}
