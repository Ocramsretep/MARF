package marf.Storage;

import java.io.Serializable;
import java.util.Vector;


/**
 * <p>Class <code>ModuleParams</code> provides ability to pass module-specific parameters
 * from an application.
 * The specific module should know in which order and how to downcast those params.</p>
 *
 * @author Serguei Mokhov
 */
public class ModuleParams
implements Cloneable, Serializable
{
	/*
	 * ---------------------
	 * Data members
	 * ---------------------
	 */

	/**
	 * A Vector of preprocessing parameters.
	 */
	protected Vector<Serializable> oPreprocessingParams     = new Vector<Serializable>();

	/**
	 * A Vector of feature extraction parameters.
	 */
	protected Vector<Serializable> oFeatureExtractionParams = new Vector<Serializable>();

	/**
	 * A Vector of classification parameters.
	 */
	protected Vector<Serializable> oClassificationParams    = new Vector<Serializable>();

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 */
	private static final long serialVersionUID = -4980315360455857956L;


	/*
	 * ---------------------
	 * Enumeration
	 * ---------------------
	 */

	/**
	 * Indicates that we manipulate on the Preprocessing Vector.
	 */
	protected static final int PREPROCESSING      = 0;

	/**
	 * Indicates that we manipulate on the Feature Extraction Vector.
	 */
	protected static final int FEATURE_EXTRACTION = 1;

	/**
	 * Indicates that we manipulate on the Classification Vector.
	 */
	protected static final int CLASSIFICATION     = 2;


	/*
	 * ---------------------
	 * Methods
	 * ---------------------
	 */

	/**
	 * Default Constructor.
	 */
	public ModuleParams()
	{
	}

	/**
	 * Copy-constructor.
	 * @param poModuleParams object to make a copy of
	 */
	@SuppressWarnings("unchecked")
	public ModuleParams(final ModuleParams poModuleParams)
	{
		this.oPreprocessingParams = (Vector<Serializable>)poModuleParams.getPreprocessingParams().clone();
		this.oFeatureExtractionParams = (Vector<Serializable>)poModuleParams.getFeatureExtractionParams().clone();
		this.oClassificationParams = (Vector<Serializable>)poModuleParams.getClassificationParams().clone();
	}

	/**
	 * Overwrites any possible previous value for a given module with a new one.
	 * @param poParams Vector of parameters
	 * @param piModuleType the module these parameters are for
	 * @throws IllegalArgumentException if the parameters vector is null or module type is invalid
	 */
	private synchronized final void setParams(Vector<Serializable> poParams, final int piModuleType)
	{
		if(poParams == null)
		{
			throw new IllegalArgumentException("Parameters vector cannot be null.");
		}

		switch(piModuleType)
		{
			case PREPROCESSING:
				this.oPreprocessingParams = poParams;
				break;

			case FEATURE_EXTRACTION:
				this.oFeatureExtractionParams = poParams;
				break;

			case CLASSIFICATION:
				this.oClassificationParams = poParams;
				break;

			default:
				throw new IllegalArgumentException("Unknown module type: " + piModuleType + ".");
		}
	}

	/**
	 * Appends params vector to whatever there possibly was.
	 * @param poParams Vector of parameters
	 * @param piModuleType the module these parameters are for
	 * @throws IllegalArgumentException if module type is invalid
	 */
	private synchronized final void addParams(Vector<Serializable> poParams, final int piModuleType)
	{
		switch(piModuleType)
		{
			case PREPROCESSING:
				this.oPreprocessingParams.addAll(poParams);
				break;

			case FEATURE_EXTRACTION:
				this.oFeatureExtractionParams.addAll(poParams);
				break;

			case CLASSIFICATION:
				this.oClassificationParams.addAll(poParams);
				break;

			default:
				throw new IllegalArgumentException("Unknown module type: " + piModuleType + ".");
		}
	}

	/**
	 * Adds a single object to the corresponding Vector.
	 * @param oParam Parameter object
	 * @param piModuleType the module this parameter is for
	 * @throws IllegalArgumentException if module type is invalid
	 */
	private synchronized final void addParam(Serializable oParam, final int piModuleType)
	{
		switch(piModuleType)
		{
			case PREPROCESSING:
				this.oPreprocessingParams.add(oParam);
				break;

			case FEATURE_EXTRACTION:
				this.oFeatureExtractionParams.add(oParam);
				break;

			case CLASSIFICATION:
				this.oClassificationParams.add(oParam);
				break;

			default:
				throw new IllegalArgumentException("Unknown module type: " + piModuleType + ".");
		}
	}

	/**
	 * Returns for a given module its parameters vector.
	 * @param piModuleType module type to get parameters for
	 * @return Vector of parameters
	 * @throws IllegalArgumentException if module type is invalid
	 */
	private synchronized final Vector<Serializable> getParams(final int piModuleType)
	{
		switch(piModuleType)
		{
			case PREPROCESSING:
				return this.oPreprocessingParams;

			case FEATURE_EXTRACTION:
				return this.oFeatureExtractionParams;

			case CLASSIFICATION:
				return this.oClassificationParams;

			default:
				throw new IllegalArgumentException("Unknown module type: " + piModuleType + ".");
		}
	}

	/**
	 * Retrieves Preprocessing module's parameters.
	 * @return preprocessing parameters vector
	 */
	public synchronized final Vector<Serializable> getPreprocessingParams()
	{
		return getParams(PREPROCESSING);
	}

	/**
	 * Sets preprocessing parameters vector.
	 * @param poParams parameters vector
	 */
	public synchronized final void setPreprocessingParams(Vector<Serializable> poParams)
	{
		setParams(poParams, PREPROCESSING);
	}

	/**
	 * Adds (appends) preprocessing parameters vector.
	 * @param poParams parameters vector to append
	 */
	public synchronized final void addPreprocessingParams(Vector<Serializable> poParams)
	{
		addParams(poParams, PREPROCESSING);
	}

	/**
	 * Adds (appends) a single preprocessing parameter object.
	 * @param poParam object to append
	 */
	public synchronized final void addPreprocessingParam(Serializable poParam)
	{
		addParam(poParam, PREPROCESSING);
	}

	/**
	 * Retrieves Feature Extraction module's parameters.
	 * @return feature extraction parameters vector
	 */
	public synchronized final Vector<Serializable> getFeatureExtractionParams()
	{
		return getParams(FEATURE_EXTRACTION);
	}

	/**
	 * Sets feature extraction parameters vector.
	 * @param poParams parameters vector
	 */
	public synchronized final void setFeatureExtractionParams(Vector<Serializable> poParams)
	{
		setParams(poParams, FEATURE_EXTRACTION);
	}

	/**
	 * Adds (appends) feature extraction parameters vector.
	 * @param poParams parameters vector to append
	 */
	public synchronized final void addFeatureExtractionParams(Vector<Serializable> poParams)
	{
		addParams(poParams, FEATURE_EXTRACTION);
	}

	/**
	 * Adds (appends) a single feature extraction parameter object.
	 * @param poParam object to append
	 */
	public synchronized final void addFeatureExtractionParam(Serializable poParam)
	{
		addParam(poParam, FEATURE_EXTRACTION);
	}

	/**
	 * Retrieves Classification module's parameters.
	 * @return classification parameters vector
	 */
	public synchronized final Vector<Serializable> getClassificationParams()
	{
		return getParams(CLASSIFICATION);
	}

	/**
	 * Sets classification parameters vector.
	 * @param poParams parameters vector
	 */
	public synchronized final void setClassificationParams(Vector<Serializable> poParams)
	{
		setParams(poParams, CLASSIFICATION);
	}

	/**
	 * Adds (appends) classification parameters vector.
	 * @param poParams parameters vector to append
	 */
	public synchronized final void addClassificationParams(Vector<Serializable> poParams)
	{
		addParams(poParams, CLASSIFICATION);
	}

	/**
	 * Adds (appends) a single classification parameter object.
	 * @param poParam object to append
	 */
	public synchronized final void addClassificationParam(Serializable poParam)
	{
		addParam(poParam, CLASSIFICATION);
	}

	/**
	 * Retrieves string version of all three types of parameters.
	 * @return String representation of this ModuleParams object 
	 * @see java.lang.Object#toString()
	 */
	public synchronized String toString()
	{
		StringBuffer oBuffer = new StringBuffer();

		oBuffer
			.append("Preprocessing params: ").append(this.oPreprocessingParams).append("\n")
			.append("Feature extraction params: ").append(this.oFeatureExtractionParams).append("\n")
			.append("Classification params: ").append(this.oClassificationParams);

		return oBuffer.toString();
	}
	
	/**
	 * Returns the size of this set of module parameters as a sum
	 * of sizes of preprocessing, feature extraction, and classification
	 * module parameters.
	 * 
	 * @return the total number of parameters
	 */
	public synchronized int size()
	{
		return
			this.oPreprocessingParams.size() +
			this.oFeatureExtractionParams.size() +
			this.oClassificationParams.size();
	}

	/**
	 * Implements Cloneable interface for ModuleParams.
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		return new ModuleParams(this);
	}
}

// EOF
