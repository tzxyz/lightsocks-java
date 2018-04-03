package org.zhuonima.lightsocks;

import org.junit.Test;

public class PasswordFactoryTest {

    @Test
    public void newPassword() {
        Password password = PasswordFactory.newPassword();
        System.out.println(password);
    }
}
