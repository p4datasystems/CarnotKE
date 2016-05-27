package org.python.ReL;

import com.thinkaurelius.titan.core.attribute.AttributeSerializer;
import com.thinkaurelius.titan.diskstorage.ScanBuffer;
import com.thinkaurelius.titan.diskstorage.WriteBuffer;
import com.thinkaurelius.titan.graphdb.database.serialize.attribute.ByteArraySerializer;
import org.python.ReL.WDB.database.wdb.metadata.ClassDef;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClassDefSerializer implements AttributeSerializer<ClassDef> {
    // use Java object serialization to convert ClassDef into byte[]
    // then leverage Titan's ByteArraySerializer to integrate with its attribute serialization flow
    private ByteArraySerializer serializer;

    public ClassDefSerializer() {
        System.out.println(">>> ClassDefSerializer constructor");
        serializer = new ByteArraySerializer();
    }

    @Override
    public ClassDef read(ScanBuffer buffer) {
        System.out.println(">>> ClassDefSerializer read");
        ClassDef attribute = null;
        byte[] data = serializer.read(buffer);
        // http://docs.oracle.com/javase/8/docs/technotes/guides/language/try-with-resources.html
        try (
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bais);
        ) {
            attribute = (ClassDef) ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return attribute;
    }

    @Override
    public void write(WriteBuffer buffer, ClassDef attribute) {
        System.out.println(">>> ClassDefSerializer write");
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
