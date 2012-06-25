package Utils;

import ParticlesSystem.Particle;

/**
 * A brush is a geometric form attached to a blob and can interfere with others
 * objects.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class Brush
{
	/** Circle's brush where blob's center is the center */
	public static final int CIRCLE = 0;
	
	/** Rectangle's brush where blob's center is the center */
	public static final int CENTERED_BOX = 1;
	
	/** Rectangle's brush where blob's center is the top-left corner */
	public static final int CORNERED_BOX = 2;
	
	protected int	type;
	protected float radius;
	protected float	radius2;
	protected float	boxWidth;
	protected float	boxHeight;
	protected float	boxHHeight;
	protected float	boxHWidth;

	
	public Brush() {
		this(CIRCLE);
	}
	
	public Brush(int type)
	{
		this.type		= type;
		this.radius		= 0.1f;
		this.radius2	= 0.01f;
		this.boxWidth	= 0.1f;
		this.boxHWidth	= 0.05f;
		this.boxHeight	= 0.1f;
		this.boxHHeight	= 0.05f;
	}
	
	public boolean intersect(float posX, float posY, float blobX, float blobY)
	{
		switch(type)
		{
			case CIRCLE:// Cercle
			{
				if(inCircle(blobX, blobY, posX, posY))
					return true;
				break;
			}

			case CORNERED_BOX:// Rectangle
			{
				if(inCorneredBox(blobX, blobY, posX, posY))
					return true;
				break;
			}

			case CENTERED_BOX:// Rectangle
			{
				if(inCenteredBox(blobX, blobY, posX, posY))
					return true;
				break;
			}
		}
		return false;
	}
	
	
	private boolean inCircle(float Cx, float Cy, float pX, float pY)
	{
		float A = Cx - pX;
		float B = Cy - pY; 
		return ((A*A) + (B*B) <= radius2);
	}
	
	// Rectangle dont C(Cx, Cy) est le centre
	private boolean inCenteredBox(float Cx, float Cy, float pX, float pY)
	{
		return (pX > (Cx - boxHWidth) && pX < (Cx + boxHWidth) && pY > (Cy - boxHHeight) && pY < (Cy + boxHHeight));
	}
	
	// Rectangle dont C(Cx, Cy) est l'angle inférieur gauche
	private boolean inCorneredBox(float Cx, float Cy, float pX, float pY)
	{
		return (pX >= Cx && pX < (Cx + boxWidth) && pY >= Cy && pY < (Cy + boxHeight));
	}
	
	public boolean intersect(Particle particle, float posX, float posY)
	{
		return intersect(particle.getX(), particle.getY(), posX, posY);
	}

	public void setBoxHeight(float boxHeight) {
		this.boxHeight = boxHeight;
		this.boxHHeight = boxHeight / 2.f;
	}

	public void setBoxWidth(float boxWidth) {
		this.boxWidth = boxWidth;
		this.boxHWidth = boxWidth / 2.f;
	}

	public void setRadius(float radius) {
		this.radius = radius;
		this.radius2 = radius * radius;
	}

	public void setType(int type) {
		this.type = type;
	}
}
