package src.solutions;

import src.meta.DayTemplate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day24 implements DayTemplate {

    private static final BigInteger TEST_MIN = BigInteger.valueOf(200_000_000_000_000L);
    private static final BigInteger TEST_MAX = BigInteger.valueOf(400_000_000_000_000L);

    @Override
    public String[] fullSolve(Scanner in) {
        List<Hailstone> stones = parse(in);
        return new String[]{Long.toString(part1(stones)), part2(stones)};
    }

    @Override
    public String solve(boolean part1, Scanner in) {
        List<Hailstone> stones = parse(in);
        return part1 ? Long.toString(part1(stones)) : part2(stones);
    }

    private long part1(List<Hailstone> stones) {
        long intersections = 0;
        for (int firstIndex = 0; firstIndex < stones.size(); firstIndex++) {
            Hailstone first = stones.get(firstIndex);
            for (int secondIndex = firstIndex + 1; secondIndex < stones.size(); secondIndex++) {
                Hailstone second = stones.get(secondIndex);
                BigInteger denominator = first.vx.multiply(second.vy).subtract(first.vy.multiply(second.vx));
                if (denominator.signum() == 0) {
                    if (collinearFutureIntersectionInArea(first, second)) {
                        intersections++;
                    }
                    continue;
                }

                BigInteger dx = second.x.subtract(first.x);
                BigInteger dy = second.y.subtract(first.y);
                BigInteger firstTime = dx.multiply(second.vy).subtract(dy.multiply(second.vx));
                BigInteger secondTime = dx.multiply(first.vy).subtract(dy.multiply(first.vx));
                if (denominator.signum() < 0) {
                    denominator = denominator.negate();
                    firstTime = firstTime.negate();
                    secondTime = secondTime.negate();
                }
                if (firstTime.signum() < 0 || secondTime.signum() < 0) {
                    continue;
                }

                BigInteger intersectionX = first.x.multiply(denominator).add(first.vx.multiply(firstTime));
                BigInteger intersectionY = first.y.multiply(denominator).add(first.vy.multiply(firstTime));
                if (inside(intersectionX, denominator) && inside(intersectionY, denominator)) {
                    intersections++;
                }
            }
        }
        return intersections;
    }

    private boolean collinearFutureIntersectionInArea(Hailstone first, Hailstone second) {
        boolean firstStationary = first.vx.signum() == 0 && first.vy.signum() == 0;
        boolean secondStationary = second.vx.signum() == 0 && second.vy.signum() == 0;
        if (firstStationary && secondStationary) {
            return first.x.equals(second.x) && first.y.equals(second.y)
                    && insidePoint(first.x, first.y);
        }
        if (firstStationary) {
            return insidePoint(first.x, first.y) && pointOnFutureRay(first.x, first.y, second);
        }
        if (secondStationary) {
            return insidePoint(second.x, second.y) && pointOnFutureRay(second.x, second.y, first);
        }

        BigInteger dx = second.x.subtract(first.x);
        BigInteger dy = second.y.subtract(first.y);
        if (dx.multiply(first.vy).subtract(dy.multiply(first.vx)).signum() != 0) {
            return false;
        }

        boolean useX = first.vx.signum() != 0;
        BigInteger firstStart = useX ? first.x : first.y;
        BigInteger secondStart = useX ? second.x : second.y;
        BigInteger firstVelocity = useX ? first.vx : first.vy;
        BigInteger secondVelocity = useX ? second.vx : second.vy;
        BigInteger otherStart = useX ? first.y : first.x;
        BigInteger otherVelocity = useX ? first.vy : first.vx;

        Fraction lower = Fraction.of(TEST_MIN);
        Fraction upper = Fraction.of(TEST_MAX);
        if (otherVelocity.signum() == 0) {
            if (otherStart.compareTo(TEST_MIN) < 0 || otherStart.compareTo(TEST_MAX) > 0) {
                return false;
            }
        } else {
            Fraction atMinimum = Fraction.of(firstStart).add(new Fraction(
                    TEST_MIN.subtract(otherStart).multiply(firstVelocity), otherVelocity));
            Fraction atMaximum = Fraction.of(firstStart).add(new Fraction(
                    TEST_MAX.subtract(otherStart).multiply(firstVelocity), otherVelocity));
            Fraction lineLower = atMinimum.compareTo(atMaximum) <= 0 ? atMinimum : atMaximum;
            Fraction lineUpper = atMinimum.compareTo(atMaximum) <= 0 ? atMaximum : atMinimum;
            if (lineLower.compareTo(lower) > 0) {
                lower = lineLower;
            }
            if (lineUpper.compareTo(upper) < 0) {
                upper = lineUpper;
            }
        }

        Fraction firstBoundary = Fraction.of(firstStart);
        if (firstVelocity.signum() > 0 && firstBoundary.compareTo(lower) > 0) {
            lower = firstBoundary;
        } else if (firstVelocity.signum() < 0 && firstBoundary.compareTo(upper) < 0) {
            upper = firstBoundary;
        }
        Fraction secondBoundary = Fraction.of(secondStart);
        if (secondVelocity.signum() > 0 && secondBoundary.compareTo(lower) > 0) {
            lower = secondBoundary;
        } else if (secondVelocity.signum() < 0 && secondBoundary.compareTo(upper) < 0) {
            upper = secondBoundary;
        }
        return lower.compareTo(upper) <= 0;
    }

    private boolean pointOnFutureRay(BigInteger x, BigInteger y, Hailstone moving) {
        BigInteger dx = x.subtract(moving.x);
        BigInteger dy = y.subtract(moving.y);
        if (dx.multiply(moving.vy).subtract(dy.multiply(moving.vx)).signum() != 0) {
            return false;
        }
        BigInteger displacement = moving.vx.signum() != 0 ? dx : dy;
        BigInteger velocity = moving.vx.signum() != 0 ? moving.vx : moving.vy;
        return new Fraction(displacement, velocity).signum() >= 0;
    }

    private boolean insidePoint(BigInteger x, BigInteger y) {
        return x.compareTo(TEST_MIN) >= 0 && x.compareTo(TEST_MAX) <= 0
                && y.compareTo(TEST_MIN) >= 0 && y.compareTo(TEST_MAX) <= 0;
    }

    private boolean inside(BigInteger numerator, BigInteger denominator) {
        return numerator.compareTo(TEST_MIN.multiply(denominator)) >= 0
                && numerator.compareTo(TEST_MAX.multiply(denominator)) <= 0;
    }

    private String part2(List<Hailstone> stones) {
        if (stones.size() < 3) {
            throw new IllegalArgumentException("At least three hailstones are required");
        }

        Fraction[][] basis = new Fraction[6][];
        int rank = 0;
        Hailstone base = stones.get(0);
        equations:
        for (int index = 1; index < stones.size(); index++) {
            for (BigInteger[] equation : equations(base, stones.get(index))) {
                Fraction[] row = new Fraction[7];
                for (int column = 0; column < row.length; column++) {
                    row[column] = Fraction.of(equation[column]);
                }

                for (int pivot = 0; pivot < 6; pivot++) {
                    if (basis[pivot] == null || row[pivot].isZero()) {
                        continue;
                    }
                    Fraction factor = row[pivot];
                    for (int column = pivot; column < row.length; column++) {
                        row[column] = row[column].subtract(factor.multiply(basis[pivot][column]));
                    }
                }

                int pivot = 0;
                while (pivot < 6 && row[pivot].isZero()) {
                    pivot++;
                }
                if (pivot == 6) {
                    if (!row[6].isZero()) {
                        throw new IllegalArgumentException("Hailstone equations are inconsistent");
                    }
                    continue;
                }

                Fraction divisor = row[pivot];
                for (int column = pivot; column < row.length; column++) {
                    row[column] = row[column].divide(divisor);
                }
                for (int existingPivot = 0; existingPivot < 6; existingPivot++) {
                    Fraction[] existing = basis[existingPivot];
                    if (existing == null || existing[pivot].isZero()) {
                        continue;
                    }
                    Fraction factor = existing[pivot];
                    for (int column = pivot; column < existing.length; column++) {
                        existing[column] = existing[column].subtract(factor.multiply(row[column]));
                    }
                }
                basis[pivot] = row;
                rank++;
                if (rank == 6) {
                    break equations;
                }
            }
        }
        Fraction[] rock;
        if (rank == 6) {
            rock = new Fraction[6];
            for (int component = 0; component < rock.length; component++) {
                rock[component] = basis[component][6];
            }
        } else if (rank == 5) {
            rock = solveRankFive(basis, stones);
        } else {
            throw new IllegalArgumentException("Hailstone equations are underdetermined (rank " + rank + ")");
        }
        if (!isIntegralRock(rock)) {
            throw new IllegalArgumentException("Rock position and velocity must be integral");
        }
        if (!hitsEveryStone(rock, stones)) {
            throw new IllegalArgumentException("Linear solution does not hit every hailstone in the future");
        }
        return rock[0].add(rock[1]).add(rock[2]).toString();
    }

    private Fraction[] solveRankFive(Fraction[][] basis, List<Hailstone> stones) {
        int free = 0;
        while (free < 6 && basis[free] != null) {
            free++;
        }
        Fraction[] offset = new Fraction[6];
        Fraction[] direction = new Fraction[6];
        for (int component = 0; component < 6; component++) {
            if (component == free) {
                offset[component] = Fraction.ZERO;
                direction[component] = Fraction.ONE;
            } else {
                offset[component] = basis[component][6];
                direction[component] = basis[component][free].negate();
            }
        }

        Hailstone base = stones.get(0);
        Fraction[][] polynomials = {
                crossPolynomial(offset, direction, base, 1, 5, 2, 4),
                crossPolynomial(offset, direction, base, 2, 3, 0, 5),
                crossPolynomial(offset, direction, base, 0, 4, 1, 3)
        };
        Fraction[] defining = null;
        for (Fraction[] polynomial : polynomials) {
            if (!polynomial[0].isZero() || !polynomial[1].isZero() || !polynomial[2].isZero()) {
                defining = polynomial;
                break;
            }
        }
        if (defining == null) {
            throw new IllegalArgumentException("Hailstones admit a family of rock trajectories");
        }

        List<Fraction> roots = rationalRoots(defining);
        Fraction[] answer = null;
        for (Fraction root : roots) {
            Fraction[] candidate = new Fraction[6];
            for (int component = 0; component < candidate.length; component++) {
                candidate[component] = offset[component].add(direction[component].multiply(root));
            }
            if (isIntegralRock(candidate) && hitsEveryStone(candidate, stones)) {
                if (answer != null) {
                    throw new IllegalArgumentException("Hailstones admit multiple rock trajectories");
                }
                answer = candidate;
            }
        }
        if (answer == null) {
            throw new IllegalArgumentException("Hailstone equations have no integral rock trajectory");
        }
        return answer;
    }

    private boolean isIntegralRock(Fraction[] rock) {
        for (Fraction component : rock) {
            if (!component.isInteger()) {
                return false;
            }
        }
        return true;
    }

    private Fraction[] crossPolynomial(Fraction[] offset, Fraction[] direction, Hailstone stone,
                                       int firstPosition, int firstVelocity,
                                       int secondPosition, int secondVelocity) {
        Fraction firstPositionConstant = offset[firstPosition]
                .subtract(Fraction.of(stone.position()[firstPosition]));
        Fraction firstVelocityConstant = offset[firstVelocity]
                .subtract(Fraction.of(stone.velocity()[firstVelocity - 3]));
        Fraction secondPositionConstant = offset[secondPosition]
                .subtract(Fraction.of(stone.position()[secondPosition]));
        Fraction secondVelocityConstant = offset[secondVelocity]
                .subtract(Fraction.of(stone.velocity()[secondVelocity - 3]));
        Fraction constant = firstPositionConstant.multiply(firstVelocityConstant)
                .subtract(secondPositionConstant.multiply(secondVelocityConstant));
        Fraction linear = firstPositionConstant.multiply(direction[firstVelocity])
                .add(direction[firstPosition].multiply(firstVelocityConstant))
                .subtract(secondPositionConstant.multiply(direction[secondVelocity]))
                .subtract(direction[secondPosition].multiply(secondVelocityConstant));
        Fraction quadratic = direction[firstPosition].multiply(direction[firstVelocity])
                .subtract(direction[secondPosition].multiply(direction[secondVelocity]));
        return new Fraction[]{constant, linear, quadratic};
    }

    private List<Fraction> rationalRoots(Fraction[] polynomial) {
        Fraction constant = polynomial[0];
        Fraction linear = polynomial[1];
        Fraction quadratic = polynomial[2];
        if (quadratic.isZero()) {
            if (linear.isZero()) {
                return List.of();
            }
            return List.of(constant.negate().divide(linear));
        }
        Fraction discriminant = linear.multiply(linear)
                .subtract(quadratic.multiply(constant).multiply(Fraction.of(BigInteger.valueOf(4))));
        Fraction squareRoot = discriminant.squareRoot();
        if (squareRoot == null) {
            return List.of();
        }
        Fraction denominator = quadratic.multiply(Fraction.of(BigInteger.TWO));
        Fraction first = linear.negate().add(squareRoot).divide(denominator);
        Fraction second = linear.negate().subtract(squareRoot).divide(denominator);
        return first.equals(second) ? List.of(first) : List.of(first, second);
    }

    private List<BigInteger[]> equations(Hailstone base, Hailstone other) {
        BigInteger[] positionDifference = subtract(base.position(), other.position());
        BigInteger[] velocityDifference = subtract(base.velocity(), other.velocity());
        BigInteger[] right = subtract(cross(base.position(), base.velocity()),
                cross(other.position(), other.velocity()));
        BigInteger zero = BigInteger.ZERO;
        return List.of(
                new BigInteger[]{zero, velocityDifference[2], velocityDifference[1].negate(), zero,
                        positionDifference[2].negate(), positionDifference[1], right[0]},
                new BigInteger[]{velocityDifference[2].negate(), zero, velocityDifference[0],
                        positionDifference[2], zero, positionDifference[0].negate(), right[1]},
                new BigInteger[]{velocityDifference[1], velocityDifference[0].negate(), zero,
                        positionDifference[1].negate(), positionDifference[0], zero, right[2]}
        );
    }

    private boolean hitsEveryStone(Fraction[] rock, List<Hailstone> stones) {
        for (Hailstone stone : stones) {
            Fraction time = null;
            BigInteger[] stonePosition = stone.position();
            BigInteger[] stoneVelocity = stone.velocity();
            for (int axis = 0; axis < 3; axis++) {
                Fraction displacement = rock[axis].subtract(Fraction.of(stonePosition[axis]));
                Fraction relativeVelocity = Fraction.of(stoneVelocity[axis]).subtract(rock[axis + 3]);
                if (relativeVelocity.isZero()) {
                    if (!displacement.isZero()) {
                        return false;
                    }
                } else {
                    Fraction axisTime = displacement.divide(relativeVelocity);
                    if (time == null) {
                        time = axisTime;
                    } else if (!time.equals(axisTime)) {
                        return false;
                    }
                }
            }
            if (time != null && time.signum() < 0) {
                return false;
            }
        }
        return true;
    }

    private BigInteger[] cross(BigInteger[] first, BigInteger[] second) {
        return new BigInteger[]{
                first[1].multiply(second[2]).subtract(first[2].multiply(second[1])),
                first[2].multiply(second[0]).subtract(first[0].multiply(second[2])),
                first[0].multiply(second[1]).subtract(first[1].multiply(second[0]))
        };
    }

    private BigInteger[] subtract(BigInteger[] first, BigInteger[] second) {
        return new BigInteger[]{
                first[0].subtract(second[0]),
                first[1].subtract(second[1]),
                first[2].subtract(second[2])
        };
    }

    private List<Hailstone> parse(Scanner in) {
        List<Hailstone> stones = new ArrayList<>();
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isBlank()) {
                continue;
            }
            String[] sides = line.split("@", -1);
            if (sides.length != 2) {
                throw new IllegalArgumentException("Malformed hailstone: " + line);
            }
            BigInteger[] position = parseTriple(sides[0], line);
            BigInteger[] velocity = parseTriple(sides[1], line);
            stones.add(new Hailstone(position[0], position[1], position[2],
                    velocity[0], velocity[1], velocity[2]));
        }
        return stones;
    }

    private BigInteger[] parseTriple(String text, String line) {
        String[] fields = text.split(",", -1);
        if (fields.length != 3) {
            throw new IllegalArgumentException("Malformed hailstone: " + line);
        }
        BigInteger[] values = new BigInteger[3];
        for (int index = 0; index < values.length; index++) {
            try {
                values[index] = new BigInteger(fields[index].trim());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Malformed hailstone: " + line, exception);
            }
        }
        return values;
    }

    private record Hailstone(BigInteger x, BigInteger y, BigInteger z,
                             BigInteger vx, BigInteger vy, BigInteger vz) {
        private BigInteger[] position() {
            return new BigInteger[]{x, y, z};
        }

        private BigInteger[] velocity() {
            return new BigInteger[]{vx, vy, vz};
        }
    }

    private static final class Fraction {
        private static final Fraction ZERO = new Fraction(BigInteger.ZERO, BigInteger.ONE);
        private static final Fraction ONE = new Fraction(BigInteger.ONE, BigInteger.ONE);

        private final BigInteger numerator;
        private final BigInteger denominator;

        private Fraction(BigInteger numerator, BigInteger denominator) {
            if (denominator.signum() == 0) {
                throw new ArithmeticException("zero denominator");
            }
            if (numerator.signum() == 0) {
                this.numerator = BigInteger.ZERO;
                this.denominator = BigInteger.ONE;
                return;
            }
            if (denominator.signum() < 0) {
                numerator = numerator.negate();
                denominator = denominator.negate();
            }
            BigInteger common = numerator.gcd(denominator);
            this.numerator = numerator.divide(common);
            this.denominator = denominator.divide(common);
        }

        private static Fraction of(BigInteger value) {
            return value.signum() == 0 ? ZERO : new Fraction(value, BigInteger.ONE);
        }

        private Fraction add(Fraction other) {
            if (other.isZero()) {
                return this;
            }
            return new Fraction(numerator.multiply(other.denominator)
                    .add(other.numerator.multiply(denominator)), denominator.multiply(other.denominator));
        }

        private Fraction subtract(Fraction other) {
            if (other.isZero()) {
                return this;
            }
            return new Fraction(numerator.multiply(other.denominator)
                    .subtract(other.numerator.multiply(denominator)), denominator.multiply(other.denominator));
        }

        private Fraction multiply(Fraction other) {
            if (isZero() || other.isZero()) {
                return ZERO;
            }
            return new Fraction(numerator.multiply(other.numerator), denominator.multiply(other.denominator));
        }

        private Fraction divide(Fraction other) {
            return new Fraction(numerator.multiply(other.denominator), denominator.multiply(other.numerator));
        }

        private Fraction negate() {
            return isZero() ? ZERO : new Fraction(numerator.negate(), denominator);
        }

        private Fraction squareRoot() {
            if (numerator.signum() < 0) {
                return null;
            }
            BigInteger[] numeratorRoot = numerator.sqrtAndRemainder();
            BigInteger[] denominatorRoot = denominator.sqrtAndRemainder();
            if (numeratorRoot[1].signum() != 0 || denominatorRoot[1].signum() != 0) {
                return null;
            }
            return new Fraction(numeratorRoot[0], denominatorRoot[0]);
        }

        private boolean isZero() {
            return numerator.signum() == 0;
        }

        private boolean isInteger() {
            return denominator.equals(BigInteger.ONE);
        }

        private int signum() {
            return numerator.signum();
        }

        private int compareTo(Fraction other) {
            return numerator.multiply(other.denominator).compareTo(other.numerator.multiply(denominator));
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Fraction fraction
                    && numerator.equals(fraction.numerator)
                    && denominator.equals(fraction.denominator);
        }

        @Override
        public int hashCode() {
            return 31 * numerator.hashCode() + denominator.hashCode();
        }

        @Override
        public String toString() {
            return denominator.equals(BigInteger.ONE) ? numerator.toString() : numerator + "/" + denominator;
        }
    }
}
