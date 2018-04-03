package org.zhuonima.lightsocks;

import org.junit.Test;

public class PasswordFactoryTest {

    @Test
    public void testNewPassword() {
        Password password = PasswordFactory.newPassword();
        System.out.println(password);
    }

    @Test
    public void testParse() {
        String s = "X0GW8QSdYYaPJe/HNkK+kXgA4tEeIf9r87ilsM3OuykywtOmDAp2mbR639Zy97YW3dlUvSwDjguzoNQUVeW3WyI1lDFSyn+aOFyr+RebTwF80oGJOUp3YsBEEKr2H2DhBTPsQ0nm3gkHZbWn2IRG9W9q6vCQTRvFGFcgGYvGUHVwDuTPyAZnVjQdXROtPqiplaLM/YB0TAKkL1gPrvowOv7r2ofpkvRFEkhH0Gmxfkts3Mkqr7K87eBeKEA/Wfyhl5zbcZjLniOfO4rBv8RO+yuCfVN7o+McJ41zGrk9LefXN2557mgkWuj4hVERbZMNiBUIJtWM8jxmuqxjw2Qugw==";
        Password password = PasswordFactory.parse(s);
        assert s.equals(password.toString());
    }
}
