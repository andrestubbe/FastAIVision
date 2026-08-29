# FastAIVision Reference & API Specification

## 1. Core Vocabulary

*   **Detection**: Single-frame classification and bounding box localization (e.g. YOLOv8, RF-DETR).
*   **Tracking (ByteTrack)**: Persistent association of detections across continuous video frames using IoU overlap matching.
*   **IoU (Intersection over Union)**: The geometric area of box overlap divided by total union area ($0.0$ to $1.0$).
*   **Grounding**: Translating natural language requests into normalized 2D coordinate targets ($[x, y, w, h]$).
*   **TrackedObject**: A stateful object maintaining continuous track ID, confidence history, age, and missed frames.

## 2. API Quick Reference

### `ByteTracker`
*   `ByteTracker(float iouThreshold, int maxMissingFrames)`: Instantiates a persistent multi-object tracker.
*   `update(List<DetectedObject> detections)`: Ingests new detections and returns updated list of `TrackedObject`.
*   `getActiveTracks()`: Returns currently tracked objects.

### `BoundingBox`
*   `centerX()` / `centerY()`: Computes normalized center coordinate.
*   `getPixelCenterX(screenWidth)`: Computes actual pixel center coordinate on screen.
*   `iou(BoundingBox other)`: Computes geometric Intersection-over-Union.

---
**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*