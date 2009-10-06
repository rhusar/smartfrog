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
 * A representation of the model object '<em><b>Composite</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composite#getSuperComposite <em>Super Composite</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composite#getComposite_Component_Container <em>Composite Component Container</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composite#getComposite_Attribute_Container <em>Composite Attribute Container</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composite#getComposite_Connector_Container <em>Composite Connector Container</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composite#getChildComposite <em>Child Composite</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composite#getModel_Member_Composites <em>Model Member Composites</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposite()
 * @model
 * @generated
 */
public interface Composite extends ModelObject
{
  /**
   * Returns the value of the '<em><b>Super Composite</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Composite}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Super Composite</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Super Composite</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposite_SuperComposite()
   * @model type="org.smartfrog.authoringtool.emf.Composite" containment="true" upper="2000"
   * @generated
   */
  EList getSuperComposite();

  /**
   * Returns the value of the '<em><b>Composite Component Container</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Component}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Composite Component Container</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Composite Component Container</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposite_Composite_Component_Container()
   * @model type="org.smartfrog.authoringtool.emf.Component" containment="true" upper="2000"
   * @generated
   */
  EList getComposite_Component_Container();

  /**
   * Returns the value of the '<em><b>Composite Attribute Container</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Attribute}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Composite Attribute Container</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Composite Attribute Container</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposite_Composite_Attribute_Container()
   * @model type="org.smartfrog.authoringtool.emf.Attribute" containment="true" upper="2000"
   * @generated
   */
  EList getComposite_Attribute_Container();

  /**
   * Returns the value of the '<em><b>Composite Connector Container</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Connectors}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Composite Connector Container</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Composite Connector Container</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposite_Composite_Connector_Container()
   * @model type="org.smartfrog.authoringtool.emf.Connectors" containment="true" upper="2000"
   * @generated
   */
  EList getComposite_Connector_Container();

  /**
   * Returns the value of the '<em><b>Child Composite</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Child Composite</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Child Composite</em>' reference.
   * @see #setChildComposite(Composite)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposite_ChildComposite()
   * @model
   * @generated
   */
  Composite getChildComposite();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Composite#getChildComposite <em>Child Composite</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Child Composite</em>' reference.
   * @see #getChildComposite()
   * @generated
   */
  void setChildComposite(Composite value);

  /**
   * Returns the value of the '<em><b>Model Member Composites</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Model Member Composites</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Model Member Composites</em>' reference.
   * @see #setModel_Member_Composites(DependencyModel)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposite_Model_Member_Composites()
   * @model
   * @generated
   */
  DependencyModel getModel_Member_Composites();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Composite#getModel_Member_Composites <em>Model Member Composites</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Model Member Composites</em>' reference.
   * @see #getModel_Member_Composites()
   * @generated
   */
  void setModel_Member_Composites(DependencyModel value);

} // Composite
