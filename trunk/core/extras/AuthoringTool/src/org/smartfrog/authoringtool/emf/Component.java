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
 * A representation of the model object '<em><b>Component</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getComp <em>Comp</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getGroup_of_components <em>Group of components</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getComps <em>Comps</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getDepends_By <em>Depends By</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getBy <em>By</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getDependOn <em>Depend On</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getOn <em>On</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent()
 * @model
 * @generated
 */
public interface Component extends ModelObject
{
  /**
   * Returns the value of the '<em><b>Comp</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Attribute}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Comp</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Comp</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_Comp()
   * @model type="org.smartfrog.authoringtool.emf.Attribute" containment="true" upper="2000"
   * @generated
   */
  EList getComp();

  /**
   * Returns the value of the '<em><b>Group of components</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Group of components</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Group of components</em>' reference.
   * @see #setGroup_of_components(Composit)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_Group_of_components()
   * @model
   * @generated
   */
  Composit getGroup_of_components();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Component#getGroup_of_components <em>Group of components</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Group of components</em>' reference.
   * @see #getGroup_of_components()
   * @generated
   */
  void setGroup_of_components(Composit value);

  /**
   * Returns the value of the '<em><b>Comps</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Comps</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Comps</em>' reference.
   * @see #setComps(DependencyModel)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_Comps()
   * @model
   * @generated
   */
  DependencyModel getComps();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Component#getComps <em>Comps</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Comps</em>' reference.
   * @see #getComps()
   * @generated
   */
  void setComps(DependencyModel value);

  /**
   * Returns the value of the '<em><b>Depends By</b></em>' reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Component}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Depends By</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Depends By</em>' reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_Depends_By()
   * @model type="org.smartfrog.authoringtool.emf.Component" upper="2147483647"
   * @generated
   */
  EList getDepends_By();

  /**
   * Returns the value of the '<em><b>By</b></em>' reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Connectors}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>By</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>By</em>' reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_By()
   * @model type="org.smartfrog.authoringtool.emf.Connectors" upper="2147483647"
   * @generated
   */
  EList getBy();

  /**
   * Returns the value of the '<em><b>Depend On</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Depend On</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Depend On</em>' reference.
   * @see #setDependOn(Component)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_DependOn()
   * @model required="true"
   * @generated
   */
  Component getDependOn();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Component#getDependOn <em>Depend On</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Depend On</em>' reference.
   * @see #getDependOn()
   * @generated
   */
  void setDependOn(Component value);

  /**
   * Returns the value of the '<em><b>On</b></em>' reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Connectors}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>On</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>On</em>' reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_On()
   * @model type="org.smartfrog.authoringtool.emf.Connectors" upper="2147483647"
   * @generated
   */
  EList getOn();

} // Component
