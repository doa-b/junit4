package junit.framework;

public class ComparisonCompactor {

    private static final String ELLIPSIS = "...";
    private static final String DELTA_END = "]";
    private static final String DELTA_START = "[";

    private int fContextLength;
    private String expected;
    private String actual;
    private  String compactExpected;
    private String compactActual;
    private int prefixIndex;
    private int suffixIndex;

    public ComparisonCompactor(int contextLength, String expected, String actual) {
        fContextLength = contextLength;
        this.expected = expected;
        this.actual = actual;
    }

    @SuppressWarnings("deprecation")
    public String formatCompactedComparison(String message) {
        if (canBeCompacted()) {
           compactExpectedAndActual();
            return Assert.format(message, compactExpected, compactActual);
        } else {
            return Assert.format(message, expected, actual);
        }
    }

    private void compactExpectedAndActual() {
        prefixIndex = findCommonPrefix();
        suffixIndex = findCommonSuffix();
        compactExpected = compactString(expected);
        compactActual = compactString(actual);
    }

    private boolean canBeCompacted() {
        return expected != null && actual != null && !areStringsEqual();
    }

    private String compactString(String source) {
        String result = DELTA_START + source.substring(prefixIndex, source.length() - suffixIndex + 1) + DELTA_END;
        if (prefixIndex > 0) {
            result = computeCommonPrefix() + result;
        }
        if (suffixIndex > 0) {
            result = result + computeCommonSuffix();
        }
        return result;
    }

    private int findCommonPrefix() {
        int index = 0;
        int end = Math.min(expected.length(), actual.length());
        for (; index < end; index++) {
            if (expected.charAt(index) != actual.charAt(index)) {
                break;
            }
        }
        return index;
    }

    private int findCommonSuffix() {
        int expectedSuffix = expected.length() - 1;
        int actualSuffix = actual.length() - 1;
        for (; actualSuffix >= prefixIndex && expectedSuffix >= prefixIndex; actualSuffix--, expectedSuffix--) {
            if (expected.charAt(expectedSuffix) != actual.charAt(actualSuffix)) {
                break;
            }
        }
        return expected.length() - expectedSuffix;
    }

    private String computeCommonPrefix() {
        return (prefixIndex > fContextLength ? ELLIPSIS : "") + expected.substring(Math.max(0, prefixIndex - fContextLength), prefixIndex);
    }

    private String computeCommonSuffix() {
        int end = Math.min(expected.length() - suffixIndex + 1 + fContextLength, expected.length());
        return expected.substring(expected.length() - suffixIndex + 1, end) + (expected.length() - suffixIndex + 1 < expected.length() - fContextLength ? ELLIPSIS : "");
    }

    private boolean areStringsEqual() {
        return expected.equals(actual);
    }
}
