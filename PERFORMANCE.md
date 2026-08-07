# Advent of Code 2023 performance

## Methodology and current full suite

All timings use the original personal input corpus with OpenJDK 23.0.1 on Apple silicon. The harness compiles the fixed pre-speed source and current source into separate empty classpaths, excludes one true-cold launch per side, then runs 10 separate JVM pairs in counterbalanced A/B order. Solver is the sum of the 25 `fullSolve` calls; main includes solver plus in-process harness work; startup is process launch through the child start marker; harness is main minus solver; wall is the complete child process.

Reproduce after compiling both trees with `java -Daoc.data.dir=<data-dir> -cp <current-cp> src.FreshJvmBenchmark --compare <previous-cp> <current-cp>`. Every published state passes all 50 independent solves, all 25 combined solves, and independent/`fullSolve` equivalence. The tables below come from a 100-pair run of the same child protocol with seeded random within-pair order and the cold pair excluded; the checked-in n=10 counterbalanced mode remains the quick reproduction default.

A fresh pre-speed-versus-current paired suite run is no longer possible: the harness requires cross-side answer equality, and the pre-speed source's Day 24 part 2 answer was wrong (truncated division — fixed by the Day 24 rewrite noted in its block, and verified against the expected-results record). Day-block Pre-PR values therefore carry the documented freeze-era measurements rather than a re-run.

Current 25-day means (n=100):

| Wall (ms) | Main (ms) | Solver (ms) | Startup (ms) | Harness (ms) |
|---:|---:|---:|---:|---:|
| 153.644 | 120.524 | 88.802 | 21.895 | 31.722 |

Latest publication gate versus the preceding replay tip (n=100):

| Metric | Previous mean (ms) | Current mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|---:|
| wall | 157.954 | 153.644 | -4.310 | [-5.931, -2.689] |
| main | 124.963 | 120.524 | -4.439 | [-4.841, -4.037] |
| solver | 93.323 | 88.802 | -4.521 | [-4.852, -4.190] |
| startup | 21.681 | 21.895 | +0.213 | [-0.034, +0.460] |
| harness | 31.640 | 31.722 | +0.082 | [-0.096, +0.260] |

## Day 01

One slurped buffer feeds a single forward scan that computes both parts at once — literal digits and switch-plus-lookahead word digits with no per-line strings, no regex iteration, and no character-table lookups.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 9.89125 | — | — |
| Current | 3.216096 | -6.675154 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was 0.028 [-0.006, 0.062].*


## Day 02

Each game is scanned once into three exact maxima, producing feasibility and power together without regex splitting or string-keyed maps.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 3.637939 | — | — |
| Current | 3.399517 | -0.238422 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -1.469 [-1.513, -1.424].*

## Day 03

One grid pass parses each maximal number once and inspects only its perimeter, sharing symbol and gear adjacency for both answers.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 5.234905 | — | — |
| Current | 3.70727 | -1.527635 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was 0.130 [0.088, 0.173].*

## Day 04

Direct token-span membership and range-difference copy propagation replace regex splits, boxed match counts, and per-card maps.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 5.992192 | — | — |
| Current | 4.288906 | -1.703286 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was 0.101 [0.062, 0.141].*

## Day 05

A fused per-stage conversion returns the mapped value and its safe interval jump in one pass over primitive stage arrays, with a single-pass digit parser and an exact BigInteger fallback for inputs beyond long range.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 2.772495 | — | — |
| Current | 1.334786 | -1.437709 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.571 [-0.594, -0.548].*

## Day 06

The spaced and concatenated races are parsed together, then an overflow-safe integer binary search counts strict winning holds.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 2.467963 | — | — |
| Current | 0.390708 | -2.077255 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was 0.083 [0.070, 0.097].*

## Day 07

Each hand becomes one flat integer key (category times 13^5 plus a base-13 tie-break) in a single parse for both parts, ranked by a stable two-pass radix sort over an index permutation, with long bids and an exact BigInteger overflow fallback.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 6.08685 | — | — |
| Current | 1.891713 | -4.195137 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.846 [-0.887, -0.804].*


## Day 08

The network is parsed once into primitive node IDs; shared traversal finds each start cycle and combines lengths with exact LCM arithmetic.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 14.395917 | — | — |
| Current | 3.014545 | -11.381372 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.368 [-0.505, -0.232].*

## Day 09

One reusable primitive difference triangle derives the next and previous values together, with exact fallback on overflow.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 3.148467 | — | — |
| Current | 2.633767 | -0.5147 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.008 [-0.039, 0.023].*

## Day 10

A reciprocal pipe-mask loop walk replaces doubled-grid flood fill; loop length and shoelace/Pick geometry provide both answers.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 11.518142 | — | — |
| Current | 2.744722 | -8.77342 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was 0.103 [0.072, 0.133].*

## Day 11

Primitive row and column counts plus prefix aggregates replace galaxy objects, pair enumeration, and repeated empty-axis scans.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 7.525475 | — | — |
| Current | 1.23789 | -6.287585 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was 0.381 [0.357, 0.406].*

