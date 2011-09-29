package org.smartfrog.services.persistence.test.performancetest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.smartfrog.services.persistence.storage.CDString;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;


public class refTestImpl {

    int loops = 1000;

    public static void main(String args[]) {
        refTestImpl test = new refTestImpl();
        test.doit();
    }

    public void doit() {
        try {
            ComponentDescription cd = createCD();
            
            bothTest(cd); serTest(cd); parseTest(cd); 
            bothTest(cd); serTest(cd); parseTest(cd); 
            bothTest(cd); serTest(cd); parseTest(cd);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ComponentDescription createCD() throws IOException, SmartFrogParseException, SmartFrogException {
        ComponentDescription cd1 = new ComponentDescriptionImpl(null, new ContextImpl(), false);
        ComponentDescription cd = new ComponentDescriptionImpl(null, new ContextImpl(), false);
        cd.sfAddAttribute("attr1", "one");
        cd.sfAddAttribute("attr2", "two");
        // cd.sfAddAttribute(Storage.STORAGE_CLASS_ATTR, "one");
        // cd.sfAddAttribute(Storage.COMPONENT_NAME_ATTR, "two");
        cd1.sfAddAttribute("attr1", "one");
        cd1.sfAddAttribute("attr2", 2);
        cd.sfAddAttribute("attr3", cd1);

        System.out.println("String length is " + cd.toString().length());
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(byteArrayOS);
        objectOS.writeObject(cd);
        objectOS.flush();
        objectOS.reset();
        byte[] bytes = byteArrayOS.toByteArray();
        System.out.println("Serialized length is " + bytes.length);

        String cdstr = CDString.toString(cd);
        System.out.println("unparsed string is " + cdstr);
        byteArrayOS = new ByteArrayOutputStream();
        objectOS = new ObjectOutputStream(byteArrayOS);
        objectOS.writeObject(cdstr);
        objectOS.flush();
        objectOS.reset();
        bytes = byteArrayOS.toByteArray();
        System.out.println("Serialized CDString cd length is " + bytes.length);

        ComponentDescription parsed = CDString.fromString(cdstr);
        System.out.println("parsed again is " + parsed);

        return cd;
    }

    public void serTest(ComponentDescription cd) throws IOException, ClassNotFoundException {

        byte[] bytes = null;

        long start = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            ObjectOutputStream objectOS = new ObjectOutputStream(byteArrayOS);
            objectOS.writeObject(cd);
            objectOS.flush();
            objectOS.reset();
            bytes = byteArrayOS.toByteArray();
        }
        long end = System.currentTimeMillis();
        System.out.println("Serialization took " + (end - start) + " millis ");

        start = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            Object obj = new ObjectInputStream(bais).readObject();
        }
        end = System.currentTimeMillis();
        System.out.println("Deserialization took " + (end - start) + " millis ");
    }

    public void parseTest(ComponentDescription cd) throws IOException, ClassNotFoundException, SmartFrogParseException,
            SmartFrogException {

        String unparse = null;

        long start = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            unparse = CDString.toString(cd);
        }
        long end = System.currentTimeMillis();
        System.out.println("Unparse took " + (end - start) + " millis ");

        long start2 = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            CDString.fromString(unparse);
        }
        long end2 = System.currentTimeMillis();
        System.out.println("Parse took " + (end2 - start2) + " millis ");
    }

    public void bothTest(ComponentDescription cd) throws IOException, ClassNotFoundException, SmartFrogParseException,
            SmartFrogException {
        String unparse = null;
        byte[] bytes = null;

        long start = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            unparse = CDString.toString(cd);
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            ObjectOutputStream objectOS = new ObjectOutputStream(byteArrayOS);
            objectOS.writeObject(unparse);
            objectOS.flush();
            objectOS.reset();
            bytes = byteArrayOS.toByteArray();
        }
        long end = System.currentTimeMillis();
        System.out.println("Both unparse/marshall took " + (end - start) + " millis ");

        long start2 = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            String unmarshall = (String) new ObjectInputStream(bais).readObject();
            CDString.fromString(unmarshall);
        }
        long end2 = System.currentTimeMillis();
        System.out.println("Both unmarshall/parse took " + (end2 - start2) + " millis ");
    }

}
