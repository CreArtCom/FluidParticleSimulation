package Utils;

/**
 * Vector3 is a common implementation of 3D Vectors.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public final class Vector3
{
	/** The X Value */
	public double x;
	
	/** The Y Value */
	public double y;
	
	/** The Z Value */
	public double z;
	
	/** The current vector's norm */
	protected double norm;
	
	/** 3D normalised vector oriented to X */
	static public Vector3 UNITX = new Vector3(1., 0., 0.);
	
	/** 3D normalised vector oriented to Y */
	static public Vector3 UNITY = new Vector3(0., 1., 0.);
	
	/** 3D normalised vector oriented to Z */
	static public Vector3 UNITZ = new Vector3(0., 0., 1.);

	/**
	 * 3D constructor
	 * @param x The X value
	 * @param y The Y value
	 * @param z The Z value
	 */
	public Vector3(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		updated();
	}
	
	/**
	 * 2D constructor (z will be set to 0)
	 * @param x The X value
	 * @param y The Y value
	 */
	public Vector3(double x, double y) {
		this(x, y, 0.);
	}
	
	/**
	 * 1D constructor (y and z will be set to 0)
	 * @param x The X value
	 */
	public Vector3(double x) {
		this(x, 0., 0.);
	}
	
	/**
	 * Default constructor
	 * Construct an zero vector (0., 0., 0.)
	 */
	public Vector3() {
		this(0., 0., 0.);
	}
	
	/**
	 * Copy constructor
	 * @param vector Vector3 to copy
	 */
	public Vector3(Vector3 vector) {
		this(vector.x, vector.y, vector.z);
	}
	
	/**
	 * Computes the addition : this + vector
	 * @param vector Vector3 to add
	 * @return A reference to this vector
	 */
	public Vector3 Add(Vector3 vector)
	{
		x += vector.x;
		y += vector.y;
		z += vector.z;
		
		updated();
		return this;
	}
	
	/**
	 * Computes an addition between to vectors : u + v
	 * @param u First vector
	 * @param v Second vector
	 * @return The result vector
	 */
	static public Vector3 Add(Vector3 u, Vector3 v)
	{
		Vector3 result = new Vector3(u);
		result.Add(v);
		return result;
	}

	/**
	 * Computes the subtraction : this - vector
	 * @param vector Vector3 to subtract
	 * @return A reference to this vector
	 */
	public Vector3 Subtract(Vector3 vector)
	{
		x -= vector.x;
		y -= vector.y;
		z -= vector.z;
		
		updated();
		return this;
	}
	
	/**
	 * Computes a subtraction between to vectors : u - v
	 * @param u Vector3 which will be subtracted
	 * @param v Vector3 to subtract
	 * @return The result vector
	 */
	static public Vector3 Subtract(Vector3 u, Vector3 v)
	{
		Vector3 result = new Vector3(u);
		result.Subtract(v);
		return result;
	}
	
	/**
	 * Computes the dot product : this . vector
	 * @param vector Product vector
	 * @return A reference to this vector
	 */
	public double Dot(Vector3 vector)
	{
		return ((x * vector.x) + (y * vector.y) + (z * vector.z));
	}
	
	/**
	 * Computes the scalar product : this.w * vector.w for each w (x, y & z)
	 * @param vector A vector containing the three scalar to multiply by.
	 * @return A reference to this vector
	 */
	public Vector3 Mul(Vector3 vector)
	{
		x *= vector.x;
		y *= vector.y;
		z *= vector.z;
		
		updated();
		return this;
	}
	
	/**
	 * Computes the scalar product : this * k
	 * @param k Scalar to product
	 * @return A reference to this vector
	 */
	public Vector3 Scalar(double k)
	{
		x *= k;
		y *= k;
		z *= k;
		
		updated();
		return this;
	}
	
	/**
	 * Computes the cross product : this ^ vector
	 * @param vector Product vector
	 * @return A reference to this vector
	 */
	public Vector3 Cross(Vector3 vector)
	{
		Vector3 temp = new Vector3(this);
		
		x = (temp.y * vector.z) - (temp.z * vector.y);
		y = (temp.z * vector.x) - (temp.x * vector.z);
		z = (temp.x * vector.y) - (temp.y * vector.x);
		
		updated();
		return this;
	}
	
	/**
	 * Getter : Norm
	 * @return Current norm
	 */
	public double getNorm()
	{
		return norm;
	}
	
	/**
	 * Normalises the vector (divides each coordinate by the norm)
	 * @return A reference to this vector
	 */
	public Vector3 Normalise()
	{
		x /= norm;
		y /= norm;
		z /= norm;
		
		return this;
	}
	
	/**
	 * Computes new norm.
	 * Must be called everytime coordinates had changed.
	 */
	protected void updated()
	{
		// Computes new norm
		norm = Math.sqrt((x * x) + (y * y) + (z * z));
	}
	
	/**
	 * Return a description of this vector
	 * @return The string description
	 */
	@Override
	public String toString()
	{
		return "Vector(" + x + ", " + y + ", " + z + ")";
	}
}
