package com.lpzahd;

import com.lpzahd.codec.Digest;

import java.io.FileInputStream;
import java.io.IOException;

public class Codec {

    public static String md5Hex(String path) {
        if(Strings.empty(path))
            return "";

        try {
            return Digest.md5Hex(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
