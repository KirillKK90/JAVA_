import java.util.ArrayList;
import java.util.Arrays;


public class FastCollinearPoints
{
    private final LineSegment[] segments;
    private static Point[] m_points;

    public FastCollinearPoints(Point[] points)     // finds all line segments containing 4 or more points
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

        ArrayList<LineSegment> allFoundSegments = new ArrayList<LineSegment>();
        int n = points.length;

        m_points = new Point[n];
        m_points = points.clone();

        Point[] local_points = new Point[n];
        System.arraycopy(m_points, 0, local_points, 0, n); // SHALLOW !

        ArrayList<Point> processedPoints = new ArrayList<Point>();
        for (Point p : m_points)
        {
            Arrays.sort(local_points, p.slopeOrder());

            for (int i = 0; i < n - 1; ++i)
            {
                double slp_et = p.slopeTo(local_points[i]);
                boolean segmentRepeat = false;
                ArrayList<Integer> currentCollinearPointsIndexSet = new ArrayList<Integer>();
                currentCollinearPointsIndexSet.add(0); // p will alway be 1st:
                // the array was sorted in ascending order; slopeTo(pointItself)
                currentCollinearPointsIndexSet.add(i);

                ++i;
                while (i < n && p.slopeTo(local_points[i]) == slp_et)
                {
                    currentCollinearPointsIndexSet.add(i++);
                }
                --i;

                int collinearPointsNumber = currentCollinearPointsIndexSet.size();
                if (collinearPointsNumber >= 4)
                {
                    Point[] segmentPoints = new Point[collinearPointsNumber];
                    int t = 0;
                    for (int pIdx : currentCollinearPointsIndexSet)
                    {
                        // ??? int idx = Collections.binarySearch(processedPoints, local_points[pIdx]);
                        // doing Linear Search here (results in N^3 alg. complexity). Will replace with binary search later
                        for (Point k : processedPoints)
                        {
                            if (k == local_points[pIdx])
                            {
                                segmentRepeat = true;
                                break;
                            }
                        }

                        if (!segmentRepeat)
                        {
                            segmentPoints[t++] = local_points[pIdx];
                        }
                        else
                        {
                            break;
                        }
                    }

                    if (segmentRepeat) continue;
                    Arrays.sort(segmentPoints);
                    LineSegment ls = new LineSegment(segmentPoints[0], segmentPoints[collinearPointsNumber - 1]);
                    allFoundSegments.add(ls);
                }

                currentCollinearPointsIndexSet.clear();
            }

            // assume that we found all segments containing current point P;
            // skip any segments containing it in the future
            processedPoints.add(p);
        }

        segments = allFoundSegments.toArray(new LineSegment[0]);
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