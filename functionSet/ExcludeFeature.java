/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package functionSet;

import org.jgap.*;
import org.jgap.gp.*;
import org.jgap.gp.impl.*;
import org.jgap.util.*;

/**
 * The subtract operation.
 *
 * @author Klaus Meffert
 * Edited by George Mweshi
 * @since 3.0
 */
public class ExcludeFeature
    extends MathCommand implements IMutateable, ICloneable {
  /**
	 * 
	 */
	private static final long serialVersionUID = 7976618625125968511L;
/** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.9 $";

  public ExcludeFeature(final GPConfiguration a_conf, Class a_returnType)
      throws InvalidConfigurationException {
    super(a_conf, 1, a_returnType);
  }

  public CommandGene applyMutation(int index, double a_percentage)
	      throws InvalidConfigurationException {
	    IncludeFeature mutant = new IncludeFeature(getGPConfiguration(), getReturnType());
	    return mutant;
	  }

  
  
  
  /**
   * Clones the object. Simple and straight forward implementation here.
   *
   * @return cloned instance of this object
   *
   * @author Klaus Meffert
   * @since 3.4
   */
  public Object clone() {
    try {
      ExcludeFeature result = new ExcludeFeature(getGPConfiguration(), getReturnType());
      return result;
    } catch (Exception ex) {
      throw new CloneException(ex);
    }
  }

  public String toString() {
    return "Exclude  &1 ";
  }

  /**
   * @return textual name of this command
   *
   * @author Klaus Meffert
   * @since 3.2
   */
  public String getName() {
    return "ExcludeFeature";
  }

  public int execute_int(ProgramChromosome c, int n, Object[] args) {
    return c.execute_int(n, 0, args)-c.execute_int(n, 0, args);
  }

  public long execute_long(ProgramChromosome c, int n, Object[] args) {
    return c.execute_long(n, 0, args)-c.execute_long(n, 0, args);
  }

  public float execute_float(ProgramChromosome c, int n, Object[] args) {
    return c.execute_float(n, 0, args)-c.execute_float(n, 0, args);
  }

  public double execute_double(ProgramChromosome c, int n, Object[] args) {
    return c.execute_double(n, 0, args)-c.execute_double(n, 0, args);
  }

  public Object execute_object(ProgramChromosome c, int n, Object[] args) {
    return ( (Compatible) c.execute_object(n, 0, args)).execute_ExcludeFeature();
  }

  protected interface Compatible {
    public Object execute_ExcludeFeature();
  }


}
