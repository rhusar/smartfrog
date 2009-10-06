/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.smartfrog.authoringtool.emf;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attribute</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.Attribute#getValue <em>Value</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Attribute#isStateData <em>State Data</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Attribute#isStateListen <em>State Listen</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Attribute#isStateNotify <em>State Notify</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Attribute#isIsLazyValue <em>Is Lazy Value</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Attribute#getComponent_Attribute <em>Component Attribute</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Attribute#getComposite_Arrtibute <em>Composite Arrtibute</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getAttribute()
 * @model
 * @generated
 */
public interface Attribute extends ModelObject
{
  /**
   * Returns the value of the '<em><b>Value</b></em>' attribute.
   * The default value is <code>"0"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Value</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Value</em>' attribute.
   * @see #setValue(String)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getAttribute_Value()
   * @model default="0"
   * @generated
   */
  String getValue();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Attribute#getValue <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Value</em>' attribute.
   * @see #getValue()
   * @generated
   */
  void setValue(String value);

  /**
   * Returns the value of the '<em><b>State Data</b></em>' attribute.
   * The default value is <code>"false"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>State Data</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>State Data</em>' attribute.
   * @see #setStateData(boolean)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getAttribute_StateData()
   * @model default="false"
   * @generated
   */
  boolean isStateData();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Attribute#isStateData <em>State Data</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>State Data</em>' attribute.
   * @see #isStateData()
   * @generated
   */
  void setStateData(boolean value);

  /**
   * Returns the value of the '<em><b>State Listen</b></em>' attribute.
   * The default value is <code>"false"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>State Listen</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>State Listen</em>' attribute.
   * @see #setStateListen(boolean)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getAttribute_StateListen()
   * @model default="false"
   * @generated
   */
  boolean isStateListen();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Attribute#isStateListen <em>State Listen</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>State Listen</em>' attribute.
   * @see #isStateListen()
   * @generated
   */
  void setStateListen(boolean value);

  /**
   * Returns the value of the '<em><b>State Notify</b></em>' attribute.
   * The default value is <code>"false"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>State Notify</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>State Notify</em>' attribute.
   * @see #setStateNotify(boolean)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getAttribute_StateNotify()
   * @model default="false"
   * @generated
   */
  boolean isStateNotify();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Attribute#isStateNotify <em>State Notify</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>State Notify</em>' attribute.
   * @see #isStateNotify()
   * @generated
   */
  void setStateNotify(boolean value);

  /**
   * Returns the value of the '<em><b>Is Lazy Value</b></em>' attribute.
   * The default value is <code>"false"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Is Lazy Value</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Is Lazy Value</em>' attribute.
   * @see #setIsLazyValue(boolean)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getAttribute_IsLazyValue()
   * @model default="false"
   * @generated
   */
  boolean isIsLazyValue();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Attribute#isIsLazyValue <em>Is Lazy Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Is Lazy Value</em>' attribute.
   * @see #isIsLazyValue()
   * @generated
   */
  void setIsLazyValue(boolean value);

  /**
   * Returns the value of the '<em><b>Component Attribute</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Component Attribute</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Component Attribute</em>' reference.
   * @see #setComponent_Attribute(Component)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getAttribute_Component_Attribute()
   * @model
   * @generated
   */
  Component getComponent_Attribute();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Attribute#getComponent_Attribute <em>Component Attribute</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Component Attribute</em>' reference.
   * @see #getComponent_Attribute()
   * @generated
   */
  void setComponent_Attribute(Component value);

  /**
   * Returns the value of the '<em><b>Composite Arrtibute</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Composite Arrtibute</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Composite Arrtibute</em>' reference.
   * @see #setComposite_Arrtibute(Composite)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getAttribute_Composite_Arrtibute()
   * @model
   * @generated
   */
  Composite getComposite_Arrtibute();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Attribute#getComposite_Arrtibute <em>Composite Arrtibute</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Composite Arrtibute</em>' reference.
   * @see #getComposite_Arrtibute()
   * @generated
   */
  void setComposite_Arrtibute(Composite value);

} // Attribute
