package src.solutions;

import src.meta.DayTemplate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Day19 implements DayTemplate {

    private static final int ACCEPT = -1;
    private static final int REJECT = -2;
    private static final long RANGE_MASK = 0xfffL;
    private static final long INITIAL_LOW = pack(1, 1, 1, 1);
    private static final long INITIAL_HIGH = pack(4000, 4000, 4000, 4000);

    @Override
    public String solve(boolean part1, Scanner in) {
        Problem problem = parse(readAll(in));
        return part1 ? acceptedRatingSum(problem) : Long.toString(acceptedCombinations(problem));
    }

    @Override
    public String[] fullSolve(Scanner in) {
        Problem problem = parse(readAll(in));
        return new String[]{acceptedRatingSum(problem), Long.toString(acceptedCombinations(problem))};
    }

    private String readAll(Scanner in) {
        return in.useDelimiter("\\A").hasNext() ? in.next() : "";
    }

    private Problem parse(String input) {
        ArrayList<RawWorkflow> rawWorkflows = new ArrayList<>();
        Map<String, Integer> workflowIds = new HashMap<>();
        int[] parts = new int[64];
        int partSize = 0;
        boolean parsingWorkflows = true;
        int offset = 0;
        while (offset < input.length()) {
            int start = offset;
            while (offset < input.length() && input.charAt(offset) != '\n'
                    && input.charAt(offset) != '\r') {
                offset++;
            }
            int end = offset;
            if (offset < input.length()) {
                char ending = input.charAt(offset++);
                if (ending == '\r' && offset < input.length() && input.charAt(offset) == '\n') {
                    offset++;
                }
            }
            start = skipWhitespace(input, start, end);
            end = trimWhitespace(input, start, end);
            if (start == end) {
                parsingWorkflows = false;
                continue;
            }
            if (parsingWorkflows) {
                RawWorkflow raw = parseWorkflow(input, start, end);
                if (workflowIds.putIfAbsent(raw.name(), rawWorkflows.size()) != null) {
                    throw new IllegalArgumentException("Duplicate workflow " + raw.name());
                }
                rawWorkflows.add(raw);
            } else {
                if (partSize + 4 > parts.length) {
                    parts = Arrays.copyOf(parts, parts.length * 2);
                }
                parsePart(input, start, end, parts, partSize);
                partSize += 4;
            }
        }
        Integer startWorkflow = workflowIds.get("in");
        if (startWorkflow == null) {
            throw new IllegalArgumentException("Missing in workflow");
        }
        Workflow[] workflows = new Workflow[rawWorkflows.size()];
        for (int id = 0; id < workflows.length; id++) {
            RawWorkflow raw = rawWorkflows.get(id);
            int[] destinations = new int[raw.destinationNames().length];
            for (int i = 0; i < destinations.length; i++) {
                destinations[i] = destination(raw.destinationNames()[i], workflowIds);
            }
            workflows[id] = new Workflow(raw.categories(), raw.greater(), raw.thresholds(),
                    destinations, destination(raw.fallbackName(), workflowIds));
        }
        return new Problem(workflows, startWorkflow, Arrays.copyOf(parts, partSize));
    }

    private RawWorkflow parseWorkflow(String input, int start, int end) {
        int open = input.indexOf('{', start);
        int close = input.lastIndexOf('}', end - 1);
        if (open < start || open >= end || close < open || close >= end
                || skipWhitespace(input, close + 1, end) != end) {
            throw new IllegalArgumentException("Malformed workflow");
        }
        String name = token(input, start, open);
        int segmentCount = 1;
        for (int i = open + 1; i < close; i++) {
            if (input.charAt(i) == ',') {
                segmentCount++;
            }
        }
        byte[] categories = new byte[Math.max(0, segmentCount - 1)];
        boolean[] greater = new boolean[categories.length];
        int[] thresholds = new int[categories.length];
        String[] destinations = new String[categories.length];
        String fallback = null;
        int rule = 0;
        int segmentStart = open + 1;
        for (int i = segmentStart; i <= close; i++) {
            if (i != close && input.charAt(i) != ',') {
                continue;
            }
            int left = skipWhitespace(input, segmentStart, i);
            int right = trimWhitespace(input, left, i);
            if (left == right) {
                throw new IllegalArgumentException("Empty workflow rule");
            }
            int colon = input.indexOf(':', left);
            if (colon < 0 || colon >= right) {
                if (i != close || fallback != null) {
                    throw new IllegalArgumentException("Fallback must be last");
                }
                fallback = token(input, left, right);
            } else {
                if (rule >= categories.length) {
                    throw new IllegalArgumentException("Missing fallback");
                }
                int cursor = skipWhitespace(input, left, colon);
                if (cursor >= colon) {
                    throw new IllegalArgumentException("Missing category");
                }
                categories[rule] = category(input.charAt(cursor++));
                cursor = skipWhitespace(input, cursor, colon);
                if (cursor >= colon || (input.charAt(cursor) != '<' && input.charAt(cursor) != '>')) {
                    throw new IllegalArgumentException("Missing comparison");
                }
                greater[rule] = input.charAt(cursor++) == '>';
                IntToken threshold = integer(input, cursor, colon);
                if (skipWhitespace(input, threshold.next(), colon) != colon) {
                    throw new IllegalArgumentException("Malformed threshold");
                }
                thresholds[rule] = threshold.value();
                destinations[rule] = token(input, colon + 1, right);
                rule++;
            }
            segmentStart = i + 1;
        }
        if (fallback == null || rule != categories.length) {
            throw new IllegalArgumentException("Malformed workflow rules");
        }
        return new RawWorkflow(name, categories, greater, thresholds, destinations, fallback);
    }

    private void parsePart(String input, int start, int end, int[] parts, int output) {
        int open = input.indexOf('{', start);
        int close = input.lastIndexOf('}', end - 1);
        if (open < start || close < open || skipWhitespace(input, start, open) != open
                || skipWhitespace(input, close + 1, end) != end) {
            throw new IllegalArgumentException("Malformed part");
        }
        int seen = 0;
        int cursor = open + 1;
        while (true) {
            cursor = skipWhitespace(input, cursor, close);
            if (cursor == close) {
                break;
            }
            byte category = category(input.charAt(cursor++));
            cursor = skipWhitespace(input, cursor, close);
            if (cursor == close || input.charAt(cursor++) != '=') {
                throw new IllegalArgumentException("Missing part value");
            }
            IntToken value = integer(input, cursor, close);
            cursor = skipWhitespace(input, value.next(), close);
            int bit = 1 << category;
            if ((seen & bit) != 0) {
                throw new IllegalArgumentException("Duplicate part category");
            }
            seen |= bit;
            parts[output + category] = value.value();
            if (cursor == close) {
                break;
            }
            if (input.charAt(cursor++) != ',') {
                throw new IllegalArgumentException("Malformed part fields");
            }
        }
        if (seen != 0b1111) {
            throw new IllegalArgumentException("Part must contain x, m, a, and s");
        }
    }

    private String acceptedRatingSum(Problem problem) {
        ExactTotal answer = new ExactTotal();
        int[] marks = new int[problem.workflows().length];
        int mark = 0;
        int[] parts = problem.parts();
        for (int offset = 0; offset < parts.length; offset += 4) {
            if (++mark == 0) {
                Arrays.fill(marks, 0);
                mark = 1;
            }
            int destination = problem.startWorkflow();
            while (destination >= 0) {
                if (marks[destination] == mark) {
                    throw new IllegalArgumentException("Non-terminating workflow cycle");
                }
                marks[destination] = mark;
                Workflow workflow = problem.workflows()[destination];
                destination = workflow.fallback();
                for (int rule = 0; rule < workflow.categories().length; rule++) {
                    int value = parts[offset + workflow.categories()[rule]];
                    if (workflow.greater()[rule] ? value > workflow.thresholds()[rule]
                            : value < workflow.thresholds()[rule]) {
                        destination = workflow.destinations()[rule];
                        break;
                    }
                }
            }
            if (destination == ACCEPT) {
                long rating = (long) parts[offset] + parts[offset + 1]
                        + parts[offset + 2] + parts[offset + 3];
                answer.add(rating);
            }
        }
        return answer.toString();
    }

    private long acceptedCombinations(Problem problem) {
        return count(problem.workflows(), problem.startWorkflow(), INITIAL_LOW, INITIAL_HIGH);
    }

    private long count(Workflow[] workflows, int destination, long low, long high) {
        if (destination == ACCEPT) {
            long volume = 1;
            for (int category = 0; category < 4; category++) {
                volume *= value(high, category) - value(low, category) + 1L;
            }
            return volume;
        }
        if (destination == REJECT) {
            return 0;
        }
        Workflow workflow = workflows[destination];
        long answer = 0;
        for (int rule = 0; rule < workflow.categories().length; rule++) {
            int category = workflow.categories()[rule];
            int lower = value(low, category);
            int upper = value(high, category);
            int threshold = workflow.thresholds()[rule];
            if (workflow.greater()[rule]) {
                if (upper > threshold) {
                    int passLower = Math.max(lower, threshold + 1);
                    answer += count(workflows, workflow.destinations()[rule],
                            withValue(low, category, passLower), high);
                }
                if (lower > threshold) {
                    return answer;
                }
                high = withValue(high, category, Math.min(upper, threshold));
            } else {
                if (lower < threshold) {
                    int passUpper = Math.min(upper, threshold - 1);
                    answer += count(workflows, workflow.destinations()[rule],
                            low, withValue(high, category, passUpper));
                }
                if (upper < threshold) {
                    return answer;
                }
                low = withValue(low, category, Math.max(lower, threshold));
            }
        }
        return answer + count(workflows, workflow.fallback(), low, high);
    }

    private int destination(String name, Map<String, Integer> workflowIds) {
        if (name.equals("A")) {
            return ACCEPT;
        }
        if (name.equals("R")) {
            return REJECT;
        }
        Integer destination = workflowIds.get(name);
        if (destination == null) {
            throw new IllegalArgumentException("Unknown workflow " + name);
        }
        return destination;
    }

    private String token(String input, int start, int end) {
        start = skipWhitespace(input, start, end);
        end = trimWhitespace(input, start, end);
        if (start == end) {
            throw new IllegalArgumentException("Missing token");
        }
        for (int i = start; i < end; i++) {
            if (!Character.isLetterOrDigit(input.charAt(i)) && input.charAt(i) != '_') {
                throw new IllegalArgumentException("Malformed token");
            }
        }
        return input.substring(start, end);
    }

    private IntToken integer(String input, int start, int end) {
        start = skipWhitespace(input, start, end);
        int numberStart = start;
        if (start < end && (input.charAt(start) == '+' || input.charAt(start) == '-')) {
            start++;
        }
        int digitStart = start;
        while (start < end && Character.isDigit(input.charAt(start))) {
            start++;
        }
        if (start == digitStart) {
            throw new IllegalArgumentException("Missing integer");
        }
        try {
            return new IntToken(Integer.parseInt(input, numberStart, start, 10), start);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Integer is out of range", e);
        }
    }

    private byte category(char category) {
        return switch (category) {
            case 'x' -> 0;
            case 'm' -> 1;
            case 'a' -> 2;
            case 's' -> 3;
            default -> throw new IllegalArgumentException("Unknown category " + category);
        };
    }

    private int skipWhitespace(String input, int start, int end) {
        while (start < end && Character.isWhitespace(input.charAt(start))) {
            start++;
        }
        return start;
    }

    private int trimWhitespace(String input, int start, int end) {
        while (end > start && Character.isWhitespace(input.charAt(end - 1))) {
            end--;
        }
        return end;
    }

    private static long pack(int x, int m, int a, int s) {
        return x | (long) m << 12 | (long) a << 24 | (long) s << 36;
    }

    private static int value(long packed, int category) {
        return (int) (packed >>> (category * 12) & RANGE_MASK);
    }

    private static long withValue(long packed, int category, int value) {
        int shift = category * 12;
        return packed & ~(RANGE_MASK << shift) | (long) value << shift;
    }

    private record Workflow(byte[] categories, boolean[] greater, int[] thresholds,
                            int[] destinations, int fallback) {}
    private record RawWorkflow(String name, byte[] categories, boolean[] greater,
                               int[] thresholds, String[] destinationNames,
                               String fallbackName) {}
    private record Problem(Workflow[] workflows, int startWorkflow, int[] parts) {}
    private record IntToken(int value, int next) {}

    private static final class ExactTotal {
        private long small;
        private BigInteger big;

        private void add(long value) {
            if (big != null) {
                big = big.add(BigInteger.valueOf(value));
                return;
            }
            try {
                small = Math.addExact(small, value);
            } catch (ArithmeticException e) {
                big = BigInteger.valueOf(small).add(BigInteger.valueOf(value));
            }
        }

        @Override
        public String toString() {
            return big == null ? Long.toString(small) : big.toString();
        }
    }
}
