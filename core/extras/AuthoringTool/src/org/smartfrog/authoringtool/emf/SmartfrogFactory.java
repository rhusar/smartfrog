/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.smartfrog.authoringtool.emf;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.smartfrog.authoringtool.emf.SmartfrogPackage
 * @generated
 */
public interface SmartfrogFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  SmartfrogFactory eINSTANCE = org.smartfrog.authoringtool.emf.impl.SmartfrogFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Memento Value</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Memento Value</em>'.
   * @generated
   */
  MementoValue createMementoValue();

  /**
   * Returns a new object of class '<em>Memento</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Memento</em>'.
   * @generated
   */
  Memento createMemento();

  /**
   * Returns a new object of class '<em>Subtype</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Subtype</em>'.
   * @generated
   */
  Subtype createSubtype();

  /**
   * Returns a new object of class '<em>Subtype Link</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Subtype Link</em>'.
   * @generated
   */
  SubtypeLink createSubtypeLink();

  /**
   * Returns a new object of class '<em>Model Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Model Object</em>'.
   * @generated
   */
  ModelObject createModelObject();

  /**
   * Returns a new object of class '<em>And</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>And</em>'.
   * @generated
   */
  And createAnd();

  /**
   * Returns a new object of class '<em>Or</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Or</em>'.
   * @generated
   */
  Or createOr();

  /**
   * Returns a new object of class '<em>Nor</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Nor</em>'.
   * @generated
   */
  Nor createNor();

  /**
   * Returns a new object of class '<em>Nand</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Nand</em>'.
   * @generated
   */
  Nand createNand();

  /**
   * Returns a new object of class '<em>Component</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Component</em>'.
   * @generated
   */
  Component createComponent();

  /**
   * Returns a new object of class '<em>Composite</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Composite</em>'.
   * @generated
   */
  Composite createComposite();

  /**
   * Returns a new object of class '<em>Dependency Model</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Dependency Model</em>'.
   * @generated
   */
  DependencyModel createDependencyModel();

  /**
   * Returns a new object of class '<em>Attribute</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute</em>'.
   * @generated
   */
  Attribute createAttribute();

  /**
   * Returns a new object of class '<em>Connectors</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Connectors</em>'.
   * @generated
   */
  Connectors createConnectors();

  /**
   * Returns a new object of class '<em>Simple Dependency Connection</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Simple Dependency Connection</em>'.
   * @generated
   */
  SimpleDependencyConnection createSimpleDependencyConnection();

  /**
   * Returns a new object of class '<em>Input Dependency Connection</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Input Dependency Connection</em>'.
   * @generated
   */
  InputDependencyConnection createInputDependencyConnection();

  /**
   * Returns a new object of class '<em>Output Dependency Connection</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Output Dependency Connection</em>'.
   * @generated
   */
  OutputDependencyConnection createOutputDependencyConnection();

  /**
   * Returns a new object of class '<em>Root</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Root</em>'.
   * @generated
   */
  Root createRoot();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  SmartfrogPackage getSmartfrogPackage();

} //SmartfrogFactory
