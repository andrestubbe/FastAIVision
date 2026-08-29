package fastaivision;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Standard parser for VLM bounding box coordinates output by Qwen2-VL, SmolVLM, and UI-Grounding models.
 * Parses patterns like `[ymin, xmin, ymax, xmax]` or `<box>(ymin, xmin, ymax, xmax)</box>` and converts to normalized BoundingBox.
 */
public final class GroundingParser {

    private static final Pattern BOX_1000_REGEX = Pattern.compile("\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]");
    private static final Pattern XML_BOX_REGEX = Pattern.compile("<box>\\((\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\)</box>");

    private GroundingParser() {}

    /**
     * Parses standard 1000-scaled coordinate strings into a normalized BoundingBox (0.0 to 1.0).
     */
    public static BoundingBox parseFrom1000Scale(int ymin, int xmin, int ymax, int xmax) {
        float x = Math.min(xmin, xmax) / 1000.0f;
        float y = Math.min(ymin, ymax) / 1000.0f;
        float w = Math.abs(xmax - xmin) / 1000.0f;
        float h = Math.abs(ymax - ymin) / 1000.0f;
        return new BoundingBox(x, y, w, h);
    }

    /**
     * Extracts bounding box from raw VLM textual output string.
     */
    public static BoundingBox extractFirstBoundingBox(String vlmOutput) {
        if (vlmOutput == null || vlmOutput.isEmpty()) {
            return null;
        }

        Matcher m1 = BOX_1000_REGEX.matcher(vlmOutput);
        if (m1.find()) {
            int ymin = Integer.parseInt(m1.group(1));
            int xmin = Integer.parseInt(m1.group(2));
            int ymax = Integer.parseInt(m1.group(3));
            int xmax = Integer.parseInt(m1.group(4));
            return parseFrom1000Scale(ymin, xmin, ymax, xmax);
        }

        Matcher m2 = XML_BOX_REGEX.matcher(vlmOutput);
        if (m2.find()) {
            int ymin = Integer.parseInt(m2.group(1));
            int xmin = Integer.parseInt(m2.group(2));
            int ymax = Integer.parseInt(m2.group(3));
            int xmax = Integer.parseInt(m2.group(4));
            return parseFrom1000Scale(ymin, xmin, ymax, xmax);
        }

        return null;
    }
}