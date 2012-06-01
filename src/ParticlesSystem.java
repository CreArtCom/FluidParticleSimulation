/**
 * Something about Licence
 * 
 * @author		Léo LEFEBVRE
 * @version		1.0
 */

import java.util.ArrayList;
import java.util.List;

public class ParticlesSystem 
{
    List<Particle> particlesGrid = new ArrayList<Particle>();
	List<Particle> particlesFree = new ArrayList<Particle>();
    ParticlesSimulation particlesSimulation;
	
	public static final int CIRCLE			= 0;
	public static final int CENTERED_BOX	= 1;
	public static final int CORNERED_BOX	= 2;

    // Paramètres par défaut
    final float	SEUIL			= 0.1f;
	final float	MOMENTUM		= 0.05f;
	final float	STIFFNESS		= 0.5f;
	final float	FRICTION		= 0.1f;
	final int	MEMORY			= 2;
	
	// Bornes coordonnées Engine
	static final float[] ENGINE_X = {0, 1};
	static final float[] ENGINE_Y = {0, 1};
	
	// Coefficients pour interpolation (linear scale)
	float[] xFrom, yFrom;
	float[] xTo, yTo;
	
	private float	stiffness;
	private float	momentum;
	private float	friction;
	private float	seuil;
	private int		memory;

	private int		nbParticlesW;
	private int		nbParticlesH;
	private float	radius2;
	private float	boxWidth;
	private float	boxHeight;
	private float	boxHHeight;
	private float	boxHWidth;
	private int		brush;

	ParticlesSystem(ParticlesSimulation particlesSimulation) 
	{
		this.particlesSimulation = particlesSimulation;
		
		this.stiffness	= STIFFNESS;
		this.momentum	= MOMENTUM;
		this.friction	= FRICTION;
		this.seuil		= SEUIL;
		this.memory		= MEMORY;
		this.brush		= CIRCLE;
		
		// Calcul des coeffs pour l'interpolation GL
		xTo = ParticlesSimulation.computeCoefs(ENGINE_X, ParticlesSimulation.GL_X);
		yTo = ParticlesSimulation.computeCoefs(ENGINE_Y, ParticlesSimulation.GL_Y);
	}

    void update(boolean applyFluid)
	{
        for(Particle p : particlesFree)
		{
			if(applyFluid)
				addFluidForce(p, particlesSimulation.applyFluid(p.getX(), p.getY()));
			
			p.update();
			p.setNewPosition();
        }
		
        for(Particle p : particlesGrid)
		{
			if(applyFluid)
				addFluidForce(p, particlesSimulation.applyFluid(p.getX(), p.getY()));
			
			p.update();
			p.setNewPosition();
        }
    }
	
	protected void reloadParticles()
	{
		particlesGrid.clear();
		
		for(int i = 0; i < nbParticlesW; i++)
		{
			for(int j = 0; j < nbParticlesH; j++)
			{
				particlesGrid.add(new Particle(i, j, scaleFrom(i, j), this));
			}
		}
	}
	
	// [min(initMat);max(initMat)] -> [0;1] pour moteur
	protected float[] scaleFrom(float x, float y) {
		return new float[]{((xFrom[0] * x) + xFrom[1]), ((yFrom[0] * y) + yFrom[1])};
	}
	
	// [0;1] -> [minGl;maxGl]
	protected float[] scaleTo(float x, float y) {
		return new float[]{((xTo[0] * x) + xTo[1]), ((yTo[0] * y) + yTo[1])};
	}

	// Place dans la outGridMatrix la position actuelle de la particule
	void setParticlePosition(int i, int j, float x, float y) {
		particlesSimulation.getGridMatrix().setcell2d(i, j, scaleTo(x, y));
	}
	
	// Place dans la outFreeMatrix la position actuelle de la particule
	void setParticlePosition(int i, Float[] x, Float[] y)
	{
		for(int j = 0; j < memory; j++)
			particlesSimulation.getFreeMatrix().setcell2d(j, i, scaleTo(x[j], y[j]));
	}
	
	// Place dans la initGridMatrix la position initiale de la particule
	void setParticleInitPosition(int i, int j, float initX, float initY) {
		particlesSimulation.getInitMatrix().setcell2d(i, j, scaleTo(initX, initY));
	}

	private Boolean inCircle(float Cx, float Cy, float pX, float pY)
	{
		float A = Cx - pX;
		float B = Cy - pY; 
		return ((A*A) + (B*B) <= radius2);
	}
	
	// Rectangle dont C(Cx, Cy) est le centre
	private Boolean inCenteredBox(float Cx, float Cy, float pX, float pY)
	{
		return (pX > (Cx - boxHWidth) && pX < (Cx + boxHWidth) && pY > (Cy - boxHHeight) && pY < (Cy + boxHHeight));
	}
	
	// Rectangle dont C(Cx, Cy) est l'angle inférieur gauche
	private Boolean inCorneredBox(float Cx, float Cy, float pX, float pY)
	{
		return (pX >= Cx && pX < (Cx + boxWidth) && pY >= Cy && pY < (Cy + boxHeight));
	}
	
