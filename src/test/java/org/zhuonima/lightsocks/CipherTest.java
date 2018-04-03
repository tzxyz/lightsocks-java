package org.zhuonima.lightsocks;

import org.junit.Test;

import java.util.Arrays;

public class CipherTest {

    private final Password password = PasswordFactory.newPassword();

    private final Cipher cipher = new Cipher(password);

    private final byte[][] tests = {
            {1, 2, 3, 4, 5},
            {-1, -2, -3, -4, -5},
            {-128, -127, -126, -125, -124, -123, -124}
    };

    @Test
    public void testEncode() {

        for (byte[] test : tests) {
            cipher.encode(test);
            cipher.decode(test);
            System.out.println(Arrays.toString(test));
        }
    }
}
