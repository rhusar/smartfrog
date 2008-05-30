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
package org.smartfrog.authoringtool.emf.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.smartfrog.authoringtool.emf.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SmartfrogFactoryImpl extends EFactoryImpl implements SmartfrogFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static SmartfrogFactory init()
  {
    try
    {
      SmartfrogFactory theSmartfrogFactory = (SmartfrogFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.smartfrog.org/sfml"); 
      if (theSmartfrogFactory != null)
      {
        return theSmartfrogFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new SmartfrogFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SmartfrogFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case SmartfrogPackage.MEMENTO_VALUE: return createMementoValue();
      case SmartfrogPackage.MEMENTO: return createMemento();
      case SmartfrogPackage.SUBTYPE: return createSubtype();
      case SmartfrogPackage.SUBTYPE_LINK: return createSubtypeLink();
      case SmartfrogPackage.MODEL_OBJECT: return createModelObject();
      case SmartfrogPackage.AND: return createAnd();
      case SmartfrogPackage.OR: return createOr();
      case SmartfrogPackage.COMPONENT: return createComponent();
      case SmartfrogPackage.COMPOSIT: return createComposit();
      case SmartfrogPackage.DEPENDENCY_MODEL: return createDependencyModel();
      case SmartfrogPackage.ATTRIBUTE: return createAttribute();
      case SmartfrogPackage.CONNECTORS: return createConnectors();
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION: return createSimpleDependencyConnection();
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION: return createInputDependencyConnection();
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION: return createOutputDependencyConnection();
      case SmartfrogPackage.ROOT: return createRoot();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MementoValue createMementoValue()
  {
    MementoValueImpl mementoValue = new MementoValueImpl();
    return mementoValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Memento createMemento()
  {
    MementoImpl memento = new MementoImpl();
    return memento;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Subtype createSubtype()
  {
    SubtypeImpl subtype = new SubtypeImpl();
    return subtype;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SubtypeLink createSubtypeLink()
  {
    SubtypeLinkImpl subtypeLink = new SubtypeLinkImpl();
    return subtypeLink;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ModelObject createModelObject()
  {
    ModelObjectImpl modelObject = new ModelObjectImpl();
    return modelObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public And createAnd()
  {
    AndImpl and = new AndImpl();
    return and;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Or createOr()
  {
    OrImpl or = new OrImpl();
    return or;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Component createComponent()
  {
    ComponentImpl component = new ComponentImpl();
    return component;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composit createComposit()
  {
    CompositImpl composit = new CompositImpl();
    return composit;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel createDependencyModel()
  {
    DependencyModelImpl dependencyModel = new DependencyModelImpl();
    return dependencyModel;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Attribute createAttribute()
  {
    AttributeImpl attribute = new AttributeImpl();
    return attribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Connectors createConnectors()
  {
    ConnectorsImpl connectors = new ConnectorsImpl();
    return connectors;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SimpleDependencyConnection createSimpleDependencyConnection()
  {
    SimpleDependencyConnectionImpl simpleDependencyConnection = new SimpleDependencyConnectionImpl();
    return simpleDependencyConnection;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public InputDependencyConnection createInputDependencyConnection()
  {
    InputDependencyConnectionImpl inputDependencyConnection = new InputDependencyConnectionImpl();
    return inputDependencyConnection;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OutputDependencyConnection createOutputDependencyConnection()
  {
    OutputDependencyConnectionImpl outputDependencyConnection = new OutputDependencyConnectionImpl();
    return outputDependencyConnection;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Root createRoot()
  {
    RootImpl root = new RootImpl();
    return root;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SmartfrogPackage getSmartfrogPackage()
  {
    return (SmartfrogPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  public static SmartfrogPackage getPackage()
  {
    return SmartfrogPackage.eINSTANCE;
  }

} //SmartfrogFactoryImpl
