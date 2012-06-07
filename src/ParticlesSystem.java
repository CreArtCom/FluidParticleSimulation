import java.util.ArrayList;
import java.util.List;

/**
 * A particles' System is a mechanical 2D system which contains free and tied up particles.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class ParticlesSystem 
{
	/** Circle's brush where blob's center is the center */
	public static final int CIRCLE = 0;
	
	/** Rectangle's brush where blob's center is the center */
	public static final int CENTERED_BOX = 1;
	
	/** Rectangle's brush where blob's center is the top-left corner */
	public static final int CORNERED_BOX = 2;

	// Bornes coordonnées Engine
	public static final float[] ENGINE_X = {0, 1};
	public static final float[] ENGINE_Y = {0, 1};
	
    // Paramètres par défaut
    private static final float	SEUIL		= 0.1f;
	private static final float	MOMENTUM	= 0.05f;
	private static final float	STIFFNESS	= 0.5f;
	private static final float	FRICTION	= 0.1f;
	private static final int	MEMORY		= 2;
	private static final int	MAXFREEPART	= 1000;

	
	// Coefficients pour interpolation (linear scale)
	private float[] xFrom, yFrom;
	private float[] xTo, yTo;
	
	protected float	stiffness;
	protected float	momentum;
	protected float	friction;
	protected float	seuil;
	protected int	memory;
	protected int	maxFreeParticles;
	protected List<FreeParticle> freeParticlesToDel;
	
    private List<GridParticle> particlesGrid;
	private List<FreeParticle> particlesFree;
    private ParticlesSimulation particlesSimulation;

	protected int		nbParticlesW;
	protected int		nbParticlesH;
	protected float		radius2;
	protected float		boxWidth;
	protected float		boxHeight;
	protected float		boxHHeight;
	protected float		boxHWidth;
	protected int		brush;
	protected boolean	applyFluidForce;
	protected int		current;

	/**
	 * Construct a particles' System
	 * @param particlesSimulation Object responsible for conducting the simulation
	 */
	public ParticlesSystem(ParticlesSimulation particlesSimulation) 
	{
		this.particlesSimulation = particlesSimulation;
		
		this.stiffness			= STIFFNESS;
		this.momentum			= MOMENTUM;
		this.friction			= FRICTION;
		this.seuil				= SEUIL;
		this.memory				= MEMORY;
		this.brush				= CIRCLE;
		this.maxFreeParticles	= MAXFREEPART;
		this.applyFluidForce	= ParticlesSimulation.FLUID_FORCE_APPLY;
		this.current			= 0;
		this.particlesGrid		= new ArrayList<GridParticle>();
		this.particlesFree		= new ArrayList<FreeParticle>();
		this.freeParticlesToDel	= new ArrayList<FreeParticle>();
		
		// Calcul des coeffs pour l'interpolation GL
		xTo = ParticlesSimulation.computeCoefs(ENGINE_X, ParticlesSimulation.GL_X);
		yTo = ParticlesSimulation.computeCoefs(ENGINE_Y, ParticlesSimulation.GL_Y);
	}

	/** Update the whole particle's System (each particle position) */
    public void update()
	{
		// Si la gomme est active on met à jour les dim de la matrice et la liste de particules
		if(particlesSimulation.applyBlobEraser() && !freeParticlesToDel.isEmpty())
		{
			for(FreeParticle particle:freeParticlesToDel)
				particlesFree.remove(particle);
			freeParticlesToDel.clear();
			
			particlesSimulation.getFreeMatrix().setDim(new int[]{memory, particlesFree.size()});
		}
		
		// On récupère la liste des blobs dont les coordonnées ont changées
		List<Float[]> blobsMouvements = particlesSimulation.getBlobsMouvements();
		
		// Mise à jour du système de particules libres
        for(int i = 0; i < particlesFree.size(); i++)
			updateFreeParticle(i, particlesFree.get(i), blobsMouvements);
		
		// Mise à jour du système de particules liées
        for(GridParticle p : particlesGrid)
			updateGridParticle(p, blobsMouvements);
		
    }
	
	/** Update a grid particle position (select which forces are applied) */
	private void updateGridParticle(GridParticle particle, List<Float[]> blobsMouvements)
	{
		// On ajoute la force du fluide aux particules
		if(applyFluidForce)
			addFluidForce(particle, particlesSimulation.applyFluid(particle.getX(), particle.getY()));
		
		// On ajoute des forces liées aux blobs
		if(particlesSimulation.applyBlobForce() || particlesSimulation.applyAttractivity())
		{
			// On récupère la liste des blobs intéressants
			for(Float[] mouvement : blobsMouvements)
			{				
				// On ajoute la force des blobs aux particles
				if(particlesSimulation.applyBlobForce())
					addForce(particle, mouvement);

				// On ajoute la force des attracteurs
				if(particlesSimulation.applyAttractivity())
					addAttractivity(particle, mouvement);
			}
		}
		
		particle.update();
		setGridParticlePosition(particle.getI(), particle.getJ(), particle);
	}
	
	/** Update a free particle position (select which forces are applied) */
	private void updateFreeParticle(int index, FreeParticle particle, List<Float[]> blobsMouvements)
	{
		// On ajoute la force du fluide aux particules
		if(applyFluidForce)
			addFluidForce(particle, particlesSimulation.applyFluid(particle.getX(), particle.getY()));
		
		// On ajoute des forces liées aux blobs
		if(particlesSimulation.applyBlobForce() || particlesSimulation.applyAttractivity())
		{
			// On récupère la liste des blobs intéressants
			for(Float[] mouvement : blobsMouvements)
			{				
				// On ajoute la force des blobs aux particles
				if(particlesSimulation.applyBlobForce())
					addForce(particle, mouvement);
				
				// On détruit les particles dans la brosse
				else if(particlesSimulation.applyBlobEraser())
					deleteFree(particle, mouvement);

				// On ajoute la force des attracteurs
				if(particlesSimulation.applyAttractivity())
					addAttractivity(particle, mouvement);
			}
		}
		
		particle.update();
		setFreeParticlePosition(index, particle);
	}
	
	private void reloadGridParticles()
	{
		particlesGrid.clear();
		
		for(int i = 0; i < nbParticlesW; i++)
		{
			for(int j = 0; j < nbParticlesH; j++)
			{
				particlesGrid.add(new GridParticle(i, j, scaleFrom(i, j), this));
			}
		}
	}
	
	// [min(initMat);max(initMat)] -> [0;1] pour moteur
	private float[] scaleFrom(float x, float y) {
		return new float[]{((xFrom[0] * x) + xFrom[1]), ((yFrom[0] * y) + yFrom[1])};
	}
	
	// [0;1] -> [minGl;maxGl]
	private float[] scaleTo(float x, float y) {
		return new float[]{((xTo[0] * x) + xTo[1]), ((yTo[0] * y) + yTo[1])};
	}

	/**
	 * Set up in the outGridMatrix the particle position scaled to FluidParticleSimulation.GL_...
	 * @param i	Column's index of the particle
	 * @param j Line's index of the particle
	 * @param position Position of the particle scaled by ParticlesSystem.ENGINE_...
	 */
	protected void setGridParticlePosition(int i, int j, GridParticle particle) {
		particlesSimulation.getGridMatrix().setcell2d(i, j, scaleTo(particle.getX(), particle.getY()));
	}
	
	/**
	 * Set up in the outFreeMatrix the particle position scaled to FluidParticleSimulation.GL_...
	 * @param i Line's index in outFreeMatrix
	 * @param x Abscissa of the particle scaled by ParticlesSystem.ENGINE_X
	 * @param y Ordinate of the particle scaled by ParticlesSystem.ENGINE_Y
	 */
	protected void setFreeParticlePosition(int index, FreeParticle particle)
	{
		for(int j = 0; j < memory; j++)
			particlesSimulation.getFreeMatrix().setcell2d(j, index, scaleTo(particle.getXHistory().get(j), particle.getYHistory().get(j)));
	}
	
	/**
	 * Set up in the initGridMatrix the particle position scaled to FluidParticleSimulation.GL_...
	 * @param i	Column's index of the particle
	 * @param j Line's index of the particle
	 * @param initX Initial abscissa of the particle scaled by ParticlesSystem.ENGINE_X
	 * @param initY Initial ordinate of the particle scaled by ParticlesSystem.ENGINE_Y
	 */
	protected void setParticleInitPosition(int i, int j, float initX, float initY) {
		particlesSimulation.getInitMatrix().setcell2d(i, j, scaleTo(initX, initY));
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
	
	private boolean intersect(Particle particle, float posX, float posY)
	{
		switch(brush)
		{
			case CIRCLE:// Cercle
			{
				if(inCircle(particle.getX(), particle.getY(), posX, posY))
					return true;
				break;
			}

			case CORNERED_BOX:// Rectangle
			{
				if(inCorneredBox(particle.getX(), particle.getY(), posX, posY))
					return true;
				break;
			}

			case CENTERED_BOX:// Rectangle
			{
				if(inCenteredBox(particle.getX(), particle.getY(), posX, posY))
					return true;
				break;
			}
		}
		return false;
	}
	
	private void addForce(Particle particle, Float[] mouvement)
	{
		if(intersect(particle, mouvement[0], mouvement[1]))
			particle.addForce(mouvement[2], mouvement[3]);
	}
	
	private void deleteFree(FreeParticle particle, Float[] mouvement)
	{
		if(intersect(particle, mouvement[0], mouvement[1]))
			freeParticlesToDel.add(particle);
			//particlesFree.remove(particle);
	}
	
	public void addAttractivity(Particle particle, Float[] mouvement)//float posX, float posY)
	{
		/*if(Math.abs(dx) > seuil || Math.abs(dy) > seuil)
		{
			for(Particle p : particlesGrid)
				if(intersect(p, posX, posY))
					p.applyForce(dx, dy);
		}*/
	}
	
	private void addFluidForce(Particle particle, float[] delta)
	{
		if(Math.abs(delta[0]) > seuil || Math.abs(delta[1]) > seuil)
		{
			particle.applyForce(delta[0], delta[1]);
		}
	}
	
	/**
	 * Add <parameter>nb</parameter> free particles in the system at the indicates position
	 * @param x Abscissa of the position to add particles scaled by ParticlesSystem.ENGINE_X
	 * @param y Ordinate of the position to add particles scaled by ParticlesSystem.ENGINE_Y
	 * @param nbToAdd Number of particles to add
	 */
	protected void addFreeParticles(float x, float y, int nbToAdd)
	{
		// Si la matrice est pas pleine on ajoute une ligne
		int diffToMax = maxFreeParticles - particlesFree.size();
		if(diffToMax > 0)
		{
			int realNbToAdd = diffToMax > nbToAdd ? nbToAdd : diffToMax;
			particlesSimulation.getFreeMatrix().setDim(new int[]{memory, particlesFree.size() + realNbToAdd});
		}
		
		for(int i = 0; i < nbToAdd; i++)
			particlesFree.add(new FreeParticle(x, y, this));
	}
	
	private int getFreeIndex()
	{
		if(particlesFree.size() >= maxFreeParticles)
			particlesFree.remove(0);
			
		return (current++ % maxFreeParticles);
	}

	void loadFreeParticles()
	{
		particlesFree.clear();
		particlesSimulation.getFreeMatrix().setDim(new int[]{memory, maxFreeParticles});
		
		for(current = 0; current < maxFreeParticles; current++)
			particlesFree.add(new FreeParticle(this));
	}
	
	/**
	 * Determine if the system has at least one tied up particle
	 * @return <code>true</code> if at least one tied up particle exists, <code>false</code> otherwise
	 */
	public boolean hasGridParticles() {
		return nbParticlesW > 0 && nbParticlesH > 0;
	}

	/**
	 * Reset the particle system.
	 * Free particles will be cleared and tied up particles will be restored to initial position
	 */
	public void reset() {
		reloadGridParticles();
		particlesFree.clear();
		particlesSimulation.getFreeMatrix().setDim(new int[]{0, 0});
		particlesSimulation.getFreeMatrix().clear();
		current = 0;
	}

	/**
	 * Properly destroy the system.
	 */
	public void destroy() {
		particlesGrid.clear();
		particlesFree.clear();
	}
	
	/**
	 * Set the circle radius
	 * @param radius New radius
	 * @return The current particle's system
	 */
	public ParticlesSystem setCircleRadius(float radius) {
		this.radius2 = radius * radius;
		return this;
	}

	/**
	 * Set the threshold
	 * @param threshold New threshold
	 * @return The current particle's system
	 */
	public ParticlesSystem setThreshold(float threshold) {
		this.seuil = threshold;
		return this;
	}
	
	/**
	 * Set the stiffness
	 * @param stiffness New stiffness
	 * @return The current particle's system
	 */
	public ParticlesSystem setStiffness(float stiffness) {
		this.stiffness = stiffness;
		return this;
	}
	
	/**
	 * Set the friction
	 * @param friction New friction
	 * @return The current particle's system
	 */
	public ParticlesSystem setFriction(float friction) {
		this.friction = friction;
		return this;
	}
	
	/**
	 * Set the momentum
	 * @param momentum New momentum
	 * @return The current particle's system
	 */
	public ParticlesSystem setMomentum(float momentum) {
		this.momentum = momentum;
		return this;
	}
	
	/**
	 * Set the rectangle's brush height
	 * @param height New rectangle's brush height
	 * @return The current particle's system
	 */
	public ParticlesSystem setBoxHeight(float height) {
		this.boxHeight = height;
		this.boxHHeight = height / 2.f;
		return this;
	}

	/**
	 * Set the rectangle's brush width
	 * @param width New rectangle's brush width
	 * @return The current particle's system
	 */
	public ParticlesSystem setBoxWidth(float width) {
		this.boxWidth = width;
		this.boxHWidth = width / 2.f;
		return this;
	}
	
	/**
	 * Set the maximum of free particles
	 * @param maxFreeParticles New maximum
	 * @return The current particle's system
	 */
	public void setMaxFreeParticles(int maxFreeParticles)
	{
		if(maxFreeParticles >= 0)
		{
			// On réduit le nombre max de particules libres
			if(this.maxFreeParticles > maxFreeParticles)
			{
				// On conserve les maxFreeParticles premières particules
				if(particlesFree.size() > maxFreeParticles)
					particlesFree = particlesFree.subList(0, maxFreeParticles);
				
				// On met à jour les dimensions de la matrice
				particlesSimulation.getFreeMatrix().setDim(new int[]{memory, particlesFree.size()});
			}

			this.maxFreeParticles = maxFreeParticles;
		}
	}
	
	/**
	 * Set free particles' memory
	 * @param memory New memory
	 */
	public void setMemory(int memory)
	{
		if(memory > 0)
		{
			this.memory = memory;
			particlesSimulation.getFreeMatrix().setDim(new int[]{memory, particlesFree.size()});
		}
	}

	/**
	 * Set dimensions of the grid for tied up particles
	 * @param nbParticlesW Width of the grid
	 * @param nbParticlesH Height of the grid
	 */
	public void setNbParticles(int nbParticlesW, int nbParticlesH) {
		this.nbParticlesW	= nbParticlesW;
		this.nbParticlesH	= nbParticlesH;
		
		// Calcul des marges pour centrer les particules dans l'Engine
		float xMargin = (1.f / (float)(nbParticlesW + 1)) * (ENGINE_X[1] - ENGINE_X[0]);
		float yMargin = (1.f / (float)(nbParticlesH + 1)) * (ENGINE_Y[1] - ENGINE_Y[0]);
		
		// Calcul des coeffs pour l'interpolation Engine
		xFrom = ParticlesSimulation.computeCoefs(0, nbParticlesW - 1, ENGINE_X[0] + xMargin, ENGINE_X[1] - xMargin);
		yFrom = ParticlesSimulation.computeCoefs(0, nbParticlesH - 1, ENGINE_Y[0] + yMargin, ENGINE_Y[1] - yMargin);
		
		reloadGridParticles();
	}

	/**
	 * Select which brush to use
	 * @param brush Index of the brush (use static members)
	 */
	public void setBrush(int brush) {
		this.brush = brush;
	}

	/**
	 * Determine if fluid will interfere with particles
	 * @param applyFluidForce 
	 */
	public void setApplyFluidForce(boolean applyFluidForce) {
		this.applyFluidForce = applyFluidForce;
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
