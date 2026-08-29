package fastaivision.tracking;

import fastaivision.BoundingBox;

public final class TrackedObject {
    private final int trackId;
    private final int classId;
    private final String label;
    private BoundingBox box;
    private float confidence;
    private int age;
    private int consecutiveMisses;

    public TrackedObject(int trackId, int classId, String label, BoundingBox box, float confidence) {
        this.trackId = trackId;
        this.classId = classId;
        this.label = label;
        this.box = box;
        this.confidence = confidence;
        this.age = 1;
        this.consecutiveMisses = 0;
    }

    public void update(BoundingBox newBox, float newConf) {
        this.box = newBox;
        this.confidence = newConf;
        this.age++;
        this.consecutiveMisses = 0;
    }

    public void markMissed() {
        this.consecutiveMisses++;
    }

    public int getTrackId() { return trackId; }
    public int getClassId() { return classId; }
    public String getLabel() { return label; }
    public BoundingBox getBox() { return box; }
    public float getConfidence() { return confidence; }
    public int getAge() { return age; }
    public int getConsecutiveMisses() { return consecutiveMisses; }
}