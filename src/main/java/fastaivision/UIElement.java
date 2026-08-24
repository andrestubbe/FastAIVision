package fastaivision;

/**
 * Represents a recognized GUI UI element or visual object with grounding coordinates.
 *
 * @param label Textual label or description of the element (e.g., "Submit Button", "Search Input").
 * @param box Normalized bounding box coordinates.
 * @param confidence Confidence score (0.0 to 1.0).
 * @param elementType Type category (BUTTON, INPUT, ICON, TEXT, WINDOW, UNKNOWN).
 */
public record UIElement(
        String label,
        BoundingBox box,
        float confidence,
        ElementType elementType
) {
    public enum ElementType {
        BUTTON,
        INPUT,
        ICON,
        TEXT,
        WINDOW,
        MENU,
        UNKNOWN
    }
}
