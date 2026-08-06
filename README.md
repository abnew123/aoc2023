2023 AoC repo. For more detailed thoughts about the problems, see https://abnew123.substack.com/

## Performance

Current 25-day timing from 100 randomized-order fresh-JVM pairs; values are arithmetic means in milliseconds. See [PERFORMANCE.md](PERFORMANCE.md) for definitions and A/B evidence.

| Wall | Main | Solver | Startup | Harness |
|---:|---:|---:|---:|---:|
| 156.658 | 121.121 | 91.477 | 21.079 | 29.644 |

| Days | 01 | 02 | 03 | 04 | 05 |
|---|---:|---:|---:|---:|---:|
| 01–05 | 3.011169 | 4.702041 | 3.476881 | 4.081787 | 1.802083 |
| 06–10 | 0.286670 | 2.660093 | 3.306899 | 2.575027 | 2.609088 |
| 11–15 | 0.823569 | 4.426936 | 1.790785 | 6.499235 | 2.798629 |
| 16–20 | 3.430983 | 7.053280 | 1.838312 | 5.207471 | 4.065815 |
| 21–25 | 1.588217 | 3.403367 | 8.212507 | 9.974293 | 1.851612 |

## Repository

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=abnew123_aoc2023&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=abnew123_aoc2023)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=abnew123_aoc2023&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=abnew123_aoc2023)

<!-- AOC TILES BEGIN -->
<h1 align="center">
  2023 - 50 ⭐
</h1>
<a href="src/solutions/Day01.java">
  <img src=".aoc_tiles/tiles/2023/01.png" width="161px">
</a>
<a href="src/solutions/Day02.java">
  <img src=".aoc_tiles/tiles/2023/02.png" width="161px">
</a>
<a href="src/solutions/Day03.java">
  <img src=".aoc_tiles/tiles/2023/03.png" width="161px">
</a>
<a href="src/solutions/Day04.java">
  <img src=".aoc_tiles/tiles/2023/04.png" width="161px">
</a>
<a href="src/solutions/Day05.java">
  <img src=".aoc_tiles/tiles/2023/05.png" width="161px">
</a>
<a href="src/solutions/Day06.java">
  <img src=".aoc_tiles/tiles/2023/06.png" width="161px">
</a>
<a href="src/solutions/Day07.java">
  <img src=".aoc_tiles/tiles/2023/07.png" width="161px">
</a>
<a href="src/solutions/Day08.java">
  <img src=".aoc_tiles/tiles/2023/08.png" width="161px">
</a>
<a href="src/solutions/Day09.java">
  <img src=".aoc_tiles/tiles/2023/09.png" width="161px">
</a>
<a href="src/solutions/Day10.java">
  <img src=".aoc_tiles/tiles/2023/10.png" width="161px">
</a>
<a href="src/solutions/Day11.java">
  <img src=".aoc_tiles/tiles/2023/11.png" width="161px">
</a>
<a href="src/solutions/Day12.java">
  <img src=".aoc_tiles/tiles/2023/12.png" width="161px">
</a>
<a href="src/solutions/Day13.java">
  <img src=".aoc_tiles/tiles/2023/13.png" width="161px">
</a>
<a href="src/solutions/Day14.java">
  <img src=".aoc_tiles/tiles/2023/14.png" width="161px">
</a>
<a href="src/solutions/Day15.java">
  <img src=".aoc_tiles/tiles/2023/15.png" width="161px">
</a>
<a href="src/solutions/Day16.java">
  <img src=".aoc_tiles/tiles/2023/16.png" width="161px">
</a>
<a href="src/solutions/Day17.java">
  <img src=".aoc_tiles/tiles/2023/17.png" width="161px">
</a>
<a href="src/solutions/Day18.java">
  <img src=".aoc_tiles/tiles/2023/18.png" width="161px">
</a>
<a href="src/solutions/Day19.java">
  <img src=".aoc_tiles/tiles/2023/19.png" width="161px">
</a>
<a href="src/solutions/Day20.java">
  <img src=".aoc_tiles/tiles/2023/20.png" width="161px">
</a>
<a href="src/solutions/Day21.java">
  <img src=".aoc_tiles/tiles/2023/21.png" width="161px">
