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
 *   <li>{@link org.smartfrog.authoringtool.emf.Connectors#getChild_Connector <em>Child Connector</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Connectors#getMember_Connector <em>Member Connector</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Connectors#getConnector_Dependent_Source <em>Connector Dependent Source</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Connectors#getComponent_Depends_On_Connector <em>Component Depends On Connector</em>}</li>
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
   * Returns the value of the '<em><b>Child Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Child Connector</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Child Connector</em>' reference.
   * @see #setChild_Connector(Composite)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getConnectors_Child_Connector()
   * @model
   * @generated
   */
  Composite getChild_Connector();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Connectors#getChild_Connector <em>Child Connector</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Child Connector</em>' reference.
   * @see #getChild_Connector()
   * @generated
   */
  void setChild_Connector(Composite value);

  /**
   * Returns the value of the '<em><b>Member Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Member Connector</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Member Connector</em>' reference.
   * @see #setMember_Connector(DependencyModel)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getConnectors_Member_Connector()
   * @model
   * @generated
   */
  DependencyModel getMember_Connector();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Connectors#getMember_Connector <em>Member Connector</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Member Connector</em>' reference.
   * @see #getMember_Connector()
   * @generated
   */
  void setMember_Connector(DependencyModel value);

  /**
   * Returns the value of the '<em><b>Connector Dependent Source</b></em>' reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.OutputDependencyConnection}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Connector Dependent Source</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Connector Dependent Source</em>' reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getConnectors_Connector_Dependent_Source()
   * @model type="org.smartfrog.authoringtool.emf.OutputDependencyConnection" upper="2147483647"
   * @generated
   */
  EList getConnector_Dependent_Source();

  /**
   * Returns the value of the '<em><b>Component Depends On Connector</b></em>' reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.InputDependencyConnection}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Component Depends On Connector</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Component Depends On Connector</em>' reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getConnectors_Component_Depends_On_Connector()
   * @model type="org.smartfrog.authoringtool.emf.InputDependencyConnection" upper="2147483647"
   * @generated
   */
  EList getComponent_Depends_On_Connector();

} // Connectors
