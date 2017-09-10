package kkkhome.kirillkandroidopengl;

public class Quaternion {
    private float a, b, c, w;

	    public Quaternion() {
            a = 0;
            b = 0;
            c = 0;
            w = 1;
        }

        public Quaternion(float i_a, float i_b, float i_c, float i_w) {
            a = i_a;
            b = i_b;
            c = i_c;
            w = i_w;
        }

        public Quaternion(float[] i_q) {
            a = i_q[0];
            b = i_q[1];
            c = i_q[2];
            w = i_q[3];
        }

        public void Assign(Quaternion i_q) {
            a = i_q.a;
            b = i_q.b;
            c = i_q.c;
            w = i_q.w;
        }

        void GetAsFloatArray(float[] o_q) {
            o_q[0] = a;
            o_q[1] = b;
            o_q[2] = c;
            o_q[3] = w;
        }

        public void GetAngleAxis(float[] o_angleAxis) {
            if (w > 1)  // if w>1 acos and sqrt will produce errors, this cant happen if quaternion is normalised
            {
                throw new RuntimeException();
                //q1.normalise();
            }
            o_angleAxis[0] = 2 * (float)Math.acos(w);
            float s = (float)Math.sqrt(1 - w * w); // assuming quaternion normalised then w is less than 1, so term always positive.
            if (s < 0.001) { // test to avoid divide by zero, s is always positive due to sqrt
                // if s close to zero then direction of axis not important
                o_angleAxis[1] = a; // if it is important that axis is normalised then replace with x=1; y=z=0;
                o_angleAxis[2] = b;
                o_angleAxis[3] = c;
            } else {
                o_angleAxis[1] = a / s; // normalise axis
                o_angleAxis[2] = b / s;
                o_angleAxis[3] = c / s;
            }
        }
}
