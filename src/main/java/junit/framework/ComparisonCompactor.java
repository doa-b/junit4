package junit.framework;

public class ComparisonCompactor {

    private static final String ELLIPSIS = "...";
    private static final String DELTA_END = "]";
    private static final String DELTA_START = "[";

    private int contextLength;
    private String expected;
    private String actual;
    private String compactExpected;
    private String compactActual;
    private int prefixLength;
    private int suffixLength;

    public ComparisonCompactor(int contextLength, String expected, String actual) {
        this.contextLength = contextLength;
        this.expected = expected;
        this.actual = actual;
    }

    @SuppressWarnings("deprecation")
    public String formatCompactedComparison(String message) {
        if (shouldBeCompacted()) {
            compactExpectedAndActual();
            return Assert.format(message, compactExpected, compactActual);
        } else {
            return Assert.format(message, expected, actual);
        }
    }

    private void compactExpectedAndActual() {
        findCommonPrefixAndSuffix();
        compactExpected = compactString(expected);
        compactActual = compactString(actual);
    }

    private boolean shouldBeCompacted() {
        return !shouldNotBeCompacted();
    }

    private boolean shouldNotBeCompacted() {
        return expected == null || actual == null || expected.equals(actual);
    }

    private String compactString(String source) {
        return computeCommonPrefix() +
                DELTA_START +
                source.substring(prefixLength, source.length() - suffixLength) +
                DELTA_END +
                computeCommonSuffix();
    }

    private void findCommonPrefixAndSuffix() {
        findCommonPrefix();
        suffixLength = 0;
        for (; !suffixOverlapsPrefix(); suffixLength++) {
            if (charFromEnd(expected, suffixLength) != charFromEnd(actual, suffixLength)) break;
        }
    }

    private boolean suffixOverlapsPrefix() {
        return actual.length() - suffixLength <= prefixLength ||
                expected.length() - suffixLength <= prefixLength;
    }

    private void findCommonPrefix() {
        prefixLength = 0;
        int end = Math.min(expected.length(), actual.length());
        for (; prefixLength < end; prefixLength++) {
            if (expected.charAt(prefixLength) != actual.charAt(prefixLength)) {
                break;
            }
        }
    }

    private char charFromEnd(String s, int i) {
        return s.charAt((s.length() - i - 1));
    }



    private String computeCommonPrefix() {
        return (prefixLength > contextLength ? ELLIPSIS : "") + expected.substring(Math.max(0, prefixLength - contextLength), prefixLength);
    }

    private String computeCommonSuffix() {
        int end = Math.min(expected.length() - suffixLength + contextLength, expected.length());
        return
                expected.substring(expected.length() - suffixLength, end) +
                        (expected.length() - suffixLength <
                                expected.length() - contextLength ?
                                ELLIPSIS : "");
    }

    private boolean areStringsEqual() {
        return expected.equals(actual);
    }

    private String compact(String s) {
        return new StringBuilder()
                .append(startingElipsis())
                .append(startingContext())
                .append(DELTA_START)
                .append(delta(s))
                .append(endingContext())
                .append(endingElipsis())
                .toString();
    }

    private String startingElipsis() {
        return prefixLength > contextLength ? ELLIPSIS: "";
    }

    private String startingContext() {
        int contextStart = Math.max(0, prefixLength - contextLength);
        int contextEnd = prefixLength;
        return expected.substring(contextStart, contextEnd);
    }

    private String delta (String s) {
        int deltaStart = prefixLength;
        int deltaEnd = s.length() - suffixLength;
        return s.substring(deltaStart, deltaEnd);
    }

    private String endingContext() {
        int contextStart = expected.length() - suffixLength;
        int contextEnd = Math.min(contextStart + contextLength, expected.length());
        return expected.substring(contextStart, contextEnd);
    }

    private String endingElipsis() {
        return suffixLength > contextLength ? ELLIPSIS: "";
    }
}
