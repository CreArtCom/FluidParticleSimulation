package BlobsSystem;

import Utils.Vector;

/**
 * A rectangle brush is a brush materialized by a 2D rectangle.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 * @since	2.0
 */
public class RectangleBrush extends Brush
{
	/** Rectangle's brush where blob's center is the rectangle center */
	public static final int CENTER = 0;
	
	/** Rectangle's brush where blob's center is the rectangle bottom-left corner */
	public static final int BOTTOM_LEFT = 1;
	
	/** Rectangle's brush where blob's center is the rectangle top-left corner */
	public static final int TOP_LEFT = 2;
	
	/** Rectangle's brush where blob's center is the rectangle bottom-right corner */
	public static final int BOTTOM_RIGHT = 3;
	
	/** Rectangle's brush where blob's center is the rectangle top-right corner */
	public static final int TOP_RIGHT = 4;
	
	/** Width of the rectangle */
	protected double width;
	
	/** Height of the rectangle */
	protected double height;
	
	/** Half-Height of the rectangle. Helps to reduce redundants computes. */
	protected double hHeight;
	
	/** Half-Width of the rectangle. Helps to reduce redundants computes. */
	protected double hWidth;
	
	/** Position of the blobCenter compared with the rectangle. (use static members) */
	protected int blobCenter;

	/** Default constructor of a Rectangle brush (0.1 square blob-centered) */
	public RectangleBrush() {
		this(0.1f, 0.1f, CENTER);
	}
	
	/**
	 * Construct a rectangle brush
	 * @param width Width of the rectangle
	 * @param height Height of the rectangle
	 * @param blobCenter Where the blob center is attached on the rectangle (use static members)
	 */
	public RectangleBrush(double width, double height, int blobCenter)
	{
		this.width		= width;
		this.hWidth		= width / 2.f;
		this.height		= height;
		this.hHeight	= height / 2.f;
		this.blobCenter = blobCenter;
	}
	
	/**
	 * Construct a blob-centered rectangle brush
	 * @param width Width of the rectangle
	 * @param height Height of the rectangle
	 */
	public RectangleBrush(double width, double height)
	{
		this(width, height, CENTER);
	}
	
	/**
	 * Determine whether the given position is in the current blob's position of this rectangle
	 * @param position Position of the given point
	 * @param blobPos Current blob position
	 * @return <code>true</code> if the given position is in the current rectangle's position, <code>false</code> otherwise
	 */
	@Override
	public boolean intersect(Vector position, Vector blobPos)
	{
		switch(blobCenter)
		{
			case CENTER:
				return (blobPos.x > (position.x - hWidth) && blobPos.x < (position.x + hWidth) && blobPos.y > (position.y - hHeight) && blobPos.y < (position.y + hHeight));

			case BOTTOM_LEFT:
				return (blobPos.x >= position.x && blobPos.x < (position.x + width) && blobPos.y >= position.y && blobPos.y < (position.y + height));
				
			case TOP_LEFT:
				return false;
				
			case BOTTOM_RIGHT:
				return false;
				
			case TOP_RIGHT:
				return false;
		}
		return false;
	}

	/**
	 * Set the rectangle height
	 * @param height New height
	 */
	public void setHeight(double height) {
		this.height = height;
		this.hHeight = height / 2.f;
	}

	/**
	 * Set the rectangle width
	 * @param width New width
	 */
	public void setWidth(double width) {
		this.width = width;
		this.hWidth = width / 2.f;
	}
}
