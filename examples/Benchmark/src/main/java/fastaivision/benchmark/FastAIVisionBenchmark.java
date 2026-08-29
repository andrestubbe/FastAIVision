package fastaivision.benchmark;

import fastaivision.BoundingBox;
import fastaivision.FastAIVision;
import fastaivision.UIElement;
import fastaivision.detection.DetectedObject;
import fastaivision.tracking.ByteTracker;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class FastAIVisionBenchmark {

    private BoundingBox boxA;
    private BoundingBox boxB;
    private ByteTracker tracker;
    private List<DetectedObject> frameDetections;
    private FastAIVision vision;
    private String vlmOutput;

    @Setup
    public void setup() {
        boxA = new BoundingBox(0.1f, 0.1f, 0.3f, 0.3f);
        boxB = new BoundingBox(0.15f, 0.15f, 0.3f, 0.3f);

        tracker = new ByteTracker(0.3f, 10);
        frameDetections = List.of(
            new DetectedObject(1, "cow", 0.92f, new BoundingBox(0.1f, 0.2f, 0.15f, 0.25f)),
            new DetectedObject(2, "person", 0.88f, new BoundingBox(0.5f, 0.4f, 0.18f, 0.30f)),
            new DetectedObject(3, "vehicle", 0.95f, new BoundingBox(0.7f, 0.1f, 0.20f, 0.20f))
        );

        vision = FastAIVision.createQwen2VL();
        vlmOutput = "The search input field is located at [120, 250, 160, 750].";
    }

    @Benchmark
    public float benchmarkBoundingBoxIoU() {
        return boxA.iou(boxB);
    }

    @Benchmark
    public Object benchmarkByteTrackerUpdate() {
        return tracker.update(frameDetections);
    }

    @Benchmark
    public UIElement benchmarkVlmGroundingParse() {
        return vision.parseGroundingResponse("Search Box", vlmOutput, UIElement.ElementType.INPUT);
    }
}