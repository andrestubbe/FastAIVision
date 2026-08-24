package fastaivision;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * High-level Vision-Language and UI Grounding engine interface.
 * Connects vision models (Qwen2-VL, SmolVLM) to autonomous screen interaction.
 */
public class FastAIVision {

    public enum VLMBackend {
        QWEN2_VL_2B,
        SMOLVLM_2B,
        CUSTOM
    }

    private final VLMBackend backend;

    public FastAIVision(VLMBackend backend) {
        this.backend = backend;
    }

    public static FastAIVision createQwen2VL() {
        return new FastAIVision(VLMBackend.QWEN2_VL_2B);
    }

    public static FastAIVision createSmolVLM() {
        return new FastAIVision(VLMBackend.SMOLVLM_2B);
    }

    public VLMBackend getBackend() {
        return backend;
    }

    /**
     * Formats prompt for model specific grounding template.
     */
    public String buildGroundingPrompt(String targetDescription) {
        if (backend == VLMBackend.QWEN2_VL_2B) {
            return "<|im_start|>system\nYou are a helpful vision-language assistant for UI grounding.<|im_end|>\n" +
                    "<|im_start|>user\nLocate the UI element: '" + targetDescription + "'. Return the bounding box coordinates [ymin, xmin, ymax, xmax] in 0-1000 range.<|im_end|>\n" +
                    "<|im_start|>assistant\n";
        } else {
            return "Task: Ground UI element.\nElement: " + targetDescription + "\nOutput: <box>(ymin, xmin, ymax, xmax)</box>";
        }
    }

    /**
     * Parses VLM grounding inference result text into a UIElement.
     */
    public UIElement parseGroundingResponse(String targetLabel, String vlmResponse, UIElement.ElementType expectedType) {
        BoundingBox box = GroundingParser.extractFirstBoundingBox(vlmResponse);
        if (box == null) {
            box = new BoundingBox(0.0f, 0.0f, 0.0f, 0.0f);
        }
        return new UIElement(targetLabel, box, 0.95f, expectedType);
    }

    /**
     * Downscales and pre-processes screen image into direct RGB tensor float buffer for VLM input.
     */
    public float[] preprocessImage(BufferedImage image, int targetWidth, int targetHeight) {
        float[] tensor = new float[3 * targetWidth * targetHeight];
        int w = image.getWidth();
        int h = image.getHeight();

        int pixelCount = targetWidth * targetHeight;
        for (int y = 0; y < targetHeight; y++) {
            int srcY = (y * h) / targetHeight;
            for (int x = 0; x < targetWidth; x++) {
                int srcX = (x * w) / targetWidth;
                int rgb = image.getRGB(srcX, srcY);

                float r = ((rgb >> 16) & 0xFF) / 255.0f;
                float g = ((rgb >> 8) & 0xFF) / 255.0f;
                float b = (rgb & 0xFF) / 255.0f;

                int idx = (y * targetWidth) + x;
                tensor[idx] = r;
                tensor[pixelCount + idx] = g;
                tensor[(2 * pixelCount) + idx] = b;
            }
        }
        return tensor;
    }
}
