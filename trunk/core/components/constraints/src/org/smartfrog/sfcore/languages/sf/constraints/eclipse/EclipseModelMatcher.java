/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.sf.constraints.eclipse;

import com.parctechnologies.eclipse.CompoundTerm;
import com.parctechnologies.eclipse.EclipseEngine;
import com.parctechnologies.eclipse.EclipseException;
import org.smartfrog.sfcore.languages.sf.constraints.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * created 20-Sep-2010 15:34:10
 */

public class EclipseModelMatcher extends BaseModelMatcher{

    /**
     * Property prefix for theory files...
     */
    private static final String MATCHERS_DIR_PREFIX = "matchers/";
    private static final String MATCHERS_FILE_SUFFIX = ".ecl";
    private static final String MATCHERS_DEFAULT = "default";

    private static final String E_INCOMPATIBLECAPREQ = "Incompatible types: cap to req:";


    private EclipseEngine eclipse;
    private String eclipseDir;


    public EclipseModelMatcher() throws ModelMatcherEngineNotFound, ModelMatcherSelectFailure {
        CoreSolver solver = CoreSolver.getInstance();
        if (solver instanceof EclipseSolver){
            eclipse=((EclipseSolver)solver).getEngine();
            eclipseDir= ((EclipseSolver) solver).getEclipseDir();
        } else throw ModelMatcherEngineNotFound.forward();

        selectDefaultMatcher();
    }

    @Override
    public void selectDefaultMatcher() throws ModelMatcherSelectFailure {
        selectMatcher(MATCHERS_DEFAULT);
    }

    //Need to check that compiling in a new one overwrites existing...
    public void selectMatcher(String name) throws ModelMatcherSelectFailure {
        try {
            eclipse.compile(new File(eclipseDir + EclipseSolver.ECLIPSE_SWITCH + MATCHERS_DIR_PREFIX + name + MATCHERS_FILE_SUFFIX));
        } catch (Exception e) {
            throw ModelMatcherSelectFailure.forward(e);
        }
    }

    @Override
    public boolean match(LinkedHashMap<String, Object> componentData, LinkedHashMap<String, Object> modelData) throws IOException, IncompatibleMatchingCapsToReqs {

        HashMap<String, Object[]> current = getCurrentCapacitiesRecord();

        String imageCapsList = processCapsIntoPrologList(current);
        String componentsList = processIntoPrologList(componentData);
        String modelList = processIntoPrologList(modelData);

        String goal = "match(" + componentsList + ", " + modelList + ", " + imageCapsList + ")";
        System.out.println("GOAL: "+goal);

        try {
            CompoundTerm result = eclipse.rpc(goal);
            //We ignore result for now...
        } catch (EclipseException e) {
            return false;
        } 

        //Subtract from image capacities...
        for (String key: current.keySet()){
            Object[] record = current.get(key);
            Object sub = componentData.get(key);

            if (record[1] instanceof Integer){
                if (sub instanceof Integer){
                    record[1] = (Integer)record[1] - (Integer)sub;
                } else throw IncompatibleMatchingCapsToReqs.forward(E_INCOMPATIBLECAPREQ+" key:"+key);
            } else if (record[1] instanceof Float) {
                if (sub instanceof Float) {
                    record[1] = (Float) record[1] - (Float) sub;
                } else throw IncompatibleMatchingCapsToReqs.forward(E_INCOMPATIBLECAPREQ + " key:" + key);
            }
        }

        return true;
    }
}
