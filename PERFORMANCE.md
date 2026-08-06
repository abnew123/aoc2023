# Advent of Code 2023 performance

## Methodology and current full suite

All timings use the original personal input corpus with OpenJDK 23.0.1 on Apple silicon. The harness compiles the fixed pre-speed source and current source into separate empty classpaths, excludes one true-cold launch per side, then runs 10 separate JVM pairs in counterbalanced A/B order. Solver is the sum of the 25 `fullSolve` calls; main includes solver plus in-process harness work; startup is process launch through the child start marker; harness is main minus solver; wall is the complete child process.

Reproduce after compiling both trees with `java -Daoc.data.dir=<data-dir> -cp <current-cp> src.FreshJvmBenchmark --compare <previous-cp> <current-cp>`. Every published state passes all 50 independent solves, all 25 combined solves, and independent/`fullSolve` equivalence. The tables below come from a 100-pair run of the same child protocol with seeded random within-pair order and the cold pair excluded; the checked-in n=10 counterbalanced mode remains the quick reproduction default.

A fresh pre-speed-versus-current paired suite run is no longer possible: the harness requires cross-side answer equality, and the pre-speed source's Day 24 part 2 answer was wrong (truncated division — fixed by the Day 24 rewrite noted in its block, and verified against the expected-results record). Day-block Pre-PR values therefore carry the documented freeze-era measurements rather than a re-run.

Current 25-day means (n=100):

| Wall (ms) | Main (ms) | Solver (ms) | Startup (ms) | Harness (ms) |
|---:|---:|---:|---:|---:|
| 180.077 | 146.016 | 114.741 | 21.707 | 31.275 |

Latest publication gate versus the preceding replay tip (n=100):

| Metric | Previous mean (ms) | Current mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|---:|
| wall | 189.976 | 180.077 | -9.899 | [-11.346, -8.451] |
| main | 154.983 | 146.016 | -8.967 | [-9.574, -8.359] |
| solver | 123.828 | 114.741 | -9.087 | [-9.544, -8.630] |
| startup | 21.914 | 21.707 | -0.207 | [-0.620, 0.207] |
| harness | 31.155 | 31.275 | 0.121 | [-0.146, 0.387] |

## Day 01

One slurped buffer feeds a single forward scan that computes both parts at once — literal digits and switch-plus-lookahead word digits with no per-line strings, no regex iteration, and no character-table lookups.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 9.89125 | — | — |
| Current | 3.166629 | -6.724621 | — |

*Pre-PR is the documented pre-speed measurement at the 2026-07-18 freeze (n=10 protocol); Current is the n=100 confirming run. The measured step versus the preceding tip (n=100) was -3.351180 [-3.406301, -3.296059].*

## Day 02

Each game is scanned once into three exact maxima, producing feasibility and power together without regex splitting or string-keyed maps.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 3.637939 | — | — |
| Current | 3.089977 | -0.547963 | [-0.680235, -0.415690] |

## Day 03

One grid pass parses each maximal number once and inspects only its perimeter, sharing symbol and gear adjacency for both answers.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 5.234905 | — | — |
| Current | 3.039173 | -2.195731 | [-2.276206, -2.115256] |

## Day 04

Direct token-span membership and range-difference copy propagation replace regex splits, boxed match counts, and per-card maps.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 5.992192 | — | — |
| Current | 3.727296 | -2.264896 | [-2.389042, -2.140749] |

## Day 05

A fused per-stage conversion returns the mapped value and its safe interval jump in one pass over primitive stage arrays, with a single-pass digit parser and an exact BigInteger fallback for inputs beyond long range.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 2.772495 | — | — |
| Current | 1.705222 | -1.067273 | [-1.103746, -1.030799] |

## Day 06

The spaced and concatenated races are parsed together, then an overflow-safe integer binary search counts strict winning holds.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 2.467963 | — | — |
| Current | 0.341666 | -2.126296 | [-2.267299, -1.985293] |

## Day 07

Each hand becomes one flat integer key (category times 13^5 plus a base-13 tie-break) in a single parse for both parts, ranked by a stable two-pass radix sort over an index permutation, with long bids and an exact BigInteger overflow fallback.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 6.08685 | — | — |
| Current | 2.374669 | -3.712181 | — |

*Pre-PR is the documented pre-speed measurement at the 2026-07-18 freeze (n=10 protocol); Current is the n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.898864 [-0.945032, -0.852695].*

## Day 08

The network is parsed once into primitive node IDs; shared traversal finds each start cycle and combines lengths with exact LCM arithmetic.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 14.395917 | — | — |
| Current | 3.080542 | -11.315375 | [-11.679960, -10.950790] |

## Day 09

One reusable primitive difference triangle derives the next and previous values together, with exact fallback on overflow.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 3.148467 | — | — |
| Current | 2.607402 | -0.541065 | [-0.691443, -0.390686] |

## Day 10

A reciprocal pipe-mask loop walk replaces doubled-grid flood fill; loop length and shoelace/Pick geometry provide both answers.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 11.518142 | — | — |
| Current | 2.758233 | -8.759908 | [-9.055380, -8.464436] |

## Day 11

Primitive row and column counts plus prefix aggregates replace galaxy objects, pair enumeration, and repeated empty-axis scans.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 7.525475 | — | — |
| Current | 0.917483 | -6.607992 | [-6.829581, -6.386403] |

