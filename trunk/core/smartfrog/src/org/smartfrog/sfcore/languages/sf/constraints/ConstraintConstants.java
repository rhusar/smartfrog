/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

 */
package org.smartfrog.sfcore.languages.sf.constraints;

import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

/**
 * Collection of constant literals used in 
 * @author andrew
 *
 */
public class ConstraintConstants {
	
	public static final String AGG_SPEC="sfIsAggregateSpecifier";
	public static final String IGNORE_TAG="sfIgnoreLink";
	
	public static final String UNIFY="unify";
	
	public static final String PREFIX="prefix";
	public static final String PREFIX_TAG="sfPrefix";
	
	public static final String PATH="path";
	public static final String PATH_TAG="sfPath";

	public static final String CARD="card";
	
	public static final String PRED="pred";
	public static final String PRED_TAG="sfPred";
	
	public static final String ARRAY="array";
	public static final String ARRAY_TAG="sfArray";
	
	public static final String UPDATE="update";
	public static final String UPDATES="aggregates";
	public static final String UPDATE_TAG="sfUpdate";
	
	public static final String KEY="key";
	public static final String KEY_TAG="sfKey";
	
	public final static String DEPLOY = "deployComponent";
	public final static String TERMINATE = "terminateComponent";
	
	public static final String CONTEXT="context";
	public static final String CONTEXT_TAG="sfContext";
	
	public static final String EXTENT="extent";
	public static final String EXTENT_TAG="sfExtent";

    public static final String SIZE = "size";
	
	public static final String GENERATOR="generator";
	public static final String GENERATOR_TAG="sfGenerator";

    public static final String TEMPLATE = "template";
	
	public static final String EFFECTS="effects";
	public static final String EFFECTS_TAG="sfEffects";
	
	public static final String GUARD="guard";
	public static final String GUARD_TAG="sfGuard";
	public static final String GUARDS="guards";
	
	public static final String VALUE="value";
	public static final String VALUE_TAG="sfValue";
	
	public static final String VALUEPATH="valuepath";
	public static final String VALUEPATH_TAG="sfValuePath";
	
	public static final String VALUEKEY="valuekey";
	public static final String VALUEKEY_TAG="sfValueKey";
	
	public static final String FREEVARS_TAG="sfFreeVars";
	public static final String RETURN_TAG="sfReturn";
	public static final String CONSTRAINT_TAG="sfConstraint";
	
	
	public static final String INDEX="sfIndex";
	public static final String TAG="sfTag";
	public static final String PROP_TAG="tag";
	
	public static final String RANGE="range";
	public static final String RANGEREF="rangeref";
	public static final String BRANGE="booleanrange";
	public static final String IRANGE="integerrange";
	
	public static final String AUTOVAR="auto";
	public static final String AUTOVAR_TAG="sfConstraintAutoVar";
	
	public static final String AUTOEFFECTS="postActions";
	
	public static final String USERVAR="user";
	public static final String USERVAR_TAG="sfConstraintUserVar";
	
	public static final String DEFVAR="default";
		
	public static final String FunctionClassStatus="sfFunctionClassStatus";
    public static final String FunctionClassEvalEarly = "sfFunctionClassEvalEarly";
    public static final String FunctionClassReturnEarly = "sfFunctionClassReturnEarly";
	public static final String FCS_DONE="done";

    public static final String PREFIXMUSTBESTRING = " prefix must be a String...";
    public static final String CANNOTRESOLVE = " can not resolve ";
    public static final String VECTOREXTENTSTRING = " vector extent should be comprised of Strings";
    public static final String EXTENTTYPE = "extent in Array should be an Integer or a Vector";
    public static final String BADLYFORMEDEXTENT = "badly formed multi-dimensional extent";
    public static final String PATHNOTRESOLVEINEFFECTS = "path in effects will not resolve: ";
    public static final String FAILEDTOFINDATTRIBEFFECTS = "Failed to find attrib in effects: ";
    public static final String PREDSHOULDYIELDBOOLEANFROMSOURCE = "In extracting values as per source, pred should yield Boolean: ";


    public static SmartFrogFunctionResolutionException relay(Class cl, ComponentDescription comp, String msg){
        return new SmartFrogFunctionResolutionException("In "+cl.getSimpleName()+" with context: "+comp+", "+msg);
        
    }

}