	public void addForce(float posX, float posY, float dx, float dy)
	{
		if(Math.abs(dx) > seuil || Math.abs(dy) > seuil)
		{
			for(Particle p : particlesGrid)
			{		
				switch(brush)
				{
					case CIRCLE:// Cercle
					{
						if(inCircle(p.getX(), p.getY(), posX, posY))
							p.Move(dx, dy);
						break;
					}

					case CORNERED_BOX:// Rectangle
					{
						if(inCorneredBox(p.getX(), p.getY(), posX, posY))
							p.Move(dx, dy);
						break;
					}

					case CENTERED_BOX:// Rectangle
					{
						if(inCenteredBox(p.getX(), p.getY(), posX, posY))
							p.Move(dx, dy);
						break;
					}
				}
			}
		}
	}
	
	public boolean hasParticles() {
		return nbParticlesW > 0 && nbParticlesH > 0;
	}
	
	private void addFluidForce(Particle particle, float[] delta)
	{
		if(Math.abs(delta[0]) > seuil || Math.abs(delta[1]) > seuil)
		{
			particle.Move(delta[0], delta[1]);
		}
	}

	void reset() {
		reloadParticles();
		particlesFree.clear();
	}

	void addParticles(float x, float y, int nb)
	{
		particlesSimulation.getFreeMatrix().setDim(new int[]{memory, particlesFree.size() + nb});
		
		for(int i = 0; i < nb; i++)
		{
			int index = particlesFree.size();
			
			for(int j = 0; j < memory; j++)
				particlesSimulation.getFreeMatrix().setcell2d(j, index, new float[]{1, 1});
			
			particlesFree.add(new Particle(index, x, y, this));
		}
	}

	void destroy() {
		particlesGrid.clear();
		particlesFree.clear();
	}
	
	/********************************* SETTERS *********************************/
	public ParticlesSystem setCircleRadius(float radius) {
		this.radius2 = radius * radius;
		return this;
	}

	public ParticlesSystem setSeuil(float seuil) {
		this.seuil = seuil;
		return this;
	}
	
	public ParticlesSystem setStiffness(float stiffness) {
		this.stiffness = stiffness;
		return this;
	}
	
	public ParticlesSystem setFriction(float friction) {
		this.friction = friction;
		return this;
	}
	
	public ParticlesSystem setMomentum(float momentum) {
		this.momentum = momentum;
		return this;
	}
	

	public ParticlesSystem setBoxHeight(float height) {
		this.boxHeight = height;
		this.boxHHeight = height / 2.f;
		return this;
	}

	public ParticlesSystem setBoxWidth(float width) {
		this.boxWidth = width;
		this.boxHWidth = width / 2.f;
		return this;
	}
	
	public void setMemory(int memory) {
		this.memory = memory;
	}

	public void setNbParticles(int nbParticlesW, int nbParticlesH) {
		this.nbParticlesW	= nbParticlesW;
		this.nbParticlesH	= nbParticlesH;
		
		// Calcul des marges pour centrer les particules dans l'Engine
		float xMargin = (1.f / (float)(nbParticlesW + 1)) * (ENGINE_X[1] - ENGINE_X[0]);
		float yMargin = (1.f / (float)(nbParticlesH + 1)) * (ENGINE_Y[1] - ENGINE_Y[0]);
		
		// Calcul des coeffs pour l'interpolation Engine
		xFrom = ParticlesSimulation.computeCoefs(0, nbParticlesW - 1, ENGINE_X[0] + xMargin, ENGINE_X[1] - xMargin);
		yFrom = ParticlesSimulation.computeCoefs(0, nbParticlesH - 1, ENGINE_Y[0] + yMargin, ENGINE_Y[1] - yMargin);
		
		reloadParticles();
	}

	public void setBrush(int brush) {
		this.brush = brush;
	}
	
	/********************************* GETTERS *********************************/
	
	/**
	 * Getter : stiffness
	 * @return	La constante de raideur de ressort appliquée aux particules du
	 *			système.
     * @since	1.0
     */
	float getStiffness() {
		return stiffness;
	}

	/**
	 * Getter : momentum
	 * @return	La pondération du moment appliquée aux particules du système.
	 *			Le moment est un le déplacement aléatoire d'une particule.
     * @since	1.0
     */
	float getMomentum() {
		return momentum;
	}
	
	/**
	 * Getter : friction
	 * @return	Pourcentage de frottement appliqué aux déplacements des 
	 *			particules du système. 0% = libre, 100% = immobile
     * @since	1.0
     */
	float getFriction() {
		return friction;
	}
	
	/**
	 * Getter : seuil
	 * @return	Seuil en deça duquel les particules du système seront 
	 *			considérées comme immobiles.
     * @since	1.0
     */
	float getSeuil() {
		return seuil;
	}

	/**
	 * Getter : memory
	 * @return	Nombre de positions à mémoriser pour le tracé du déplacement des
	 *			particules libres du système.
     * @since	1.0
     */
	public int getMemory() {
		return memory;
	}
}
