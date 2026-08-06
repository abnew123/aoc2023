package src.solutions;

import src.meta.DayTemplate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day05 implements DayTemplate {
    private long[] seeds;
    private Stage[] stages;
    private BigInteger[] bigSeeds;
    private BigStage[] bigStages;
    private boolean big;

    @Override
    public String[] fullSolve(Scanner in) {
        initialize(in);
        return big
                ? new String[]{solveBigPart1().toString(), solveBigPart2().toString()}
                : new String[]{Long.toString(solvePart1()), Long.toString(solvePart2())};
    }

    @Override
    public String solve(boolean part1, Scanner in) {
        initialize(in);
        if (big) {
            return (part1 ? solveBigPart1() : solveBigPart2()).toString();
        }
        return Long.toString(part1 ? solvePart1() : solvePart2());
    }

    private void initialize(Scanner in) {
        List<String> lines = new ArrayList<>();
        while (in.hasNextLine()) {
            lines.add(in.nextLine());
        }
        try {
            initializeLong(lines);
            big = false;
        } catch (NeedsBigInteger ignored) {
            initializeBig(lines);
            big = true;
        }
    }

    private void initializeLong(List<String> lines) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Missing seed list");
        }
        String seedLine = lines.get(0);
        int colon = seedLine.indexOf(':');
        if (colon < 0) {
            throw new IllegalArgumentException("Malformed seed list");
        }
        seeds = parseNumbers(seedLine, colon + 1);
        if ((seeds.length & 1) == 0) {
            for (int pair = 0; pair < seeds.length; pair += 2) {
                requireRepresentableRange(seeds[pair], seeds[pair + 1]);
            }
        }

        List<Stage> parsedStages = new ArrayList<>();
        LongList destination = null;
        LongList source = null;
        LongList length = null;
        for (int lineIndex = 1; lineIndex < lines.size(); lineIndex++) {
            String line = lines.get(lineIndex);
            if (line.isBlank()) {
                continue;
            }
            if (line.endsWith("map:")) {
                if (destination != null) {
                    parsedStages.add(new Stage(destination.toArray(), source.toArray(), length.toArray()));
                }
                destination = new LongList();
                source = new LongList();
                length = new LongList();
                continue;
            }
            if (destination == null) {
                throw new IllegalArgumentException("Mapping row before mapping header");
            }
            long[] values = parseNumbers(line, 0);
            if (values.length != 3) {
                throw new IllegalArgumentException("Mapping row must contain three numbers");
            }
            destination.add(values[0]);
            source.add(values[1]);
            length.add(values[2]);
        }
        if (destination != null) {
            parsedStages.add(new Stage(destination.toArray(), source.toArray(), length.toArray()));
        }
        stages = parsedStages.toArray(Stage[]::new);
    }

    private void initializeBig(List<String> lines) {
        String seedLine = lines.get(0);
        bigSeeds = parseBigNumbers(seedLine, seedLine.indexOf(':') + 1);

        List<BigStage> parsedStages = new ArrayList<>();
        List<BigInteger> destination = null;
        List<BigInteger> source = null;
        List<BigInteger> length = null;
        for (int lineIndex = 1; lineIndex < lines.size(); lineIndex++) {
            String line = lines.get(lineIndex);
            if (line.isBlank()) {
                continue;
            }
            if (line.endsWith("map:")) {
                if (destination != null) {
                    parsedStages.add(new BigStage(destination, source, length));
                }
                destination = new ArrayList<>();
                source = new ArrayList<>();
                length = new ArrayList<>();
                continue;
            }
            if (destination == null) {
                throw new IllegalArgumentException("Mapping row before mapping header");
            }
            BigInteger[] values = parseBigNumbers(line, 0);
            if (values.length != 3) {
                throw new IllegalArgumentException("Mapping row must contain three numbers");
            }
            destination.add(values[0]);
            source.add(values[1]);
            length.add(values[2]);
        }
        if (destination != null) {
            parsedStages.add(new BigStage(destination, source, length));
        }
        bigStages = parsedStages.toArray(BigStage[]::new);
    }

    private long solvePart1() {
        if (seeds.length == 0) {
            throw new IllegalArgumentException("No seeds");
        }
        long answer = Long.MAX_VALUE;
        for (long seed : seeds) {
            long value = seed;
            for (Stage stage : stages) {
                value = stage.convert(value);
            }
            answer = Math.min(answer, value);
        }
        return answer;
    }

    private long solvePart2() {
        if ((seeds.length & 1) != 0) {
            throw new IllegalArgumentException("Seed ranges require start/length pairs");
        }
        long answer = Long.MAX_VALUE;
        boolean found = false;
        long[] conversion = new long[2];
        for (int pair = 0; pair < seeds.length; pair += 2) {
            long start = seeds[pair];
            long length = seeds[pair + 1];
            if (length == 0) {
                continue;
            }
            long consumed = 0;
            while (consumed < length) {
                long value = start + consumed;
                long safeAdvance = length - consumed - 1;
                for (Stage stage : stages) {
                    stage.convertAndBound(value, conversion);
                    value = conversion[0];
                    safeAdvance = Math.min(safeAdvance, conversion[1]);
                }
                answer = Math.min(answer, value);
                found = true;
                consumed += safeAdvance + 1;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("No seeds in the declared ranges");
        }
        return answer;
    }

    private BigInteger solveBigPart1() {
        if (bigSeeds.length == 0) {
            throw new IllegalArgumentException("No seeds");
        }
        BigInteger answer = null;
        for (BigInteger seed : bigSeeds) {
            BigInteger value = seed;
            for (BigStage stage : bigStages) {
                value = stage.convert(value);
            }
            if (answer == null || value.compareTo(answer) < 0) {
                answer = value;
            }
        }
        return answer;
    }

    private BigInteger solveBigPart2() {
        if ((bigSeeds.length & 1) != 0) {
            throw new IllegalArgumentException("Seed ranges require start/length pairs");
        }
        BigInteger answer = null;
        BigInteger[] conversion = new BigInteger[2];
        for (int pair = 0; pair < bigSeeds.length; pair += 2) {
            BigInteger start = bigSeeds[pair];
            BigInteger length = bigSeeds[pair + 1];
            if (length.signum() == 0) {
                continue;
            }
            BigInteger consumed = BigInteger.ZERO;
            while (consumed.compareTo(length) < 0) {
                BigInteger value = start.add(consumed);
                BigInteger safeAdvance = length.subtract(consumed).subtract(BigInteger.ONE);
                for (BigStage stage : bigStages) {
                    stage.convertAndBound(value, conversion);
                    value = conversion[0];
                    if (conversion[1] != null && conversion[1].compareTo(safeAdvance) < 0) {
                        safeAdvance = conversion[1];
                    }
                }
                if (answer == null || value.compareTo(answer) < 0) {
                    answer = value;
                }
                consumed = consumed.add(safeAdvance).add(BigInteger.ONE);
            }
        }
        if (answer == null) {
            throw new IllegalArgumentException("No seeds in the declared ranges");
        }
        return answer;
    }

    private static long[] parseNumbers(String line, int from) {
        LongList numbers = new LongList();
        int index = from;
        while (index < line.length()) {
            while (index < line.length() && Character.isWhitespace(line.charAt(index))) {
                index++;
            }
            if (index == line.length()) {
                break;
            }
            if (line.charAt(index) < '0' || line.charAt(index) > '9') {
                throw new IllegalArgumentException("Expected a nonnegative integer");
            }
            long value = 0;
            do {
                int digit = line.charAt(index) - '0';
                if (value > (Long.MAX_VALUE - digit) / 10) {
                    throw NeedsBigInteger.INSTANCE;
                }
                value = value * 10 + digit;
                index++;
            } while (index < line.length() && line.charAt(index) >= '0' && line.charAt(index) <= '9');
            if (index < line.length() && !Character.isWhitespace(line.charAt(index))) {
                throw new IllegalArgumentException("Malformed integer list");
            }
            numbers.add(value);
        }
        return numbers.toArray();
    }

    private static BigInteger[] parseBigNumbers(String line, int from) {
        List<BigInteger> numbers = new ArrayList<>();
        int index = from;
        while (index < line.length()) {
            while (index < line.length() && Character.isWhitespace(line.charAt(index))) {
                index++;
            }
            if (index == line.length()) {
                break;
            }
            int start = index;
            while (index < line.length() && line.charAt(index) >= '0' && line.charAt(index) <= '9') {
                index++;
            }
            if (start == index || index < line.length() && !Character.isWhitespace(line.charAt(index))) {
                throw new IllegalArgumentException("Malformed integer list");
            }
            numbers.add(new BigInteger(line.substring(start, index)));
        }
        return numbers.toArray(BigInteger[]::new);
    }

    private static void requireRepresentableRange(long start, long length) {
        if (length != 0 && start > Long.MAX_VALUE - (length - 1)) {
            throw NeedsBigInteger.INSTANCE;
        }
    }

    private static final class Stage {
        private final long[] destination;
        private final long[] source;
        private final long[] length;

        Stage(long[] destination, long[] source, long[] length) {
            this.destination = destination;
            this.source = source;
            this.length = length;
            for (int i = 0; i < length.length; i++) {
                requireRepresentableRange(source[i], length[i]);
                requireRepresentableRange(destination[i], length[i]);
            }
        }

        long convert(long value) {
            for (int i = 0; i < source.length; i++) {
                if (value >= source[i]) {
                    long offset = value - source[i];
                    if (offset < length[i]) {
                        return destination[i] + offset;
                    }
                }
            }
            return value;
        }

        void convertAndBound(long value, long[] result) {
            boolean hasNext = false;
            long next = 0;
            for (int i = 0; i < source.length; i++) {
                if (value >= source[i]) {
                    long offset = value - source[i];
                    if (offset < length[i]) {
                        result[0] = destination[i] + offset;
                        long rangeBound = length[i] - offset - 1;
                        result[1] = hasNext ? Math.min(rangeBound, next - value - 1) : rangeBound;
                        return;
                    }
                } else if (length[i] != 0 && (!hasNext || source[i] < next)) {
                    next = source[i];
                    hasNext = true;
                }
            }
            result[0] = value;
            result[1] = hasNext ? next - value - 1 : Long.MAX_VALUE;
        }
    }

    private static final class BigStage {
        private final BigInteger[] destination;
        private final BigInteger[] source;
        private final BigInteger[] length;

        BigStage(List<BigInteger> destination, List<BigInteger> source, List<BigInteger> length) {
            this.destination = destination.toArray(BigInteger[]::new);
            this.source = source.toArray(BigInteger[]::new);
            this.length = length.toArray(BigInteger[]::new);
        }

        BigInteger convert(BigInteger value) {
            for (int i = 0; i < source.length; i++) {
                if (value.compareTo(source[i]) >= 0) {
                    BigInteger offset = value.subtract(source[i]);
                    if (offset.compareTo(length[i]) < 0) {
                        return destination[i].add(offset);
                    }
                }
            }
            return value;
        }

        void convertAndBound(BigInteger value, BigInteger[] result) {
            BigInteger next = null;
            for (int i = 0; i < source.length; i++) {
                if (value.compareTo(source[i]) >= 0) {
                    BigInteger offset = value.subtract(source[i]);
                    if (offset.compareTo(length[i]) < 0) {
                        result[0] = destination[i].add(offset);
                        BigInteger rangeBound = length[i].subtract(offset).subtract(BigInteger.ONE);
                        BigInteger earlierBound = next == null ? null : next.subtract(value).subtract(BigInteger.ONE);
                        result[1] = earlierBound == null || rangeBound.compareTo(earlierBound) <= 0
                                ? rangeBound : earlierBound;
                        return;
                    }
                } else if (length[i].signum() != 0 && (next == null || source[i].compareTo(next) < 0)) {
                    next = source[i];
                }
            }
            result[0] = value;
            result[1] = next == null ? null : next.subtract(value).subtract(BigInteger.ONE);
        }
    }

    private static final class LongList {
        private long[] values = new long[16];
        private int size;

        void add(long value) {
            if (size == values.length) {
                values = Arrays.copyOf(values, size * 2);
            }
            values[size++] = value;
        }

        long[] toArray() {
            return Arrays.copyOf(values, size);
        }
    }

    private static final class NeedsBigInteger extends RuntimeException {
        static final NeedsBigInteger INSTANCE = new NeedsBigInteger();

        private NeedsBigInteger() {
            super(null, null, false, false);
        }
    }
}
