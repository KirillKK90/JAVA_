package kkkhome.kirillkandroidopengl;

public class Vector3D {
        public float x;
        public float y;
        public float z;

        public Vector3D() {
				x = 0;
                y = 0;
                z = 0;
        }

        public Vector3D(float x, float y, float z) {
                this.x = x;
                this.y = y;
                this.z = z;
        }

        public Vector3D(float[] attributes) {
                if (attributes.length != 3) {
                        throw new RuntimeException("Vector 3D attributes must be length 3.");
                }

                this.x = attributes[0];
                this.y = attributes[1];
                this.z = attributes[2];
        }

        public Vector3D(Vector3D v) {
                if (null == v) {
                        x = y = z = 0;
                } else {
                        this.x = v.x;
                        this.y = v.y;
                        this.z = v.z;
                }
        }

        public Vector3D divide(float scalar) {
                return new Vector3D(x / scalar, y / scalar, z / scalar);
        }

        public Vector3D mult(float scalar) {
                return new Vector3D(x * scalar, y * scalar, z * scalar);
        }
    
		public void multThis(float scalar) {
			x *= scalar;
			y *= scalar;
			z *= scalar;
		}

  
        public Vector3D add(Vector3D v) {
                return new Vector3D(x + v.x, y + v.y, z + v.z);
        }
    
		public void addToThis(Vector3D v) {
			x += v.x;
			y += v.y;
			z += v.z;
		}

     
        public Vector3D subtract(Vector3D v) {
                return new Vector3D(x - v.x, y - v.y, z - v.z);
        }

    
        public float length() {
                return (float) Math.sqrt(lengthSquared());
        }

        
        public float lengthSquared() {
                return x * x + y * y + z * z;
        }

 
        public float dot(Vector3D v) {
                return x * v.x + y * v.y + z * v.z;
        }

        public Vector3D cross(Vector3D v) {
                return new Vector3D(
                        ((y * v.z) - (z * v.y)),
                        ((z * v.x) - (x * v.z)),
                        ((x * v.y) - (y * v.x)));
        }
   
        public void negate() {
                x = -x;
                y = -y;
                z = -z;
        }

  
        public Vector3D normalize() {
                float length = length();

                return divide(length);
        }
        

        public void unitize() {
                float length = length();
                        
                float fInvLength = 1.0f/length;
                x *= fInvLength;
                y *= fInvLength;
                z *= fInvLength;
        }

        public String toString() {
                return "(" + x + "," + y + "," + z + ")";
        }

}
