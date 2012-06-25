package ParticlesSystem;

/**
 * A magnet is an entity which attracts particles to itself.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
abstract public class Magnet
{
	protected float forceOnGridParticles;
	protected float forceOnFreeParticles;
	
	public Magnet(float forceOnFreeParticles, float forceOnGridParticles)
	{
		this.forceOnFreeParticles	= forceOnFreeParticles / 1000.f;
		this.forceOnGridParticles	= forceOnGridParticles / 1000.f;
	}
	
	/**
	 * Apply the appropriate attractive force
	 * @param particle Particle on which force will be apply
	 */
	public void applyMagnetForce(Particle particle)
	{
		// Particle is a GridParticle
		if(particle instanceof GridParticle)
			particle.addForce(getMagnetForce(particle, forceOnGridParticles));
		else if(particle instanceof FreeParticle)
			particle.addForce(getMagnetForce(particle, forceOnFreeParticles));
	}
	
	/**
	 * Compute the attractive force to apply
	 * @param particle Particle on which force will be apply
	 */
	protected abstract float[] getMagnetForce(Particle particle, float force);
	
}
