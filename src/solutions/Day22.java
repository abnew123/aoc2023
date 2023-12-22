package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day22 extends DayTemplate {
    public String solve(boolean part1, Scanner in) {
        int[][][]grid;
        long answer = 0;
        int maxX = 0;
        int maxY = 0;
        int maxZ = 0;
        int[] xs = new int[]{1,0,0};
        int[] ys = new int[]{0,1,0};
        int[] zs = new int[]{0,0,1};
        List<Brick> bricks = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            Brick b = new Brick(line);
            bricks.add(b);
            maxX = Math.max(maxX, b.x + b.size);
            maxY = Math.max(maxY, b.y + b.size);
            maxZ = Math.max(maxZ, b.z + b.size);
        }
        grid = new int[maxX][maxY][maxZ];
        for(int i = 0 ; i < grid.length; i++){
            for(int j = 0; j < grid[0].length; j++){
                for(int k = 0; k < grid[0][0].length; k++){
                    grid[i][j][k] = -1;
                }
            }
        }
        bricks.sort(null);
        for(int i = 0; i < bricks.size(); i++){
            bricks.get(i).id = i;
        }
        for(Brick b: bricks){
            for(int i = 0; i < b.size; i++){
                grid[b.x + i * xs[b.dir]][b.y + i * ys[b.dir]][b.z + i * zs[b.dir]] = b.id;
            }
        }
        boolean somethingMoved = true;
        while(somethingMoved){
            somethingMoved = false;
            for (Brick b : bricks) {
                boolean supported = false;
                if (b.z > 1) {
                    if (b.dir == 2) {
                        if (grid[b.x][b.y][b.z - 1] != -1) {
                            supported = true;
                        }
                    } else {
                        for (int k = 0; k < b.size; k++) {
                            if (grid[b.x + k * xs[b.dir]][b.y + k * ys[b.dir]][b.z - 1] != -1) {
                                supported = true;
                                break;
                            }
                        }
                    }
                    if (!supported) {
                        for (int k = 0; k < b.size; k++) {
                            grid[b.x + k * xs[b.dir]][b.y + k * ys[b.dir]][b.z + k * zs[b.dir] - 1] = b.id;
                            grid[b.x + k * xs[b.dir]][b.y + k * ys[b.dir]][b.z + k * zs[b.dir]] = -1;
                        }
                        b.z = b.z - 1;
                        somethingMoved = true;
                    }
                }
            }
        }
        for(Brick b: bricks){
            if(b.dir != 2){
                for(int k = 0; k < b.size; k++){
                    if(grid[b.x + k * xs[b.dir]][b.y + k * ys[b.dir]][b.z + 1] != -1){
                        bricks.get(grid[b.x + k * xs[b.dir]][b.y + k * ys[b.dir]][b.z  + 1]).dependencies.add(b.id);
                    }
                }
            }
            else{
                if(grid[b.x][b.y][b.z + b.size] != -1){
                    bricks.get(grid[b.x][b.y][b.z + b.size]).dependencies.add(b.id);
                }
            }
        }
        int[] hardDependencies = new int[bricks.size()];
        for(Brick b: bricks){
            boolean allSame = true;
            if(b.dependencies.size() > 0){
                int id = b.dependencies.get(0);
                for(int i = 1; i < b.dependencies.size(); i++){
                    if (b.dependencies.get(i) != id) {
                        allSame = false;
                        break;
                    }
                }
                if(allSame){
                    hardDependencies[b.dependencies.get(0)]++;
                }
            }
        }
        if(part1){
            for (int hardDependency : hardDependencies) {
                if (hardDependency == 0) {
                    answer++;
                }
            }
        }
        else{
            Map<Integer, Set<Integer>> cache = new HashMap<>();
            for(int i = 0; i < bricks.size(); i++){
                if(hardDependencies[i] == 0){
                    continue;
                }
                Set<Integer> deadBricks = new HashSet<>();
                deadBricks.add(i);
                boolean noneAdded = false;
                while(!noneAdded){
                    noneAdded = true;
                    for(int j = 0; j < bricks.size(); j++){
                        if(deadBricks.contains(j)){
                            continue;
                        }
                        List<Integer> dependencies = bricks.get(j).dependencies;
                        boolean allDeps = dependencies.size() > 0;
                        if(allDeps){
                            for(Integer b: dependencies){
                                if(!deadBricks.contains(b)){
                                    allDeps = false;
                                    break;
                                }
                            }
                        }
                        if(allDeps){
                            if(cache.get(j) != null){
                                deadBricks.addAll(cache.get(j));
                            }
                            else{
                                deadBricks.add(j);
                            }
                            noneAdded = false;
                        }
                    }
                }
                cache.put(i, deadBricks);
                answer+= deadBricks.size() - 1;
            }

        }

        return answer + "";
    }
}

class Brick implements Comparable<Brick>{
    int id;
    int x;
    int y;
    int z;
    int size;
    int dir;
    List<Integer> dependencies = new ArrayList<>();

    public Brick(String line){
        String[] ends = line.split("~");
        int x1 = Integer.parseInt(ends[0].split(",")[0]);
        int y1 = Integer.parseInt(ends[0].split(",")[1]);
        int z1 = Integer.parseInt(ends[0].split(",")[2]);
        int x2 = Integer.parseInt(ends[1].split(",")[0]);
        int y2 = Integer.parseInt(ends[1].split(",")[1]);
        int z2 = Integer.parseInt(ends[1].split(",")[2]);
        if(x1 == x2){
            x = x1;
        }
        else{
            dir = 0;
            x = Math.min(x1,x2);
            size = Math.abs(x1 - x2);
        }
        if(y1 == y2){
            y = y1;
        }
        else{
            dir = 1;
            y = Math.min(y1,y2);
            size = Math.abs(y1 - y2);
        }
        if(z1 == z2){
            z = z1;
        }
        else{
            dir = 2;
            z = Math.min(z1,z2);
            size = Math.abs(z1 - z2);
        }
        size++;

    }

    @Override
    public int compareTo(Brick o) {
        return o.z - z;
    }
}
