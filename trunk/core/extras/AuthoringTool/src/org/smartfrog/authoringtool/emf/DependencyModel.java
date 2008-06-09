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
 * A representation of the model object '<em><b>Dependency Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.DependencyModel#getSFModel <em>SF Model</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.DependencyModel#getDepmodel <em>Depmodel</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.DependencyModel#getRootModel <em>Root Model</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getDependencyModel()
 * @model
 * @generated
 */
public interface DependencyModel extends ModelObject
{
  /**
   * Returns the value of the '<em><b>SF Model</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Composite}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>SF Model</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>SF Model</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getDependencyModel_SFModel()
   * @model type="org.smartfrog.authoringtool.emf.Composite" containment="true" upper="2000"
   * @generated
   */
  EList getSFModel();

  /**
   * Returns the value of the '<em><b>Depmodel</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Component}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Depmodel</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Depmodel</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getDependencyModel_Depmodel()
   * @model type="org.smartfrog.authoringtool.emf.Component" containment="true" upper="2000"
   * @generated
   */
  EList getDepmodel();

  /**
   * Returns the value of the '<em><b>Root Model</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Connectors}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Root Model</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Root Model</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getDependencyModel_RootModel()
   * @model type="org.smartfrog.authoringtool.emf.Connectors" containment="true" upper="2000"
   * @generated
   */
  EList getRootModel();

} // DependencyModel
