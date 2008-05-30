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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Memento</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.Memento#getId <em>Id</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Memento#getData <em>Data</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getMemento()
 * @model
 * @generated
 */
public interface Memento extends EObject
{
  /**
   * Returns the value of the '<em><b>Id</b></em>' attribute.
   * The default value is <code>"0"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Id</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Id</em>' attribute.
   * @see #setId(String)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getMemento_Id()
   * @model default="0"
   * @generated
   */
  String getId();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Memento#getId <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Id</em>' attribute.
   * @see #getId()
   * @generated
   */
  void setId(String value);

  /**
   * Returns the value of the '<em><b>Data</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.MementoValue}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Data</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Data</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getMemento_Data()
   * @model type="org.smartfrog.authoringtool.emf.MementoValue" containment="true" upper="2000"
   * @generated
   */
  EList getData();

} // Memento
