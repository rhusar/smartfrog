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
import org.smartfrog.authoringtool.emf.Composit;
import org.smartfrog.authoringtool.emf.Connectors;
import org.smartfrog.authoringtool.emf.DependencyModel;
import org.smartfrog.authoringtool.emf.SmartfrogPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Composit</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositImpl#getSuperComposit <em>Super Composit</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositImpl#getComponents <em>Components</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositImpl#getCompos <em>Compos</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositImpl#getCompo <em>Compo</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositImpl#getChileComposit <em>Chile Composit</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositImpl#getComposits <em>Composits</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CompositImpl extends ModelObjectImpl implements Composit
{
  /**
   * The cached value of the '{@link #getSuperComposit() <em>Super Composit</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSuperComposit()
   * @generated
   * @ordered
   */
  protected EList superComposit;

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
   * The cached value of the '{@link #getChileComposit() <em>Chile Composit</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getChileComposit()
   * @generated
   * @ordered
   */
  protected Composit chileComposit;

  /**
   * The cached value of the '{@link #getComposits() <em>Composits</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComposits()
   * @generated
   * @ordered
   */
  protected DependencyModel composits;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CompositImpl()
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
    return SmartfrogPackage.Literals.COMPOSIT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getSuperComposit()
  {
    if (superComposit == null)
    {
      superComposit = new EObjectContainmentEList(Composit.class, this, SmartfrogPackage.COMPOSIT__SUPER_COMPOSIT);
    }
    return superComposit;
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
      components = new EObjectContainmentEList(Component.class, this, SmartfrogPackage.COMPOSIT__COMPONENTS);
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
      compos = new EObjectContainmentEList(Attribute.class, this, SmartfrogPackage.COMPOSIT__COMPOS);
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
      compo = new EObjectContainmentEList(Connectors.class, this, SmartfrogPackage.COMPOSIT__COMPO);
    }
    return compo;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composit getChileComposit()
  {
    if (chileComposit != null && chileComposit.eIsProxy())
    {
      InternalEObject oldChileComposit = (InternalEObject)chileComposit;
      chileComposit = (Composit)eResolveProxy(oldChileComposit);
      if (chileComposit != oldChileComposit)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.COMPOSIT__CHILE_COMPOSIT, oldChileComposit, chileComposit));
      }
    }
    return chileComposit;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composit basicGetChileComposit()
  {
    return chileComposit;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setChileComposit(Composit newChileComposit)
  {
    Composit oldChileComposit = chileComposit;
    chileComposit = newChileComposit;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.COMPOSIT__CHILE_COMPOSIT, oldChileComposit, chileComposit));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel getComposits()
  {
    if (composits != null && composits.eIsProxy())
    {
      InternalEObject oldComposits = (InternalEObject)composits;
      composits = (DependencyModel)eResolveProxy(oldComposits);
      if (composits != oldComposits)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.COMPOSIT__COMPOSITS, oldComposits, composits));
      }
    }
    return composits;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel basicGetComposits()
  {
    return composits;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setComposits(DependencyModel newComposits)
  {
    DependencyModel oldComposits = composits;
    composits = newComposits;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.COMPOSIT__COMPOSITS, oldComposits, composits));
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
      case SmartfrogPackage.COMPOSIT__SUPER_COMPOSIT:
        return ((InternalEList)getSuperComposit()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.COMPOSIT__COMPONENTS:
        return ((InternalEList)getComponents()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.COMPOSIT__COMPOS:
        return ((InternalEList)getCompos()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.COMPOSIT__COMPO:
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
      case SmartfrogPackage.COMPOSIT__SUPER_COMPOSIT:
        return getSuperComposit();
      case SmartfrogPackage.COMPOSIT__COMPONENTS:
        return getComponents();
      case SmartfrogPackage.COMPOSIT__COMPOS:
        return getCompos();
      case SmartfrogPackage.COMPOSIT__COMPO:
        return getCompo();
      case SmartfrogPackage.COMPOSIT__CHILE_COMPOSIT:
        if (resolve) return getChileComposit();
        return basicGetChileComposit();
      case SmartfrogPackage.COMPOSIT__COMPOSITS:
        if (resolve) return getComposits();
        return basicGetComposits();
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
      case SmartfrogPackage.COMPOSIT__SUPER_COMPOSIT:
        getSuperComposit().clear();
        getSuperComposit().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPOSIT__COMPONENTS:
        getComponents().clear();
        getComponents().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPOSIT__COMPOS:
        getCompos().clear();
        getCompos().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPOSIT__COMPO:
        getCompo().clear();
        getCompo().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPOSIT__CHILE_COMPOSIT:
        setChileComposit((Composit)newValue);
        return;
      case SmartfrogPackage.COMPOSIT__COMPOSITS:
        setComposits((DependencyModel)newValue);
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
      case SmartfrogPackage.COMPOSIT__SUPER_COMPOSIT:
        getSuperComposit().clear();
        return;
      case SmartfrogPackage.COMPOSIT__COMPONENTS:
        getComponents().clear();
        return;
      case SmartfrogPackage.COMPOSIT__COMPOS:
        getCompos().clear();
        return;
      case SmartfrogPackage.COMPOSIT__COMPO:
        getCompo().clear();
        return;
      case SmartfrogPackage.COMPOSIT__CHILE_COMPOSIT:
        setChileComposit((Composit)null);
        return;
      case SmartfrogPackage.COMPOSIT__COMPOSITS:
        setComposits((DependencyModel)null);
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
      case SmartfrogPackage.COMPOSIT__SUPER_COMPOSIT:
        return superComposit != null && !superComposit.isEmpty();
      case SmartfrogPackage.COMPOSIT__COMPONENTS:
        return components != null && !components.isEmpty();
      case SmartfrogPackage.COMPOSIT__COMPOS:
        return compos != null && !compos.isEmpty();
      case SmartfrogPackage.COMPOSIT__COMPO:
        return compo != null && !compo.isEmpty();
      case SmartfrogPackage.COMPOSIT__CHILE_COMPOSIT:
        return chileComposit != null;
      case SmartfrogPackage.COMPOSIT__COMPOSITS:
        return composits != null;
    }
    return super.eIsSet(featureID);
  }

} //CompositImpl
