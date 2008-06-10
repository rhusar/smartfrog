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
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.smartfrog.authoringtool.emf.Attribute;
import org.smartfrog.authoringtool.emf.Component;
import org.smartfrog.authoringtool.emf.Composite;
import org.smartfrog.authoringtool.emf.Connectors;
import org.smartfrog.authoringtool.emf.DependencyModel;
import org.smartfrog.authoringtool.emf.SimpleDependencyConnection;
import org.smartfrog.authoringtool.emf.SmartfrogPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Component</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getComp <em>Comp</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getGroup_of_components <em>Group of components</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getComps <em>Comps</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getDepends_By <em>Depends By</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getBy <em>By</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getDependOn <em>Depend On</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getOn <em>On</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ComponentImpl extends ModelObjectImpl implements Component
{
  /**
   * The cached value of the '{@link #getComp() <em>Comp</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComp()
   * @generated
   * @ordered
   */
  protected EList comp;

  /**
   * The cached value of the '{@link #getGroup_of_components() <em>Group of components</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGroup_of_components()
   * @generated
   * @ordered
   */
  protected Composite group_of_components;

  /**
   * The cached value of the '{@link #getComps() <em>Comps</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComps()
   * @generated
   * @ordered
   */
  protected DependencyModel comps;

  /**
   * The cached value of the '{@link #getDepends_By() <em>Depends By</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDepends_By()
   * @generated
   * @ordered
   */
  protected EList depends_By;

  /**
   * The cached value of the '{@link #getBy() <em>By</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBy()
   * @generated
   * @ordered
   */
  protected EList by;

  /**
   * The cached value of the '{@link #getDependOn() <em>Depend On</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDependOn()
   * @generated
   * @ordered
   */
  protected SimpleDependencyConnection dependOn;

  /**
   * The cached value of the '{@link #getOn() <em>On</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOn()
   * @generated
   * @ordered
   */
  protected EList on;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ComponentImpl()
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
    return SmartfrogPackage.Literals.COMPONENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getComp()
  {
    if (comp == null)
    {
      comp = new EObjectContainmentEList(Attribute.class, this, SmartfrogPackage.COMPONENT__COMP);
    }
    return comp;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composite getGroup_of_components()
  {
    if (group_of_components != null && group_of_components.eIsProxy())
    {
      InternalEObject oldGroup_of_components = (InternalEObject)group_of_components;
      group_of_components = (Composite)eResolveProxy(oldGroup_of_components);
      if (group_of_components != oldGroup_of_components)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.COMPONENT__GROUP_OF_COMPONENTS, oldGroup_of_components, group_of_components));
      }
    }
    return group_of_components;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composite basicGetGroup_of_components()
  {
    return group_of_components;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setGroup_of_components(Composite newGroup_of_components)
  {
    Composite oldGroup_of_components = group_of_components;
    group_of_components = newGroup_of_components;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.COMPONENT__GROUP_OF_COMPONENTS, oldGroup_of_components, group_of_components));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel getComps()
  {
    if (comps != null && comps.eIsProxy())
    {
      InternalEObject oldComps = (InternalEObject)comps;
      comps = (DependencyModel)eResolveProxy(oldComps);
      if (comps != oldComps)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.COMPONENT__COMPS, oldComps, comps));
      }
    }
    return comps;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel basicGetComps()
  {
    return comps;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setComps(DependencyModel newComps)
  {
    DependencyModel oldComps = comps;
    comps = newComps;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.COMPONENT__COMPS, oldComps, comps));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getDepends_By()
  {
    if (depends_By == null)
    {
      depends_By = new EObjectResolvingEList(SimpleDependencyConnection.class, this, SmartfrogPackage.COMPONENT__DEPENDS_BY);
    }
    return depends_By;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getBy()
  {
    if (by == null)
    {
      by = new EObjectResolvingEList(Connectors.class, this, SmartfrogPackage.COMPONENT__BY);
    }
    return by;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SimpleDependencyConnection getDependOn()
  {
    if (dependOn != null && dependOn.eIsProxy())
    {
      InternalEObject oldDependOn = (InternalEObject)dependOn;
      dependOn = (SimpleDependencyConnection)eResolveProxy(oldDependOn);
      if (dependOn != oldDependOn)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.COMPONENT__DEPEND_ON, oldDependOn, dependOn));
      }
    }
    return dependOn;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SimpleDependencyConnection basicGetDependOn()
  {
    return dependOn;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDependOn(SimpleDependencyConnection newDependOn)
  {
    SimpleDependencyConnection oldDependOn = dependOn;
    dependOn = newDependOn;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.COMPONENT__DEPEND_ON, oldDependOn, dependOn));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getOn()
  {
    if (on == null)
    {
      on = new EObjectResolvingEList(Connectors.class, this, SmartfrogPackage.COMPONENT__ON);
    }
    return on;
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
      case SmartfrogPackage.COMPONENT__COMP:
        return ((InternalEList)getComp()).basicRemove(otherEnd, msgs);
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
      case SmartfrogPackage.COMPONENT__COMP:
        return getComp();
      case SmartfrogPackage.COMPONENT__GROUP_OF_COMPONENTS:
        if (resolve) return getGroup_of_components();
        return basicGetGroup_of_components();
      case SmartfrogPackage.COMPONENT__COMPS:
        if (resolve) return getComps();
        return basicGetComps();
      case SmartfrogPackage.COMPONENT__DEPENDS_BY:
        return getDepends_By();
      case SmartfrogPackage.COMPONENT__BY:
        return getBy();
      case SmartfrogPackage.COMPONENT__DEPEND_ON:
        if (resolve) return getDependOn();
        return basicGetDependOn();
      case SmartfrogPackage.COMPONENT__ON:
        return getOn();
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
      case SmartfrogPackage.COMPONENT__COMP:
        getComp().clear();
        getComp().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPONENT__GROUP_OF_COMPONENTS:
        setGroup_of_components((Composite)newValue);
        return;
      case SmartfrogPackage.COMPONENT__COMPS:
        setComps((DependencyModel)newValue);
        return;
      case SmartfrogPackage.COMPONENT__DEPENDS_BY:
        getDepends_By().clear();
        getDepends_By().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPONENT__BY:
        getBy().clear();
        getBy().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPONENT__DEPEND_ON:
        setDependOn((SimpleDependencyConnection)newValue);
        return;
      case SmartfrogPackage.COMPONENT__ON:
        getOn().clear();
        getOn().addAll((Collection)newValue);
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
      case SmartfrogPackage.COMPONENT__COMP:
        getComp().clear();
        return;
      case SmartfrogPackage.COMPONENT__GROUP_OF_COMPONENTS:
        setGroup_of_components((Composite)null);
        return;
      case SmartfrogPackage.COMPONENT__COMPS:
        setComps((DependencyModel)null);
        return;
      case SmartfrogPackage.COMPONENT__DEPENDS_BY:
        getDepends_By().clear();
        return;
      case SmartfrogPackage.COMPONENT__BY:
        getBy().clear();
        return;
      case SmartfrogPackage.COMPONENT__DEPEND_ON:
        setDependOn((SimpleDependencyConnection)null);
        return;
      case SmartfrogPackage.COMPONENT__ON:
        getOn().clear();
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
      case SmartfrogPackage.COMPONENT__COMP:
        return comp != null && !comp.isEmpty();
      case SmartfrogPackage.COMPONENT__GROUP_OF_COMPONENTS:
        return group_of_components != null;
      case SmartfrogPackage.COMPONENT__COMPS:
        return comps != null;
      case SmartfrogPackage.COMPONENT__DEPENDS_BY:
        return depends_By != null && !depends_By.isEmpty();
      case SmartfrogPackage.COMPONENT__BY:
        return by != null && !by.isEmpty();
      case SmartfrogPackage.COMPONENT__DEPEND_ON:
        return dependOn != null;
      case SmartfrogPackage.COMPONENT__ON:
        return on != null && !on.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //ComponentImpl