</a>
<a href="src/solutions/Day22.java">
  <img src=".aoc_tiles/tiles/2023/22.png" width="161px">
</a>
<a href="src/solutions/Day23.java">
  <img src=".aoc_tiles/tiles/2023/23.png" width="161px">
</a>
<a href="src/solutions/Day24.java">
  <img src=".aoc_tiles/tiles/2023/24.png" width="161px">
</a>
<a href="src/solutions/Day25.java">
  <img src=".aoc_tiles/tiles/2023/25.png" width="161px">
</a>
<!-- AOC TILES END -->

| Day | Problem | Solution | Benchmark (ms) |
| --- | --- | --- |---------------:|
| 1 | [Trebuchet?!](https://adventofcode.com/2023/day/1) | [Source](src/solutions/Day01.java) |             49 |
| 2 | [Cube Conundrum](https://adventofcode.com/2023/day/2) | [Source](src/solutions/Day02.java) |             20 |
| 3 | [Gear Ratios](https://adventofcode.com/2023/day/3) | [Source](src/solutions/Day03.java) |             15 |
| 4 | [Scratchcards](https://adventofcode.com/2023/day/4) | [Source](src/solutions/Day04.java) |             34 |
| 5 | [If You Give A Seed A Fertilizer](https://adventofcode.com/2023/day/5) | [Source](src/solutions/Day05.java) |             13 |
| 6 | [Wait For It](https://adventofcode.com/2023/day/6) | [Source](src/solutions/Day06.java) |              8 |
| 7 | [Camel Cards](https://adventofcode.com/2023/day/7) | [Source](src/solutions/Day07.java) |             25 |
| 8 | [Haunted Wasteland](https://adventofcode.com/2023/day/8) | [Source](src/solutions/Day08.java) |             28 |
| 9 | [Mirage Maintenance](https://adventofcode.com/2023/day/9) | [Source](src/solutions/Day09.java) |             10 |
| 10 | [Pipe Maze](https://adventofcode.com/2023/day/10) | [Source](src/solutions/Day10.java) |             31 |
| 11 | [Cosmic Expansion](https://adventofcode.com/2023/day/11) | [Source](src/solutions/Day11.java) |             11 |
| 12 | [Hot Springs](https://adventofcode.com/2023/day/12) | [Source](src/solutions/Day12.java) |             26 |
| 13 | [Point of Incidence](https://adventofcode.com/2023/day/13) | [Source](src/solutions/Day13.java) |             12 |
| 14 | [Parabolic Reflector Dish](https://adventofcode.com/2023/day/14) | [Source](src/solutions/Day14.java) |             36 |
| 15 | [Lens Library](https://adventofcode.com/2023/day/15) | [Source](src/solutions/Day15.java) |             27 |
| 16 | [The Floor Will Be Lava](https://adventofcode.com/2023/day/16) | [Source](src/solutions/Day16.java) |            137 |
| 17 | [Clumsy Crucible](https://adventofcode.com/2023/day/17) | [Source](src/solutions/Day17.java) |            174 |
| 18 | [Lavaduct Lagoon](https://adventofcode.com/2023/day/18) | [Source](src/solutions/Day18.java) |             12 |
| 19 | [Aplenty](https://adventofcode.com/2023/day/19) | [Source](src/solutions/Day19.java) |             23 |
| 20 | [Pulse Propagation](https://adventofcode.com/2023/day/20) | [Source](src/solutions/Day20.java) |             86 |
| 21 | [Step Counter](https://adventofcode.com/2023/day/21) | [Source](src/solutions/Day21.java) |             13 |
| 22 | [Sand Slabs](https://adventofcode.com/2023/day/22) | [Source](src/solutions/Day22.java) |             40 |
| 23 | [A Long Walk](https://adventofcode.com/2023/day/23) | [Source](src/solutions/Day23.java) |            108 |
| 24 | [Never Tell Me The Odds](https://adventofcode.com/2023/day/24) | [Source](src/solutions/Day24.java) |             24 |
| 25 | [Snowverload](https://adventofcode.com/2023/day/25) | [Source](src/solutions/Day25.java) |             26 |


<!-- CHAR COUNTS BEGIN -->

## Character counts

The table below counts non-whitespace characters in each normal solution file and its golfed sibling. The golfed files are intentionally separate from the readable/performance-oriented `DayXX.java` files so the PR tracks can merge independently. Every golfed solver receives the same neutral `String[]` of logical input lines from `MasterSolver`; input parsing and all puzzle logic remain in its `DayXXGolfed.java` file.

| Day | Solution | Golfed solution | Source chars | Golfed chars |
| --- | --- | --- |-------------:|-------------:|
| 1 | [Source](src/solutions/Day01.java) | [Golfed](src/solutions/Day01Golfed.java) | 2,583 | 234 |
| 2 | [Source](src/solutions/Day02.java) | [Golfed](src/solutions/Day02Golfed.java) | 3,704 | 252 |
| 3 | [Source](src/solutions/Day03.java) | [Golfed](src/solutions/Day03Golfed.java) | 4,094 | 394 |
| 4 | [Source](src/solutions/Day04.java) | [Golfed](src/solutions/Day04Golfed.java) | 4,094 | 252 |
| 5 | [Source](src/solutions/Day05.java) | [Golfed](src/solutions/Day05Golfed.java) | 9,506 | 522 |
| 6 | [Source](src/solutions/Day06.java) | [Golfed](src/solutions/Day06Golfed.java) | 3,189 | 258 |
| 7 | [Source](src/solutions/Day07.java) | [Golfed](src/solutions/Day07Golfed.java) | 4,191 | 400 |
| 8 | [Source](src/solutions/Day08.java) | [Golfed](src/solutions/Day08Golfed.java) | 2,834 | 375 |
| 9 | [Source](src/solutions/Day09.java) | [Golfed](src/solutions/Day09Golfed.java) | 3,732 | 210 |
| 10 | [Source](src/solutions/Day10.java) | [Golfed](src/solutions/Day10Golfed.java) | 3,976 | 353 |
| 11 | [Source](src/solutions/Day11.java) | [Golfed](src/solutions/Day11Golfed.java) | 1,930 | 256 |
| 12 | [Source](src/solutions/Day12.java) | [Golfed](src/solutions/Day12Golfed.java) | 3,704 | 432 |
| 13 | [Source](src/solutions/Day13.java) | [Golfed](src/solutions/Day13Golfed.java) | 3,026 | 347 |
| 14 | [Source](src/solutions/Day14.java) | [Golfed](src/solutions/Day14Golfed.java) | 4,963 | 431 |
| 15 | [Source](src/solutions/Day15.java) | [Golfed](src/solutions/Day15Golfed.java) | 2,897 | 372 |
| 16 | [Source](src/solutions/Day16.java) | [Golfed](src/solutions/Day16Golfed.java) | 8,114 | 456 |
| 17 | [Source](src/solutions/Day17.java) | [Golfed](src/solutions/Day17Golfed.java) | 5,466 | 442 |
| 18 | [Source](src/solutions/Day18.java) | [Golfed](src/solutions/Day18Golfed.java) | 3,268 | 255 |
| 19 | [Source](src/solutions/Day19.java) | [Golfed](src/solutions/Day19Golfed.java) | 10,233 | 624 |
| 20 | [Source](src/solutions/Day20.java) | [Golfed](src/solutions/Day20Golfed.java) | 6,278 | 674 |
| 21 | [Source](src/solutions/Day21.java) | [Golfed](src/solutions/Day21Golfed.java) | 2,337 | 418 |
| 22 | [Source](src/solutions/Day22.java) | [Golfed](src/solutions/Day22Golfed.java) | 4,524 | 516 |
| 23 | [Source](src/solutions/Day23.java) | [Golfed](src/solutions/Day23Golfed.java) | 7,981 | 584 |
| 24 | [Source](src/solutions/Day24.java) | [Golfed](src/solutions/Day24Golfed.java) | 20,062 | 990 |
| 25 | [Source](src/solutions/Day25.java) | [Golfed](src/solutions/Day25Golfed.java) | 5,843 | 437 |
| Total |  |  | 132,529 | 10,484 |

<!-- CHAR COUNTS END -->
