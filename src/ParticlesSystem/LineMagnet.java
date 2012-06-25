package ParticlesSystem;


import Utils.Vector3;

/**
 * A line magnet is a magnet materialized by an infinite line.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class LineMagnet extends Magnet
{
	// a*x + b*y + c = 0
    protected float a;
	protected float b;
	protected float c;
	
	protected Vector3 normal;
	protected Vector3 dir;
	
	protected float a2;
	protected float b2;
	
	/**
	 * Construct a line magnet
	 * @param a Abscissa of the point scaled by ParticlesSystem.ENGINE_X
	 * @param b Ordinate of the point scaled by ParticlesSystem.ENGINE_Y
	 */
	public LineMagnet(float a, float b, float c, float forceOnFreeParticles, float forceOnGridParticles)
	{
		super(forceOnFreeParticles, forceOnGridParticles);
		this.a = a;
        this.b = b;
		this.c = c;
		
		normal = new Vector3(a, b).Normalise();
		dir = new Vector3(-b, a).Normalise();
		
		a2 = a * a;
		b2 = b * b;
	}
    
	/**
	 * Compute the attractive force to apply
	 * @param particle Particle on which force will be apply
	 */
	@Override	
	protected float[] getMagnetForce(Particle particle, float force)
	{
		// On cherche le projeté H(hx, hy)
		double hx, hy;
		double mx = particle.getX();
		double my = particle.getY();
		
		// b et ux sont différents de 0
		if(Math.abs(b) > 0.001)
		{
			double u = dir.y / dir.x;
			double num = (- normal.x * (mx + (u * my))) - (c / b * normal.y);
			double denom = normal.y + (normal.x * u);
			hy = num / denom;
			hx = mx + (u * (my - hy));
		}
		else
		{
			hy = my;
			hx = - (c / a) - (my * (normal.y / normal.x)); 
		}
		
		return new PointMagnet((float)hx, (float)hy, forceOnFreeParticles, forceOnGridParticles).getMagnetForce(particle, force);
	}
}
