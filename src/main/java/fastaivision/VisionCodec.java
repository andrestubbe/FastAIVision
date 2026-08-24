package fastaivision;

import fastfileformat.BinaryHeader;
import fastfileformat.BinaryReader;
import fastfileformat.BinaryWriter;
import fastfileformat.FastFileFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * High-speed binary serializer and stream decoder for Vision Grounding log files (.visionbin).
 * Built on top of FastFileFormat and FastBinary VarInt compression.
 */
public final class VisionCodec {
    /**
     * Payload type identifier for FastJava Vision Grounding Logs (0x0006).
     */
    public static final short PAYLOAD_TYPE_VISIONBIN = 0x0006;

    private VisionCodec() {}

    /**
     * Encodes a list of UIElement detections into a compressed FastFileFormat binary byte array.
     */
    public static byte[] encode(List<UIElement> elements) {
        if (elements == null || elements.isEmpty()) {
            BinaryWriter finalWriter = FastFileFormat.binaryWriter(12);
            finalWriter.writeHeader(FastFileFormat.DEFAULT_MAGIC, FastFileFormat.DEFAULT_VERSION, PAYLOAD_TYPE_VISIONBIN, 0);
            return finalWriter.toByteArray();
        }

        BinaryWriter payloadWriter = FastFileFormat.binaryWriter(elements.size() * 32);
        payloadWriter.writeVarInt(elements.size());

        for (UIElement elem : elements) {
            payloadWriter.writeString(elem.label() != null ? elem.label() : "");
            payloadWriter.writeFloat(elem.box().x());
            payloadWriter.writeFloat(elem.box().y());
            payloadWriter.writeFloat(elem.box().width());
            payloadWriter.writeFloat(elem.box().height());
            payloadWriter.writeFloat(elem.confidence());
            payloadWriter.writeByte((byte) elem.elementType().ordinal());
        }

        byte[] payload = payloadWriter.toByteArray();

        BinaryWriter finalWriter = FastFileFormat.binaryWriter(12 + payload.length);
        finalWriter.writeHeader(
                FastFileFormat.DEFAULT_MAGIC,
                FastFileFormat.DEFAULT_VERSION,
                PAYLOAD_TYPE_VISIONBIN,
                payload.length
        );
        finalWriter.writeBytes(payload);
        return finalWriter.toByteArray();
    }

    /**
     * Decodes a .visionbin binary payload into a list of UIElement instances.
     */
    public static List<UIElement> decode(byte[] bytes) {
        if (bytes == null || bytes.length < 12) {
            return Collections.emptyList();
        }

        BinaryReader reader = FastFileFormat.binaryReader(bytes);
        BinaryHeader header = reader.readHeader();

        if (header.getMagic() != FastFileFormat.DEFAULT_MAGIC) {
            throw new IllegalArgumentException("Invalid FastFileFormat magic header: " + Integer.toHexString(header.getMagic()));
        }
        if (header.getPayloadType() != PAYLOAD_TYPE_VISIONBIN) {
            throw new IllegalArgumentException("Unexpected payload type for Visionbin: " + header.getPayloadType());
        }
        if (header.getPayloadLength() == 0) {
            return Collections.emptyList();
        }

        int count = reader.readVarInt();
        List<UIElement> list = new ArrayList<>(count);
        UIElement.ElementType[] types = UIElement.ElementType.values();

        for (int i = 0; i < count; i++) {
            String label = reader.readString();
            float x = reader.readFloat();
            float y = reader.readFloat();
            float w = reader.readFloat();
            float h = reader.readFloat();
            float conf = reader.readFloat();
            int typeIdx = reader.readByte() & 0xFF;
            UIElement.ElementType type = (typeIdx < types.length) ? types[typeIdx] : UIElement.ElementType.UNKNOWN;

            list.add(new UIElement(label, new BoundingBox(x, y, w, h), conf, type));
        }
        return Collections.unmodifiableList(list);
    }

    public static void writeToFile(Path path, List<UIElement> elements) throws IOException {
        byte[] bytes = encode(elements);
        Files.write(path, bytes);
    }

    public static List<UIElement> readFromFile(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return decode(bytes);
    }
}
