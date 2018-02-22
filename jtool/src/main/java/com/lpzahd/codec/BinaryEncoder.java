package com.lpzahd.codec;

public interface BinaryEncoder extends Encoder {
    byte[] encode(byte[] var1) throws EncoderException;
}
