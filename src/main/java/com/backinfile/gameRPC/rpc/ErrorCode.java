package com.backinfile.gameRPC.rpc;

public class ErrorCode {
	public static final int OK = 0;
	public static final int RPC_CALL_TIMEOUT = -1;
	public static final int GEN_CANNOT_GEN = -2;
	public static final int GEN_FILE_CREATE_FAILED = -3;
	public static final int GEN_FILE_WRITE_FAILED = -4;
	public static final int GEN_TEMPLATE_FILE_NOT_FOUND = -5;
}
