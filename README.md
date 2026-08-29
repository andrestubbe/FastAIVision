# FastAIVision 0.1.1 [ALPHA] — Real-Time Multimodal Vision, YOLO Detection, ByteTrack & Screen-VLM Grounding for Java

[![Status](https://img.shields.io/badge/status-0.1.1-brightgreen.svg)](https://github.com/andrestubbe/FastAIVision/releases/tag/0.1.1)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Cross--Platform-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-0.1.1-green.svg)](https://jitpack.io/#andrestubbe/FastAIVision)

---

**⚡ Real-time YOLOv8/v11 & RF-DETR object detection, zero-allocation ByteTrack multi-object tracking, and Qwen2-VL/SmolVLM screen-grounding substrate for Java.**

**FastAIVision** is a high-throughput computer vision engine built for autonomous desktop automation (**[FastUIA](https://github.com/andrestubbe/FastUIA)**, **[FastRobot](https://github.com/andrestubbe/FastRobot)**) and live video analytics (**[FastCamera](https://github.com/andrestubbe/FastCamera)**, **[FastScreen](https://github.com/andrestubbe/FastScreen)**). It combines sub-5ms local neural detector inference with persistent multi-object tracking across video frames at tens of millions of operations per second.

---

## Quick Start

```java
import fastaivision.BoundingBox;
import fastaivision.detection.DetectedObject;
import fastaivision.tracking.ByteTracker;
import fastaivision.tracking.TrackedObject;
import java.util.List;

public class Example {
    public static void main(String[] args) {
        // 1. Initialize persistent ByteTracker
        ByteTracker tracker = new ByteTracker(0.35f, 15);

        // 2. Ingest detections from YOLO / RF-DETR frame
        List<DetectedObject> detections = List.of(
            new DetectedObject(1, "cow_eating", 0.94f, new BoundingBox(0.10f, 0.20f, 0.15f, 0.25f)),
            new DetectedObject(2, "cow_standing", 0.91f, new BoundingBox(0.50f, 0.40f, 0.18f, 0.30f))
        );

        // 3. Update tracking state across video stream
        List<TrackedObject> activeTracks = tracker.update(detections);
        for (TrackedObject t : activeTracks) {
            System.out.printf("Track ID #%d | Label: %s | Conf: %.2f | Center: (%.2f, %.2f)%n",
                t.getTrackId(), t.getLabel(), t.getConfidence(), t.getBox().centerX(), t.getBox().centerY());
        }
    }
}
```

---

## Table of Contents

- [Why FastAIVision?](#why-fastaivision)
- [Quick Start](#quick-start)
- [Features](#features)
- [Performance Benchmarks](#performance-benchmarks)
- [API Quick Reference](#api-quick-reference)
- [Technical Examples & Hero Demos](#technical-examples--hero-demos)
- [Installation](#installation)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

---

## Why FastAIVision?

Standard Java computer vision stacks either force heavy Python/OpenCV bindings or lack persistent object tracking across video streams:

- **Isolated Frame Syndrome**: Standard detectors classify objects frame-by-frame, losing identity across motion and temporary occlusions.
- **Latency Spikes**: Cloud multimodal vision APIs (GPT-4V) take 800–2,000 ms, making real-time desktop automation and camera tracking impossible.
- **Garbage Collection Overhead**: Allocating thousands of temporary rectangle objects causes JVM stutter during 60 FPS video pipelines.

**FastAIVision** solves this:

- **Zero-Allocation ByteTrack**: Maintains persistent object identities and trajectories with hardware-accelerated IoU matching at **> 12,500,000 ops/sec**.
- **Edge-Fast Local Detection**: Connects directly to compiled YOLO and RF-DETR models via **[FastAIModel](https://github.com/andrestubbe/FastAIModel)** ONNX execution.
- **Deterministic VLM Grounding**: Parses Qwen2-VL, SmolVLM, and UI-Grounding coordinate tokens into normalized pixel coordinates in `< 5 µs`.

---

## Features

- **🎯 Real-Time YOLO & RF-DETR Detection**: Sub-5ms object, bounding box, and UI-element extraction.
- **📍 Zero-Allocation ByteTrack**: High-throughput multi-object tracking across continuous video streams.
- **📐 Hardware-Accelerated IoU Matching**: Intersection-over-Union geometric operations running at **> 128,000,000 ops/sec**.
- **🧠 Screen-VLM Grounding**: Extracts click coordinates and interactive element targets directly from local multimodal LLMs.
- **📊 FastANSI 120-Column HUD**: Terminal telemetry displaying tracking age, confidence metrics, and pixel bounding boxes.

---

## Performance Benchmarks

FastAIVision is rigorously profiled using **JMH** to guarantee zero overhead.

| Metric / Operation Type | Score (ops/ms) | Ops per Second |
|---|---|---|
| **Bounding Box IoU Calculation** | **~128,533 ops/ms** | **> 128.5 Million** |
| **ByteTrack Multi-Object Update** | **~12,501 ops/ms** | **> 12.5 Million** |
| **VLM Grounding Coordinate Parsing** | **~3,056 ops/ms** | **> 3.05 Million** |

*Measured on Windows 11 x64, Intel Core i5 (Surface Pro 8), JDK 21.0.12.1.*

---

## API Quick Reference

| Method | Description |
|---|---|
| `FastAIVision.createYOLO()` | Instantiates YOLO detection substrate. |
| `FastAIVision.createQwen2VL()` | Instantiates Qwen2-VL screen-grounding parser. |
| `tracker.update(detections)` | Updates active object tracks and matches persistent IDs across frames. |
| `boundingBox.iou(other)` | Computes geometric Intersection-over-Union between two boxes. |
| `vision.parseGroundingResponse(...)` | Parses raw VLM output tokens into interactive `UIElement` records. |

---

## Technical Examples & Hero Demos

| Case | Java Example | Launcher | Description |
|---|---|---|---|
| **Interactive 120-Column HUD Demo** | [Demo.java](src/main/java/fastaivision/Demo.java) | `run-demo.bat` | Terminal demonstration of real-time object tracking persistence and VLM screen grounding. |
| **JMH Microbenchmark Suite** | [FastAIVisionBenchmark.java](examples/Benchmark/src/main/java/fastaivision/benchmark/FastAIVisionBenchmark.java) | `run-benchmark.bat` | Formal OpenJDK JMH throughput measurements across IoU calculations and tracking loops. |

---

## Installation

### Option 1: Maven (Recommended)

Add the JitPack repository and the dependencies to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <!-- FastAIVision Core -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastAIVision</artifactId>
        <version>0.1.1</version>
    </dependency>

    <!-- FastAIModel ONNX Execution Substrate -->
    <dependency>
        <groupId>com.github.andrestubbe.FastAIModel</groupId>
        <artifactId>fastaimodel-onnx</artifactId>
        <version>0.1.4</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastAIVision:0.1.1'
    implementation 'com.github.andrestubbe.FastAIModel:fastaimodel-onnx:0.1.4'
}
```

### Option 3: Direct Download (No Build Tool)
Download the latest JARs directly to add them to your classpath:

1. 📦 **[FastAIVision-0.1.1.jar](https://github.com/andrestubbe/FastAIVision/releases/download/0.1.1/FastAIVision-0.1.1.jar)** (The Core Vision Engine)
2. 🤖 **[fastaimodel-onnx-0.1.4.jar](https://github.com/andrestubbe/FastAIModel/releases/download/0.1.4/fastaimodel-onnx-0.1.4.jar)** (The ONNX Neural Inference Substrate)
3. ⚙️ **[fastcore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/fastcore-0.1.0.jar)** (The Mandatory Runtime Substrate)

### 🧠 Neural Model Weights & Downloads

FastAIVision executes standard ONNX detection models and GGUF multimodal VLMs:

| Model Type | Recommended Checkpoint | Format | Purpose | Upstream Link |
|---|---|---|---|---|
| **Real-Time Detection** | `yolov8n.onnx` / `yolov11n.onnx` | ONNX (~6 MB) | <5ms Object & UI Bounding Boxes | [Ultralytics YOLO Releases](https://github.com/ultralytics/assets/releases) |
| **Real-Time DETR** | `rf-detr-nano.onnx` | ONNX (~12 MB) | Transformer Object Detection | [RF-DETR ONNX Hub](https://huggingface.co/models?search=rf-detr) |
| **Screen VLM Grounding** | `Qwen2-VL-7B-Instruct` | GGUF / ONNX | Complex Screen Coordinate Grounding | [Qwen2-VL on HuggingFace](https://huggingface.co/Qwen/Qwen2-VL-7B-Instruct) |
| **Lightweight VLM** | `SmolVLM-500M-Instruct` | GGUF / ONNX | Fast Edge Multimodal Grounding | [HuggingFaceTB/SmolVLM-500M](https://huggingface.co/HuggingFaceTB/SmolVLM-500M-Instruct) |

---

## Documentation

* **[REFERENCE.md](docs/REFERENCE.md)**: Full API descriptions, tracking algorithms, and coordinate spaces.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: The architectural rationale for local real-time vision on the JVM.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future milestones, Kalman filtering, and native Vulkan/CUDA vision pipelines.
* **[CHANGELOG.md](docs/CHANGELOG.md)**: Release history and version migration details.

---

## Platform Support

| Platform | Status |
|---|---|
| Windows 10/11 (x64) | ✅ Fully Supported |
| Linux (x64 / AArch64) | ✅ Fully Supported |
| macOS (Apple Silicon / Intel) | ✅ Fully Supported |

---

## Related Projects

Combine FastAIVision with other FastJava vision and automation engines:

* [**FastScreen**](https://github.com/andrestubbe/FastScreen) — Ultra-fast native framebuffer screen capture.
* [**FastCamera**](https://github.com/andrestubbe/FastCamera) — Zero-copy direct video feed capture.
* [**FastUIA**](https://github.com/andrestubbe/FastUIA) — Windows UI Automation engine for autonomous agents.
* [**FastRobot**](https://github.com/andrestubbe/FastRobot) — Hardware-level keyboard and mouse simulation.

---

**Part of the FastJava Ecosystem** — *Making the JVM faster.*