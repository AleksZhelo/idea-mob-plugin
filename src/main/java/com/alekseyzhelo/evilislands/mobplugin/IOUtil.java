package com.alekseyzhelo.evilislands.mobplugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class IOUtil {

    private IOUtil() {

    }

    public static String readUTF8String(InputStream inputStream) throws IOException {
        int length = inputStream.available();
        byte[] bytes = new byte[length];
        int bytesRead = inputStream.read(bytes, 0, length);
        if (bytesRead != length) {
            throw new IOException(String.format("Failed to read %d bytes as a UTF-8 string", length));
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String readTextFromResources(String resourceName) {
        try {
            return readUTF8String(IOUtil.class.getResourceAsStream(resourceName))
                    .replace("\r", "");  // weird line separator bug
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();  // TODO: ROFL
        }
    }
}
