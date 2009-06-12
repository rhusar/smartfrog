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
 *   <li>{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getRelevant <em>Relevant</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getEnabled <em>Enabled</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getInput_Dependency_Name <em>Input Dependency Name</em>}</li>
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

  /**
   * Returns the value of the '<em><b>Relevant</b></em>' attribute.
   * The default value is <code>"0"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Relevant</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Relevant</em>' attribute.
   * @see #setRelevant(String)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getInputDependencyConnection_Relevant()
   * @model default="0"
   * @generated
   */
  String getRelevant();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getRelevant <em>Relevant</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Relevant</em>' attribute.
   * @see #getRelevant()
   * @generated
   */
  void setRelevant(String value);

  /**
   * Returns the value of the '<em><b>Enabled</b></em>' attribute.
   * The default value is <code>"0"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Enabled</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Enabled</em>' attribute.
   * @see #setEnabled(String)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getInputDependencyConnection_Enabled()
   * @model default="0"
   * @generated
   */
  String getEnabled();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getEnabled <em>Enabled</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Enabled</em>' attribute.
   * @see #getEnabled()
   * @generated
   */
  void setEnabled(String value);

  /**
   * Returns the value of the '<em><b>Input Dependency Name</b></em>' attribute.
   * The default value is <code>"0"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Input Dependency Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Input Dependency Name</em>' attribute.
   * @see #setInput_Dependency_Name(String)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getInputDependencyConnection_Input_Dependency_Name()
   * @model default="0"
   * @generated
   */
  String getInput_Dependency_Name();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getInput_Dependency_Name <em>Input Dependency Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Input Dependency Name</em>' attribute.
   * @see #getInput_Dependency_Name()
   * @generated
   */
  void setInput_Dependency_Name(String value);

} // InputDependencyConnection
