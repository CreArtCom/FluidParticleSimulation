package Simulation;

import BlobsSystem.Blob;
import MagnetsSystem.MagnetsSystem;
import ParticlesSystem.ParticlesSystem;
import Utils.Line;
import Utils.LinearScale2;
import Utils.Vector;
import com.cycling74.jitter.JitterMatrix;
import com.cycling74.max.Atom;
import com.cycling74.max.MaxObject;

/**
 * Particles is a max instatiable object that is responsible for the particles
 * system simulation.
 * This class initialise needed objects and route messages to determine what 
 * action to do for each known message.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class Particles extends Simulation
{
	// Particles' system settings messages
    /** 
	 * Particle system's stiffness message : ~ stiffness
	 * @see ParticlesSystem.ParticlesSystem
	 */
    public static String MSG_STIFFNESS = "stiffness";
	
    /** 
	 * Particle system's momentum message : ~ momentum
	 * @see ParticlesSystem.ParticlesSystem
	 */
    public static String MSG_MOMENTUM = "momentum";
	
    /** 
	 * Particle system's threshold message : ~ min max
	 * @see ParticlesSystem.ParticlesSystem
	 */
	public static String MSG_PARTSEUIL = "seuil";
	
    /** 
	 * Particle system's friction message : ~ friction
	 * @see ParticlesSystem.ParticlesSystem
	 */
	public static String MSG_FRICTION = "friction";
	
    /** 
	 * Particle system's memory message : ~ memory
	 * @see ParticlesSystem.ParticlesSystem
	 */
	public static String MSG_MEMORY = "memory";
	
    /** 
	 * Particle system's maximum message : ~ max
	 * @see ParticlesSystem.ParticlesSystem
	 */
	public static String MSG_MAXPART = "max";
	
    /** 
	 * Particle system's edges message : ~ left bottom right top
	 * @see ParticlesSystem.ParticlesSystem
	 */
	public static String MSG_EDGES = "edges";
	
	
	// Magnets settings messages
    /** Magnet apply message : ~ apply */
	public static String MSG_MAGNET_APPLY = "magnet_apply";
		
	/** Magnet add vertical ligne message : ~ */
	public static String MSG_MAGNET_RESET = "magnet_reset";
	
	/** Magnet list indexes message : ~ */
	public static String MSG_MAGNET_LIST = "magnet_list";
	
	/** Magnet get info message : ~ index */
	public static String MSG_MAGNET_INFO = "magnet_info";
	
	/** 
	 * Magnet set a point's message : ~ index x y force
	 * @see ParticlesSystem.PointMagnet
	 */
	public static String MSG_MAGNET_POINT = "magnet_point";
	
	/** 
	 * Magnet set a line message : ~ index x1 y1 x2 y2 force
	 * @see ParticlesSystem.LineMagnet
	 */
	public static String MSG_MAGNET_LINE = "magnet_line";
	
	/** 
	 * Magnet add vertical ligne message : ~ index c force
	 * @see ParticlesSystem.LineMagnet
	 */
	public static String MSG_MAGNET_VLINE = "magnet_vline";
	
	/** 
	 * Magnet add horizontal ligne message : ~ index c force
	 * @see ParticlesSystem.LineMagnet
	 */
	public static String MSG_MAGNET_HLINE = "magnet_hline";
	
	/** Magnet delete message : ~ index */
	public static String MSG_MAGNET_DEL = "magnet_delete";
	
	/** Magnet set force message : ~ index force */
	public static String MSG_MAGNET_FORCE = "magnet_force";
	
	
	// Fluid settings messages
	/** Fluid force message : ~ force */
	public static String MSG_FLUIDFORCE	= "fluid_force";
	
	/** Fluid apply message : ~ apply */
	public static String MSG_FLUIDAPPLY	= "fluid_apply";
	
	// Default parameters
    private static final float		FLUID_FORCE			= 0.6f;
    private static final boolean	FLUID_FORCE_APPLY	= false;
	
	// Outlets for output and init matrix
	private static final int	OUTLET_MOUT		= 0;
	private static final int	OUTLET_MINIT	= 1;
	private static final int	OUTLET_MAGNET	= 2;
	private static final int	OUTLET_BLOB		= 3;
	
	/** Coefficient used to weight fluid forces on particles */
	protected double fluidForce;
	
	/** Magnets System use to manage the magnets system simulation */
    protected MagnetsSystem magnetsSystem;
	
	/** Particles System use to manage the particles system simulation */
    protected ParticlesSystem particlesSystem;
	
	/** Simulation.Fluid max's object to listen for retrieve forces */
	protected MaxObject fluidSimulation;
	
	/** 
	 * Total number of cells in the fluid system.
	 * @see FluidSystem.FluidSolver
	 */
	protected int fluidCells;
	
	/** 
	 * Number of cells in width in the fluid system.
	 * @see FluidSystem.FluidSolver
	 */
	protected int fluidWidth;
	
	/** 
	 * Number of cells in height in the fluid system.
	 * @see FluidSystem.FluidSolver
	 */
	protected int fluidHeight;
	
	/** 
	 * Current U array of the fluid system : forces applied on X axis
	 * @see FluidSystem.FluidSolver
	 */
	protected float[] fluidU;
	
	/** 
	 * Current V array of the fluid system : forces applied on Y axis
	 * @see FluidSystem.FluidSolver
	 */
	protected float[] fluidV;
	
	/** Width of a cell in engine units */
	protected double stepWidth;
	
	/** Height of a cell in engine units*/
	protected double stepHeight;
	
	/** Determine if fluid forces are applied to particles */
	protected boolean applyFluidForce;
	
	/** Output matrix : matrix of current particles positions */
    protected JitterMatrix outMatrix;
	
	/** Init matrix : matrix of initial particles positions */
	protected JitterMatrix initMatrix;
	
	/** LinearScale used to scale output position (from engine to output) */
	protected LinearScale2 fluidScale;
	
	/**
	 * Construct a max object for particles system simulation
	 * @param args Max Object's  arguments - not use but required
	 */
    public Particles(Atom[] args)
    {
		super(args);
		
        // 2 inputs - 4 outputs
        declareIO(2, 4);
        setInletAssist(0, "Bang and setting's messages");
		setInletAssist(1, "Matrix of initials particles positions");
        setOutletAssist(OUTLET_MOUT, "Matrix of current particles positions");
		setOutletAssist(OUTLET_MINIT, "Matrix of initial particles positions");
		setOutletAssist(OUTLET_MAGNET, "Output of current magnets' settings");
		setOutletAssist(OUTLET_BLOB, "Output of current blobs' settings");

		outMatrix			= new JitterMatrix(2, "float32", 0, 0);
		initMatrix			= new JitterMatrix(2, "float32", 0, 0);
		fluidForce			= FLUID_FORCE;
		applyFluidForce		= FLUID_FORCE_APPLY;
		fluidScale			= new LinearScale2(ENGINE_MIN, ENGINE_MAX, Fluid.FLUID_MIN, Fluid.FLUID_MAX);
		magnetsSystem		= new MagnetsSystem(this);
		particlesSystem		= new ParticlesSystem(this, magnetsSystem, blobsSystem);
    }
 
	/**
	 * Routine when a bang message occurs on first inlet.
	 * For each update, this method retrieve the fluidSolver attributes (if 
	 * applyFluidForce is enabled), update the particlesSystem and output the 
	 * particles positions matrix.
	 * @see ParticlesSystem.ParticlesSystem
	 */
    @Override
    protected void bang()
    {
        if(getInlet() == 0)
        {
			if(applyFluidForce)
			{
				fluidU = fluidSimulation.getAttrFloatArray("fluid_u");
				fluidV = fluidSimulation.getAttrFloatArray("fluid_v");
				fluidCells = fluidU.length;
				
				if(fluidSimulation.getAttrInt("fluid_w") != fluidWidth) {
					fluidWidth	= fluidSimulation.getAttrInt("fluid_w");
					stepWidth	= 1. / (double) fluidWidth;
				}
				
				if(fluidSimulation.getAttrInt("fluid_h") != fluidHeight) {
					fluidHeight	= fluidSimulation.getAttrInt("fluid_h");
					stepHeight	= 1. / (double) fluidHeight;
				}
			}
			
			particlesSystem.update();
			
			if(particlesSystem.hasParticles())
				outlet(OUTLET_MOUT, MSG_MATRIX, outMatrix.getName());
        }
    }
	
	/**
	 * Routine when something other than a bang message occurs.
	 * Execute appropriates routines for known messages (on appropriate inlet).
	 * Call first Simulation.TreatMessage.
	 * Call Max.unknownMessage if the message is unknown.
	 * @param message Max's header message
	 * @param args Max's parameters message
	 */
    @Override
    protected void anything(String message, Atom[] args)
    {
		boolean unknownMessage = false;
		
		if(TreatMessage(message, args))
		{
			if(getInlet() == 0)
			{
				if(args.length == 0)
				{
					if(message.contentEquals(MSG_RESET))
					{
						particlesSystem.reset();

						if(particlesSystem.hasParticles())
							outlet(OUTLET_MINIT, MSG_MATRIX, initMatrix.getName());
					}

					else if(message.contentEquals(MSG_MAGNET_RESET))
						magnetsSystem.resetMagnets();
					
					else if(message.contentEquals(MSG_MAGNET_LIST))
						outlet(OUTLET_MAGNET, magnetsSystem.listIndexes());

					else
						unknownMessage = true;
				}

				else if(args.length == 1)
				{		
					if(message.contentEquals(MSG_STIFFNESS))
						particlesSystem.setStiffness(args[0].toDouble());

					else if(message.contentEquals(MSG_MOMENTUM))
						particlesSystem.setMomentum(args[0].toDouble());

					else if(message.contentEquals(MSG_FRICTION))
						particlesSystem.setFriction(args[0].toDouble());

					else if(message.contentEquals(MSG_FLUIDFORCE))
						fluidForce = args[0].toDouble();

					else if(message.contentEquals(MSG_MAGNET_APPLY))
						magnetsSystem.setEnable(args[0].toBoolean());

					else if(message.contentEquals(MSG_FLUIDAPPLY))
					{
						fluidSimulation = MaxObject.getContext().getMaxObject("Simulation.Fluid");
						applyFluidForce = args[0].toBoolean() && fluidSimulation != null;
						
						if(applyFluidForce)
							printOut("Simulation.Fluid successfully loaded !");
						else if(args[0].toBoolean())
							printOut("Unable to find a Max Object named \"Simulation.Fluid\" in your patch...");
					}

					else if(message.contentEquals(MSG_MEMORY))
						particlesSystem.setMemory(args[0].toInt());

					else if(message.contentEquals(MSG_MAXPART))
						particlesSystem.setMaxParticles(args[0].toInt());
					
					else if(message.contentEquals(MSG_MAGNET_DEL))
						magnetsSystem.deleteMagnet(args[0].toInt());
					
					else if(message.contentEquals(MSG_MAGNET_INFO))
						outlet(OUTLET_MAGNET, magnetsSystem.getInfo(args[0].toInt()));

					else
						unknownMessage = true;

				}

				else if(args.length == 2)
				{
					if(message.contentEquals(MSG_PARTSEUIL))
						particlesSystem.setThreshold(args[0].toDouble(), args[1].toDouble());
					
					else if(message.contentEquals(MSG_MAGNET_FORCE))
						magnetsSystem.setMagnetForce(args[0].toInt(), args[1].toFloat());

					else
						unknownMessage = true;
				}
				
				else if(args.length == 3)
				{
					if(message.contentEquals(MSG_MAGNET_VLINE)) {
						magnetsSystem.setLineMagnet(args[0].toInt(), new Line(-1., 0., args[1].toDouble()), args[2].toDouble());
					}
					
					else if(message.contentEquals(MSG_MAGNET_HLINE)) {
						magnetsSystem.setLineMagnet(args[0].toInt(), new Line(0., -1., args[1].toDouble()), args[2].toDouble());
					}
					
					else
						unknownMessage = true;
				}
				
				else if(args.length == 4)
				{
					if(message.contentEquals(MSG_EDGES))
						particlesSystem.setEdges(args[0].toInt(), args[1].toInt(), args[2].toInt(), args[3].toInt());
					
					else if(message.contentEquals(MSG_MAGNET_POINT))
						magnetsSystem.setPointMagnet(args[0].toInt(), new Vector(args[1].toDouble(), args[2].toDouble()), args[3].toDouble());
					
					else
						unknownMessage = true;
				}
				
				else if(args.length == 6)
				{
					if(message.contentEquals(MSG_MAGNET_LINE))
						magnetsSystem.setLineMagnet(args[0].toInt(), new Line(0., new Vector(args[1].toDouble(), args[2].toDouble()), new Vector(args[3].toDouble(), args[4].toDouble())), args[5].toDouble());
					
					else
						unknownMessage = true;
				}

				else
					unknownMessage = true;
			}
			
			// Load init particles
			else if(getInlet() == 1)
			{
				if(args.length == 1 && message.contentEquals(MSG_MATRIX))
				{
					JitterMatrix jm = new JitterMatrix(args[0].toString());
					particlesSystem.loadParticles(jm);
				}
				
				else
					unknownMessage = true;
			}
		}
		
		if(unknownMessage)
			unknownMessage(message, args);
    }
    
	/**
	 * Define whatever to do when a blob occurs.
	 * In this case, blob forces and erase events are transmitted to the 
	 * particlesSystem.
	 * @param index Blob's index
	 * @param position Position on which blob occurs
	 */
	@Override
	protected void applyBlob(int index, Vector position)
	{
		Blob blob = blobsSystem.setPosition(index, scaleFrom.Scale(position));
		
		// On ajoute des particles
		if(blob != null && blob.getToAdd() > 0)
			particlesSystem.addParticles(blob.getPosition(), blob.getToAdd());
	}
	
	/**
	 * Get the current fluid force on the given position
	 * @param position Position of the point which feels the fluid
	 * @return The current fluid force applied to the givent point
	 */
	public Vector applyFluid(Vector position)
	{
		Vector fluidPos = fluidScale.Scale(position);
		int w = (int) Math.floor(fluidPos.x / stepWidth);
		int h = (int) Math.floor(fluidPos.y / stepHeight);
		int index = ( h * fluidWidth) + w;

		if(index < fluidCells)
			return new Vector(fluidU[index], fluidV[index]).Scalar(fluidForce);

		return new Vector();
	}

	/**
	 * Routine when max object is deleted.
	 * Destroy the particlesSystem.
	 */
    @Override
    public void notifyDeleted()
    {
		particlesSystem.destroy();
		outMatrix.freePeer();
		initMatrix.freePeer();
    }
	
	/**
	 * Determine if the fluid is applied on particles
	 * @return <code>true</code> if fluid force is applied on particles, 
	 * <code>false</code> otherwise
	 */
	public boolean applyFluidForce() {
		return applyFluidForce;
	}
	
	/**
	 * Get the output matrix
	 * @return Current output matrix
     * @since 1.0
     */
	public JitterMatrix getOutMatrix() {
		return outMatrix;
	}
	
	/**
	 * Set a cell in output matrix
	 * @param i Index <code>i</code> on the matrix
	 * @param j Index <code>j</code> on the matrix
	 * @param value Value to set
	 */
	public void setOutMatrix(int i, int j, Vector value) {
		outMatrix.setcell2d(i, j, scaleTo.Scale(value.x, value.y));
	}
    
	/**
	 * Get the init matrix
	 * @return Current init matrix
     * @since 1.0
     */
	public JitterMatrix getInitMatrix() {
		return initMatrix;
	}

	/**
	 * Set a cell in init matrix
	 * @param i Index <code>i</code> on the matrix
	 * @param j Index <code>j</code> on the matrix
	 * @param value Value to set
	 */
	public void setInitMatrix(int i, int j, Vector value) {
		initMatrix.setcell2d(i, j, scaleTo.Scale(value.x, value.y));
	}
	
	/**
	 * Set the output matrix dimensions.
	 * If dimension are not strictly positive, nowhere matrix will be outputed.
	 * @param width New width of the output matrix
	 * @param height New height of the output matrix
	 */
	public void setOutMatrixDim(int width, int height)
	{
		if(width > 0 && height > 0)
		{
			outMatrix.setDim(new int[]{width, height});
			initMatrix.setDim(new int[]{width, height});
		}
		else
		{
			outMatrix.clear();
			initMatrix.clear();
			outMatrix.setDim(new int[]{0, 0});
			initMatrix.setDim(new int[]{0, 0});
			outlet(OUTLET_MOUT, MSG_MATRIX, nowhereMatrix.getName());
			outlet(OUTLET_MINIT, MSG_MATRIX, nowhereMatrix.getName());
		}
	}

	@Override
	protected void outBlob(String message) {
		outlet(OUTLET_BLOB, message);
	}
}
