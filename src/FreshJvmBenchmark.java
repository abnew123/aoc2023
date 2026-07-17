package src;

import src.meta.DayTemplate;
import src.meta.SolverFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.ToLongFunction;

public final class FreshJvmBenchmark {

    private static final int DAY_COUNT = 25;
    private static final int ANSWER_COUNT = DAY_COUNT * 2;
    private static final int MEASURED_RUNS = 10;
    private static final String PROTOCOL_VERSION = "2";
    private static final double T_CRITICAL_95_DF_9 = 2.262157;
    private static final String START_PREFIX = "FRESH_JVM_START ";
    private static final String RESULT_PREFIX = "FRESH_JVM_RESULT ";
    private static final Path DATA_DIRECTORY = Path.of(
            System.getProperty("aoc.data.dir", "data")).toAbsolutePath().normalize();

    private FreshJvmBenchmark() {
    }

    public static void main(String[] args) throws Exception {
        long mainStartNanos = System.nanoTime();
        Locale.setDefault(Locale.ROOT);

        if (args.length == 1 && args[0].equals("--verify")) {
            printVerification(verify());
            return;
        }
        if (args.length == 1 && args[0].equals("--child")) {
            runChild(mainStartNanos);
            return;
        }
        if (args.length == 3 && args[0].equals("--compare")) {
            runComparison(args[1], args[2]);
            return;
        }
        if (args.length != 0) {
            throw new IllegalArgumentException(
                    "Usage: FreshJvmBenchmark [--verify|--child|--compare BASE_CP CURRENT_CP]");
        }
        runParent();
    }

    private static Verification verify() throws Exception {
        List<String> independentAnswers = new ArrayList<>(ANSWER_COUNT);
        List<String> fullSolveAnswers = new ArrayList<>(ANSWER_COUNT);
        for (int day = 1; day <= DAY_COUNT; day++) {
            Path input = inputPath(day);
            for (int part = 0; part < 2; part++) {
                String answer;
                try (Scanner scanner = new Scanner(input, StandardCharsets.UTF_8)) {
                    answer = newSolver(day).solve(part == 0, scanner);
                }
                requireAnswer("solve", day, part, answer);
                independentAnswers.add(answer);
            }

            String[] answers;
            try (Scanner scanner = new Scanner(input, StandardCharsets.UTF_8)) {
                answers = newSolver(day).fullSolve(scanner);
            }
            requireAnswerPair("fullSolve", day, answers);
            for (int part = 0; part < 2; part++) {
                requireAnswer("fullSolve", day, part, answers[part]);
                requireEqual("fullSolve versus solve", day, part,
                        answers[part], independentAnswers.get(answerIndex(day, part)));
                fullSolveAnswers.add(answers[part]);
            }
        }
        return new Verification(checksum(fullSolveAnswers));
    }

