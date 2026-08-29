package fastaivision;

import fastaivision.detection.DetectedObject;
import fastaivision.tracking.ByteTracker;
import fastaivision.tracking.TrackedObject;

import java.util.List;

public final class FastAIVision {

    private final String modelName;
    private final ByteTracker tracker;

    public FastAIVision(String modelName) {
        this.modelName = modelName;
        this.tracker = new ByteTracker();
    }

    public static FastAIVision createQwen2VL() {
        return new FastAIVision("Qwen2-VL-7B-Instruct");
    }

    public static FastAIVision createSmolVLM() {
        return new FastAIVision("SmolVLM-500M-Instruct");
    }

    public static FastAIVision createYOLO() {
        return new FastAIVision("YOLOv8n-ONNX");
    }

    public List<TrackedObject> track(List<DetectedObject> detections) {
        return tracker.update(detections);
    }

    public UIElement parseGroundingResponse(String label, String vlmOutput, UIElement.ElementType type) {
        BoundingBox box = GroundingParser.extractFirstBoundingBox(vlmOutput);
        if (box == null) {
            box = new BoundingBox(0.0f, 0.0f, 0.0f, 0.0f);
        }
        return new UIElement(label, box, 1.0f, type);
    }

    public String getModelName() {
        return modelName;
    }
}