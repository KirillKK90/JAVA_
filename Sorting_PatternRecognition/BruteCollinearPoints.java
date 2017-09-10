import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints
{
    private final LineSegment[] segments;
    private static Point[] m_points;

    public BruteCollinearPoints(Point[] points)    // finds all line segments containing 4 points
    {
        if (points == null) throw new java.lang.IllegalArgumentException();
        for (Point p: points)
        {
            if (p == null) throw new java.lang.IllegalArgumentException();
        }

        // check if there are duplicates in input array
        for (int j=0; j < points.length; j++)
            for (int k = j+1; k < points.length; k++)
                if (k !=j && points[k].equals(points[j]))
                    throw new java.lang.IllegalArgumentException();

        ArrayList<Point> ordered_points = new ArrayList<Point>();

        int n = points.length;

        m_points = new Point[n];
        m_points = points.clone();

        for (int i = 0; i < n - 3; ++i)
        {
            for (int j = i + 1; j < n - 2; ++j)
            {
                for (int k = j + 1; k < n - 1; ++k)
                {
                    for (int x = k + 1; x < n; ++x)
                    {
                        Point p = m_points[i];
                        Point q = m_points[j];
                        Point r = m_points[k];
                        Point s = m_points[x];

                        if (p.slopeTo(q) == Double.NEGATIVE_INFINITY || p.slopeTo(r) == Double.NEGATIVE_INFINITY ||
                                p.slopeTo(s) == Double.NEGATIVE_INFINITY)
                            throw new java.lang.IllegalArgumentException();

                        if (p.slopeTo(q) == p.slopeTo(r) && p.slopeTo(q) == p.slopeTo(s))
                        {
                            // 4 points are collinear !
                            ordered_points.add(p);
                            ordered_points.add(q);
                            ordered_points.add(r);
                            ordered_points.add(s);
                        }
                    }
                }
            }
        }

        segments = new LineSegment[ordered_points.size() / 4];
        // deal with quads of points
        for (int i = 0; i < ordered_points.size(); i += 4)
        {
            Point[] pquad = new Point[4];
            pquad[0] = ordered_points.get(i);
            pquad[1] = ordered_points.get(i + 1);
            pquad[2] = ordered_points.get(i + 2);
            pquad[3] = ordered_points.get(i + 3);
            Arrays.sort(pquad);

            LineSegment ls = new LineSegment(pquad[0], pquad[3]);
            segments[i / 4] = ls;
        }
    }

    public           int numberOfSegments()        // the number of line segments
    {
        return segments.length;
    }

    public LineSegment[] segments()                // the line segments
    {
        LineSegment[] oSgmnts = new LineSegment[segments.length];
        oSgmnts = segments.clone();
        return oSgmnts;
    }
}