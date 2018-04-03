package org.zhuonima.lightsocks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PasswordFactory {

    public static Password newPassword() {
        List<Integer> n = IntStream.range(0, 256).boxed().collect(Collectors.toList());
        Collections.shuffle(n);
        Iterator<Integer> iterator = n.iterator();
        byte[] data = new byte[256];
        for (int i = 0; i < data.length; i ++) {
            data[i] = iterator.next().byteValue();
        }
        return new Password(data);
    }
}
