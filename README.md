# FastAIVision 0.1.1 [ALPHA] â€” Multimodal Vision, UI-Element Grounding & Screen-VLM Engine

[![Status](https://img.shields.io/badge/status-0.1.1-brightgreen.svg)](https://github.com/andrestubbe/FastAIVision/releases/tag/0.1.1)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Cross--Platform-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastAIVision)

---

**âš¡ High-speed local multimodal vision, UI-element grounding, zero-copy image preprocessing, and `.visionbin` binary streaming engine for Java.**

**FastAIVision** connects compact Vision-Language Models (**Qwen2-VL-2B**, **SmolVLM-2B**) with autonomous desktop GUI agents (`FastAIAgent`, `FastRobot`). It transforms raw desktop screenshots into structured, normalized bounding-box coordinates for buttons, inputs, icons, and menus with sub-millisecond parsing and FastFileFormat binary persistence.

---

## Quick Start

```java
import fastaivision.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
        // 1. Initialize Qwen2-VL or SmolVLM Grounding Engine
        FastAIVision vision = FastAIVision.createQwen2VL();

        // 2. Build prompt & parse model response
        String prompt = vision.buildGroundingPrompt("Search Input Box");
        String vlmOutput = "The element is at [120, 250, 160, 750].";

        UIElement searchInput = vision.parseGroundingResponse(
                "Search Input", vlmOutput, UIElement.ElementType.INPUT
        );

        System.out.printf("Center Pixel at 1080p: (%d, %d)\n",
                searchInput.box().getPixelCenterX(1920),
                searchInput.box().getPixelCenterY(1080));

        // 3. Compact FastFileFormat Binary Serialization (.visionbin)
        byte[] binary = VisionCodec.encode(List.of(searchInput));
        List<UIElement> restored = VisionCodec.decode(binary);
    }
}
```

---

## ðŸ“‘ Table of Contents

- [Why FastAIVision?](#why-fastaivision)
- [Key Features](#key-features)
- [Real-World Scenarios](#real-world-scenarios)
- [Performance Benchmarks](#performance-benchmarks)
- [API Quick Reference](#api-quick-reference)
- [Technical Examples & Hero Demos](#technical-examples--hero-demos)
- [Installation](#installation)
- [Documentation](#documentation)
- [License](#license)

---

## Why FastAIVision?

> [!IMPORTANT]
> **"Sub-Millisecond Coordinate Grounding Over DOM Traversal. Zero-Copy Tensors Over JVM Image Churn."**

Standard desktop automation tools rely on brittle accessibility trees (UIA/DOM) or heavyweight OCR frameworks that fail on dynamic canvas elements and custom game/desktop widgets.

`FastAIVision` transforms lightweight Vision-Language Models (**Qwen2-VL**, **SmolVLM**) into ultra-low-latency desktop grounding sensors:

1. **Deterministic Bounding Box Extraction**: Directly translates VLM token streams into normalized screen coordinates using zero-allocation `FastRegex` scanners.
2. **Sub-Pixel Precision**: Maps floating-point model bounding boxes to arbitrary monitor resolutions and multi-DPI desktop coordinates.
3. **Zero-Copy Tensor Normalization**: Converts raw desktop frames into normalized float arrays directly consumable by ONNX/DirectML runtimes without intermediate AWT image conversions.

---

## Key Features

- **ðŸ‘ï¸ Multimodal UI-Grounding** â€” Direct coordinate parsing for Qwen2-VL-2B (`[ymin, xmin, ymax, xmax]`) and SmolVLM-2B (`<box>(...)</box>`).
- **ðŸŽ¯ Coordinate Space Translation** â€” Normalized bounding box space (0.0 to 1.0) with instant screen resolution pixel mapping.
- **âš¡ Zero-Copy Tensor Preprocessing** â€” High-speed RGB buffer extraction for direct ONNX/DirectML and GGUF matrix feeding.
- **ðŸ“¦ FastFileFormat `.visionbin` Compression** â€” High-density binary persistence for vision recognition snapshots (Payload ID `0x0006`).
- **ðŸ¤– Autonomous Agent Synergy** â€” Direct integration with `FastAIAgent`, `FastRobot`, `FastScreen`, and `FastAIState`.

---

## Real-World Scenarios

- **ðŸ¤– Autonomous GUI Navigation** â€” Empowering AI agents to click buttons and type into textboxes on desktop applications via vision alone.
- **ðŸ“± Cross-Platform UI Testing** â€” Automated visual test assertions and responsive layout verification without brittle XPath/DOM selectors.
- **ðŸ›¡ï¸ Visual Bot Detection & Verification** â€” Recognizing visual CAPTCHA elements and dynamic UI prompts in real-time.
- **â™¿ Visual Accessibility Tools** â€” Real-time bounding-box screen narration and focus-target highlighting.

---

## Performance Benchmarks

FastAIVision is profiled using **JMH** to guarantee maximum parsing speed and zero vision stream bottleneck.

| Benchmark Operation | Score (ops/ms) | Throughput | Memory Overhead |
|---|---|---|---|
| **Grounding Coordinate Parsing** | **~1,900 ops/ms** | **> 1.9 Million boxes/sec** | **0 bytes allocation** |
| **Binary Stream Decoding (`.visionbin`)** | **~345,000 ops/ms** | **> 345 Million elements/sec** | **Zero-Copy Streaming** |
| **Binary Stream Encoding (`.visionbin`)** | **~47,000 ops/ms** | **> 47 Million elements/sec** | **Compact VarInt Delta Buffer** |

*Run the benchmarks locally:* `.\run-benchmark.bat`

---

## API Quick Reference

| Method / Class | Description |
|---|---|
| `FastAIVision.createQwen2VL()` / `createSmolVLM()` | Initializes VLM-specific vision grounding engine. |
| `vision.buildGroundingPrompt(target)` | Formats standard prompt template for model UI grounding. |
| `vision.parseGroundingResponse(label, out, type)` | Parses raw VLM output text into a structured `UIElement`. |
| `vision.preprocessImage(image, w, h)` | Transforms `BufferedImage` into normalized float RGB tensor array. |
| `VisionCodec.encode(elements)` | Serializes UI elements into compressed FastFileFormat binary byte array. |
| `VisionCodec.decode(bytes)` | Deserializes `.visionbin` binary bytes back into `List<UIElement>`. |

---

## Technical Examples & Hero Demos

| Case | Java Example | Launcher | Description |
|---|---|---|---|
| **Live VLM Grounding & Tensor Demo** | [Demo.java](examples/Demo/src/main/java/fastaivision/demo/Demo.java) | `run-demo.bat` | Qwen2-VL & SmolVLM prompt formatting, grounding coordinate extraction, and `.visionbin` streaming. |
| **JMH Microbenchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastaivision/benchmark/Benchmark.java) | `run-benchmark.bat` | High-throughput grounding coordinate parsing and 100-element binary codec benchmarks. |

---

## Installation

### Option 1: Maven (JitPack)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastAIVision</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastFileFormat</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastBinary</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastAIVision:0.1.0'
    implementation 'com.github.andrestubbe:FastFileFormat:0.1.0'
    implementation 'com.github.andrestubbe:FastBinary:0.1.0'
    implementation 'com.github.andrestubbe:fastcore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JARs directly to add them to your classpath:

1. ðŸ‘ï¸ **[FastAIVision-0.1.0.jar](https://github.com/andrestubbe/FastAIVision/releases/download/0.1.0/FastAIVision-0.1.0.jar)** (Multimodal Vision & UI Grounding Engine)
2. ðŸ“„ **[FastFileFormat-0.1.0.jar](https://github.com/andrestubbe/FastFileFormat/releases/download/0.1.0/FastFileFormat-0.1.0.jar)** (Dual Binary & Text File Format)
3. âš¡ **[FastBinary-0.1.0.jar](https://github.com/andrestubbe/FastBinary/releases/download/0.1.0/FastBinary-0.1.0.jar)** (VarInt & Binary Packing)
4. âš™ï¸ **[fastcore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/fastcore-0.1.0.jar)** (Foundation Library)

---

## Documentation

* **[REFERENCE.md](docs/REFERENCE.md)**: Full API reference and method signatures.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Architectural design principles and multimodal agent grounding.
* **[CHANGELOG.md](docs/CHANGELOG.md)**: Release history and version notes.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones and planned features.
* **[COMPILE.md](docs/COMPILE.md)**: Instructions for compiling from source.

---

## Platform Support

| Platform | Status |
|----------|--------|
| Windows 10/11 (x64) | ✅ Fully Supported |
| Linux | 🚧 Planned |
| macOS | 🚧 Planned |

---

## License

MIT License. See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastAIModel](https://github.com/andrestubbe/FastAIModel) â€” Local ONNX & GGUF matrix inference runtime
- [FastAIAgent](https://github.com/andrestubbe/FastAIAgent) â€” Autonomous agent logic, tools & planning
- [FastRobot](https://github.com/andrestubbe/FastRobot) â€” Ultra-low latency native OS input automation
- [FastScreen](https://github.com/andrestubbe/FastScreen) â€” High-speed desktop screen capture
- [FastFileFormat](https://github.com/andrestubbe/FastFileFormat) â€” Universal dual-format binary & text document engine

---

**Part of the FastJava Ecosystem** â€” *Making the JVM faster. Small package. Maximum speed. Zero bloat. ðŸš€ðŸ“‹*
