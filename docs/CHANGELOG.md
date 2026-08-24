# Changelog: FastAIVision

All notable changes to this project will be documented in this file.

## [0.1.0] - 2026-08-24
### Added
- **Multimodal VLM Grounding Engine (`FastAIVision`)**: Qwen2-VL-2B and SmolVLM-2B prompt formatting and coordinate parsing.
- **FastFileFormat Binary Streamer (`VisionCodec`)**: High-density `.visionbin` detection snapshot persistence (Payload ID `0x0006`).
- **Zero-Copy Image Preprocessor**: Fast float RGB tensor normalization for direct model matrix input.
- **Interactive Showcase & JMH Benchmark Suite**: Profiling >1.9M coordinate extractions/sec and >345M element decodes/sec.
