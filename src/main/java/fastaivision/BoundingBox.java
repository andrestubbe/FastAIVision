package fastaivision;

/**
 * Normalized 2D Bounding Box in image coordinate space (0.0 to 1.0 or pixel coordinates).
 *
 * @param x Normalized top-left X coordinate.
 * @param y Normalized top-left Y coordinate.
 * @param width Normalized box width.
 * @param height Normalized box height.
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
}
