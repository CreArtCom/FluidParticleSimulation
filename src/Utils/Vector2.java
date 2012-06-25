package Utils;

/**
 * Vector2 is a common implementation of 2D Vectors.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public final class Vector2
{
	/** The X Value */
	public double x;
	
	/** The Y Value */
	public double y;
	
	/** The current vector's norm */
	protected double norm;
	
	/** 3D normalised vector oriented to X */
	static public Vector2 UNITX = new Vector2(1., 0.);
	
	/** 3D normalised vector oriented to Y */
	static public Vector2 UNITY = new Vector2(0., 1.);

	/**
	 * 3D constructor
	 * @param x The X value
	 * @param y The Y value
	 */
	public Vector2(double x, double y)
	{
		this.x = x;
		this.y = y;
		updated();
	}
	
	/**
	 * 1D constructor (y will be set to 0)
	 * @param x The X value
	 */
	public Vector2(double x) {
		this(x, 0.);
	}
	
	/**
	 * Default constructor
	 * Construct an zero vector (0., 0.)
	 */
	public Vector2() {
		this(0., 0.);
	}
	
	/**
	 * Copy constructor
	 * @param vector Vector3 to copy
	 */
	public Vector2(Vector2 vector) {
		this(vector.x, vector.y);
	}
	
	/**
	 * Computes the addition : this + vector
	 * @param vector Vector3 to add
	 * @return A reference to this vector
	 */
	public Vector2 Add(Vector2 vector)
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
	static public Vector2 Add(Vector2 u, Vector2 v)
	{
		Vector2 result = new Vector2(u);
		result.Add(v);
		return result;
	}

	/**
	 * Computes the subtraction : this - vector
	 * @param vector Vector3 to subtract
	 * @return A reference to this vector
	 */
	public Vector2 Subtract(Vector2 vector)
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
	static public Vector2 Subtract(Vector2 u, Vector2 v)
	{
		Vector2 result = new Vector2(u);
		result.Subtract(v);
		return result;
	}
	
	/**
	 * Computes the dot product : this . vector
	 * @param vector Product vector
	 * @return A reference to this vector
	 */
	public double Dot(Vector2 vector)
	{
		return ((x * vector.x) + (y * vector.y));
	}
	
	/**
	 * Computes the scalar product : this.w * vector.w for each w (x, y & z)
	 * @param vector A vector containing the three scalar to multiply by.
	 * @return A reference to this vector
	 */
	public Vector2 Mul(Vector2 vector)
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
	public Vector2 Scalar(double k)
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
	public double getNorm()
	{
		return norm;
	}
	
	/**
	 * Normalises the vector (divides each coordinate by the norm)
	 * @return A reference to this vector
	 */
	public Vector2 Normalise()
	{
		x /= norm;
		y /= norm;
		
		return this;
	}
	
	/**
	 * Computes new norm.
	 * Must be called everytime coordinates had changed.
	 */
	protected void updated()
	{
		// Computes new norm
		norm = Math.sqrt((x * x) + (y * y));
	}
	
	/**
	 * Return a description of this vector
	 * @return The string description
	 */
	@Override
	public String toString()
	{
		return "Vector(" + x + ", " + y + ")";
	}
}
