/**
 * Something about Licence
 * 
 * @author		
 * @author		Léo LEFEBVRE
 * @version		1.0
 */

public class Blob
{
	// Normalisées de minEngine à maxEngine
	private float x;
	private float y;
	private float deltaX;
	private float deltaY;
	
	private FluidParticleSimulation fluidParticleSimulation;

	/**
	 * Construit un blob en normalisant ses coordonnées
     * @param x				Abcisse du blob borné selon msg_x_scale
	 * @param y				Ordonnée du blob borné selon msg_y_scale
	 * @param simulation	Objet chargé d'effectuer la simulation
     * @since				1.0
     */
	public Blob(float x, float y, FluidParticleSimulation simulation)
	{
		this.fluidParticleSimulation = simulation;
		float[] norms = scale(x, y);
		this.x = norms[0];
		this.y = norms[1];
	}

	public void Move(float posX, float posY)
	{
		float[] norms = scale(posX, posY);
		deltaX = norms[0] - x;
		x = norms[0];
		//deltaX = Math.abs(deltaX) > fluidParticleSimulation.getBlobSeuil() ? deltaX : 0;

		deltaY = norms[1] - y;
		y = norms[1];
		//deltaY = Math.abs(deltaY) > fluidParticleSimulation.getBlobSeuil() ? deltaY : 0;
	}
	

	private float[] scale(float posX, float posY)
	{
		float[] xCoefs = fluidParticleSimulation.getXCoefs();
		float[] yCoefs = fluidParticleSimulation.getYCoefs();
		
		return new float[]{((xCoefs[0] * posX) + xCoefs[1]), ((yCoefs[0] * posY) + yCoefs[1])};
	}
	
	/********************************* GETTERS *********************************/
	
	/**
	 * Getter : x
	 * @return	Position courante du blob sur l'axe X normalisée entre 
	 *			ParticlesSystem.ENGINE_X[0]	et ParticlesSystem.ENGINE_X[1]
     * @since	1.0
     */
	public float getX() {
		return x;
	}

	/**
	 * Getter : y
	 * @return	Position courante du blob sur l'axe Y normalisée entre 
	 *			ParticlesSystem.ENGINE_X[0]	et ParticlesSystem.ENGINE_X[1]
     * @since	1.0
     */
	public float getY() {
		return y;
	}
	
	/**
	 * Getter : deltaX
	 * @return	La différence de distance entre les deux dernières positions du 
	 *			blob selon l'axe X, le tout pondéré par la valeur de
	 *			fluidParticleSimulation.blobForce
     * @since	1.0
     */
	public float getDeltaX() {
		return deltaX * fluidParticleSimulation.getBlobForce();
	}

	/**
	 * Getter : deltaY
	 * @return	La différence de distance entre les deux dernières positions du 
	 *			blob selon l'axe Y, le tout pondéré par la valeur de
	 *			fluidParticleSimulation.blobForce
     * @since	1.0
     */
	public float getDeltaY() {
		return deltaY * fluidParticleSimulation.getBlobForce();
	}
}
