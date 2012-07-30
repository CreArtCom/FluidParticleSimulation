package Utils;

/**
 * LinearScale is a common math object that easily computes linear scales in 2D.
 * Computes Y from X with the following relation :
 * Y = (A * X) + B, where all symbols are Vector2.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 * @since	2.0
 */
public class LinearScale2
{
	// A * X + B where X(x,y)
	protected Vector A;
	protected Vector B;
	
	public LinearScale2(Vector FromMin, Vector FromMax, Vector ToMin, Vector ToMax)
	{
		double[] xCoefs = computeCoefs(FromMin.x, FromMax.x, ToMin.x, ToMax.x);
		double[] yCoefs = computeCoefs(FromMin.y, FromMax.y, ToMin.y, ToMax.y);
		
		A = new Vector(xCoefs[0], yCoefs[0]);
		B = new Vector(xCoefs[1], yCoefs[1]);
	}
	
	/**
	 * Computes A and B coefficients for a 1D linear scale (y = a * x + b)
	 * 
	 * @param minFrom Lower bound of original interval
	 * @param maxFrom Higher bound of original interval
	 * @param minTo Lower bound of destination interval
	 * @param maxTo Higher bound of destination interval
	 * @return A and B coefficients
	 */
	private double[] computeCoefs(double minFrom, double maxFrom, double minTo, double maxTo)
	{
		double a, b;
		if(minFrom == maxFrom || minTo == maxTo)
		{
			a = 0;
			b = minTo;
		}
		
		// minFrom != 0 && minFrom != maxFrom
		else if(minFrom != 0)
		{
			double r = maxFrom / minFrom;
			b = ((maxTo - (minTo*r))/(1 - r));
			a = ((minTo - b) / minFrom);
		}
		
		// minFrom == 0 && minFrom != maxFrom
		else
		{
			b = minTo;
			a = ((maxTo - b) / maxFrom);
		}
		
		return new double[]{a, b};
	}
	
	/**
	 * Alias : Scale
	 * @param x The abscissa to scale
	 * @param y The ordinate to scale
	 * @return A float array like {new_x, new_y}
	 */
	public float[] Scale(double x, double y) {
		return Scale(new Vector(x, y)).toFloatArray();
	}
	
	/**
	 * Computes and return the scaled vector :  Y = (A * X) + B
	 * @param X The vector to scale
	 * @return The scaled vector
	 */
	public Vector Scale(Vector X)
	{
		Vector result = new Vector(X);
		return result.Mul(A).Add(B);
	}
	
	/**
	 * Computes and return the scaled vector :  Y = (A * X) + B for each vector 
	 * in the given array
	 * @param xArray The vectors array to scale
	 * @return The array of scaled vectors
	 */
	public Vector[] Scale(Vector[] xArray)
	{
		Vector[] result = new Vector[xArray.length];
		
		for(int i = 0; i < xArray.length; i++) {
			result[i] = Scale(xArray[i]);
		}
		
		return result;
	}
	
	/**
	 * Return a readable string which contains A and B vectors.
	 * Usefull for debug.
	 * @return A readable string which describes this object
	 */
	@Override
	public String toString() {
		return "A = " + A.toString() + "\n B = " + B.toString();
	}
}
