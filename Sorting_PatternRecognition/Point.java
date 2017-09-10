/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *  
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import edu.princeton.cs.algs4.StdDraw;

public class Point implements Comparable<Point> {

    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param  x the <em>x</em>-coordinate of the point
     * @param  y the <em>y</em>-coordinate of the point
     * Assume that the constructor arguments x and y are each between 0 and 32,767
     * (To avoid potential complications with integer overflow or floating-point precision).
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param  that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        if (that.x == x && that.y == y) return Double.NEGATIVE_INFINITY;
        else if (that.x == x && that.y != y) return Double.POSITIVE_INFINITY;
        else if (that.x != x && that.y == y) return +0.0;

        return (double) (that.y - y) / (that.x - x);
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param  that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    public int compareTo(Point that)
    {
        if (y < that.y || (y == that.y && x < that.x)) return -1;
        else if (x == that.x && y == that.y) return 0;
        return 1;
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder()
    {
        return new Comparator<Point>()
        {
            @Override
            public int compare(Point o1, Point o2)
            {
                if (slopeTo(o1) < slopeTo(o2)) return -1;
                else if (slopeTo(o1) == slopeTo(o2)) return 0;
                else return 1;
            }
        };
    }


    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args)
    {
        Point p1 = new Point(10, 0);
        Point p2 = new Point(0, 10);
        Point p3 = new Point(3, 7);
        Point p4 = new Point(7, 3);
        Point p5 = new Point(20, 21);
        Point p6 = new Point(3, 4);
        Point p7 = new Point(14, 15);
        Point p8 = new Point(6, 7);

        Point[] arr = new Point[8];
        arr[0] = p1;
        arr[1] = p2;
        arr[2] = p3;
        arr[3] = p4;
        arr[4] = p5;
        arr[5] = p6;
        arr[6] = p7;
        arr[7] = p8;

        System.out.println("Before sorting:");
        for (Point i : arr)
        {
            System.out.println(i.toString());
        }

        Arrays.sort(arr, p1.slopeOrder());

        System.out.println("After sorting:");
        for (Point i : arr)
        {
            System.out.println(i.toString());
        }

        double[] slopes = new double[8];
        Point pt = p1;
        slopes[0] = pt.slopeTo(p2);
        slopes[1] = pt.slopeTo(p3);
        slopes[2] = pt.slopeTo(p4);
        slopes[3] = pt.slopeTo(p5);
        slopes[4] = pt.slopeTo(p6);
        slopes[5] = pt.slopeTo(p7);
        slopes[6] = pt.slopeTo(p8);

        System.out.println("Slope values, initial:");
        for (double d : slopes)
        {
            System.out.println(d);
        }

        for (int i = 0; i < 8; ++i)
        {
            slopes[i] = pt.slopeTo(arr[i]);
        }

        System.out.println("Slope values, final:");
        for (double d : slopes)
        {
            System.out.println(d);
        }
    }
}
