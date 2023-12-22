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
        int index = 0;
        while (in.hasNext()) {
            String line = in.nextLine();
            Brick b = new Brick(line, index);
            bricks.add(b);
            maxX = Math.max(maxX, b.x + b.size);
            maxY = Math.max(maxY, b.y + b.size);
            maxZ = Math.max(maxZ, b.z + b.size);
            index++;
        }
        grid = new int[maxX][maxY][maxZ];
        for(int i = 0 ; i < grid.length; i++){
            for(int j = 0; j < grid[0].length; j++){
                for(int k = 0; k < grid[0][0].length; k++){
                    grid[i][j][k] = -1;
                }
            }
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
                        b.setZ(b.z - 1);
                        somethingMoved = true;
                    }
                }
            }
        }

        for(Brick b: bricks){
            if(b.dir != 2){
                for(int k = 0; k < b.size; k++){
                    if(grid[b.x + k * xs[b.dir]][b.y + k * ys[b.dir]][b.z + 1] != -1){
                        Brick b2 = bricks.get(grid[b.x + k * xs[b.dir]][b.y + k * ys[b.dir]][b.z  + 1]);
                        b2.dependencies.add(b.id);
                    }
                }
            }
            else{
                if(grid[b.x][b.y][b.z + b.size] != -1){
                    Brick b2 = bricks.get(grid[b.x][b.y][b.z + b.size]);
                    b2.dependencies.add(b.id);
                }
            }
        }
        Map<Integer, List<Integer>> dependencyTree= new HashMap<>();
        int[] hardDependencies = new int[bricks.size()];
        for(Brick b: bricks){
            boolean allSame = true;
            if(b.dependencies.size() > 0 ){
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
            for(int i = 0; i < bricks.size(); i++){
                List<Integer> deadBricks = new ArrayList<>();
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
//                        System.out.println("brick " + j + " dependencies " + dependencies);
                        for(Integer b: dependencies){
                            if(!deadBricks.contains(b)){
                                allDeps = false;
                                break;
                            }
                        }
                        if(allDeps){
                            deadBricks.add(j);
                            noneAdded = false;
                        }
                    }
                }
//                System.out.println(deadBricks);
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

    public Brick(String line, int index){
        id = index;
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

    public String toString(){
        return "Brick with id: " + id + " at " + x+" " + y +" " + z + " facing " + dir + " with size " + size;
    }

    public void setZ(int newz){
        z = newz;
    }
    @Override
    public int compareTo(Brick o) {
        return o.z - z;
    }
}
