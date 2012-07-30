package Simulation;

import BlobsSystem.Blob;
import FluidSystem.FluidSolver;
import Utils.LinearScale2;
import Utils.Vector;
import com.cycling74.max.Atom;
import java.awt.Color;
import msafluid.MSAFluidSolver2D;

/**
 * Fluid is a max instantiable object that is responsible for the fluid 
 * simulation.
 * This class initialise needed objects and route messages to determine what 
 * action to do for each known message.
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class Fluid extends Simulation
{
	// Bornes coordonnées GL
	protected static Vector FLUID_MIN = new Vector(0., 0.);
	protected static Vector FLUID_MAX = new Vector(1., 1.);
	
    /** 
	 * Fluid's dimensions message : ~ width height
	 * @see MSAFluidSolver2D
	 */
	protected static String MSG_FLUID_DIM = "fluid_dim";
	
    /** 
	 * Fluid's delta t message : ~ deltaT
	 * @see MSAFluidSolver2D
	 */
    protected static String MSG_DELTAT = "deltaT";
	
    /** 
	 * Fluid's viscosity message : ~ viscosity
	 * @see MSAFluidSolver2D
	 */
    protected static String MSG_VISCOSITY = "viscosity";
	
    /** 
	 * Fluid's fade speed message : ~ fadeSpeed
	 * @see MSAFluidSolver2D
	 */
	protected static String MSG_FADESPEED = "fade_speed";
	
    /** 
	 * Fluid's iterations message : ~ iterations
	 * @see MSAFluidSolver2D
	 */
	protected static String MSG_ITERATIONS = "iterations";
	
    /** 
	 * Fluid's velocity message : ~ velocity 
	 * @see FluidSystem.FluidSolver
	 */
	protected static String MSG_VELOCITY = "velocity";
	
    /** 
	 * Fluid's color message : ~ red green blue 
	 * @see FluidSystem.FluidSolver
	 */
	protected static String MSG_COLOR = "color";
	
    /** 
	 * Fluid's noColor message : ~ noColor 
	 * @see FluidSystem.FluidSolver
	 */
	protected static String MSG_NOCOLOR = "no_color";
	
    /** 
	 * Fluid's random color message : ~ randomColor 
	 * @see FluidSystem.FluidSolver
	 */
	protected static String MSG_RANDOMCOLOR	= "random_color";


	// Outlets for output and init matrix
	private static final int	OUTLET_MOUT		= 0;
	private static final int	OUTLET_MAGNET	= 1;
	private static final int	OUTLET_BLOB		= 2;
	
	/** Fluid Solver use to manage the fluid simulation */
    private FluidSolver fluidSolver;
	
	/** 
	 * Current fluid forces for each cells on X axis.
	 * This member is a max attribute that allow an acces to other max object. 
	 */
	private float[] fluid_u;
	
	/** 
	 * Current fluid forces for each cells on Y axis.
	 * This member is a max attribute that allow an acces to other max object. 
	 */
	private float[] fluid_v;
	
	/** 
	 * Current fluid width.
	 * This member is a max attribute that allow an acces to other max object. 
	 */
	private int fluid_w;
	
	/** 
	 * Current fluid height.
	 * This member is a max attribute that allow an acces to other max object. 
	 */
	private int fluid_h;
	
	/** LinearScale used to scale input position (from input to fluid) */
	protected LinearScale2 fluidScaleFrom;
	
	/**
	 * Construct a max object for fluid simulation
	 * @param args Max Object's  arguments - not use but required
	 */
    public Fluid(Atom[] args)
    {
		super(args);
		
        // 1 input - 3 outputs
        declareIO(1, 3);
        setInletAssist(0, "Bang and setting's messages");
        setOutletAssist(OUTLET_MOUT, "Matrix of fluid image");
		setOutletAssist(OUTLET_MAGNET, "Output of current magnets' settings");
		setOutletAssist(OUTLET_BLOB, "Output of current blobs' settings");
		
		// Déclaration des attributs
		declareAttribute("fluid_u");
		declareAttribute("fluid_v");
		declareAttribute("fluid_w");
		declareAttribute("fluid_h");
		
        // On récupère le resolveur de fluide
        fluidSolver = new FluidSolver();

		// On initialise les paramètres
		fluidScaleFrom	= new LinearScale2(outdoorMin, outdoorMax, FLUID_MIN, FLUID_MAX);
		fluid_u			= new float[]{};
		fluid_v			= new float[]{};
		fluid_w			= fluidSolver.getWidth();
		fluid_h			= fluidSolver.getHeight();
    }

	/**
	 * Routine when a bang message occurs on first inlet.
	 * For each update, this method update the fluidSolver, fluid_u, fluid_v and
	 * output the image matrix.
	 * @see FluidSystem.FluidSolver
	 */
    @Override
    protected void bang()
    {
        if(getInlet() == 0)
        {
            fluidSolver.update();
			fluid_u = fluidSolver.getUArray();
			fluid_v = fluidSolver.getVArray();
			fluid_w = fluidSolver.getWidth();
			fluid_h = fluidSolver.getHeight();
			
			String outPutMatrixName = fluidSolver.getImgFluidName();
			if(outPutMatrixName != null)
				outlet(0, MSG_MATRIX, outPutMatrixName);
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
			// Messages de paramétrage
			if(args.length == 0)
			{
				if(message.contentEquals(MSG_RESET))
				{
					fluidSolver.reset();
					outlet(0, MSG_MATRIX, fluidSolver.getImgFluidName());
				}

				else
					unknownMessage = true;
			}

			// Messages de paramétrage
			else if(args.length == 1)
			{
				if(message.contentEquals(MSG_DELTAT))
					fluidSolver.setDeltaT(args[0].toFloat());

				else if(message.contentEquals(MSG_VISCOSITY))
					fluidSolver.setViscosity(args[0].toFloat());

				else if(message.contentEquals(MSG_FADESPEED))
					fluidSolver.setFadeSpeed(args[0].toFloat());

				else if(message.contentEquals(MSG_VELOCITY))
					fluidSolver.setVelocity(args[0].toFloat());

				else if(message.contentEquals(MSG_NOCOLOR))
					fluidSolver.setColored(!args[0].toBoolean());

				else if(message.contentEquals(MSG_RANDOMCOLOR))
					fluidSolver.setRandomizeColor(args[0].toBoolean());

				else if(message.contentEquals(MSG_ITERATIONS))
					fluidSolver.setSolverIterations(args[0].toInt());

				else
					unknownMessage = true;
			}

			else if(args.length == 2)
			{
				if(message.contentEquals(MSG_FLUID_DIM)) 
					fluidSolver.setCells(args[0].toInt(), args[1].toInt());

				else
					unknownMessage = true;
			}

			else if(args.length == 3)
			{
				if(message.contentEquals(MSG_COLOR))
					fluidSolver.setColor(new Color(args[0].toInt(), args[1].toInt(), args[2].toInt()));

				else
					unknownMessage = true;
			}
			
			else
				unknownMessage = true;
		}

		if(message.contentEquals(MSG_XSCALE)) {
			fluidScaleFrom = new LinearScale2(outdoorMin, outdoorMax, FLUID_MIN, FLUID_MAX);
		}

		if(message.contentEquals(MSG_YSCALE)) {
			fluidScaleFrom = new LinearScale2(outdoorMin, outdoorMax, FLUID_MIN, FLUID_MAX);
		}
		
		if(unknownMessage)
			unknownMessage(message, args);
    }
    
	/**
	 * Define whatever to do when a blob occurs.
	 * In this case, blob forces are transmitted to the fluidSolver.
	 * @param index Blob's index
	 * @param position Position on which blob occurs
	 */
	@Override
	protected void applyBlob(int index, Vector position)
	{
		Blob blob = blobsSystem.setPosition(index, fluidScaleFrom.Scale(position));
		
		if(blob != null)
			fluidSolver.addForce(blob.getPosition(), blob.getDelta());
	}
	
	/**
	 * Routine when max object is deleted.
	 * Destroy the fluidSolver.
	 */
    @Override
    public void notifyDeleted() {
        fluidSolver.destroy();
    }

	@Override
	protected void outBlob(String message) {
		outlet(OUTLET_BLOB, message);
	}
}
