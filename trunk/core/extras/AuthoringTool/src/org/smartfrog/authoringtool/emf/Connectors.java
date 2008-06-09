/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.smartfrog.authoringtool.emf;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Connectors</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.Connectors#getDepConnector <em>Dep Connector</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Connectors#getGenDepConnector <em>Gen Dep Connector</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Connectors#getBy <em>By</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Connectors#getOn <em>On</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getConnectors()
 * @model
 * @generated
 */
public interface Connectors extends ModelObject
{
  /**
   * Returns the value of the '<em><b>Dep Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Dep Connector</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Dep Connector</em>' reference.
   * @see #setDepConnector(Composite)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getConnectors_DepConnector()
   * @model
   * @generated
   */
  Composite getDepConnector();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Connectors#getDepConnector <em>Dep Connector</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Dep Connector</em>' reference.
   * @see #getDepConnector()
   * @generated
   */
  void setDepConnector(Composite value);

  /**
   * Returns the value of the '<em><b>Gen Dep Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Gen Dep Connector</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Gen Dep Connector</em>' reference.
   * @see #setGenDepConnector(DependencyModel)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getConnectors_GenDepConnector()
   * @model
   * @generated
   */
  DependencyModel getGenDepConnector();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Connectors#getGenDepConnector <em>Gen Dep Connector</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Gen Dep Connector</em>' reference.
   * @see #getGenDepConnector()
   * @generated
   */
  void setGenDepConnector(DependencyModel value);

  /**
   * Returns the value of the '<em><b>By</b></em>' reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Component}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>By</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>By</em>' reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getConnectors_By()
   * @model type="org.smartfrog.authoringtool.emf.Component" upper="2147483647"
   * @generated
   */
  EList getBy();

  /**
   * Returns the value of the '<em><b>On</b></em>' reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Component}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>On</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>On</em>' reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getConnectors_On()
   * @model type="org.smartfrog.authoringtool.emf.Component" upper="2147483647"
   * @generated
   */
  EList getOn();

} // Connectors