    private static void requireAnswerPair(String source, int day, String[] answers) {
        if (answers == null || answers.length != 2) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Day %02d %s returned %s answers",
                    day, source, answers == null ? "null" : answers.length));
        }
    }

    private static void requireAnswer(String source, int day, int part, String answer) {
        if (answer == null) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Day %02d part %d %s returned null", day, part + 1, source));
        }
    }

    private static void requireEqual(String source, int day, int part,
                                     String actual, String expected) {
        if (!expected.equals(actual)) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Day %02d part %d %s mismatch: solve <%s>, fullSolve <%s>",
                    day, part + 1, source, expected, actual));
        }
    }

    private static void printVerification(Verification verification) {
        System.out.printf(Locale.ROOT,
                "VERIFY_OK solve_answers=%d full_solve_pairs=%d%n",
                ANSWER_COUNT, DAY_COUNT);
    }

    private static void runChild(long mainStartNanos) throws Exception {
        System.out.printf(Locale.ROOT, "%sprotocol=%s%n", START_PREFIX, PROTOCOL_VERSION);
        System.out.flush();

        long solverNanos = 0;
        long[] dayNanos = new long[DAY_COUNT];
        List<String> answers = new ArrayList<>(ANSWER_COUNT);
        for (int day = 1; day <= DAY_COUNT; day++) {
            DayTemplate solver = newSolver(day);
            String[] dayAnswers;
            try (Scanner scanner = new Scanner(inputPath(day), StandardCharsets.UTF_8)) {
                long solverStartNanos = System.nanoTime();
                dayAnswers = solver.fullSolve(scanner);
                dayNanos[day - 1] = System.nanoTime() - solverStartNanos;
                solverNanos += dayNanos[day - 1];
            }
            requireAnswerPair("fullSolve", day, dayAnswers);
            for (int part = 0; part < 2; part++) {
                requireAnswer("fullSolve", day, part, dayAnswers[part]);
                answers.add(dayAnswers[part]);
            }
        }

        String checksum = checksum(answers);
        long mainNanos = System.nanoTime() - mainStartNanos;
        System.out.printf(Locale.ROOT,
                "%sprotocol=%s main_ns=%d solver_ns=%d day_ns=%s checksum=%s%n",
                RESULT_PREFIX, PROTOCOL_VERSION, mainNanos, solverNanos,
                formatDayNanos(dayNanos), checksum);
    }

    private static void runParent() throws Exception {
        Verification verification = verify();
        printVerification(verification);
        printRuntimeMetadata();

        String classPath = System.getProperty("java.class.path");
        Sample cold = launchChild(verification.checksum(), classPath);
        List<Sample> samples = new ArrayList<>(MEASURED_RUNS);
        for (int run = 0; run < MEASURED_RUNS; run++) {
            samples.add(launchChild(verification.checksum(), classPath));
        }

        System.out.println("Timing definitions: startup=parent launch to child start marker; "
                + "harness=child main minus summed solver invocations.");
        System.out.println("Cold fresh JVM (not included in summary):");
        printSample("cold", cold);
        System.out.println("Measured fresh JVM samples:");
        for (int run = 0; run < samples.size(); run++) {
            printSample("run_" + (run + 1), samples.get(run));
        }
        printSummary(samples);
    }

    private static void runComparison(String baselineClassPath,
                                      String currentClassPath) throws Exception {
        Verification currentVerification = verify();
        printVerification(currentVerification);
        printRuntimeMetadata();

        Sample coldBaseline = launchChild(null, baselineClassPath);
        Sample coldCurrent = launchChild(currentVerification.checksum(), currentClassPath);
        List<Sample> baseline = new ArrayList<>(MEASURED_RUNS);
        List<Sample> current = new ArrayList<>(MEASURED_RUNS);
        for (int pair = 0; pair < MEASURED_RUNS; pair++) {
            if ((pair & 1) == 0) {
                baseline.add(launchChild(coldBaseline.checksum(), baselineClassPath));
                current.add(launchChild(coldCurrent.checksum(), currentClassPath));
            } else {
                current.add(launchChild(coldCurrent.checksum(), currentClassPath));
                baseline.add(launchChild(coldBaseline.checksum(), baselineClassPath));
            }
        }

        System.out.println("Cold fresh JVMs (not included in comparison):");
        printSample("baseline_cold", coldBaseline);
        printSample("current_cold", coldCurrent);
        System.out.printf(Locale.ROOT,
                "Paired comparison (current - baseline, milliseconds, n=%d):%n",
                MEASURED_RUNS);
        System.out.println("metric baseline_mean_ms current_mean_ms delta_ms ci95_low_ms ci95_high_ms");
        printComparison("wall", baseline, current, Sample::wallNanos);
        printComparison("main", baseline, current, Sample::mainNanos);
        printComparison("solver", baseline, current, Sample::solverNanos);
        printComparison("startup", baseline, current, Sample::startupNanos);
        printComparison("harness", baseline, current, Sample::harnessNanos);
        for (int day = 0; day < DAY_COUNT; day++) {
            int index = day;
            printComparison(String.format(Locale.ROOT, "day_%02d", day + 1),
                    baseline, current, sample -> sample.dayNanos()[index]);
        }
    }

    private static void printRuntimeMetadata() {
        System.out.printf(Locale.ROOT,
                "Runtime: java=%s vm=%s os=%s arch=%s processors=%d%n",
                System.getProperty("java.version"),
                System.getProperty("java.vm.name"),
                System.getProperty("os.name"),
                System.getProperty("os.arch"),
                Runtime.getRuntime().availableProcessors());
    }

    private static Sample launchChild(String expectedChecksum, String classPath) throws Exception {
        String executableName = java.io.File.separatorChar == '\\' ? "java.exe" : "java";
        String javaExecutable = ProcessHandle.current().info().command().orElseGet(() ->
                Path.of(System.getProperty("java.home"), "bin", executableName).toString());
        ProcessBuilder processBuilder = new ProcessBuilder(
                javaExecutable,
                "-Daoc.data.dir=" + DATA_DIRECTORY,
                "-cp",
                classPath,
                FreshJvmBenchmark.class.getName(),
                "--child");
        processBuilder.redirectErrorStream(true);

        long wallStartNanos = System.nanoTime();
        Process process = processBuilder.start();
        Long startupNanos = null;
        ChildResult childResult = null;
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                if (line.startsWith(START_PREFIX)) {
                    if (startupNanos != null) {
                        throw new IllegalStateException("Child emitted more than one start record");
                    }
                    requireProtocol(line, START_PREFIX);
                    startupNanos = System.nanoTime() - wallStartNanos;
                } else if (line.startsWith(RESULT_PREFIX)) {
                    if (childResult != null) {
                        throw new IllegalStateException("Child emitted more than one result record");
                    }
                    childResult = parseChildResult(line);
                }
            }
        }
        int exitCode = process.waitFor();
        long wallNanos = System.nanoTime() - wallStartNanos;
        if (exitCode != 0) {
            throw new IllegalStateException("Child JVM exited with " + exitCode + ":\n" + output);
        }
        if (startupNanos == null || childResult == null) {
            throw new IllegalStateException("Child JVM omitted protocol records:\n" + output);
        }
        if (expectedChecksum != null && !childResult.checksum().equals(expectedChecksum)) {
            throw new IllegalStateException("Child answers changed between samples");
        }
        if (startupNanos <= 0 || startupNanos > wallNanos
                || childResult.solverNanos() <= 0
                || childResult.mainNanos() < childResult.solverNanos()) {
            throw new IllegalStateException("Invalid child timings: " + childResult);
        }
        return new Sample(wallNanos, childResult.mainNanos(), childResult.solverNanos(),
                startupNanos, childResult.dayNanos(), childResult.checksum());
    }

    private static void requireProtocol(String line, String prefix) {
        String expected = prefix + "protocol=" + PROTOCOL_VERSION;
        if (!line.equals(expected)) {
            throw new IllegalStateException("Unsupported child protocol record: " + line);
        }
    }

    private static ChildResult parseChildResult(String line) {
        String[] fields = line.substring(RESULT_PREFIX.length()).trim().split("\\s+");
        String protocol = null;
        Long mainNanos = null;
        Long solverNanos = null;
        long[] dayNanos = null;
        String checksum = null;
        for (String field : fields) {
            String[] pair = field.split("=", 2);
            if (pair.length != 2) {
                throw new IllegalStateException("Malformed child field: " + field);
            }
            switch (pair[0]) {
                case "protocol" -> {
                    requireMissing(pair[0], protocol);
                    protocol = pair[1];
                }
                case "main_ns" -> {
                    requireMissing(pair[0], mainNanos);
                    mainNanos = parseNanos(pair[0], pair[1]);
                }
                case "solver_ns" -> {
                    requireMissing(pair[0], solverNanos);
                    solverNanos = parseNanos(pair[0], pair[1]);
                }
                case "day_ns" -> {
                    requireMissing(pair[0], dayNanos);
                    dayNanos = parseDayNanos(pair[1]);
                }
                case "checksum" -> {
                    requireMissing(pair[0], checksum);
                    checksum = pair[1];
                }
                default -> throw new IllegalStateException("Unknown child field: " + pair[0]);
            }
        }
        if (!PROTOCOL_VERSION.equals(protocol)
                || mainNanos == null || solverNanos == null || dayNanos == null
                || checksum == null || !checksum.matches("[0-9a-f]{64}")) {
            throw new IllegalStateException("Incomplete child result: " + line);
        }
        long dayTotal = 0;
        for (long day : dayNanos) dayTotal += day;
        if (dayTotal != solverNanos) {
            throw new IllegalStateException("Day timings do not sum to solver total");
        }
        return new ChildResult(mainNanos, solverNanos, dayNanos, checksum);
    }

    private static void requireMissing(String field, Object currentValue) {
        if (currentValue != null) {
            throw new IllegalStateException("Duplicate child field: " + field);
        }
    }

    private static long parseNanos(String field, String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Invalid " + field + ": " + value, exception);
        }
    }

    private static long[] parseDayNanos(String value) {
        String[] fields = value.split(",", -1);
        if (fields.length != DAY_COUNT) {
            throw new IllegalStateException("Expected " + DAY_COUNT + " day timings");
        }
        long[] result = new long[DAY_COUNT];
        for (int i = 0; i < result.length; i++) {
            result[i] = parseNanos("day_ns", fields[i]);
        }
        return result;
    }

    private static String formatDayNanos(long[] dayNanos) {
        StringBuilder result = new StringBuilder();
        for (int day = 0; day < dayNanos.length; day++) {
            if (day > 0) result.append(',');
            result.append(dayNanos[day]);
        }
        return result.toString();
    }

    private static void printSample(String label, Sample sample) {
        System.out.printf(Locale.ROOT,
                "%s wall_ms=%.6f main_ms=%.6f solver_ms=%.6f startup_ms=%.6f harness_ms=%.6f%n",
                label,
                toMilliseconds(sample.wallNanos()),
                toMilliseconds(sample.mainNanos()),
                toMilliseconds(sample.solverNanos()),
                toMilliseconds(sample.startupNanos()),
                toMilliseconds(sample.harnessNanos()));
    }

    private static void printSummary(List<Sample> samples) {
        System.out.printf(Locale.ROOT, "Summary (milliseconds, n=%d):%n", samples.size());
        System.out.println("metric mean_ms median_ms sample_stddev_ms");
        printStats("wall", samples, Sample::wallNanos);
        printStats("main", samples, Sample::mainNanos);
        printStats("solver", samples, Sample::solverNanos);
        printStats("startup", samples, Sample::startupNanos);
        printStats("harness", samples, Sample::harnessNanos);
        for (int day = 0; day < DAY_COUNT; day++) {
            int index = day;
            printStats(String.format(Locale.ROOT, "day_%02d", day + 1),
                    samples, sample -> sample.dayNanos()[index]);
        }
    }

    private static void printComparison(String name, List<Sample> baseline,
                                        List<Sample> current,
                                        ToLongFunction<Sample> measurement) {
        double baselineSum = 0;
        double currentSum = 0;
        double[] deltas = new double[baseline.size()];
        for (int i = 0; i < deltas.length; i++) {
            long baselineValue = measurement.applyAsLong(baseline.get(i));
            long currentValue = measurement.applyAsLong(current.get(i));
            baselineSum += baselineValue;
            currentSum += currentValue;
            deltas[i] = currentValue - baselineValue;
        }
        double baselineMean = baselineSum / deltas.length;
        double currentMean = currentSum / deltas.length;
        double deltaMean = currentMean - baselineMean;
        double squaredDifferences = 0;
        for (double delta : deltas) {
            double difference = delta - deltaMean;
            squaredDifferences += difference * difference;
        }
        double deltaStandardDeviation = Math.sqrt(
                squaredDifferences / (deltas.length - 1));
        double halfWidth = T_CRITICAL_95_DF_9 * deltaStandardDeviation
                / Math.sqrt(deltas.length);
        System.out.printf(Locale.ROOT, "%s %.6f %.6f %.6f %.6f %.6f%n",
                name,
                toMilliseconds(baselineMean),
                toMilliseconds(currentMean),
                toMilliseconds(deltaMean),
                toMilliseconds(deltaMean - halfWidth),
                toMilliseconds(deltaMean + halfWidth));
    }

    private static void printStats(String name, List<Sample> samples,
                                   ToLongFunction<Sample> measurement) {
        long[] values = new long[samples.size()];
        double sum = 0;
        for (int i = 0; i < samples.size(); i++) {
            values[i] = measurement.applyAsLong(samples.get(i));
            sum += values[i];
        }
        Arrays.sort(values);
        double mean = sum / values.length;
        double median = values.length % 2 == 0
                ? (values[values.length / 2 - 1] + values[values.length / 2]) / 2.0
                : values[values.length / 2];
        double squaredDifferences = 0;
        for (long value : values) {
            double difference = value - mean;
            squaredDifferences += difference * difference;
        }
        double sampleStandardDeviation = values.length > 1
                ? Math.sqrt(squaredDifferences / (values.length - 1))
                : 0;
        System.out.printf(Locale.ROOT, "%s %.6f %.6f %.6f%n",
                name,
                toMilliseconds(mean),
                toMilliseconds(median),
                toMilliseconds(sampleStandardDeviation));
    }

    private static double toMilliseconds(long nanoseconds) {
        return nanoseconds / 1_000_000.0;
    }

    private static double toMilliseconds(double nanoseconds) {
        return nanoseconds / 1_000_000.0;
    }

    private static int answerIndex(int day, int part) {
        return (day - 1) * 2 + part;
    }

    private static Path inputPath(int day) {
        return DATA_DIRECTORY.resolve(String.format(Locale.ROOT, "day%02d.txt", day));
    }

    private static DayTemplate newSolver(int day) {
        return SolverFactory.create(day);
    }

    private static String checksum(List<String> answers) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        for (String answer : answers) {
            byte[] bytes = answer.getBytes(StandardCharsets.UTF_8);
            digest.update((byte) (bytes.length >>> 24));
            digest.update((byte) (bytes.length >>> 16));
            digest.update((byte) (bytes.length >>> 8));
            digest.update((byte) bytes.length);
            digest.update(bytes);
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private record Verification(String checksum) {
    }

    private record ChildResult(long mainNanos, long solverNanos,
                               long[] dayNanos, String checksum) {
    }

    private record Sample(long wallNanos, long mainNanos, long solverNanos,
                          long startupNanos, long[] dayNanos, String checksum) {
        long harnessNanos() {
            return mainNanos - solverNanos;
        }
    }
}
