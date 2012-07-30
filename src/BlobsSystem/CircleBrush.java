package BlobsSystem;

import Utils.Vector;

/**
 * A circle brush is a brush materialized by a 2D circle.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class CircleBrush extends Brush
{
	/** Radius of the circle */
	protected double radius;

	/** Default constructor of a Brush (radius = 0.1) */
	public CircleBrush() {
		this.radius		= 0.1f;
	}
	
	/**
	 * Construct a circle brush
	 * @param radius Radius of the circle
	 */
	public CircleBrush(double radius) {
		this.radius		= radius;
	}
	
	/**
	 * Determine whether the given position is in the current blob's position of this circle
	 * @param position Position of the given point
	 * @param blobPos Current blob position
	 * @return <code>true</code> if the given position is in the current circle's position, <code>false</code> otherwise
	 */
	@Override
	public boolean intersect(Vector position, Vector blobPos)
	{
		Vector temp = new Vector(position);
		temp.Subtract(blobPos);
		return (temp.getNorm() <= radius);
	}
	
	/**
	 * Set the radius value
	 * @param radius New radius
	 */
	public void setRadius(double radius) {
		this.radius		= radius;
	}
}
