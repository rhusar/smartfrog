/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.smartfrog.authoringtool.emf;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getMementos <em>Mementos</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getSubtypes <em>Subtypes</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getRealRoot <em>Real Root</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getAnd <em>And</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getOr <em>Or</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getNor <em>Nor</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getNand <em>Nand</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getComponent <em>Component</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getComposite <em>Composite</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getDependencyModel <em>Dependency Model</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getAttribute <em>Attribute</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getConnectors <em>Connectors</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getSimpleDependencyConnection <em>Simple Dependency Connection</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getInputDependencyConnection <em>Input Dependency Connection</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Root#getOutputDependencyConnection <em>Output Dependency Connection</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot()
 * @model
 * @generated
 */
public interface Root extends EObject
{
  /**
   * Returns the value of the '<em><b>Mementos</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Memento}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Mementos</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Mementos</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_Mementos()
   * @model type="org.smartfrog.authoringtool.emf.Memento" containment="true" upper="2000"
   * @generated
   */
  EList getMementos();

  /**
   * Returns the value of the '<em><b>Subtypes</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Subtype}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Subtypes</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Subtypes</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_Subtypes()
   * @model type="org.smartfrog.authoringtool.emf.Subtype" containment="true" upper="2000"
   * @generated
   */
  EList getSubtypes();

  /**
   * Returns the value of the '<em><b>Real Root</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Real Root</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Real Root</em>' containment reference.
   * @see #setRealRoot(DependencyModel)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_RealRoot()
   * @model containment="true"
   * @generated
   */
  DependencyModel getRealRoot();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Root#getRealRoot <em>Real Root</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Real Root</em>' containment reference.
   * @see #getRealRoot()
   * @generated
   */
  void setRealRoot(DependencyModel value);

  /**
   * Returns the value of the '<em><b>And</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.And}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>And</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>And</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_And()
   * @model type="org.smartfrog.authoringtool.emf.And" containment="true" upper="2000"
   * @generated
   */
  EList getAnd();

  /**
   * Returns the value of the '<em><b>Or</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Or}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Or</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Or</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_Or()
   * @model type="org.smartfrog.authoringtool.emf.Or" containment="true" upper="2000"
   * @generated
   */
  EList getOr();

  /**
   * Returns the value of the '<em><b>Nor</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Nor}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Nor</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Nor</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_Nor()
   * @model type="org.smartfrog.authoringtool.emf.Nor" containment="true" upper="2000"
   * @generated
   */
  EList getNor();

  /**
   * Returns the value of the '<em><b>Nand</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Nand}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Nand</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Nand</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_Nand()
   * @model type="org.smartfrog.authoringtool.emf.Nand" containment="true" upper="2000"
   * @generated
   */
  EList getNand();

  /**
   * Returns the value of the '<em><b>Component</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Component}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Component</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Component</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_Component()
   * @model type="org.smartfrog.authoringtool.emf.Component" containment="true" upper="2000"
   * @generated
   */
  EList getComponent();

  /**
   * Returns the value of the '<em><b>Composite</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Composite}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Composite</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Composite</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_Composite()
   * @model type="org.smartfrog.authoringtool.emf.Composite" containment="true" upper="2000"
   * @generated
   */
  EList getComposite();

  /**
   * Returns the value of the '<em><b>Dependency Model</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.DependencyModel}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Dependency Model</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Dependency Model</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_DependencyModel()
   * @model type="org.smartfrog.authoringtool.emf.DependencyModel" containment="true" upper="2000"
   * @generated
   */
  EList getDependencyModel();

  /**
   * Returns the value of the '<em><b>Attribute</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Attribute}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attribute</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attribute</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_Attribute()
   * @model type="org.smartfrog.authoringtool.emf.Attribute" containment="true" upper="2000"
   * @generated
   */
  EList getAttribute();

  /**
   * Returns the value of the '<em><b>Connectors</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Connectors}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Connectors</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Connectors</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_Connectors()
   * @model type="org.smartfrog.authoringtool.emf.Connectors" containment="true" upper="2000"
   * @generated
   */
  EList getConnectors();

  /**
   * Returns the value of the '<em><b>Simple Dependency Connection</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.SimpleDependencyConnection}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Simple Dependency Connection</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Simple Dependency Connection</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_SimpleDependencyConnection()
   * @model type="org.smartfrog.authoringtool.emf.SimpleDependencyConnection" containment="true" upper="2000"
   * @generated
   */
  EList getSimpleDependencyConnection();

  /**
   * Returns the value of the '<em><b>Input Dependency Connection</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.InputDependencyConnection}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Input Dependency Connection</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Input Dependency Connection</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_InputDependencyConnection()
   * @model type="org.smartfrog.authoringtool.emf.InputDependencyConnection" containment="true" upper="2000"
   * @generated
   */
  EList getInputDependencyConnection();

  /**
   * Returns the value of the '<em><b>Output Dependency Connection</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.OutputDependencyConnection}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Output Dependency Connection</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Output Dependency Connection</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getRoot_OutputDependencyConnection()
   * @model type="org.smartfrog.authoringtool.emf.OutputDependencyConnection" containment="true" upper="2000"
   * @generated
   */
  EList getOutputDependencyConnection();

} // Root
