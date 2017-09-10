package kkkhome.kirillkandroidopengl;


public class VirtualTrackball {
    /*
     * This size should really be based on the distance from the center of
     * rotation to the point on the object underneath the mouse.  That
     * point would then track the mouse as closely as possible.  This is a
     * simple example, though, so that is left as an Exercise for the
     * Programmer.
     */
    final float TRACKBALLSIZE = 0.7f;

    VirtualTrackball() {
    }

    static void vzero(float[] v) {
        if (v.length < 3) {
            throw new RuntimeException("Wrong input array length!");
        }

        v[0] = 0.0f;
        v[1] = 0.0f;
        v[2] = 0.0f;
    }

    static void vset(float[] v, float x, float y, float z) {
        if (v.length < 3) {
            throw new RuntimeException("Wrong input array length!");
        }
        v[0] = x;
        v[1] = y;
        v[2] = z;
    }

    static void vsub(final float[] src1, final float[] src2, float[] dst) {
        if (src1.length < 3 || src2.length < 3 || dst.length < 3) {
            throw new RuntimeException("Wrong input array length!");
        }
        dst[0] = src1[0] - src2[0];
        dst[1] = src1[1] - src2[1];
        dst[2] = src1[2] - src2[2];
    }

    static void vcopy(final float[] v1, float[] v2) {
        if (v1.length < 3 || v2.length < 3) {
            throw new RuntimeException("Wrong input array length!");
        }
        int i;
        for (i = 0; i < 3; i++)
            v2[i] = v1[i];
    }

    static void vcross(final float[] v1, final float[] v2, float[] cross) {
        if (v1.length < 3 || v2.length < 3 || cross.length < 3) {
            throw new RuntimeException("Wrong input array length!");
        }
        float[] temp = new float[3];

        temp[0] = (v1[1] * v2[2]) - (v1[2] * v2[1]);
        temp[1] = (v1[2] * v2[0]) - (v1[0] * v2[2]);
        temp[2] = (v1[0] * v2[1]) - (v1[1] * v2[0]);
        vcopy(temp, cross);
    }

    static float vlength(final float[] v) {
        if (v.length < 3) {
            throw new RuntimeException("Wrong input array length!");
        }
        return (float)Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    }

    static void vscale(float[] v, float div) {
        if (v.length < 3) {
            throw new RuntimeException("Wrong input array length!");
        }
        v[0] *= div;
        v[1] *= div;
        v[2] *= div;
    }

    static void vnormal(float[] v) {
        vscale(v, 1.0f / vlength(v));
    }

    static float vdot(final float[] v1, final float[] v2) {
        if (v1.length < 3 || v2.length < 3) {
            throw new RuntimeException("Wrong input array length!");
        }
        return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
    }

    static void vadd(final float[] src1, final float[] src2, float[] dst) {
        if (src1.length < 3 || src2.length < 3 || dst.length < 3) {
            throw new RuntimeException("Wrong input array length!");
        }
        dst[0] = src1[0] + src2[0];
        dst[1] = src1[1] + src2[1];
        dst[2] = src1[2] + src2[2];
    }


     /*
     * Pass the x and y coordinates of the last and current positions of
     * the mouse, scaled so they are from (-1.0 ... 1.0).
     *
     * The resulting rotation is returned as a quaternion rotation.
     */
    Quaternion generate_quaternion_from_swipe(float p1x, float p1y, float p2x, float p2y) {
        float[] q = new float[4];
        gen_quat_from_swipe_internal(q, p1x, p1y, p2x, p2y);
        Quaternion o_quat = new Quaternion(q[0], q[1], q[2], q[3]);

        return o_quat;
    }

    /*
     * Ok, simulate a track-ball.  Project the points onto the virtual
     * trackball, then figure out the axis of rotation, which is the cross
     * product of P1 P2 and O P1 (O is the center of the ball, 0,0,0)
     * Note:  This is a deformed trackball-- is a trackball in the center,
     * but is deformed into a hyperbolic sheet of rotation away from the
     * center.  This particular function was chosen after trying out
     * several variations.
     *
     * It is assumed that the arguments to this routine are in the range
     * (-1.0 ... 1.0)
     */
    private void gen_quat_from_swipe_internal(float[] q, float p1x, float p1y, float p2x, float p2y) {
        if (q.length < 4) {
            throw new RuntimeException("Wrong input array length!");
        }

        float[] a = new float[3]; /* Axis of rotation */
        float phi;  /* how much to rotate about axis */
        float[] p1 = new float[3];
        float[] p2 = new float[3];
        float[] d = new float[3];
        float t;

        if (p1x == p2x && p1y == p2y) {
        /* Zero rotation */
            vzero(q);
            q[3] = 1.0f;
            return;
        }

        /*
         * First, figure out z-coordinates for projection of P1 and P2 to
         * deformed sphere
         */
        vset(p1, p1x, p1y, tb_project_to_sphere(TRACKBALLSIZE, p1x, p1y));
        vset(p2, p2x, p2y, tb_project_to_sphere(TRACKBALLSIZE, p2x, p2y));

        /*
         *  Now, we want the cross product of P1 and P2
        */
        vcross(p2, p1, a);

        /*
        *  Figure out how much to rotate around that axis.
        */
        vsub(p1, p2, d);
        t = vlength(d) / (2.0f * TRACKBALLSIZE);

        /*
         * Avoid problems with out-of-control values...
         */
        if (t > 1.0) t = 1.0f;
        if (t < -1.0) t = -1.0f;
        phi = 2.0f * (float)Math.asin(t);

        axis_to_quat(a, phi, q);
    }

