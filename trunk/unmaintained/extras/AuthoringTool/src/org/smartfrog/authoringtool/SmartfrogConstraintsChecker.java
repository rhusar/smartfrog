
package org.smartfrog.authoringtool;

import org.gems.designer.model.AbstractConstraintsChecker;

import org.gems.designer.model.Container;
import org.gems.designer.model.ModelObject;
import org.gems.designer.model.ModelUtilities;
import org.gems.designer.model.Model;
import org.gems.designer.model.event.ModelChangeEvent;
import org.gems.designer.model.ExecutableConstraint;
import org.gems.designer.model.ExecutableEventConstraint;
import org.gems.designer.model.Root;
import org.gems.designer.metamodel.ConstraintMemento;
import org.gems.designer.model.actions.EventInterestFactory;
import org.gems.designer.model.actions.EventInterestFactoryRepository;
import org.gems.designer.model.actions.PersistentModelEventInterest;

public class SmartfrogConstraintsChecker extends AbstractConstraintsChecker {

    /**
     * 
     */
    public SmartfrogConstraintsChecker() {
        super();
    }
    
    public void createConstraints() {
        addConnectionConstraint(Component.class,
                                Component.class,
                                0,
                                2147483647,
                                1,
                                1,
                                SimpleDependencyConnectionType.INSTANCE);
       
        addConnectionConstraint(Component.class,
                                Connectors.class,
                                0,
                                2147483647,
                                0,
                                2147483647,
                                InputDependencyConnectionType.INSTANCE);
       
        addConnectionConstraint(Connectors.class,
                                Component.class,
                                0,
                                2147483647,
                                0,
                                2147483647,
                                OutputDependencyConnectionType.INSTANCE);
       
        addConnectionConstraint(Connectors.class,
                                Component.class,
                                0,
                                2147483647,
                                0,
                                2147483647,
                                OutputDependencyConnectionType.INSTANCE);
       
        addConnectionConstraint(Component.class,
                                Connectors.class,
                                0,
                                2147483647,
                                0,
                                2147483647,
                                InputDependencyConnectionType.INSTANCE);
       
       
        addContainmentConstraint(Component.class,
                                 Attribute.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(Composite.class,
                                 Component.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(DependencyModel.class,
                                 Component.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(Composite.class,
                                 Composite.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(Composite.class,
                                 Component.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(Composite.class,
                                 Attribute.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(Composite.class,
                                 Connectors.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(DependencyModel.class,
                                 Composite.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(DependencyModel.class,
                                 Composite.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(DependencyModel.class,
                                 Component.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(DependencyModel.class,
                                 Connectors.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(Component.class,
                                 Attribute.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(Composite.class,
                                 Attribute.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(Composite.class,
                                 Connectors.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(DependencyModel.class,
                                 Connectors.class,
                                 0,
                                 2147483647);
        addContainmentConstraint(Root.class,
                                 Composite.class,
                                 0,
                                 Integer.MAX_VALUE);
        addContainmentConstraint(Root.class,
                                 Component.class,
                                 0,
                                 Integer.MAX_VALUE);
        addContainmentConstraint(Root.class,
                                 Connectors.class,
                                 0,
                                 Integer.MAX_VALUE);
        
    }
    
    public java.util.List<org.gems.designer.Memento> getConstraintMementos(){
        java.util.LinkedList<org.gems.designer.Memento> mems = new java.util.LinkedList<org.gems.designer.Memento>();
    	 
        return mems;
    }


}
