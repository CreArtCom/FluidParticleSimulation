package FluidSimulation;

import com.cycling74.jitter.JitterMatrix;
import java.awt.Color;
import msafluid.MSAFluidSolver2D;

/**
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class FluidSolver
{
	// Paramètres par défaut
	protected final boolean	ENABLE_RGB		= true;
	protected final int		FLUID_WIDTH		= 120;
	protected final int		FLUID_HEIGHT	= 80;
	protected final float	FADE_SPEED		= 0.003f;
	protected final float	DELTA_T			= 0.5f;
	protected final float	VISC			= 0.0001f;
	protected final float	VELOCITY		= 30.0f;
	protected final boolean RANDOMIZE_COLOR	= true;
	protected final boolean	APPLY_COLOR		= true;

	
	protected MSAFluidSolver2D msa;

	// Attributs du FluidSolver
	protected float		velocity;
	protected boolean	applyColor;
	protected Color		color;
	protected boolean	randomizeColor;
	protected int		frameCount;
	protected float		fadeSpeed;
	protected float		deltaT;
	protected float		viscosity;

	// Jitter Objects
	protected JitterMatrix imgFluid;

    public FluidSolver()
	{
		// Initialisation de la matrice
		imgFluid = new JitterMatrix("imgFluid", 4, "char", FLUID_WIDTH, FLUID_HEIGHT);
		
		// Initialisation des paramètres
		velocity		= VELOCITY;
		frameCount		= 0;
		randomizeColor	= RANDOMIZE_COLOR;
		applyColor		= APPLY_COLOR;
		color			= Color.white;
		viscosity		= VISC;
		deltaT			= DELTA_T;
		fadeSpeed		= FADE_SPEED;
		
        // Création et paramétrage du resolveur de fluide
        msa = new MSAFluidSolver2D(FLUID_WIDTH, FLUID_HEIGHT);
        msa.enableRGB(ENABLE_RGB).setFadeSpeed(fadeSpeed).setDeltaT(deltaT).setVisc(viscosity);
    }

	public void update()
	{
		frameCount = (frameCount + 1) % 360;
		msa.update();
		
		if(applyColor)
		{
			for(int i = 0; i < msa.getNumCells(); i++) 
			{
				float d = 3.f;

				int x = i % msa.getWidth();
				int y = (int) Math.floor(i / msa.getWidth());

				imgFluid.copyArrayToVector(4, new int[]{x, y}, new float[]{255, msa.r[i] * d, msa.g[i] * d, msa.b[i] * d}, 4, 0);
			}
		}
	}
	
    // Gère la force appliquée au fluide
    public void addForce(float x, float y, float dx, float dy)
    {
		x = (x < 0) ? 0 : x;
		x = (x > 1) ? 1 : x;
		y = (y < 0) ? 0 : y;
		y = (y > 1) ? 1 : y;

		int index = msa.getIndexForNormalizedPosition(x, y);

		msa.uOld[index] += dx * velocity;
		msa.vOld[index] += dy * velocity;

		if(applyColor)
		{
			if(randomizeColor)
				addRandomDye(index, (((x + y) * 180.f + frameCount ) % 360.f));
			else
				addFixedDye(index);
		}
    }
	
	// Gère une teinte aléatoire du fluide
	public void addRandomDye(int index, float hue)
	{
		int rgb = Color.HSBtoRGB(hue, 1, 1);
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;

		msa.rOld[index] = red;
		msa.gOld[index] = green;
		msa.bOld[index] = blue;
	}
	
	// Gère une teinte fixée du fluide
	public void addFixedDye(int index)
	{
		msa.rOld[index] += (color.getRed() / 255.f) * 5.;
		msa.gOld[index] += (color.getGreen() / 255.f) * 5.;
		msa.bOld[index] += (color.getBlue() / 255.f) * 5.;
	}
	
	public void reset()
	{
		msa.reset();
		imgFluid.setall(0);
	}

	public void destroy()
	{
		msa.destroy();
		imgFluid.freePeer();
	}

	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}

	public void setFadeSpeed(float fadeSpeed) {
		this.fadeSpeed = fadeSpeed;
		msa.setFadeSpeed(fadeSpeed);
	}

	public void setViscosity(float viscosity) {
		this.viscosity = viscosity;
		msa.setVisc(viscosity);
	}

	public void setDeltaT(float deltaT) {
		this.deltaT = deltaT;
		msa.setDeltaT(deltaT);
	}

	public void setCells(int cellsX, int cellsY)
	{
		msa.setup(cellsX, cellsY);
        msa.enableRGB(ENABLE_RGB).setFadeSpeed(fadeSpeed).setDeltaT(deltaT).setVisc(viscosity);
		imgFluid.setDim(new int[]{msa.getWidth(), msa.getHeight()});
	}

	public void setSolverIterations(int solverIterations) {
		msa.setSolverIterations(solverIterations);
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	public void setRandomizeColor(boolean randomizeColor) {
		this.randomizeColor = randomizeColor;
	}

	public void setColored(boolean colored) {
		this.applyColor = colored;
	}
	
	/********************************* GETTERS *********************************/
	/**
	 * Getter : imgFluidName
	 * @return	Le nom de l'image matricielle des rubans colorés à un instant 
	 *			donné dans le fluide.
     * @since	1.0
     */
	public String getImgFluidName() {
		return imgFluid.getName();
	}

	/**
	 * Getter : UArray
	 * @return	Tableau des forces internes au fluide appliquées selon U = X
     * @since	1.0
     */
	public float[] getUArray() {
		return msa.u;
	}
	
	/**
	 * Getter : VArray
	 * @return	Tableau des forces internes au fluide appliquées selon V = Y
     * @since	1.0
     */
	public float[] getVArray() {
		return msa.v;
	}

	/**
	 * Getter : Cells
	 * @return	Nombre total de cellules dans le système de fluide.
     * @since	1.0
     */
	int getCells() {
		return msa.getNumCells();
	}

	/**
	 * Getter : Width
	 * @return	Largeur du système de fluide.
     * @since	1.0
     */
	public int getWidth() {
		return msa.getWidth();
	}
	
	/**
	 * Getter : Height
	 * @return	Hauteur du système de fluide.
     * @since	1.0
     */
	public int getHeight() {
		return msa.getHeight();
	}
}
