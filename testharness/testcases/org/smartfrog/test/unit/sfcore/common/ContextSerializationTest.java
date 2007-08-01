package org.smartfrog.test.unit.sfcore.common;

import junit.framework.TestCase;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogContextException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ContextSerializationTest extends TestCase {
    private ObjectInputStream ois;

    public void testSfTransient() throws SmartFrogContextException, IOException, ClassNotFoundException {
        try {

            Context ctx = new ContextImpl();
            ctx.put("foo", new Object());
            ctx.sfAddTag("foo", "sfTransient");
            ctx.put("bar", "value");
            
            ByteArrayOutputStream output = new ByteArrayOutputStream(10000);
            ObjectOutputStream oos = new ObjectOutputStream(output);
            oos.writeObject(ctx);
            oos.close();

            ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
            ois = new ObjectInputStream(input);
            Context deserialized = (Context) ois.readObject();

            assertFalse("The \"foo\" attribute marked with \"sfTransient\" should be omitted when serializing", deserialized.containsKey("foo"));
            assertTrue("Normal attributes should still be there after deserializing", deserialized.containsKey("bar"));

        } finally {
            if (ois != null) ois.close();
        }
    }

}