## Day 12

The unchanged rolling dynamic program now runs in a streaming per-line pipeline: manual character parsing, an in-place fivefold unfold, and persistent primitive buffers sized to the unfolded maximum, with zero steady-state allocation per record.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 9.825717 | — | — |
| Current | 4.304984 | -5.520733 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.120 [-0.169, -0.072].*


## Day 13

Flat character storage counts mirrored mismatches once per split and stops after the second mismatch, serving both reflection criteria.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 2.222617 | — | — |
| Current | 1.327902 | -0.894715 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.431 [-0.457, -0.405].*

## Day 14

Tilting moves per-segment stone counts through four precomputed pile-position tables (O(stones + segments) per tilt); the board is materialized only at parse, cycle states hash the east-piled count vector, and loads come from closed forms.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 21.278437 | — | — |
| Current | 6.386567 | -14.89187 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.315 [-0.394, -0.237].*


## Day 15

The initialization sequence is parsed once by character index into direct box arrays, avoiding regex tokens and repeated label allocation.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 5.997988 | — | — |
| Current | 1.942574 | -4.055414 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.994 [-1.028, -0.960].*

## Day 16

The mirror graph is condensed once — deterministic beam traces between canonical splitter nodes, strongly connected components collapsed, energized bitsets memoized per component in dependency order — so each of the hundreds of part-2 starts costs one short trace plus popcounts instead of a full simulation.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 88.265783 | — | — |
| Current | 3.751805 | -84.513978 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was 0.221 [0.184, 0.258].*


## Day 17

An uninformed cost-layered dial search (axis-collapsed states, power-of-two bucket ring, cumulative-cost run expansion) replaces the guided search — on a cold JVM the heuristic pass cost more than its guidance saved — fed by a one-shot anchor-token drain parse.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 103.350354 | — | — |
| Current | 6.916535 | -96.433819 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.531 [-0.615, -0.447].*


## Day 18

Both instruction interpretations stream through exact shoelace and boundary accumulators without regexes, objects, or coordinate histories.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 2.304442 | — | — |
| Current | 1.837914 | -0.466528 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.118 [-0.150, -0.085].*

## Day 19

Workflows and parts are parsed once into primitive IDs and packed bounds, then ordered range splits accumulate accepted volume directly.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 6.119175 | — | — |
| Current | 5.769631 | -0.349544 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was 0.465 [0.400, 0.529].*

## Day 20

The module graph compiles to one encoded long per edge (level bit, destination id, conjunction memory bit), so firing a module is a constant copy into a long ring; conjunction decisions are O(1) via a shared bitset with high-counts, and sink pulses are counted, never enqueued. Part 2 proves feeder periodicity by a reset-state check (second-high and direct-simulation fallbacks retained) and combines periods with the LCM.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 35.083462 | — | — |
| Current | 4.083 | -31.000462 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.006 [-0.035, 0.024].*


## Day 21

A flat int-queue BFS computes exact tile distances once; part 1 reads parities, and part 2 derives the three diamond samples from the distance tallies and Newton-extrapolates — after validating the quadratic preconditions, with an exact tiled-BFS fallback for grids (like the official example) that lack them.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 7.754142 | — | — |
| Current | 1.618508 | -6.135634 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was 0.009 [-0.006, 0.024].*


## Day 22

Bricks settle through the height map while immediate dominators fold inline (topological nearest-common-ancestor intersection); part 2 is the dominator-tree depth sum and part 1 counts bricks that dominate nothing — one linear pass replaces the per-brick cascade walk.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 28.294933 | — | — |
| Current | 2.692372 | -25.602561 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.401 [-0.546, -0.256].*


## Day 23

Input-adaptive junction contraction feeds an iterative bitmask depth-first search; part 2 adds branch and bound with a flood-fill reachability prune and an admissible two-heaviest-edges bound, replacing the previous fixed-lattice profile dynamic program with a strictly more general search that explores thousands of nodes instead of millions.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 36.417317 | — | — |
| Current | 8.5873 | -27.830017 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was 0.558 [0.474, 0.643].*


## Day 24

Checked-long intersections with exact fallback and rank-aware rational rock recovery replace truncated division and incomplete validation.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 13.352154 | — | — |
| Current | 10.014801 | -3.337353 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.244 [-0.330, -0.157].*

## Day 25

The exact three-wire cut now aims its capped max-flow at the BFS-farthest node first (with an exact all-candidates fallback), and an open-addressing token interner with flat adjacency replaces the boxed parse — five flow traversals instead of seventy-two.

| Version | Solver mean (ms) | Delta (ms) | Paired 95% CI (ms) |
|---|---:|---:|---:|
| Pre-PR | 7.967013 | — | — |
| Current | 1.70812 | -6.258893 | — |

*Pre-PR is the documented pre-speed measurement; Current is the 2026-08-07 n=100 confirming run. The measured step versus the preceding tip (n=100) was -0.180 [-0.200, -0.161].*

