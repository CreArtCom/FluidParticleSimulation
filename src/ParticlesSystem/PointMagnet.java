package ParticlesSystem;

/**
 * A point magnet is a magnet materialized by a point.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class PointMagnet extends Magnet
{
	/**
	 * Current abscissa of the point magnet scaled by ParticlesSystem.ENGINE_X
	 */
    protected float x;
	
	/**
	 * Current ordinate of the point magnet scaled by ParticlesSystem.ENGINE_Y
	 */
	protected float y;
	
	/**
	 * Construct a point magnet
	 * @param x Abscissa of the point scaled by ParticlesSystem.ENGINE_X
	 * @param y Ordinate of the point scaled by ParticlesSystem.ENGINE_Y
	 */
	public PointMagnet(float x, float y, float forceOnFreeParticles, float forceOnGridParticles)
	{
		super(forceOnFreeParticles, forceOnGridParticles);
		this.x = x;
        this.y = y;
	}
    
	/**
	 * Compute the attractive force to apply
	 * @param particle Particle on which force will be apply
	 */
	@Override
	protected float[] getMagnetForce(Particle particle, float force)
	{
		float dx = particle.getX() - x;
		float dy = particle.getY() - y;
		double d = Math.sqrt((dx * dx) + (dy * dy));

		double d2 = d * d;
		double d3 = d2 * d;
		double kp = - force / d3;
		
		double dxp = kp * dx;
		double dyp = kp * dy;
		//double dp = Math.sqrt((dxp * dxp) + (dyp * dyp));
		
		return new float[]{(float)dxp, (float)dyp};
	}
}
