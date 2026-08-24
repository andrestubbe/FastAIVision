package fastaivision;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FastAIVisionTest {

    @Test
    public void testGroundingParserQwen2VL() {
        String sampleOutput = "The search bar is located at [120, 250, 160, 750].";
        BoundingBox box = GroundingParser.extractFirstBoundingBox(sampleOutput);

        assertNotNull(box);
        assertEquals(0.250f, box.x(), 0.001f);
        assertEquals(0.120f, box.y(), 0.001f);
        assertEquals(0.500f, box.width(), 0.001f);
        assertEquals(0.040f, box.height(), 0.001f);

        assertEquals(0.500f, box.centerX(), 0.001f);
        assertEquals(0.140f, box.centerY(), 0.001f);
    }

    @Test
    public void testGroundingParserSmolVLM() {
        String sampleOutput = "Target element detected: <box>(50, 100, 150, 300)</box>";
        BoundingBox box = GroundingParser.extractFirstBoundingBox(sampleOutput);

        assertNotNull(box);
        assertEquals(0.100f, box.x(), 0.001f);
        assertEquals(0.050f, box.y(), 0.001f);
        assertEquals(0.200f, box.width(), 0.001f);
        assertEquals(0.100f, box.height(), 0.001f);
    }

    @Test
    public void testVisionCodecSerialization(@TempDir Path tempDir) throws IOException {
        List<UIElement> elements = List.of(
                new UIElement("Submit Button", new BoundingBox(0.4f, 0.8f, 0.2f, 0.05f), 0.98f, UIElement.ElementType.BUTTON),
                new UIElement("Email Field", new BoundingBox(0.2f, 0.3f, 0.6f, 0.06f), 0.95f, UIElement.ElementType.INPUT)
        );

        byte[] encoded = VisionCodec.encode(elements);
        assertNotNull(encoded);
        assertTrue(encoded.length >= 12);

        List<UIElement> decoded = VisionCodec.decode(encoded);
        assertEquals(2, decoded.size());
        assertEquals("Submit Button", decoded.get(0).label());
        assertEquals(UIElement.ElementType.BUTTON, decoded.get(0).elementType());
        assertEquals(0.4f, decoded.get(0).box().x(), 0.001f);

        Path testFile = tempDir.resolve("screen.visionbin");
        VisionCodec.writeToFile(testFile, elements);
        assertTrue(testFile.toFile().exists());

        List<UIElement> fromDisk = VisionCodec.readFromFile(testFile);
        assertEquals(2, fromDisk.size());
    }

    @Test
    public void testImagePreprocessing() {
        FastAIVision vision = FastAIVision.createQwen2VL();
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        float[] tensor = vision.preprocessImage(img, 64, 64);

        assertNotNull(tensor);
        assertEquals(3 * 64 * 64, tensor.length);
    }
}
