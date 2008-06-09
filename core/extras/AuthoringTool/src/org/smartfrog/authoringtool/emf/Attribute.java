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
 *   <li>{@link org.smartfrog.authoringtool.emf.Attribute#getAttri_Name <em>Attri Name</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Attribute#getValue <em>Value</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Attribute#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.Attribute#getComposite_arrtibutes <em>Composite arrtibutes</em>}</li>
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
   * Returns the value of the '<em><b>Attri Name</b></em>' attribute.
   * The default value is <code>"0"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attri Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attri Name</em>' attribute.
   * @see #setAttri_Name(String)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getAttribute_Attri_Name()
   * @model default="0"
   * @generated
   */
  String getAttri_Name();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Attribute#getAttri_Name <em>Attri Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Attri Name</em>' attribute.
   * @see #getAttri_Name()
   * @generated
   */
  void setAttri_Name(String value);

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
   * Returns the value of the '<em><b>Attributes</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attributes</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attributes</em>' reference.
   * @see #setAttributes(Component)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getAttribute_Attributes()
   * @model
   * @generated
   */
  Component getAttributes();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Attribute#getAttributes <em>Attributes</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Attributes</em>' reference.
   * @see #getAttributes()
   * @generated
   */
  void setAttributes(Component value);

  /**
   * Returns the value of the '<em><b>Composite arrtibutes</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Composite arrtibutes</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Composite arrtibutes</em>' reference.
   * @see #setComposite_arrtibutes(Composite)
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#getAttribute_Composite_arrtibutes()
   * @model
   * @generated
   */
  Composite getComposite_arrtibutes();

  /**
   * Sets the value of the '{@link org.smartfrog.authoringtool.emf.Attribute#getComposite_arrtibutes <em>Composite arrtibutes</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Composite arrtibutes</em>' reference.
   * @see #getComposite_arrtibutes()
   * @generated
   */
  void setComposite_arrtibutes(Composite value);

} // Attribute