## Day 12

The unchanged rolling dynamic program now runs in a streaming per-line pipeline: manual character parsing, an in-place fivefold unfold, and persistent primitive buffers sized to the unfolded maximum, with zero steady-state allocation per record.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 9.825717 | — | — |
| Current | 4.415836 | -5.409881 | — |

*Pre-PR is the documented pre-speed measurement at the 2026-07-18 freeze (n=10 protocol); Current is the n=100 confirming run. The measured step versus the preceding tip (n=100) was -4.255433 [-4.351045, -4.159820].*

## Day 13

Flat character storage counts mirrored mismatches once per split and stops after the second mismatch, serving both reflection criteria.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 2.222617 | — | — |
| Current | 1.493121 | -0.729496 | [-0.873994, -0.584998] |

## Day 14

A flat row-major board with self-move-skipping tilts feeds exact-state cycle detection keyed on the full cloned board (collision-proof, cached hash), parsed by one delimiter-free slurp.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 21.278437 | — | — |
| Current | 11.935072 | -9.343365 | — |

*Pre-PR is the documented pre-speed measurement at the 2026-07-18 freeze (n=10 protocol); Current is the n=100 confirming run. The measured step versus the preceding tip (n=100) was -2.243760 [-2.321982, -2.165538].*

## Day 15

The initialization sequence is parsed once by character index into direct box arrays, avoiding regex tokens and repeated label allocation.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 5.997988 | — | — |
| Current | 3.253175 | -2.744813 | [-3.335447, -2.154178] |

## Day 16

The mirror graph is condensed once — deterministic beam traces between canonical splitter nodes, strongly connected components collapsed, energized bitsets memoized per component in dependency order — so each of the hundreds of part-2 starts costs one short trace plus popcounts instead of a full simulation.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 88.265783 | — | — |
| Current | 3.458243 | -84.807540 | — |

*Pre-PR is the documented pre-speed measurement at the 2026-07-18 freeze (n=10 protocol); Current is the n=100 confirming run. The measured step versus the preceding tip (n=100) was -12.654564 [-12.720342, -12.588787].*

## Day 17

The axis-collapsed bucket-queue search becomes A* with an exact relaxed-walk heuristic: one backward unconstrained Dijkstra from the goal, shared by both parts, steers the crucible search through a provable subset of the plain Dijkstra frontier on a widened bucket ring.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 103.350354 | — | — |
| Current | 11.389749 | -91.960605 | — |

*Pre-PR is the documented pre-speed measurement at the 2026-07-18 freeze (n=10 protocol); Current is the n=100 confirming run. The measured step versus the preceding tip (n=100) was -6.129061 [-7.208054, -5.050068].*

## Day 18

Both instruction interpretations stream through exact shoelace and boundary accumulators without regexes, objects, or coordinate histories.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 2.304442 | — | — |
| Current | 1.701760 | -0.602681 | [-0.685590, -0.519773] |

## Day 19

Workflows and parts are parsed once into primitive IDs and packed bounds, then ordered range splits accumulate accepted volume directly.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 6.119175 | — | — |
| Current | 5.309508 | -0.809666 | [-0.973995, -0.645338] |

## Day 20

Modules are parsed once into primitive arrays; one record-free pulse stream simultaneously supplies the thousand-press count and cycle observations.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 35.083462 | — | — |
| Current | 8.101187 | -26.982275 | [-28.475862, -25.488687] |

## Day 21

One parsed garden and shared reachable-distance pass provide the finite-step count and the quadratic infinite-grid extrapolation.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 7.754142 | — | — |
| Current | 4.968513 | -2.785629 | [-3.091764, -2.479494] |

## Day 22

Bricks settle once against an exact top surface, directly producing the support graph reused for removal and cascade counts.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 28.294933 | — | — |
| Current | 7.307504 | -20.987429 | [-22.131220, -19.843638] |

## Day 23

Input-adaptive junction contraction feeds an iterative bitmask depth-first search; part 2 adds branch and bound with a flood-fill reachability prune and an admissible two-heaviest-edges bound, replacing the previous fixed-lattice profile dynamic program with a strictly more general search that explores thousands of nodes instead of millions.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 36.417317 | — | — |
| Current | 7.957705 | -28.459612 | — |

*Pre-PR is the documented pre-speed measurement at the 2026-07-18 freeze (n=10 protocol); Current is the n=100 confirming run. The measured step versus the preceding tip (n=100) was -31.506571 [-31.844003, -31.169140].*

## Day 24

Checked-long intersections with exact fallback and rank-aware rational rock recovery replace truncated division and incomplete validation.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 13.352154 | — | — |
| Current | 13.236475 | -0.115679 | [-0.732269, 0.500910] |

## Day 25

The exact three-wire cut now aims its capped max-flow at the BFS-farthest node first (with an exact all-candidates fallback), and an open-addressing token interner with flat adjacency replaces the boxed parse — five flow traversals instead of seventy-two.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 7.967013 | — | — |
| Current | 1.980520 | -5.986493 | — |

*Pre-PR is the documented pre-speed measurement at the 2026-07-18 freeze (n=10 protocol); Current is the n=100 confirming run. The measured step versus the preceding tip (n=100) was -5.762594 [-5.861740, -5.663449].*
