/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.smartfrog.authoringtool.emf.util;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.smartfrog.authoringtool.emf.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.smartfrog.authoringtool.emf.SmartfrogPackage
 * @generated
 */
public class SmartfrogSwitch
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static SmartfrogPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SmartfrogSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = SmartfrogPackage.eINSTANCE;
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  public Object doSwitch(EObject theEObject)
  {
    return doSwitch(theEObject.eClass(), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected Object doSwitch(EClass theEClass, EObject theEObject)
  {
    if (theEClass.eContainer() == modelPackage)
    {
      return doSwitch(theEClass.getClassifierID(), theEObject);
    }
    else
    {
      List eSuperTypes = theEClass.getESuperTypes();
      return
        eSuperTypes.isEmpty() ?
          defaultCase(theEObject) :
          doSwitch((EClass)eSuperTypes.get(0), theEObject);
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected Object doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
      case SmartfrogPackage.MEMENTO_VALUE:
      {
        MementoValue mementoValue = (MementoValue)theEObject;
        Object result = caseMementoValue(mementoValue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.MEMENTO:
      {
        Memento memento = (Memento)theEObject;
        Object result = caseMemento(memento);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.SUBTYPE:
      {
        Subtype subtype = (Subtype)theEObject;
        Object result = caseSubtype(subtype);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.SUBTYPE_LINK:
      {
        SubtypeLink subtypeLink = (SubtypeLink)theEObject;
        Object result = caseSubtypeLink(subtypeLink);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.MODEL_OBJECT:
      {
        ModelObject modelObject = (ModelObject)theEObject;
        Object result = caseModelObject(modelObject);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.AND:
      {
        And and = (And)theEObject;
        Object result = caseAnd(and);
        if (result == null) result = caseConnectors(and);
        if (result == null) result = caseModelObject(and);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.OR:
      {
        Or or = (Or)theEObject;
        Object result = caseOr(or);
        if (result == null) result = caseConnectors(or);
        if (result == null) result = caseModelObject(or);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.NOR:
      {
        Nor nor = (Nor)theEObject;
        Object result = caseNor(nor);
        if (result == null) result = caseConnectors(nor);
        if (result == null) result = caseModelObject(nor);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.NAND:
      {
        Nand nand = (Nand)theEObject;
        Object result = caseNand(nand);
        if (result == null) result = caseConnectors(nand);
        if (result == null) result = caseModelObject(nand);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.COMPONENT:
      {
        Component component = (Component)theEObject;
        Object result = caseComponent(component);
        if (result == null) result = caseModelObject(component);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.COMPOSITE:
      {
        Composite composite = (Composite)theEObject;
        Object result = caseComposite(composite);
        if (result == null) result = caseModelObject(composite);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.DEPENDENCY_MODEL:
      {
        DependencyModel dependencyModel = (DependencyModel)theEObject;
        Object result = caseDependencyModel(dependencyModel);
        if (result == null) result = caseModelObject(dependencyModel);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.ATTRIBUTE:
      {
        Attribute attribute = (Attribute)theEObject;
        Object result = caseAttribute(attribute);
        if (result == null) result = caseModelObject(attribute);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.CONNECTORS:
      {
        Connectors connectors = (Connectors)theEObject;
        Object result = caseConnectors(connectors);
        if (result == null) result = caseModelObject(connectors);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION:
      {
        SimpleDependencyConnection simpleDependencyConnection = (SimpleDependencyConnection)theEObject;
        Object result = caseSimpleDependencyConnection(simpleDependencyConnection);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION:
      {
        InputDependencyConnection inputDependencyConnection = (InputDependencyConnection)theEObject;
        Object result = caseInputDependencyConnection(inputDependencyConnection);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION:
      {
        OutputDependencyConnection outputDependencyConnection = (OutputDependencyConnection)theEObject;
        Object result = caseOutputDependencyConnection(outputDependencyConnection);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case SmartfrogPackage.ROOT:
      {
        Root root = (Root)theEObject;
        Object result = caseRoot(root);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default: return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Memento Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Memento Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseMementoValue(MementoValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Memento</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Memento</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseMemento(Memento object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Subtype</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Subtype</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseSubtype(Subtype object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Subtype Link</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Subtype Link</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseSubtypeLink(SubtypeLink object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Model Object</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Model Object</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseModelObject(ModelObject object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>And</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>And</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseAnd(And object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Or</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Or</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseOr(Or object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Nor</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Nor</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseNor(Nor object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Nand</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Nand</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseNand(Nand object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Component</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Component</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseComponent(Component object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Composite</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Composite</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseComposite(Composite object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Dependency Model</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Dependency Model</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseDependencyModel(DependencyModel object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Attribute</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attribute</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseAttribute(Attribute object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Connectors</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Connectors</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseConnectors(Connectors object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Simple Dependency Connection</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Simple Dependency Connection</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseSimpleDependencyConnection(SimpleDependencyConnection object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Input Dependency Connection</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Input Dependency Connection</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseInputDependencyConnection(InputDependencyConnection object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Output Dependency Connection</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Output Dependency Connection</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseOutputDependencyConnection(OutputDependencyConnection object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Root</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Root</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseRoot(Root object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  public Object defaultCase(EObject object)
  {
    return null;
  }

} //SmartfrogSwitch
