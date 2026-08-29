package fastaivision;

import fastaivision.detection.DetectedObject;
import fastaivision.tracking.ByteTracker;
import fastaivision.tracking.TrackedObject;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FastAIVisionTest {

    @Test
    public void testBoundingBoxIoU() {
        BoundingBox a = new BoundingBox(0.0f, 0.0f, 0.5f, 0.5f);
        BoundingBox b = new BoundingBox(0.25f, 0.0f, 0.5f, 0.5f);

        float iou = a.iou(b);
        assertTrue(iou > 0.3f && iou < 0.4f);
    }

    @Test
    public void testByteTrackerPersistence() {
        ByteTracker tracker = new ByteTracker(0.25f, 3);

        List<DetectedObject> frame1 = List.of(
            new DetectedObject(1, "button", 0.95f, new BoundingBox(0.1f, 0.1f, 0.2f, 0.2f))
        );
        List<TrackedObject> tracks1 = tracker.update(frame1);
        assertEquals(1, tracks1.size());
        int trackId = tracks1.get(0).getTrackId();

        List<DetectedObject> frame2 = List.of(
            new DetectedObject(1, "button", 0.92f, new BoundingBox(0.11f, 0.11f, 0.2f, 0.2f))
        );
        List<TrackedObject> tracks2 = tracker.update(frame2);
        assertEquals(1, tracks2.size());
        assertEquals(trackId, tracks2.get(0).getTrackId());
        assertEquals(2, tracks2.get(0).getAge());
    }
}