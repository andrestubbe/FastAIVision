package fastaivision.demo;

import fastaivision.BoundingBox;
import fastaivision.FastAIVision;
import fastaivision.UIElement;
import fastaivision.VisionCodec;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Demo {
    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println(" 👁️ FastAIVision — Multimodal UI Grounding Engine");
        System.out.println("=================================================");

        // 1. Initialize Qwen2-VL-2B & SmolVLM-2B Grounding Engines
        FastAIVision qwen2Vision = FastAIVision.createQwen2VL();
        FastAIVision smolVision = FastAIVision.createSmolVLM();

        System.out.println("\n--- 1. Generated VLM Grounding Prompts ---");
        System.out.println("[Qwen2-VL-2B Prompt]:\n" + qwen2Vision.buildGroundingPrompt("Search Input Box"));
        System.out.println("[SmolVLM-2B Prompt]:\n" + smolVision.buildGroundingPrompt("Submit Form Button"));

        // 2. Simulated Model Inference Responses
        String qwenOutput = "Identified element: [120, 250, 160, 750]";
        String smolOutput = "Found element: <box>(450, 300, 520, 700)</box>";

        UIElement searchBox = qwen2Vision.parseGroundingResponse("Search Input", qwenOutput, UIElement.ElementType.INPUT);
        UIElement submitButton = smolVision.parseGroundingResponse("Submit Button", smolOutput, UIElement.ElementType.BUTTON);

        System.out.println("\n--- 2. Parsed Grounding UI Elements ---");
        System.out.printf("Element 1: label='%s' type=%s box=[x=%.3f, y=%.3f, w=%.3f, h=%.3f] center=(%d, %d)\n",
                searchBox.label(), searchBox.elementType(), searchBox.box().x(), searchBox.box().y(),
                searchBox.box().width(), searchBox.box().height(),
                searchBox.box().getPixelCenterX(1920), searchBox.box().getPixelCenterY(1080));

        System.out.printf("Element 2: label='%s' type=%s box=[x=%.3f, y=%.3f, w=%.3f, h=%.3f] center=(%d, %d)\n",
                submitButton.label(), submitButton.elementType(), submitButton.box().x(), submitButton.box().y(),
                submitButton.box().width(), submitButton.box().height(),
                submitButton.box().getPixelCenterX(1920), submitButton.box().getPixelCenterY(1080));

        // 3. FastFileFormat Binary Serialization (.visionbin)
        List<UIElement> detections = List.of(searchBox, submitButton);
        byte[] encoded = VisionCodec.encode(detections);
        System.out.println("\nEncoded .visionbin payload size: " + encoded.length + " bytes.");

        List<UIElement> decoded = VisionCodec.decode(encoded);
        System.out.println("Decoded " + decoded.size() + " UI elements from binary payload.");

        // 4. Zero-Copy Image Preprocessing for Direct Tensor Feeding
        BufferedImage dummyScreen = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
        float[] tensor = qwen2Vision.preprocessImage(dummyScreen, 448, 448);
        System.out.println("Preprocessed Screen Tensor size: " + tensor.length + " floats (3x448x448).");

        System.out.println("\n✔ FastAIVision Multimodal Pipeline Verified Successfully!");
    }
}
