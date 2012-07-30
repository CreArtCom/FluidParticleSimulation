package FluidSystem;

import Utils.Vector;
import com.cycling74.jitter.JitterMatrix;
import java.awt.Color;
import msafluid.MSAFluidSolver2D;

/**
 * FluidSolver provides an interface for the MSAFluidSolver2D lib.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 * @see msafluid.MSAFluidSolver2D
 */
public class FluidSolver
{
	// Default parameters
	private final boolean	ENABLE_RGB		= true;
	private final int		FLUID_WIDTH		= 120;
	private final int		FLUID_HEIGHT	= 80;
	private final float		FADE_SPEED		= 0.003f;
	private final float		DELTA_T			= 0.5f;
	private final float		VISC			= 0.0001f;
	private final float		VELOCITY		= 30.0f;
	private final boolean	RANDOMIZE_COLOR	= true;
	private final boolean	APPLY_COLOR		= true;

	/**
	 * Object responsible for the fluid simulation 
	 * @see MSAFluidSolver2D
	 */
	protected MSAFluidSolver2D msa;
	
	/** 
	 * Fade speed
	 * @see MSAFluidSolver2D
	 */
	protected float fadeSpeed;
	
	/** 
	 * Delta T
	 * @see MSAFluidSolver2D
	 */
	protected float deltaT;
	
	/** 
	 * Viscosity
	 * @see MSAFluidSolver2D
	 */
	protected float viscosity;
	
	/**	Velocity is a coeficient that weight each force value */
	protected float velocity;
	
	/** Determine if color are applied (and computes...) */
	protected boolean applyColor;
	
	/** Current fixed color to draw */
	protected Color color;
	
	/** Determine if color are randomly choosen (overpass fixed color) */
	protected boolean randomizeColor;
	
	/** Current frame. Uses to generate random colors. */
	protected int frameCount;

	/** Matrix of current colors' marks */
	protected JitterMatrix imgFluid;

    public FluidSolver()
	{
		imgFluid		= new JitterMatrix("imgFluid", 4, "char", FLUID_WIDTH, FLUID_HEIGHT);
		velocity		= VELOCITY;
		frameCount		= 0;
		randomizeColor	= RANDOMIZE_COLOR;
		applyColor		= APPLY_COLOR;
		color			= Color.white;
		viscosity		= VISC;
		deltaT			= DELTA_T;
		fadeSpeed		= FADE_SPEED;
		
        // Create and set up the MSAFluidSolver2D
        msa = new MSAFluidSolver2D(FLUID_WIDTH, FLUID_HEIGHT);
        msa.enableRGB(ENABLE_RGB).setFadeSpeed(fadeSpeed).setDeltaT(deltaT).setVisc(viscosity);
    }

	/**
	 * Update the current system.
	 * Next iteration of forces and colors will be computes (if theirs enabled).
	 */
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
	
	/**
	 * Add force to the fluid system
	 * @param position Position where to add force
	 * @param delta Force to add
	 */
    public void addForce(Vector position, Vector delta)
    {
		int index = msa.getIndexForNormalizedPosition((float)position.x, (float)position.y);

		msa.uOld[index] += delta.x * velocity;
		msa.vOld[index] += delta.y * velocity;

		if(applyColor)
		{
			if(randomizeColor)
				addRandomDye(index, (float)(((position.x + position.y) * 180.f + frameCount) % 360.f));
			else
				addFixedDye(index);
		}
    }
	
	/**
	 * Add random dye to the fluid image
	 * @param index Index of the cell on which add dye
	 * @param hue Hue of the dye
	 */
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
	
	/**
	 * Add fixed dye to the fluid image.
	 * @param index Index of the cell on which add dye
	 * @see Color
	 */
	public void addFixedDye(int index)
	{
		msa.rOld[index] += (color.getRed() / 255.f) * 5.;
		msa.gOld[index] += (color.getGreen() / 255.f) * 5.;
		msa.bOld[index] += (color.getBlue() / 255.f) * 5.;
	}
	
	/**
	 * Reset the current system.
	 * All forces and colors will be reset.
	 */
	public void reset()
	{
		msa.reset();
		imgFluid.setall(0);
	}

	/** Destroy the system and release memory */
	public void destroy()
	{
		msa.destroy();
		imgFluid.freePeer();
	}

	/**
	 * Set the velocity value
	 * @param velocity New velocity value
	 */
	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}
	
	/**
	 * Set the fade speed value
	 * @param fadeSpeed New fade speed value
	 * @see MSAFluidSolver2D
	 */
	public void setFadeSpeed(float fadeSpeed)
	{
		this.fadeSpeed = fadeSpeed;
		msa.setFadeSpeed(fadeSpeed);
	}

	/**
	 * Set the viscosity value
	 * @param viscosity New viscosity value
	 * @see MSAFluidSolver2D
	 */
	public void setViscosity(float viscosity) {
		this.viscosity = viscosity;
		msa.setVisc(viscosity);
	}

	/**
	 * Set the delta T value
	 * @param deltaT New delta T value
	 * @see MSAFluidSolver2D
	 */
	public void setDeltaT(float deltaT) {
		this.deltaT = deltaT;
		msa.setDeltaT(deltaT);
	}

	/**
	 * Resize the solver
	 * @param cellsX number of cells in width
	 * @param cellsY number of cells in height
	 */
	public void setCells(int cellsX, int cellsY)
	{
		msa.setup(cellsX, cellsY);
        msa.enableRGB(ENABLE_RGB).setFadeSpeed(fadeSpeed).setDeltaT(deltaT).setVisc(viscosity);
		imgFluid.setDim(new int[]{msa.getWidth(), msa.getHeight()});
	}

	/**
	 * Set the solver iterations value
	 * @param solverIterations New solver iterations value
	 * @see MSAFluidSolver2D
	 */
	public void setSolverIterations(int solverIterations) {
		msa.setSolverIterations(solverIterations);
	}
	
	/**
	 * Set the fixed color value
	 * @param color New fixed color value
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Enable or disable the use of randomize colors
	 * @param randomizeColor <code>true</code> to enable random colors, <code>false</code> otherwise
	 */
	public void setRandomizeColor(boolean randomizeColor) {
		this.randomizeColor = randomizeColor;
	}

	/**
	 * Enable or disable the use of colors
	 * @param colored <code>true</code> to enable colors in system, <code>false</code> otherwise
	 */
	public void setColored(boolean colored) {
		this.applyColor = colored;
	}

	/**
	 * Get the name of the fluid image's matrix (which contains colors)
	 * @return	The name of the matrix
     * @since	1.0
     */
	public String getImgFluidName() {
		return imgFluid.getName();
	}

	/**
	 * Get array of U values
	 * @return	Array which contains current applied forces on X axis
     * @since	1.0
     */
	public float[] getUArray() {
		return msa.u;
	}
	
	/**
	 * Get array of V values
	 * @return	Array which contains current applied forces on Y axis
     * @since	1.0
     */
	public float[] getVArray() {
		return msa.v;
	}

	/**
	 * Get the total number of cells
	 * @return	Total number of cells in current system
     * @since	1.0
     */
	public int getCells() {
		return msa.getNumCells();
	}

	/**
	 * Get the number of cells in width
	 * @return	Current number of cells in width
     * @since	1.0
     */
	public int getWidth() {
		return msa.getWidth();
	}
	
	/**
	 * Get the number of cells in height
	 * @return	Current number of cells in height
     * @since	1.0
     */
	public int getHeight() {
		return msa.getHeight();
	}
}
