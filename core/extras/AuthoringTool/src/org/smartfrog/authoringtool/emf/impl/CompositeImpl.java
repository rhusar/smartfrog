/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.smartfrog.authoringtool.emf.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.smartfrog.authoringtool.emf.Attribute;
import org.smartfrog.authoringtool.emf.Component;
import org.smartfrog.authoringtool.emf.Composite;
import org.smartfrog.authoringtool.emf.Connectors;
import org.smartfrog.authoringtool.emf.DependencyModel;
import org.smartfrog.authoringtool.emf.SmartfrogPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Composite</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositeImpl#getSuperComposite <em>Super Composite</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositeImpl#getComponents <em>Components</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositeImpl#getCompos <em>Compos</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositeImpl#getCompo <em>Compo</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositeImpl#getChildComposite <em>Child Composite</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositeImpl#getComposites <em>Composites</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CompositeImpl extends ModelObjectImpl implements Composite
{
  /**
   * The cached value of the '{@link #getSuperComposite() <em>Super Composite</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSuperComposite()
   * @generated
   * @ordered
   */
  protected EList superComposite;

  /**
   * The cached value of the '{@link #getComponents() <em>Components</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComponents()
   * @generated
   * @ordered
   */
  protected EList components;

  /**
   * The cached value of the '{@link #getCompos() <em>Compos</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCompos()
   * @generated
   * @ordered
   */
  protected EList compos;

  /**
   * The cached value of the '{@link #getCompo() <em>Compo</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCompo()
   * @generated
   * @ordered
   */
  protected EList compo;

  /**
   * The cached value of the '{@link #getChildComposite() <em>Child Composite</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getChildComposite()
   * @generated
   * @ordered
   */
  protected Composite childComposite;

  /**
   * The cached value of the '{@link #getComposites() <em>Composites</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComposites()
   * @generated
   * @ordered
   */
  protected DependencyModel composites;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CompositeImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EClass eStaticClass()
  {
    return SmartfrogPackage.Literals.COMPOSITE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getSuperComposite()
  {
    if (superComposite == null)
    {
      superComposite = new EObjectContainmentEList(Composite.class, this, SmartfrogPackage.COMPOSITE__SUPER_COMPOSITE);
    }
    return superComposite;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getComponents()
  {
    if (components == null)
    {
      components = new EObjectContainmentEList(Component.class, this, SmartfrogPackage.COMPOSITE__COMPONENTS);
    }
    return components;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getCompos()
  {
    if (compos == null)
    {
      compos = new EObjectContainmentEList(Attribute.class, this, SmartfrogPackage.COMPOSITE__COMPOS);
    }
    return compos;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getCompo()
  {
    if (compo == null)
    {
      compo = new EObjectContainmentEList(Connectors.class, this, SmartfrogPackage.COMPOSITE__COMPO);
    }
    return compo;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composite getChildComposite()
  {
    if (childComposite != null && childComposite.eIsProxy())
    {
      InternalEObject oldChildComposite = (InternalEObject)childComposite;
      childComposite = (Composite)eResolveProxy(oldChildComposite);
      if (childComposite != oldChildComposite)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.COMPOSITE__CHILD_COMPOSITE, oldChildComposite, childComposite));
      }
    }
    return childComposite;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composite basicGetChildComposite()
  {
    return childComposite;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setChildComposite(Composite newChildComposite)
  {
    Composite oldChildComposite = childComposite;
    childComposite = newChildComposite;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.COMPOSITE__CHILD_COMPOSITE, oldChildComposite, childComposite));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel getComposites()
  {
    if (composites != null && composites.eIsProxy())
    {
      InternalEObject oldComposites = (InternalEObject)composites;
      composites = (DependencyModel)eResolveProxy(oldComposites);
      if (composites != oldComposites)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.COMPOSITE__COMPOSITES, oldComposites, composites));
      }
    }
    return composites;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel basicGetComposites()
  {
    return composites;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setComposites(DependencyModel newComposites)
  {
    DependencyModel oldComposites = composites;
    composites = newComposites;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.COMPOSITE__COMPOSITES, oldComposites, composites));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case SmartfrogPackage.COMPOSITE__SUPER_COMPOSITE:
        return ((InternalEList)getSuperComposite()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.COMPOSITE__COMPONENTS:
        return ((InternalEList)getComponents()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.COMPOSITE__COMPOS:
        return ((InternalEList)getCompos()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.COMPOSITE__COMPO:
        return ((InternalEList)getCompo()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case SmartfrogPackage.COMPOSITE__SUPER_COMPOSITE:
        return getSuperComposite();
      case SmartfrogPackage.COMPOSITE__COMPONENTS:
        return getComponents();
      case SmartfrogPackage.COMPOSITE__COMPOS:
        return getCompos();
      case SmartfrogPackage.COMPOSITE__COMPO:
        return getCompo();
      case SmartfrogPackage.COMPOSITE__CHILD_COMPOSITE:
        if (resolve) return getChildComposite();
        return basicGetChildComposite();
      case SmartfrogPackage.COMPOSITE__COMPOSITES:
        if (resolve) return getComposites();
        return basicGetComposites();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case SmartfrogPackage.COMPOSITE__SUPER_COMPOSITE:
        getSuperComposite().clear();
        getSuperComposite().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPOSITE__COMPONENTS:
        getComponents().clear();
        getComponents().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPOSITE__COMPOS:
        getCompos().clear();
        getCompos().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPOSITE__COMPO:
        getCompo().clear();
        getCompo().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPOSITE__CHILD_COMPOSITE:
        setChildComposite((Composite)newValue);
        return;
      case SmartfrogPackage.COMPOSITE__COMPOSITES:
        setComposites((DependencyModel)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case SmartfrogPackage.COMPOSITE__SUPER_COMPOSITE:
        getSuperComposite().clear();
        return;
      case SmartfrogPackage.COMPOSITE__COMPONENTS:
        getComponents().clear();
        return;
      case SmartfrogPackage.COMPOSITE__COMPOS:
        getCompos().clear();
        return;
      case SmartfrogPackage.COMPOSITE__COMPO:
        getCompo().clear();
        return;
      case SmartfrogPackage.COMPOSITE__CHILD_COMPOSITE:
        setChildComposite((Composite)null);
        return;
      case SmartfrogPackage.COMPOSITE__COMPOSITES:
        setComposites((DependencyModel)null);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case SmartfrogPackage.COMPOSITE__SUPER_COMPOSITE:
        return superComposite != null && !superComposite.isEmpty();
      case SmartfrogPackage.COMPOSITE__COMPONENTS:
        return components != null && !components.isEmpty();
      case SmartfrogPackage.COMPOSITE__COMPOS:
        return compos != null && !compos.isEmpty();
      case SmartfrogPackage.COMPOSITE__COMPO:
        return compo != null && !compo.isEmpty();
      case SmartfrogPackage.COMPOSITE__CHILD_COMPOSITE:
        return childComposite != null;
      case SmartfrogPackage.COMPOSITE__COMPOSITES:
        return composites != null;
    }
    return super.eIsSet(featureID);
  }

} //CompositeImpl
