/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/
package org.smartfrog.authoringtool.emf;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Composit</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composit#getSuperComposit <em>Super Composit</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composit#getComponents <em>Components</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composit#getCompos <em>Compos</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composit#getCompo <em>Compo</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composit#getChileComposit <em>Chile Composit</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Composit#getComposits <em>Composits</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposit()
 * @model
 * @generated
 */
public interface Composit extends ModelObject
{
  /**
   * Returns the value of the '<em><b>Super Composit</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Composit}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Super Composit</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Super Composit</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposit_SuperComposit()
   * @model type="org.smartfrog.authoringtool.emf.Composit" containment="true" upper="2000"
   * @generated
   */
  EList getSuperComposit();

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
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposit_Components()
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
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposit_Compos()
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
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposit_Compo()
   * @model type="org.smartfrog.authoringtool.emf.Connectors" containment="true" upper="2000"
   * @generated
   */
  EList getCompo();

  /**
   * Returns the value of the '<em><b>Chile Composit</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Chile Composit</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Chile Composit</em>' reference.
   * @see #setChileComposit(Composit)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposit_ChileComposit()
   * @model
   * @generated
   */
  Composit getChileComposit();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Composit#getChileComposit <em>Chile Composit</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Chile Composit</em>' reference.
   * @see #getChileComposit()
   * @generated
   */
  void setChileComposit(Composit value);

  /**
   * Returns the value of the '<em><b>Composits</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Composits</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Composits</em>' reference.
   * @see #setComposits(DependencyModel)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComposit_Composits()
   * @model
   * @generated
   */
  DependencyModel getComposits();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Composit#getComposits <em>Composits</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Composits</em>' reference.
   * @see #getComposits()
   * @generated
   */
  void setComposits(DependencyModel value);

} // Composit
