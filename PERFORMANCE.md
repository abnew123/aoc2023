# Performance Notes

The README table uses warm 10-run averages for each part because that makes solver-to-solver changes easier to compare. A single `MasterSolver` run is the better number for "I cloned the repo and ran all 50 parts once"; on this branch that is roughly 195ms and still prints `Everything checks out!`.

The first invocation of a Java solver is often slower because the JVM is still loading classes, verifying bytecode, linking methods, compiling hot paths with the JIT, filling CPU caches, and sometimes paying one-time allocation or GC costs. These costs are real for a one-shot command, so the warm table should not be read as the literal end-to-end startup experience.

## Visual Examples

Bars compare the earlier README-listed combined day timing to the current warm combined part timing. Lower is better.

### Day 16: The Floor Will Be Lava

The solver still simulates beams over the mirror grid, but the optimized version avoids much of the queue/object churn by using compact direction/state tracking.

```text
Before 137.0 ms | ############################
After   13.9 ms | ###
```

### Day 17: Clumsy Crucible

The solver still runs a constrained shortest-path search, but the optimized version uses flatter primitive state and tighter queue handling.

```text
Before 174.0 ms | ###################################
After   21.6 ms | ####
```

### Day 22: Sand Slabs

The solver still settles bricks and evaluates support cascades, but it reuses the support graph more directly for each disintegration check.

```text
Before  40.0 ms | ########
After   12.0 ms | ##
```
