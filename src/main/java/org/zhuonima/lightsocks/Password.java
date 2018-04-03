package org.zhuonima.lightsocks;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;

@Data
@AllArgsConstructor
public class Password {

    private final byte[] data;

    private static final Base64 base64 = new Base64();

    @Override
    public String toString() {
        return base64.encodeToString(data);
    }
}
