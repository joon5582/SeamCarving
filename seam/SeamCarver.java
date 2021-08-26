/* *****************************************************************************
 *  Name: Junwoo Lee
 *  Date: 6/4/2020
 *  Description: https://coursera.cs.princeton.edu/algs4/assignments/seam/specification.php
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;

import java.util.Arrays;
import java.util.Iterator;

public class SeamCarver {
    private Picture cp;
    private int V;
    private int width;
    private int height;
    //private double[][] energys;
    private int[][] colors;

    // private Digraph VG;
    // private Digraph HG;


    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException();
        Picture replica = new Picture(picture);
        width = replica.width();
        height = replica.height();
        cp = replica;
        V = cp.width() * cp.height();
        setColors();
        //  setEnergys();


    }

    private void setColors() {
        colors = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                colors[i][j] = cp.getRGB(i, j);

            }
        }

    }
/*
    private void setEnergys() {
        energys = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                energys[i][j] = energy(i, j);

            }
        }
    }

 */


    // current picture
    public Picture picture() {
        Picture picture = new Picture(width, height);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                picture.setRGB(i, j, colors[i][j]);
            }
        }
        cp = picture;
        return cp;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            throw new IllegalArgumentException();
        if (x == 0 || y == 0 || x == width - 1 || y == height - 1)
            return 1000;

        int rx, gx, bx;
        int ry, gy, by;
        double energy;


        int x1blue = colors[x + 1][y] & 0xFF;
        int x1green = (colors[x + 1][y] >> 8) & 0xFF;
        int x1red = (colors[x + 1][y] >> 16) & 0xFF;


        int x2blue = colors[x - 1][y] & 0xFF;
        int x2green = (colors[x - 1][y] >> 8) & 0xFF;
        int x2red = (colors[x - 1][y] >> 16) & 0xFF;


        int y1blue = colors[x][y + 1] & 0xFF;
        int y1green = (colors[x][y + 1] >> 8) & 0xFF;
        int y1red = (colors[x][y + 1] >> 16) & 0xFF;


        int y2blue = colors[x][y - 1] & 0xFF;
        int y2green = (colors[x][y - 1] >> 8) & 0xFF;
        int y2red = (colors[x][y - 1] >> 16) & 0xFF;
        /*
        Color x1 = cp.get(x + 1, y);
        Color x2 = cp.get(x - 1, y);
        Color y1 = cp.get(x, y + 1);
        Color y2 = cp.get(x, y - 1);



        rx = x1.getRed() - x2.getRed();
        gx = x1.getGreen() - x2.getGreen();
        bx = x1.getBlue() - x2.getBlue();
        ry = y1.getRed() - y2.getRed();
        gy = y1.getGreen() - y2.getGreen();
        by = y1.getBlue() - y2.getBlue();

         */
        rx = x1red - x2red;
        gx = x1green - x2green;
        bx = x1blue - x2blue;
        ry = y1red - y2red;
        gy = y1green - y2green;
        by = y1blue - y2blue;


        energy = Math.sqrt((rx * rx) + (gx * gx) + (bx * bx) + (ry * ry) + (gy * gy) + (by * by));
        return energy;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {


        Acyclichori sp = new Acyclichori(V, V);

        Iterator<Integer> iterator = sp.pathTo(V + 1).iterator();
        int[] route = new int[width];
        for (int i = 0; i < width; i++) {
            route[i] = iterator.next() / width;
        }
        return route;


    }


    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {


        Acyclicvert sp = new Acyclicvert(V, V);


        Iterator<Integer> iterator = sp.pathTo(V + 1).iterator();


        int[] route = new int[height];


        for (int i = 0; i < height; i++) {
            route[i] = iterator.next() % width;
        }
        return route;

    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException();
        if (seam.length != width)
            throw new IllegalArgumentException();
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) >= 2)
                throw new IllegalArgumentException();
        }
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= height)
                throw new IllegalArgumentException();
        }
        // Picture newpic = new Picture(width, height - 1);
        int[][] temp = new int[width][height - 1];
        int a = 0;
        int b = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height - 1; j++) {
                if (seam[a] != b) {
                    //   temp[i][j] = energys[a][b];
                    // newpic.setRGB(i, j, colors[a][b]);
                    temp[i][j] = colors[a][b];
                }
                else {
                    b++;
                    //  temp[i][j] = energys[a][b];
                    // newpic.setRGB(i, j, colors[a][b]);
                    temp[i][j] = colors[a][b];
                }
                b++;
            }
            a++;
            b = 0;
        }
        //  energys = temp;

        colors = temp;
        //cp = newpic;

        height--;
        V = width * height;


        //setEnergys();

    }

    // remove vertical seam from current picture

    public void removeVerticalSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException();
        if (seam.length != height)
            throw new IllegalArgumentException();
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) >= 2)
                throw new IllegalArgumentException();
        }
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width)
                throw new IllegalArgumentException();
        }
        //Picture newpic = new Picture(width - 1, height);

        int[][] temp = new int[width - 1][height];
        int a = 0;
        int b = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width - 1; j++) {
                if (seam[a] != b) {
                    //  temp[j][i] = energys[b][a];
                    //newpic.setRGB(j, i, colors[b][a]);
                    temp[j][i] = colors[b][a];
                }
                else {
                    b++;
                    //    temp[j][i] = energys[b][a];
                    //newpic.setRGB(j, i, colors[b][a]);
                    temp[j][i] = colors[b][a];
                }
                b++;
            }
            a++;
            b = 0;
        }
        // energys = temp;
        colors = temp;
        // cp = newpic;
        width--;
        V = width * height;


        // setEnergys();

    }

    private class Acyclicvert {
        private double[] distTo;
        private int[] edgeTo;

        public Acyclicvert(int V, int s) {
            edgeTo = new int[V + 2];
            distTo = new double[V + 2];
            Arrays.fill(edgeTo, Integer.MAX_VALUE);

            for (int v = 0; v < V + 2; v++)
                distTo[v] = Double.POSITIVE_INFINITY;
            distTo[s] = 0.0;


            for (int i = 0; i < width; i++) {

                relax(V, i);

            }
            for (int j = 0; j < height - 1; j++) {

                for (int i = 0; i < width; i++) {

                    int from;
                    int to1;
                    int to2;
                    int to3;
                    from = j * width + i;
                    to1 = (j + 1) * width + i;
                    to2 = (j + 1) * width + i + 1;
                    to3 = (j + 1) * width + i - 1;

                    relax(from, to1);
                    if (i < width - 1) {

                        relax(from, to2);
                    }
                    if (i > 0) {

                        relax(from, to3);
                    }
                }

            }
            for (int i = 0; i < width; i++) {


                relax((height - 1) * width + i, V + 1);
            }


        }

        private void relax(int from, int to) {
            if (to == V + 1) {
                if (distTo[to] > distTo[from]) {
                    distTo[to] = distTo[from];
                    edgeTo[to] = from;
                }
            }
            else if (distTo[to] > distTo[from] + energy(to % width, to / width)) {
                distTo[to] = distTo[from] + energy(to % width, to / width);
                edgeTo[to] = from;
            }

        }

        public Iterable<Integer> pathTo(int v) {
            Stack<Integer> path = new Stack<Integer>();
            for (int e = v; e != V; e = edgeTo[e])
                path.push(e);
            return path;
        }

    }

    private class Acyclichori {
        private double[] distTo;
        private int[] edgeTo;

        public Acyclichori(int V, int s) {
            edgeTo = new int[V + 2];
            distTo = new double[V + 2];
            Arrays.fill(edgeTo, Integer.MAX_VALUE);

            for (int v = 0; v < V + 2; v++)
                distTo[v] = Double.POSITIVE_INFINITY;
            distTo[s] = 0.0;

            for (int i = 0; i < height; i++) {

                relax(V, width * i);

            }
            for (int i = 0; i < width - 1; i++) {

                for (int j = 0; j < height; j++) {
                    int from;
                    int to1;
                    int to2;
                    int to3;
                    from = j * width + i;
                    to1 = j * width + i + 1;
                    to2 = (j + 1) * width + i + 1;
                    to3 = (j - 1) * width + i + 1;

                    relax(from, to1);
                    if (j < height - 1) {

                        relax(from, to2);
                    }

                    if (j > 0) {

                        relax(from, to3);
                    }
                }

            }
            for (int i = 0; i < height; i++) {


                relax(width * (i + 1) - 1, V + 1);
            }


        }

        private void relax(int from, int to) {
            if (to == V + 1) {
                if (distTo[to] > distTo[from]) {
                    distTo[to] = distTo[from];
                    edgeTo[to] = from;
                }
            }
            else if (distTo[to] > distTo[from] + energy(to % width, to / width)) {
                distTo[to] = distTo[from] + energy(to % width, to / width);
                edgeTo[to] = from;
            }

        }

        public Iterable<Integer> pathTo(int v) {
            Stack<Integer> path = new Stack<Integer>();
            for (int e = v; e != V; e = edgeTo[e])
                path.push(e);
            return path;
        }

    }


    //  unit testing (optional)
    public static void main(String[] args) {


    }
}
