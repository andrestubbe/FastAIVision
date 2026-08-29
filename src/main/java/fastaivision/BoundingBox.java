package fastaivision;

/**
 * Normalized 2D Bounding Box in image coordinate space (0.0 to 1.0 or pixel coordinates).
 */
public record BoundingBox(float x, float y, float width, float height) {
    public float centerX() {
        return x + (width / 2.0f);
    }

    public float centerY() {
        return y + (height / 2.0f);
    }

    public int getPixelCenterX(int screenWidth) {
        return Math.round(centerX() * screenWidth);
    }

    public int getPixelCenterY(int screenHeight) {
        return Math.round(centerY() * screenHeight);
    }

    public boolean contains(float px, float py) {
        return px >= x && px <= (x + width) && py >= y && py <= (y + height);
    }

    /**
     * Computes Intersection-over-Union (IoU) with another bounding box.
     */
    public float iou(BoundingBox other) {
        float x1 = Math.max(this.x, other.x);
        float y1 = Math.max(this.y, other.y);
        float x2 = Math.min(this.x + this.width, other.x + other.width);
        float y2 = Math.min(this.y + this.height, other.y + other.height);

        float interArea = Math.max(0.0f, x2 - x1) * Math.max(0.0f, y2 - y1);
        float thisArea = this.width * this.height;
        float otherArea = other.width * other.height;
        float unionArea = thisArea + otherArea - interArea;

        return unionArea <= 0.0f ? 0.0f : interArea / unionArea;
    }
}