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
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Composit}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>SF Model</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>SF Model</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getDependencyModel_SFModel()
   * @model type="org.smartfrog.authoringtool.emf.Composit" containment="true" upper="2000"
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
