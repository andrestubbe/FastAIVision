package fastaivision;

import fastregex.FastRegex;
import fastregex.MatchResult;

/**
 * Standard parser for VLM bounding box coordinates output by Qwen2-VL, SmolVLM, and UI-Grounding models.
 * Parses patterns like `[ymin, xmin, ymax, xmax]` or `<box>(ymin, xmin, ymax, xmax)</box>` and converts to normalized BoundingBox.
 */
public final class GroundingParser {

    private static final FastRegex BOX_1000_REGEX = FastRegex.compile("\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]");
    private static final FastRegex XML_BOX_REGEX = FastRegex.compile("<box>\\((\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\)</box>");

    private static final ThreadLocal<MatchResult> MATCH_RESULT_HOLDER = ThreadLocal.withInitial(MatchResult::new);

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
     * Extracts bounding box from raw VLM textual output string using zero-allocation FastRegex.
     */
    public static BoundingBox extractFirstBoundingBox(String vlmOutput) {
        if (vlmOutput == null || vlmOutput.isEmpty()) {
            return null;
        }

        MatchResult result = MATCH_RESULT_HOLDER.get();

        if (BOX_1000_REGEX.find(vlmOutput, result)) {
            int ymin = result.parseGroupAsInt(vlmOutput, 1);
            int xmin = result.parseGroupAsInt(vlmOutput, 2);
            int ymax = result.parseGroupAsInt(vlmOutput, 3);
            int xmax = result.parseGroupAsInt(vlmOutput, 4);
            return parseFrom1000Scale(ymin, xmin, ymax, xmax);
        }

        if (XML_BOX_REGEX.find(vlmOutput, result)) {
            int ymin = result.parseGroupAsInt(vlmOutput, 1);
            int xmin = result.parseGroupAsInt(vlmOutput, 2);
            int ymax = result.parseGroupAsInt(vlmOutput, 3);
            int xmax = result.parseGroupAsInt(vlmOutput, 4);
            return parseFrom1000Scale(ymin, xmin, ymax, xmax);
        }

        return null;
    }
}
