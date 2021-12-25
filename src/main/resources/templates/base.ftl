package ${packagePath};

import com.backinfile.gameRPC.rpc.ISerializable;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import java.util.BitSet;

/**
 * 此类是自动生成的，不可修改
 */
public abstract class DSyncBase implements ISerializable {
    protected BitSet changedMap;

    public abstract static class Builder {
        protected BitSet changedMap;
    }
}
