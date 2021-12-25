package ${packagePath};

import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import java.io.IOException;


/**
 * 此类是自动生成的，不可修改
 */
public interface ISerializable {
    public abstract void writeTo(MessagePacker packer) throws IOException;
    public abstract void readFrom(MessageUnpacker unpacker) throws IOException;
}
