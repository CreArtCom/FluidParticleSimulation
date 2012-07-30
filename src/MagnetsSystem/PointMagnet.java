package MagnetsSystem;

import ParticlesSystem.Particle;
import Utils.Vector;

/**
 * A point magnet is a magnet materialized by a point.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class PointMagnet extends Magnet
{
	/** Current position of the point magnet scaled by Max.ENGINE_... */
    protected Vector position;

	/**
	 * Construct a point magnet
	 * @param position Position of the point scaled by Max.ENGINE_...
	 */
	public PointMagnet(Vector position, double force)
	{
		super(force);
		this.position = position;
	}
    
	/**
	 * Compute the attractive force to apply
	 * @param particle Particle on which force will be apply
	 */
	@Override
	public void applyMagnetForce(Particle particle)
	{
		Vector dir = new Vector(particle.getPosition()).Subtract(position);
		if(dir.getNorm() > 0)
		{
			double k = force / (dir.getNorm() * dir.getNorm());
			particle.addForce(dir.Normalise().Scalar(-k));
		}
	}

	@Override
	public String toString() {
		return "PointMagnet " + position.x + " " + position.y + " " + force;
	}
}
