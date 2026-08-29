package fastaivision.tracking;

import fastaivision.BoundingBox;
import fastaivision.detection.DetectedObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ByteTracker {

    private int nextTrackId = 1;
    private final List<TrackedObject> activeTracks = new ArrayList<>();
    private final float iouThreshold;
    private final int maxMissingFrames;

    public ByteTracker() {
        this(0.35f, 15);
    }

    public ByteTracker(float iouThreshold, int maxMissingFrames) {
        this.iouThreshold = iouThreshold;
        this.maxMissingFrames = maxMissingFrames;
    }

    /**
     * Updates active tracks with new frame detections using IoU matching.
     */
    public synchronized List<TrackedObject> update(List<DetectedObject> detections) {
        boolean[] matchedDetections = new boolean[detections.size()];
        boolean[] matchedTracks = new boolean[activeTracks.size()];

        // 1. Match active tracks with current detections by max IoU
        for (int t = 0; t < activeTracks.size(); t++) {
            TrackedObject track = activeTracks.get(t);
            float bestIou = iouThreshold;
            int bestDetIdx = -1;

            for (int d = 0; d < detections.size(); d++) {
                if (matchedDetections[d]) continue;
                DetectedObject det = detections.get(d);
                if (det.classId() != track.getClassId()) continue;

                float iou = track.getBox().iou(det.box());
                if (iou > bestIou) {
                    bestIou = iou;
                    bestDetIdx = d;
                }
            }

            if (bestDetIdx != -1) {
                DetectedObject det = detections.get(bestDetIdx);
                track.update(det.box(), det.confidence());
                matchedTracks[t] = true;
                matchedDetections[bestDetIdx] = true;
            }
        }

        // 2. Mark unmatched tracks as missed
        Iterator<TrackedObject> it = activeTracks.iterator();
        int tIdx = 0;
        while (it.hasNext()) {
            TrackedObject track = it.next();
            if (!matchedTracks[tIdx]) {
                track.markMissed();
                if (track.getConsecutiveMisses() > maxMissingFrames) {
                    it.remove();
                }
            }
            tIdx++;
        }

        // 3. Spawn new tracks for unmatched detections
        for (int d = 0; d < detections.size(); d++) {
            if (!matchedDetections[d]) {
                DetectedObject det = detections.get(d);
                activeTracks.add(new TrackedObject(nextTrackId++, det.classId(), det.label(), det.box(), det.confidence()));
            }
        }

        return new ArrayList<>(activeTracks);
    }

    public synchronized List<TrackedObject> getActiveTracks() {
        return new ArrayList<>(activeTracks);
    }
}