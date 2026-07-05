2023 AoC repo. For more detailed thoughts about the problems, see https://abnew123.substack.com/

Total run time of all 50 parts is roughly 1.3 seconds on my 2019 Macbook Pro, roughly 1 second on my 2024 Macbook Pro before optimization, and roughly 195ms for a one-pass `MasterSolver` run after optimization. Benchmark times below are warm 10-run averages per part using the existing `DayTemplate.timer` convention; those numbers are useful for comparing individual solver changes, but they understate the first-run cost someone sees when running all parts once.

See [performance notes](PERFORMANCE.md) for visual before/after examples and benchmark caveats.

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

| Day | Problem | Solution | Part 1 (ms) | Part 2 (ms) |
| --- | --- | --- |------------:|------------:|
| 1 | [Trebuchet?!](https://adventofcode.com/2023/day/1) | [Source](src/solutions/Day01.java) | 1.502 | 2.107 |
| 2 | [Cube Conundrum](https://adventofcode.com/2023/day/2) | [Source](src/solutions/Day02.java) | 2.026 | 2.095 |
| 3 | [Gear Ratios](https://adventofcode.com/2023/day/3) | [Source](src/solutions/Day03.java) | 2.510 | 2.245 |
| 4 | [Scratchcards](https://adventofcode.com/2023/day/4) | [Source](src/solutions/Day04.java) | 2.931 | 3.084 |
| 5 | [If You Give A Seed A Fertilizer](https://adventofcode.com/2023/day/5) | [Source](src/solutions/Day05.java) | 1.199 | 1.251 |
| 6 | [Wait For It](https://adventofcode.com/2023/day/6) | [Source](src/solutions/Day06.java) | 0.517 | 0.597 |
| 7 | [Camel Cards](https://adventofcode.com/2023/day/7) | [Source](src/solutions/Day07.java) | 3.418 | 3.318 |
| 8 | [Haunted Wasteland](https://adventofcode.com/2023/day/8) | [Source](src/solutions/Day08.java) | 3.894 | 7.632 |
| 9 | [Mirage Maintenance](https://adventofcode.com/2023/day/9) | [Source](src/solutions/Day09.java) | 2.123 | 2.174 |
| 10 | [Pipe Maze](https://adventofcode.com/2023/day/10) | [Source](src/solutions/Day10.java) | 5.629 | 6.595 |
| 11 | [Cosmic Expansion](https://adventofcode.com/2023/day/11) | [Source](src/solutions/Day11.java) | 3.555 | 3.539 |
| 12 | [Hot Springs](https://adventofcode.com/2023/day/12) | [Source](src/solutions/Day12.java) | 2.577 | 6.584 |
| 13 | [Point of Incidence](https://adventofcode.com/2023/day/13) | [Source](src/solutions/Day13.java) | 1.900 | 1.836 |
| 14 | [Parabolic Reflector Dish](https://adventofcode.com/2023/day/14) | [Source](src/solutions/Day14.java) | 1.291 | 7.510 |
| 15 | [Lens Library](https://adventofcode.com/2023/day/15) | [Source](src/solutions/Day15.java) | 2.499 | 4.944 |
| 16 | [The Floor Will Be Lava](https://adventofcode.com/2023/day/16) | [Source](src/solutions/Day16.java) | 1.207 | 12.652 |
| 17 | [Clumsy Crucible](https://adventofcode.com/2023/day/17) | [Source](src/solutions/Day17.java) | 8.955 | 12.633 |
| 18 | [Lavaduct Lagoon](https://adventofcode.com/2023/day/18) | [Source](src/solutions/Day18.java) | 1.800 | 2.149 |
| 19 | [Aplenty](https://adventofcode.com/2023/day/19) | [Source](src/solutions/Day19.java) | 3.888 | 4.643 |
| 20 | [Pulse Propagation](https://adventofcode.com/2023/day/20) | [Source](src/solutions/Day20.java) | 4.481 | 13.548 |
| 21 | [Step Counter](https://adventofcode.com/2023/day/21) | [Source](src/solutions/Day21.java) | 3.424 | 3.397 |
| 22 | [Sand Slabs](https://adventofcode.com/2023/day/22) | [Source](src/solutions/Day22.java) | 5.588 | 6.383 |
| 23 | [A Long Walk](https://adventofcode.com/2023/day/23) | [Source](src/solutions/Day23.java) | 6.997 | 11.799 |
| 24 | [Never Tell Me The Odds](https://adventofcode.com/2023/day/24) | [Source](src/solutions/Day24.java) | 2.597 | 4.994 |
| 25 | [Snowverload](https://adventofcode.com/2023/day/25) | [Source](src/solutions/Day25.java) | 5.419 | 0.001 |
