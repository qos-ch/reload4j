package org.apache.log4j.net;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;

@Ignore
public class HashmapDOSTest {

    Hashtable<Object, Object> ht = new Hashtable<Object, Object>();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos;

    ObjectInputStream ois;
    int maxHashMapCapacity = getMaxHashMapCapacityValue();

    @Before
    public void beforeEach() throws IOException {
        oos = new ObjectOutputStream(baos);
    }
    @Test
    public void smokeHashMap() throws Exception {
        HashMap<Object, Object> ht = createDeepMap(null, 10);
        setSizeUsingReflection(ht, maxHashMapCapacity);
        oos.writeObject(ht);
        oos.flush();
        oos.close();

        byte[] byteArray = baos.toByteArray();

        System.out.println("byteArray,length="+byteArray.length);


        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ois = new ObjectInputStream(bais);

        Object o = ois.readObject();


    }
    @Test
    public void smokeHashtable() throws Exception {
        Hashtable ht = new Hashtable();
        ht.put("1", "1");
        oos.writeObject(ht);
        oos.flush();
        oos.close();

        byte[] byteArray = baos.toByteArray();

        System.out.println("byteArray,length="+byteArray.length);


        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ois = new ObjectInputStream(bais);

        Hashtable htback = (Hashtable) ois.readObject();

        System.out.println(htback.get("1"));

    }

    private static void setSizeUsingReflection(HashMap map, int size) throws Exception {
        Field sizeField = HashMap.class.getDeclaredField("size");
        sizeField.setAccessible(true);

        while (map != null) {
            sizeField.set(map, size);
            map = (HashMap) map.keySet().iterator().next();
        }
    }

    static int getMaxHashMapCapacityValue() {
        Field maxHashMapCapacityField = null;
        try {
            maxHashMapCapacityField = HashMap.class.getDeclaredField("MAXIMUM_CAPACITY");
            maxHashMapCapacityField.setAccessible(true);

            int maxHashMapCapacity = maxHashMapCapacityField.getInt(null);
            return maxHashMapCapacity;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Unable to obtain field HashMap.MAXIMUM_CAPACITY", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to read HashMap.MAXIMUM_CAPACITY", e);
        }
    }


    private static HashMap createDeepMap(HashMap child, int depth) {
        if (child == null) {
            child = new HashMap();
            // add one last element so that buffer is flushed
            child.put(null, null);
        }

        if (depth <= 1) {
            return child;
        }

        HashMap parent = new HashMap();
        parent.put(child, null);

        return createDeepMap(parent, depth - 1);
    }

}
