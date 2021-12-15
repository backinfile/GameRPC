package com.backinfile.gameRPC.rpc;

import java.util.HashMap;

import com.backinfile.mrpc.serilize.InputStream;
import com.backinfile.mrpc.serilize.OutputStream;

@SuppressWarnings("unchecked")
public class Result implements IResult {

	private Object singleValue = null;
	private HashMap<String, Object> mapValues = null;

	private Object singleContext = null;
	private HashMap<String, Object> mapContexts = null;

	private boolean isError = false;

	/**
	 * 仅供序列化使用
	 */
	public Result() {
	}

	public Result(Object[] values) {
		updateValues(values);
	}

	public void updateValues(Object[] values) {
		if (values == null)
			return;
		if (values.length == 0)
			return;
		if (values.length == 1) {
			singleValue = values[0];
			return;
		}
		if (values.length % 2 == 0) {
			if (mapValues == null) {
				mapValues = new HashMap<>();
			}
			for (int i = 0; i < values.length; i += 2) {
				Object first = values[i];
				Object second = values[i + 1];
				if (!(first instanceof String))
					break;
				mapValues.put((String) first, second);
			}
		}
	}

	public void updateContexts(Object[] contexts) {
		if (contexts == null)
			return;
		if (contexts.length == 0)
			return;
		if (contexts.length == 1) {
			singleContext = contexts[0];
			return;
		}

		if (contexts.length % 2 == 0) {
			if (mapContexts == null) {
				mapContexts = new HashMap<>();
			}
			for (int i = 0; i < contexts.length; i += 2) {
				Object first = contexts[i];
				Object second = contexts[i + 1];
				if (!(first instanceof String))
					break;
				mapContexts.put((String) first, second);
			}
		}
	}

	@Override
	public void writeTo(OutputStream out) {
		out.write(singleValue);
		out.write(mapValues);
		out.write(singleContext);
		out.write(mapContexts);
		out.write(isError);
	}

	@Override
	public void readFrom(InputStream in) {
		singleValue = in.read();
		mapValues = in.read();
		singleContext = in.read();
		mapContexts = in.read();
		isError = in.read();
	}

	@Override
	public <T> T getResult() {
		return (T) singleValue;
	}

	@Override
	public <T> T getResult(String key) {
		if (mapValues != null) {
			return (T) mapValues.get(key);
		}
		return null;
	}

	@Override
	public <T> T getContext() {
		return (T) singleContext;
	}

	@Override
	public <T> T getContext(String key) {
		if (mapContexts != null) {
			return (T) mapContexts.get(key);
		}
		return null;
	}

	public void setError(boolean error) {
		isError = error;
	}

	@Override
	public boolean isErrorOccurred() {
		return isError;
	}

}
