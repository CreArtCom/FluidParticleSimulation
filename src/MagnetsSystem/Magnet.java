package MagnetsSystem;

import ParticlesSystem.Particle;

/**
 * A magnet is an entity which attracts particles to itself.
 * Attractive force is computed as a gravitation force : 
 * f = k / d² (f = force, k = constant, d = distance)
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
abstract public class Magnet
{
	protected static double WEIGHT = 1000.;
	
	/** Force that this magnet apply on its environnement. */
	protected double force;
	
	/** Super abstract constructor */
	public Magnet(double force) {
		this.force	= force / WEIGHT;
	}
	
	/**
	 * Apply the attractive force on the particle
	 * @param particle Particle on which force will be apply
	 */
	public abstract void applyMagnetForce(Particle particle);

	public void setForce(double newForce) {
		this.force = newForce;
	}
	
	@Override
	public abstract String toString();
}
