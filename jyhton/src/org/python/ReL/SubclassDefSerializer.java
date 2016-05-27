package org.python.ReL;

import com.thinkaurelius.titan.core.attribute.AttributeSerializer;
import com.thinkaurelius.titan.diskstorage.ScanBuffer;
import com.thinkaurelius.titan.diskstorage.WriteBuffer;
import com.thinkaurelius.titan.graphdb.database.serialize.attribute.ByteArraySerializer;
import org.python.ReL.WDB.database.wdb.metadata.SubclassDef;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SubclassDefSerializer implements AttributeSerializer<SubclassDef> {
    // use Java object serialization to convert WDBObject into byte[]
    // then leverage Titan's ByteArraySerializer to integrate with its attribute serialization flow
    private ByteArraySerializer serializer;

    public SubclassDefSerializer() {
        System.out.println(">>> SubclassDefSerializer constructor");
        serializer = new ByteArraySerializer();
    }

    @Override
    public SubclassDef read(ScanBuffer buffer) {
        System.out.println(">>> SubclassDefSerializer read");
        SubclassDef attribute = null;
        byte[] data = serializer.read(buffer);
        // http://docs.oracle.com/javase/8/docs/technotes/guides/language/try-with-resources.html
        try (
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bais);
        ) {
            attribute = (SubclassDef) ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return attribute;
    }

    @Override
    public void write(WriteBuffer buffer, SubclassDef attribute) {
        System.out.println(">>> SubclassDefSerializer write");
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
        ) {
            oos.writeObject(attribute);
            byte[] data = baos.toByteArray();
            serializer.write(buffer, data);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
