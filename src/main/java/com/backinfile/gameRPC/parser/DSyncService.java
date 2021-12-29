package com.backinfile.gameRPC.parser;


import com.backinfile.support.Utils;

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

        public String getMethodName() {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }

        public String getMethodHashName() {
            StringJoiner sj = new StringJoiner("_");
            sj.add(Utils.convertVarName(name));
            for (DSyncVariable param : callParams) {
                sj.add(param.getTypeNameForConst());
            }
            return sj.toString();
        }


        public int getMethodHashCode() {
            return getMethodHashName().hashCode();
        }

        // login(LoginContext context, @ClientField long id, String name, boolean local)
        public String getMethodBodyString() {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append("(");
            sb.append(Utils.format("{}Context context", getMethodName()));
            if (clientVar != null || !callParams.isEmpty()) {
                sb.append(", ");
            }

            if (clientVar != null) {
                sb.append(Utils.format("@ClientField {} {}", clientVar.getTypeName(), clientVar.name));
                if (!callParams.isEmpty()) {
                    sb.append(", ");
                }
            }
            for (int i = 0; i < callParams.size(); i++) {
                DSyncVariable param = callParams.get(i);
                sb.append(Utils.format("{} {}", param.getTypeName(), param.name));
                if (i != callParams.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            return sb.toString();
        }

        // login(new LoginContext(from), (long) clientVar, (String) args[0], (boolean) args[1]);
        public String getMethodCallString() {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append("(");

            sb.append(Utils.format("new {}Context(from)", getMethodName()));
            if (clientVar != null || !callParams.isEmpty()) {
                sb.append(", ");
            }

            if (clientVar != null) {
                sb.append(Utils.format("({}) clientVar", clientVar.getTypeName()));
                if (!callParams.isEmpty()) {
                    sb.append(", ");
                }
            }
            for (int i = 0; i < callParams.size(); i++) {
                DSyncVariable param = callParams.get(i);
                sb.append(Utils.format("({}) args[{}]", param.getTypeName(), i));
                if (i != callParams.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(");");
            return sb.toString();
        }

        public String getMethodReturnsString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < returnParams.size(); i++) {
                DSyncVariable param = returnParams.get(i);
                sb.append(Utils.format("{} {}", param.getTypeName(), param.name));
                if (i != returnParams.size() - 1) {
                    sb.append(", ");
                }
            }
            return sb.toString();
        }
    }
}
