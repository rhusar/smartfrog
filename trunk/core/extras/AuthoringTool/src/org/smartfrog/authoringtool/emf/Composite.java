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
 *   <li>{@link org.smartfrog.authoringtool.emf.Composite#getComponents <em>Components</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composite#getCompos <em>Compos</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composite#getCompo <em>Compo</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composite#getChildComposite <em>Child Composite</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composite#getComposites <em>Composites</em>}</li>
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
   * Returns the value of the '<em><b>Components</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Component}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Components</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Components</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposite_Components()
   * @model type="org.smartfrog.authoringtool.emf.Component" containment="true" upper="2000"
   * @generated
   */
  EList getComponents();

  /**
   * Returns the value of the '<em><b>Compos</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Attribute}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Compos</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Compos</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposite_Compos()
   * @model type="org.smartfrog.authoringtool.emf.Attribute" containment="true" upper="2000"
   * @generated
   */
  EList getCompos();

  /**
   * Returns the value of the '<em><b>Compo</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Connectors}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Compo</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Compo</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposite_Compo()
   * @model type="org.smartfrog.authoringtool.emf.Connectors" containment="true" upper="2000"
   * @generated
   */
  EList getCompo();

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
   * Returns the value of the '<em><b>Composites</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Composites</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Composites</em>' reference.
   * @see #setComposites(DependencyModel)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposite_Composites()
   * @model
   * @generated
   */
  DependencyModel getComposites();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Composite#getComposites <em>Composites</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Composites</em>' reference.
   * @see #getComposites()
   * @generated
   */
  void setComposites(DependencyModel value);

} // Composite
