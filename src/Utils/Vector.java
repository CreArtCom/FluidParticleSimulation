package Utils;

/**
 * Vector2 is a common implementation of 2D Vectors.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public final class Vector
{
	/** The X Value */
	public double x;
	
	/** The Y Value */
	public double y;
	
	/** The current vector's norm */
	protected double norm;
	
	/** 2D normalised vector oriented to Y */
	static public Vector NULL = new Vector(0., 0.);
	
	/** 2D normalised vector oriented to X */
	static public Vector UNITX = new Vector(1., 0.);
	
	/** 2D normalised vector oriented to Y */
	static public Vector UNITY = new Vector(0., 1.);

	/**
	 * 2D constructor
	 * @param x The X value
	 * @param y The Y value
	 */
	public Vector(double x, double y)
	{
		this.x = x;
		this.y = y;
		updated();
	}
	
	/**
	 * 1D constructor (y will be set to 0)
	 * @param x The X value
	 */
	public Vector(double x) {
		this(x, 0.);
	}
	
	/**
	 * Default constructor
	 * Construct an zero vector (0., 0.)
	 */
	public Vector() {
		this(0., 0.);
	}
	
	/**
	 * Copy constructor
	 * @param vector Vector3 to copy
	 */
	public Vector(Vector vector) {
		this(vector.x, vector.y);
	}
	
	/**
	 * Computes the addition : this + vector
	 * @param vector Vector3 to add
	 * @return A reference to this vector
	 */
	public Vector Add(Vector vector)
	{
		x += vector.x;
		y += vector.y;
		
		updated();
		return this;
	}
	
	/**
	 * Computes an addition between to vectors : u + v
	 * @param u First vector
	 * @param v Second vector
	 * @return The result vector
	 */
	static public Vector Add(Vector u, Vector v)
	{
		Vector result = new Vector(u);
		result.Add(v);
		return result;
	}

	/**
	 * Computes the subtraction : this - vector
	 * @param vector Vector3 to subtract
	 * @return A reference to this vector
	 */
	public Vector Subtract(Vector vector)
	{
		x -= vector.x;
		y -= vector.y;
		
		updated();
		return this;
	}
	
	/**
	 * Computes a subtraction between to vectors : u - v
	 * @param u Vector3 which will be subtracted
	 * @param v Vector3 to subtract
	 * @return The result vector
	 */
	static public Vector Subtract(Vector u, Vector v)
	{
		Vector result = new Vector(u);
		result.Subtract(v);
		return result;
	}
	
	/**
	 * Computes the dot product : this . vector
	 * @param vector Product vector
	 * @return A reference to this vector
	 */
	public double Dot(Vector vector) {
		return ((x * vector.x) + (y * vector.y));
	}
	
	/**
	 * Computes the scalar product : this.w * vector.w for each w (x & y)
	 * Hacking way to avoid homogeneous coordinates and the use of matrix
	 * @param vector A vector containing the two scalar to multiply by.
	 * @return A reference to this vector
	 */
	public Vector Mul(Vector vector)
	{
		x *= vector.x;
		y *= vector.y;
		
		updated();
		return this;
	}
	
	/**
	 * Computes the scalar product : this * k
	 * @param k Scalar to product
	 * @return A reference to this vector
	 */
	public Vector Scalar(double k)
	{
		x *= k;
		y *= k;
		
		updated();
		return this;
	}
	
	/**
	 * Getter : Norm
	 * @return Current norm
	 */
	public double getNorm() {
		return norm;
	}
	
	/**
	 * Normalises the vector (divides each coordinate by the norm)
	 * @return A reference to this vector
	 */
	public Vector Normalise()
	{
		x /= norm;
		y /= norm;
		
		return this;
	}
	
	/**
	 * Computes new norm.
	 * Must be called everytime coordinates had changed.
	 */
	protected void updated() {
		norm = Math.sqrt((x * x) + (y * y));
	}
	
	/**
	 * Return a description of this vector
	 * @return The string description
	 */
	@Override
	public String toString() {
		return "Vector(" + x + ", " + y + ")";
	}
	
	/**
	 * Return this vector as a float array
	 * @return The float array
	 */
	public float[] toFloatArray() {
		return new float[]{(float)x, (float)y};
	}
}
