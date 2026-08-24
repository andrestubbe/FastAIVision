package fastaivision.benchmark;

import fastaivision.BoundingBox;
import fastaivision.GroundingParser;
import fastaivision.UIElement;
import fastaivision.VisionCodec;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class Benchmark {

    private String qwen2Sample;
    private List<UIElement> sampleElements;
    private byte[] sampleBinary;

    @Setup
    public void setup() {
        qwen2Sample = "The target login button is located at coordinates: [340, 450, 390, 620].";
        sampleElements = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            sampleElements.add(new UIElement(
                    "Element-" + i,
                    new BoundingBox(0.1f + (i * 0.005f), 0.2f + (i * 0.004f), 0.15f, 0.05f),
                    0.95f,
                    UIElement.ElementType.BUTTON
            ));
        }
        sampleBinary = VisionCodec.encode(sampleElements);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public BoundingBox benchmarkGroundingParsing() {
        return GroundingParser.extractFirstBoundingBox(qwen2Sample);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public byte[] benchmarkEncode100UIElements() {
        return VisionCodec.encode(sampleElements);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public List<UIElement> benchmarkDecode100UIElements() {
        return VisionCodec.decode(sampleBinary);
    }
}
