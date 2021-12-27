package com.backinfile.gameRPC.parser;

import java.util.ArrayList;
import java.util.List;

public class DSyncService {
    public String name;
    public final List<String> comments = new ArrayList<>();
    public final List<DSyncRPC> rpcList = new ArrayList<>();

    public DSyncService() {
    }


    public static class DSyncRPC {
        public String name;
        public DSyncVariable clientVar;
        public final List<String> comments = new ArrayList<>();
        public final List<DSyncVariable> callParams = new ArrayList<>();
        public final List<DSyncVariable> returnParams = new ArrayList<>();
    }
}
