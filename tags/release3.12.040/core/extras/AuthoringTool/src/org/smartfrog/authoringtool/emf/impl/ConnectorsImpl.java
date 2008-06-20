/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.smartfrog.authoringtool.emf.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.smartfrog.authoringtool.emf.Component;
import org.smartfrog.authoringtool.emf.Composite;
import org.smartfrog.authoringtool.emf.Connectors;
import org.smartfrog.authoringtool.emf.DependencyModel;
import org.smartfrog.authoringtool.emf.SmartfrogPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Connectors</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ConnectorsImpl#getDepConnector <em>Dep Connector</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ConnectorsImpl#getGenDepConnector <em>Gen Dep Connector</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ConnectorsImpl#getBy <em>By</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ConnectorsImpl#getOn <em>On</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ConnectorsImpl extends ModelObjectImpl implements Connectors
{
  /**
   * The cached value of the '{@link #getDepConnector() <em>Dep Connector</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDepConnector()
   * @generated
   * @ordered
   */
  protected Composite depConnector;

  /**
   * The cached value of the '{@link #getGenDepConnector() <em>Gen Dep Connector</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGenDepConnector()
   * @generated
   * @ordered
   */
  protected DependencyModel genDepConnector;

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
  protected ConnectorsImpl()
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
    return SmartfrogPackage.Literals.CONNECTORS;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composite getDepConnector()
  {
    if (depConnector != null && depConnector.eIsProxy())
    {
      InternalEObject oldDepConnector = (InternalEObject)depConnector;
      depConnector = (Composite)eResolveProxy(oldDepConnector);
      if (depConnector != oldDepConnector)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.CONNECTORS__DEP_CONNECTOR, oldDepConnector, depConnector));
      }
    }
    return depConnector;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composite basicGetDepConnector()
  {
    return depConnector;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDepConnector(Composite newDepConnector)
  {
    Composite oldDepConnector = depConnector;
    depConnector = newDepConnector;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.CONNECTORS__DEP_CONNECTOR, oldDepConnector, depConnector));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel getGenDepConnector()
  {
    if (genDepConnector != null && genDepConnector.eIsProxy())
    {
      InternalEObject oldGenDepConnector = (InternalEObject)genDepConnector;
      genDepConnector = (DependencyModel)eResolveProxy(oldGenDepConnector);
      if (genDepConnector != oldGenDepConnector)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.CONNECTORS__GEN_DEP_CONNECTOR, oldGenDepConnector, genDepConnector));
      }
    }
    return genDepConnector;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel basicGetGenDepConnector()
  {
    return genDepConnector;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setGenDepConnector(DependencyModel newGenDepConnector)
  {
    DependencyModel oldGenDepConnector = genDepConnector;
    genDepConnector = newGenDepConnector;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.CONNECTORS__GEN_DEP_CONNECTOR, oldGenDepConnector, genDepConnector));
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
      by = new EObjectResolvingEList(Component.class, this, SmartfrogPackage.CONNECTORS__BY);
    }
    return by;
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
      on = new EObjectResolvingEList(Component.class, this, SmartfrogPackage.CONNECTORS__ON);
    }
    return on;
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
      case SmartfrogPackage.CONNECTORS__DEP_CONNECTOR:
        if (resolve) return getDepConnector();
        return basicGetDepConnector();
      case SmartfrogPackage.CONNECTORS__GEN_DEP_CONNECTOR:
        if (resolve) return getGenDepConnector();
        return basicGetGenDepConnector();
      case SmartfrogPackage.CONNECTORS__BY:
        return getBy();
      case SmartfrogPackage.CONNECTORS__ON:
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
      case SmartfrogPackage.CONNECTORS__DEP_CONNECTOR:
        setDepConnector((Composite)newValue);
        return;
      case SmartfrogPackage.CONNECTORS__GEN_DEP_CONNECTOR:
        setGenDepConnector((DependencyModel)newValue);
        return;
      case SmartfrogPackage.CONNECTORS__BY:
        getBy().clear();
        getBy().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.CONNECTORS__ON:
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
      case SmartfrogPackage.CONNECTORS__DEP_CONNECTOR:
        setDepConnector((Composite)null);
        return;
      case SmartfrogPackage.CONNECTORS__GEN_DEP_CONNECTOR:
        setGenDepConnector((DependencyModel)null);
        return;
      case SmartfrogPackage.CONNECTORS__BY:
        getBy().clear();
        return;
      case SmartfrogPackage.CONNECTORS__ON:
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
      case SmartfrogPackage.CONNECTORS__DEP_CONNECTOR:
        return depConnector != null;
      case SmartfrogPackage.CONNECTORS__GEN_DEP_CONNECTOR:
        return genDepConnector != null;
      case SmartfrogPackage.CONNECTORS__BY:
        return by != null && !by.isEmpty();
      case SmartfrogPackage.CONNECTORS__ON:
        return on != null && !on.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //ConnectorsImpl
