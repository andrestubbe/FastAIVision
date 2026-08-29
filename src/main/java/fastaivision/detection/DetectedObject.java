package fastaivision.detection;

import fastaivision.BoundingBox;

public record DetectedObject(
    int classId,
    String label,
    float confidence,
    BoundingBox box
) {}