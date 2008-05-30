/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.authoringtool.emf.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.smartfrog.authoringtool.emf.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.smartfrog.authoringtool.emf.SmartfrogPackage
 * @generated
 */
public class SmartfrogAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static SmartfrogPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SmartfrogAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = SmartfrogPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch the delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SmartfrogSwitch modelSwitch =
    new SmartfrogSwitch()
    {
      public Object caseMementoValue(MementoValue object)
      {
        return createMementoValueAdapter();
      }
      public Object caseMemento(Memento object)
      {
        return createMementoAdapter();
      }
      public Object caseSubtype(Subtype object)
      {
        return createSubtypeAdapter();
      }
      public Object caseSubtypeLink(SubtypeLink object)
      {
        return createSubtypeLinkAdapter();
      }
      public Object caseModelObject(ModelObject object)
      {
        return createModelObjectAdapter();
      }
      public Object caseAnd(And object)
      {
        return createAndAdapter();
      }
      public Object caseOr(Or object)
      {
        return createOrAdapter();
      }
      public Object caseComponent(Component object)
      {
        return createComponentAdapter();
      }
      public Object caseComposit(Composit object)
      {
        return createCompositAdapter();
      }
      public Object caseDependencyModel(DependencyModel object)
      {
        return createDependencyModelAdapter();
      }
      public Object caseAttribute(Attribute object)
      {
        return createAttributeAdapter();
      }
      public Object caseConnectors(Connectors object)
      {
        return createConnectorsAdapter();
      }
      public Object caseSimpleDependencyConnection(SimpleDependencyConnection object)
      {
        return createSimpleDependencyConnectionAdapter();
      }
      public Object caseInputDependencyConnection(InputDependencyConnection object)
      {
        return createInputDependencyConnectionAdapter();
      }
      public Object caseOutputDependencyConnection(OutputDependencyConnection object)
      {
        return createOutputDependencyConnectionAdapter();
      }
      public Object caseRoot(Root object)
      {
        return createRootAdapter();
      }
      public Object defaultCase(EObject object)
      {
        return createEObjectAdapter();
      }
    };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  public Adapter createAdapter(Notifier target)
  {
    return (Adapter)modelSwitch.doSwitch((EObject)target);
  }


  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.MementoValue <em>Memento Value</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.MementoValue
   * @generated
   */
  public Adapter createMementoValueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.Memento <em>Memento</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.Memento
   * @generated
   */
  public Adapter createMementoAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.Subtype <em>Subtype</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.Subtype
   * @generated
   */
  public Adapter createSubtypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.SubtypeLink <em>Subtype Link</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.SubtypeLink
   * @generated
   */
  public Adapter createSubtypeLinkAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.ModelObject <em>Model Object</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.ModelObject
   * @generated
   */
  public Adapter createModelObjectAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.And <em>And</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.And
   * @generated
   */
  public Adapter createAndAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.Or <em>Or</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.Or
   * @generated
   */
  public Adapter createOrAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.Component <em>Component</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.Component
   * @generated
   */
  public Adapter createComponentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.Composit <em>Composit</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.Composit
   * @generated
   */
  public Adapter createCompositAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.DependencyModel <em>Dependency Model</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.DependencyModel
   * @generated
   */
  public Adapter createDependencyModelAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.Attribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.Attribute
   * @generated
   */
  public Adapter createAttributeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.Connectors <em>Connectors</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.Connectors
   * @generated
   */
  public Adapter createConnectorsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.SimpleDependencyConnection <em>Simple Dependency Connection</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.SimpleDependencyConnection
   * @generated
   */
  public Adapter createSimpleDependencyConnectionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.InputDependencyConnection <em>Input Dependency Connection</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.InputDependencyConnection
   * @generated
   */
  public Adapter createInputDependencyConnectionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.OutputDependencyConnection <em>Output Dependency Connection</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.OutputDependencyConnection
   * @generated
   */
  public Adapter createOutputDependencyConnectionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.smartfrog.authoringtool.emf.Root <em>Root</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.smartfrog.authoringtool.emf.Root
   * @generated
   */
  public Adapter createRootAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} //SmartfrogAdapterFactory
