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
 *   <li>{@link org.smartfrog.authoringtool.emf.DependencyModel#isRun <em>Run</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.DependencyModel#getModel_composite_Container <em>Model composite Container</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.DependencyModel#getModel_Component_Container <em>Model Component Container</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.DependencyModel#getModel_Connector_Container <em>Model Connector Container</em>}</li>
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
   * Returns the value of the '<em><b>Run</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Run</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Run</em>' attribute.
   * @see #setRun(boolean)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getDependencyModel_Run()
   * @model default="true"
   * @generated
   */
  boolean isRun();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.DependencyModel#isRun <em>Run</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Run</em>' attribute.
   * @see #isRun()
   * @generated
   */
  void setRun(boolean value);

  /**
   * Returns the value of the '<em><b>Model composite Container</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Composite}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Model composite Container</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Model composite Container</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getDependencyModel_Model_composite_Container()
   * @model type="org.smartfrog.authoringtool.emf.Composite" containment="true" upper="2000"
   * @generated
   */
  EList getModel_composite_Container();

  /**
   * Returns the value of the '<em><b>Model Component Container</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Component}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Model Component Container</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Model Component Container</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getDependencyModel_Model_Component_Container()
   * @model type="org.smartfrog.authoringtool.emf.Component" containment="true" upper="2000"
   * @generated
   */
  EList getModel_Component_Container();

  /**
   * Returns the value of the '<em><b>Model Connector Container</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Connectors}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Model Connector Container</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Model Connector Container</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getDependencyModel_Model_Connector_Container()
   * @model type="org.smartfrog.authoringtool.emf.Connectors" containment="true" upper="2000"
   * @generated
   */
  EList getModel_Connector_Container();

} // DependencyModel
