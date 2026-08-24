# FastAIVision API Reference

## Core Classes

### 1. `fastaivision.FastAIVision`
* `public static FastAIVision createQwen2VL()`: Creates Qwen2-VL grounding engine.
* `public static FastAIVision createSmolVLM()`: Creates SmolVLM grounding engine.
* `public String buildGroundingPrompt(String targetDescription)`: Formats grounding prompt template.
* `public UIElement parseGroundingResponse(String targetLabel, String vlmResponse, UIElement.ElementType expectedType)`: Converts output text to `UIElement`.
* `public float[] preprocessImage(BufferedImage image, int targetWidth, int targetHeight)`: Scales and normalizes screen image to float RGB tensor buffer.

### 2. `fastaivision.GroundingParser`
* `public static BoundingBox extractFirstBoundingBox(String vlmOutput)`: Extracts normalized bounding box coordinates from arbitrary VLM response text.
* `public static BoundingBox parseFrom1000Scale(int ymin, int xmin, int ymax, int xmax)`: Converts standard 0-1000 integer range coordinates to [0.0 - 1.0] normalized space.

### 3. `fastaivision.VisionCodec`
* `public static byte[] encode(List<UIElement> elements)`: FastFileFormat binary serializer.
* `public static List<UIElement> decode(byte[] bytes)`: FastFileFormat binary deserializer.
* `public static void writeToFile(Path path, List<UIElement> elements)`: Writes elements directly to `.visionbin` file.
* `public static List<UIElement> readFromFile(Path path)`: Reads elements directly from `.visionbin` file.
