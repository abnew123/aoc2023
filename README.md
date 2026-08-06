2023 AoC repo. For more detailed thoughts about the problems, see https://abnew123.substack.com/

## Performance

Current 25-day timing from 10 separate fresh-JVM pairs; values are arithmetic means in milliseconds. See [PERFORMANCE.md](PERFORMANCE.md) for definitions and A/B evidence.

| Wall | Main | Solver | Startup | Harness |
|---:|---:|---:|---:|---:|
| 193.308 | 158.453 | 125.282 | 22.636 | 33.171 |

| Days | 01 | 02 | 03 | 04 | 05 |
|---|---:|---:|---:|---:|---:|
| 01–05 | 6.745768 | 3.065485 | 2.994422 | 3.689097 | 1.705222 |
| 06–10 | 0.297809 | 2.374669 | 4.043306 | 2.612378 | 2.713743 |
| 11–15 | 0.902139 | 8.700187 | 1.978733 | 11.935072 | 3.094669 |
| 16–20 | 3.458243 | 11.389749 | 1.765255 | 5.194900 | 8.107804 |
| 21–25 | 6.460490 | 6.998361 | 7.957705 | 9.210750 | 7.885641 |

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
| 1 | [Source](src/solutions/Day01.java) | [Golfed](src/solutions/Day01Golfed.java) | 1,559 | 234 |
| 2 | [Source](src/solutions/Day02.java) | [Golfed](src/solutions/Day02Golfed.java) | 1,632 | 252 |
| 3 | [Source](src/solutions/Day03.java) | [Golfed](src/solutions/Day03Golfed.java) | 2,466 | 394 |
| 4 | [Source](src/solutions/Day04.java) | [Golfed](src/solutions/Day04Golfed.java) | 1,819 | 252 |
| 5 | [Source](src/solutions/Day05.java) | [Golfed](src/solutions/Day05Golfed.java) | 2,860 | 522 |
| 6 | [Source](src/solutions/Day06.java) | [Golfed](src/solutions/Day06Golfed.java) | 2,014 | 258 |
| 7 | [Source](src/solutions/Day07.java) | [Golfed](src/solutions/Day07Golfed.java) | 2,311 | 400 |
| 8 | [Source](src/solutions/Day08.java) | [Golfed](src/solutions/Day08Golfed.java) | 1,606 | 375 |
| 9 | [Source](src/solutions/Day09.java) | [Golfed](src/solutions/Day09Golfed.java) | 1,552 | 210 |
| 10 | [Source](src/solutions/Day10.java) | [Golfed](src/solutions/Day10Golfed.java) | 3,011 | 353 |
| 11 | [Source](src/solutions/Day11.java) | [Golfed](src/solutions/Day11Golfed.java) | 1,952 | 256 |
| 12 | [Source](src/solutions/Day12.java) | [Golfed](src/solutions/Day12Golfed.java) | 2,977 | 432 |
| 13 | [Source](src/solutions/Day13.java) | [Golfed](src/solutions/Day13Golfed.java) | 2,639 | 347 |
| 14 | [Source](src/solutions/Day14.java) | [Golfed](src/solutions/Day14Golfed.java) | 2,616 | 431 |
| 15 | [Source](src/solutions/Day15.java) | [Golfed](src/solutions/Day15Golfed.java) | 2,296 | 372 |
| 16 | [Source](src/solutions/Day16.java) | [Golfed](src/solutions/Day16Golfed.java) | 4,020 | 456 |
| 17 | [Source](src/solutions/Day17.java) | [Golfed](src/solutions/Day17Golfed.java) | 2,806 | 442 |
| 18 | [Source](src/solutions/Day18.java) | [Golfed](src/solutions/Day18Golfed.java) | 3,519 | 255 |
| 19 | [Source](src/solutions/Day19.java) | [Golfed](src/solutions/Day19Golfed.java) | 4,196 | 624 |
| 20 | [Source](src/solutions/Day20.java) | [Golfed](src/solutions/Day20Golfed.java) | 5,148 | 674 |
| 21 | [Source](src/solutions/Day21.java) | [Golfed](src/solutions/Day21Golfed.java) | 2,022 | 418 |
| 22 | [Source](src/solutions/Day22.java) | [Golfed](src/solutions/Day22Golfed.java) | 4,199 | 516 |
| 23 | [Source](src/solutions/Day23.java) | [Golfed](src/solutions/Day23Golfed.java) | 8,580 | 584 |
| 24 | [Source](src/solutions/Day24.java) | [Golfed](src/solutions/Day24Golfed.java) | 4,574 | 990 |
| 25 | [Source](src/solutions/Day25.java) | [Golfed](src/solutions/Day25Golfed.java) | 2,858 | 437 |
| Total |  |  | 75,232 | 10,484 |

<!-- CHAR COUNTS END -->