    /*
     *  Given an axis and angle, compute quaternion.
     */
    void axis_to_quat(float[] a, float phi, float[] q) {
        if (a.length < 3 || q.length < 4) {
            throw new RuntimeException("Wrong input array length!");
        }
        vnormal(a);
        vcopy(a, q);
        vscale(q, (float)Math.sin(phi / 2.0f));
        q[3] = (float)Math.cos(phi / 2.0f);
    }

    /*
     * Project an x,y pair onto a sphere of radius r OR a hyperbolic sheet
     * if we are away from the center of the sphere.
     */
    static float tb_project_to_sphere(float r, float x, float y) {
        float d, t, z;

        d = (float)Math.sqrt(x * x + y * y);
        if (d < r * 0.70710678118654752440) {    /* Inside sphere */
            z = (float)Math.sqrt(r * r - d * d);
        } else {           /* On hyperbola */
            t = r / 1.41421356237309504880f;
            z = t * t / d;
        }
        return z;
    }


    Quaternion combine_2rotations_using_quaternions(Quaternion i_q1, Quaternion i_q2) {
        float[] q1fa = new float[4];
        float[] q2fa = new float[4];
        float[] resQuatFloatArr = new float[4];
        i_q1.GetAsFloatArray(q1fa);
        i_q1.GetAsFloatArray(q2fa);
        sum_rots_from_2quats_internal(q1fa, q2fa, resQuatFloatArr);
        Quaternion q = new Quaternion(resQuatFloatArr);

        return q;
    }
/*
 * Given two rotations, e1 and e2, expressed as quaternion rotations,
 * figure out the equivalent single rotation and stuff it into dest.
 *
 * This routine also normalizes the result every RENORMCOUNT times it is
 * called, to keep error from creeping in.
 *
 * NOTE: This routine is written so that q1 or q2 may be the same
 * as dest (or each other).
 */

    final int RENORMCOUNT = 97;
    static int count = 0;

    public void sum_rots_from_2quats_internal(float[] q1, float[] q2, float[] dest) {
        if (q1.length < 4 || q2.length < 4 || dest.length < 4) {
            throw new RuntimeException("Wrong input array length!");
        }

        float[] t1 = new float[4];
        float[] t2 = new float[4];
        float[] t3 = new float[4];
        float[] tf = new float[4];

        vcopy(q1, t1);
        vscale(t1, q2[3]);

        vcopy(q2, t2);
        vscale(t2, q1[3]);

        vcross(q2, q1, t3);
        vadd(t1, t2, tf);
        vadd(t3, tf, tf);
        tf[3] = q1[3] * q2[3] - vdot(q1, q2);

        dest[0] = tf[0];
        dest[1] = tf[1];
        dest[2] = tf[2];
        dest[3] = tf[3];

        if (++count > RENORMCOUNT) {
            count = 0;
            normalize_quat(dest);
        }
    }

    /*
     * Quaternions always obey:  a^2 + b^2 + c^2 + d^2 = 1.0
     * If they don't add up to 1.0, dividing by their magnitued will
     * renormalize them.
     */
    static void
    normalize_quat(float[] q) {
        if (q.length < 4) {
            throw new RuntimeException("Wrong input array length!");
        }
        int i;
        float mag;

        mag = (q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
        for (i = 0; i < 4; i++) q[i] /= mag;
    }

    /*
     * Build a rotation matrix, given a quaternion rotation.
     *
     */
    void build_rotmatrix(float[][] m, float[] q) {
        if (m.length < 4 || m[0].length < 4 || q.length < 4) {
            throw new RuntimeException("Wrong input array length!");
        }

        m[0][0] = 1.0f - 2.0f * (q[1] * q[1] + q[2] * q[2]);
        m[0][1] = 2.0f * (q[0] * q[1] - q[2] * q[3]);
        m[0][2] = 2.0f * (q[2] * q[0] + q[1] * q[3]);
        m[0][3] = 0.0f;

        m[1][0] = 2.0f * (q[0] * q[1] + q[2] * q[3]);
        m[1][1] = 1.0f - 2.0f * (q[2] * q[2] + q[0] * q[0]);
        m[1][2] = 2.0f * (q[1] * q[2] - q[0] * q[3]);
        m[1][3] = 0.0f;

        m[2][0] = 2.0f * (q[2] * q[0] - q[1] * q[3]);
        m[2][1] = 2.0f * (q[1] * q[2] + q[0] * q[3]);
        m[2][2] = 1.0f - 2.0f * (q[1] * q[1] + q[0] * q[0]);
        m[2][3] = 0.0f;

        m[3][0] = 0.0f;
        m[3][1] = 0.0f;
        m[3][2] = 0.0f;
        m[3][3] = 1.0f;
    }
}