package com.backinfile.gameRPC.parser;

import com.backinfile.gameRPC.support.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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

        public String getMethodHashName() {
            StringJoiner sj = new StringJoiner("_");
            sj.add(Utils.convertVarName(name));
            for (var param : callParams) {
                sj.add(param.getTypeNameForConst());
            }
            return sj.toString();
        }

        public int getMethodHashCode() {
            return getMethodHashName().hashCode();
        }

        public String getMethodCallString() {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append("(");

            if (clientVar != null) {
                sb.append(Utils.format("({}) clientVar", clientVar.getTypeName()));
                if (!callParams.isEmpty()) {
                    sb.append(", ");
                }
            }
            for (int i = 0; i < callParams.size(); i++) {
                var param = callParams.get(i);
                sb.append(Utils.format("({}) args[{}]", param.getTypeName(), i));
                if (i != callParams.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(");");
            return sb.toString();
        }
    }
}
