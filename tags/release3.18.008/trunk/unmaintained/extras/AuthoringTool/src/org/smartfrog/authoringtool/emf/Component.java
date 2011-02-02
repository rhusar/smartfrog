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
 * A representation of the model object '<em><b>Component</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getExtends <em>Extends</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#isIsAbstract <em>Is Abstract</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getComponent_Class <em>Component Class</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getComponent_Attribute_Container <em>Component Attribute Container</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getChild_Components <em>Child Components</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getModel_Member_Components <em>Model Member Components</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getSimple_Dependent_Source <em>Simple Dependent Source</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getComponent_Dependent_Source <em>Component Dependent Source</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getSimple_Depend_On <em>Simple Depend On</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Component#getConnector_Depend_On_Component <em>Connector Depend On Component</em>}</li>
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
   * Returns the value of the '<em><b>Extends</b></em>' attribute.
   * The default value is <code>"0"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Extends</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Extends</em>' attribute.
   * @see #setExtends(String)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_Extends()
   * @model default="0"
   * @generated
   */
  String getExtends();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Component#getExtends <em>Extends</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Extends</em>' attribute.
   * @see #getExtends()
   * @generated
   */
  void setExtends(String value);

  /**
   * Returns the value of the '<em><b>Is Abstract</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Is Abstract</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Is Abstract</em>' attribute.
   * @see #setIsAbstract(boolean)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_IsAbstract()
   * @model default="true"
   * @generated
   */
  boolean isIsAbstract();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Component#isIsAbstract <em>Is Abstract</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Is Abstract</em>' attribute.
   * @see #isIsAbstract()
   * @generated
   */
  void setIsAbstract(boolean value);

  /**
   * Returns the value of the '<em><b>Component Class</b></em>' attribute.
   * The default value is <code>"null"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Component Class</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Component Class</em>' attribute.
   * @see #setComponent_Class(String)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_Component_Class()
   * @model default="null"
   * @generated
   */
  String getComponent_Class();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Component#getComponent_Class <em>Component Class</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Component Class</em>' attribute.
   * @see #getComponent_Class()
   * @generated
   */
  void setComponent_Class(String value);

  /**
   * Returns the value of the '<em><b>Component Attribute Container</b></em>' containment reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.Attribute}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Component Attribute Container</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Component Attribute Container</em>' containment reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_Component_Attribute_Container()
   * @model type="org.smartfrog.authoringtool.emf.Attribute" containment="true" upper="2000"
   * @generated
   */
  EList getComponent_Attribute_Container();

  /**
   * Returns the value of the '<em><b>Child Components</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Child Components</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Child Components</em>' reference.
   * @see #setChild_Components(Composite)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_Child_Components()
   * @model
   * @generated
   */
  Composite getChild_Components();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Component#getChild_Components <em>Child Components</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Child Components</em>' reference.
   * @see #getChild_Components()
   * @generated
   */
  void setChild_Components(Composite value);

  /**
   * Returns the value of the '<em><b>Model Member Components</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Model Member Components</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Model Member Components</em>' reference.
   * @see #setModel_Member_Components(DependencyModel)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_Model_Member_Components()
   * @model
   * @generated
   */
  DependencyModel getModel_Member_Components();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Component#getModel_Member_Components <em>Model Member Components</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Model Member Components</em>' reference.
   * @see #getModel_Member_Components()
   * @generated
   */
  void setModel_Member_Components(DependencyModel value);

  /**
   * Returns the value of the '<em><b>Simple Dependent Source</b></em>' reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.SimpleDependencyConnection}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Simple Dependent Source</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Simple Dependent Source</em>' reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_Simple_Dependent_Source()
   * @model type="org.smartfrog.authoringtool.emf.SimpleDependencyConnection" upper="2147483647"
   * @generated
   */
  EList getSimple_Dependent_Source();

  /**
   * Returns the value of the '<em><b>Component Dependent Source</b></em>' reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.InputDependencyConnection}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Component Dependent Source</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Component Dependent Source</em>' reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_Component_Dependent_Source()
   * @model type="org.smartfrog.authoringtool.emf.InputDependencyConnection" upper="2147483647"
   * @generated
   */
  EList getComponent_Dependent_Source();

  /**
   * Returns the value of the '<em><b>Simple Depend On</b></em>' reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.SimpleDependencyConnection}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Simple Depend On</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Simple Depend On</em>' reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_Simple_Depend_On()
   * @model type="org.smartfrog.authoringtool.emf.SimpleDependencyConnection" upper="2147483647"
   * @generated
   */
  EList getSimple_Depend_On();

  /**
   * Returns the value of the '<em><b>Connector Depend On Component</b></em>' reference list.
   * The list contents are of type {@link org.smartfrog.authoringtool.emf.OutputDependencyConnection}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Connector Depend On Component</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Connector Depend On Component</em>' reference list.
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getComponent_Connector_Depend_On_Component()
   * @model type="org.smartfrog.authoringtool.emf.OutputDependencyConnection" upper="2147483647"
   * @generated
   */
  EList getConnector_Depend_On_Component();

} // Component
