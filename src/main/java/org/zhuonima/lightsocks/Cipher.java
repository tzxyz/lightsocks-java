package org.zhuonima.lightsocks;

import java.util.HashMap;
import java.util.Map;

public class Cipher {

    private final Password password;
    private final Map<Byte, Byte> encode = new HashMap<>();
    private final Map<Byte, Byte> decode = new HashMap<>();

    public Cipher(Password password) {
        this.password = password;
        for (int i = 0; i < password.getData().length; i++) {
            encode.put(password.getData()[i], (byte) i);
            decode.put((byte) i, password.getData()[i]);
        }
    }

    public void encode(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = encode.get(data[i]);
        }
    }

    public void decode(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = decode.get(data[i]);
        }
    }
}
