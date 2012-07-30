package Utils;

/**
 * Line is a common implementation of a 2D infinite line.
 * Every lines verify this equation for every (x, y) : (a * x) + (b * y) + c = 0
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 * @since	2.0
 */
public class Line
{
	/** Coefficient "a" in the equation : (a * x) + (b * y) + c = 0 */
	protected double a;
	
	/** Coefficient "b" in the equation : (a * x) + (b * y) + c = 0 */
	protected double b;
	
	/** Coefficient "c" in the equation : (a * x) + (b * y) + c = 0 */
	protected double c;
	
	/** Precision use for computes. Values below this one are considered null. */
	protected double precision;

	/**
	 * Construct a line with two vectors
	 * @param director Line's vector director
	 * @param point Point which is on the line
	 * @param precision Precision value
	 */
	public Line(Vector director, Vector point, double precision)
	{
		this.a			= director.y;
		this.b			= - director.x;
		this.c			= - ((a * point.x) + (b * point.y));
		this.precision	= precision;
	}
	
	/**
	 * Construct a line from two points
	 * @param point1 Point which is on the line
	 * @param point2 Point which is on the line
	 */
	public Line(Vector point1, Vector point2) {
		this(new Vector(point2).Subtract(point1), point1, 0.);
	}
	
	/**
	 * Construct a line with its coeficients' equation : a * x + b * y + c = 0
	 * @param a The a value
	 * @param b The b value
	 * @param c The c value
	 * @param precision Precision value
	 */
	public Line(double a, double b, double c, double precision) {
		this.a			= a;
		this.b			= b;
		this.c			= c;
		this.precision	= precision;
	}
	
	public Line(double precision, Vector A, Vector B) {
		this(new Vector(B).Subtract(A), A, precision);
	}
	
	/**
	 * Construct a line with its coeficients' equation : a * x + b * y + c = 0
	 * @param a The a value
	 * @param b The b value
	 * @param c The c value
	 */
	public Line(double a, double b, double c) {
		this(a, b, c, 0.);
	}
	
	/**
	 * Determine if the given point is on the line
	 * @param point The given point to test
	 * @return <code>true</code> if the point is on the line, <code>else</code> otherwise
	 */
	public boolean IsOn(Vector point) {
		return ((a * point.x) + (b * point.y) + c) <= precision;
	}
	
	/**
	 * Get the normal normalised vector of this line
	 * @return The normal normalized vector
	 */
	public Vector getNormal() {
		return new Vector(a, b).Normalise();
	}
	
	/**
	 * Get the director normalised vector of this line
	 * @return The director normalized vector
	 */
	public Vector getDirector() {
		return new Vector(-b, a).Normalise();
	}
	
	public Vector getAPoint() {
		if(b != 0)
			return new Vector(0., -c / b);
		else
			return new Vector(-c / a, 0.);
	}
	
	/**
	 * Get the orthographic projection of the given point on this line
	 * @param point The given point
	 * @return The orthographic projection of the given point on this line
	 */
	public Vector getOrthographicProjection(Vector point)
	{
		return new Vector(getDirector()).Scalar(getDirector().Dot(new Vector(point).Subtract(getAPoint()))).Add(getAPoint());
//		// On cherche le projeté H(hx, hy)
//		double hx, hy;
//		double mx = point.x;
//		double my = point.y;
//		Vector normal = getNormal();
//		
//		// b (et ux) sont différents de 0
//		if(Math.abs(b) > precision)
//		{
//			Vector dir = getDirector();
//			double u = dir.y / dir.x;
//			double num = (- normal.x * (mx + (u * my))) - (c / b * normal.y);
//			double denom = normal.y + (normal.x * u);
//			hy = num / denom;
//			hx = mx + (u * (my - hy));
//		}
//		else
//		{
//			hy = my;
//			hx = - (c / a) - (my * (normal.y / normal.x)); 
//		}
//		
//		return new Vector(hx, hy);
	}
}
