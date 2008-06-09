/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.smartfrog.authoringtool.emf;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Input Dependency Connection</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getSource <em>Source</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getTarget <em>Target</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getInputDependencyConnection()
 * @model
 * @generated
 */
public interface InputDependencyConnection extends EObject
{
  /**
   * Returns the value of the '<em><b>Source</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source</em>' reference.
   * @see #setSource(Component)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getInputDependencyConnection_Source()
   * @model
   * @generated
   */
  Component getSource();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getSource <em>Source</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source</em>' reference.
   * @see #getSource()
   * @generated
   */
  void setSource(Component value);

  /**
   * Returns the value of the '<em><b>Target</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Target</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target</em>' reference.
   * @see #setTarget(Connectors)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getInputDependencyConnection_Target()
   * @model
   * @generated
   */
  Connectors getTarget();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getTarget <em>Target</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Target</em>' reference.
   * @see #getTarget()
   * @generated
   */
  void setTarget(Connectors value);

} // InputDependencyConnection
