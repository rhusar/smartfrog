/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl.references;

import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.services.xml.java5.NamespaceUtils;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;

/**
 */
public class ReferencePath {
    public static final String ERROR_NON_REFERENCE = "Trying to create a reference path from a non-reference";

    public ReferencePath() {
    }

    /**
     * Build from a source
     * @param source
     * @throws IllegalArgumentException
     */
    public ReferencePath(PropertyList source) {
        String refRootValue = source.getRefRootValue();
        String refValue = source.getRefValue();
        if(refValue==null) {
            throw new IllegalArgumentException(ERROR_NON_REFERENCE);
        }

        if (refRootValue != null) {
            QName resolved = source.resolveQName(refRootValue);
            setRefroot(resolved);
        }


    }

    private QName refroot;

    /** the steps in the path */
    private List<Step> steps=new ArrayList<Step>();

    public List<Step> getSteps() {
        return steps;
    }

    public QName getRefroot() {
        return refroot;
    }

    public void setRefroot(QName refroot) {
        assert refroot!=null;
        this.refroot = refroot;
    }

    /**
     * add a new step to the path
     * @param step
     */
    public void append(Step step) {
        assert step != null;
        steps.add(step);
    }

    /**
     * Test for the path being empty
     * @return
     */
    public boolean isEmpty() {
        return steps.isEmpty();
    }

    /**
     * This function can only be called on a non-empty path.
     * @return true iff this is a relative path.
     */
    public boolean isRelative() {
        return !steps.get(0).isRoot();
    }

    /**
     * Returns a string representation of the object.
     * Has an extra / at the end, whether you want it or not.
     */
    public String toString() {
        StringBuffer result=new StringBuffer();
        if(refroot!=null) {
            result.append('[');
            result.append(refroot);
            result.append("]#");
        }
        if(!isEmpty()) {
            for(Step step:steps) {
                result.append(step.toString());
                if(!step.isRoot()) {
                    result.append('/');
                }
            }
        }

        return result.toString();
    }

    /**
     * Build from a path of the spec
     * . | .. | localname | prefix:localname separated by /
     * @param path
     */
    public void build(String path) {
        //this is very easy to parse; no need for any complex recursive
        //parser. Even so, regexp work may make this tractable
        steps = new ArrayList<Step>();
        int start=0;
        final int pathlength = path.length();
        if(path.startsWith("/")) {
            append(new StepRoot());
            start=1;
            if(pathlength ==1) {
                return;
            }
        }
        //now, scan through the source looking for stuff
        boolean finished=false;

        while (!finished) {
            int slash=path.indexOf('/',start);
            String qname;
            if(slash<0) {
                finished=true;
                qname=path.substring(start);
            } else {
                qname=path.substring(start,slash);
                start = slash + 1;
                finished=start>=pathlength;
            }
            //here qname is the current element. Extract but do not yet evaluate
            if(".".equals(qname)) {
                append(new StepHere());
            } else if ("..".equals(qname)) {
                append(new StepUp());
            } else{
                String prefix= NamespaceUtils.extractNamespacePrefix(qname);
                String localname= NamespaceUtils.extractLocalname(qname);
                StepDown step = new StepDown(prefix, localname);
                append(step);
            }
        }


    }

    //pattern
    /*

    */
}
