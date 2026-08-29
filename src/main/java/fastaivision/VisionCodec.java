package fastaivision;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class VisionCodec {

    public static byte[] encodeRgbFrame(byte[] rgbPixels, int width, int height) {
        ByteBuffer buf = ByteBuffer.allocate(12 + rgbPixels.length).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(0x56495331); // Magic "VIS1"
        buf.putInt(width);
        buf.putInt(height);
        buf.put(rgbPixels);
        return buf.array();
    }

    public static byte[] decodeRgbFrame(byte[] binaryData, int[] dimensions) {
        if (binaryData.length < 12) return new byte[0];
        ByteBuffer buf = ByteBuffer.wrap(binaryData).order(ByteOrder.LITTLE_ENDIAN);
        int magic = buf.getInt();
        if (magic != 0x56495331) return new byte[0];
        int width = buf.getInt();
        int height = buf.getInt();
        if (dimensions != null && dimensions.length >= 2) {
            dimensions[0] = width;
            dimensions[1] = height;
        }
        byte[] pixels = new byte[binaryData.length - 12];
        buf.get(pixels);
        return pixels;
    }
}