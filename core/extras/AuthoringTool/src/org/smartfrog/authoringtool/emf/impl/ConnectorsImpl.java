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

import org.smartfrog.authoringtool.emf.Composite;
import org.smartfrog.authoringtool.emf.Connectors;
import org.smartfrog.authoringtool.emf.DependencyModel;
import org.smartfrog.authoringtool.emf.InputDependencyConnection;
import org.smartfrog.authoringtool.emf.OutputDependencyConnection;
import org.smartfrog.authoringtool.emf.SmartfrogPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Connectors</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ConnectorsImpl#getChild_Connector <em>Child Connector</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ConnectorsImpl#getMember_Connector <em>Member Connector</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ConnectorsImpl#getConnector_Dependent_Source <em>Connector Dependent Source</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ConnectorsImpl#getComponent_Depends_On_Connector <em>Component Depends On Connector</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ConnectorsImpl extends ModelObjectImpl implements Connectors
{
  /**
   * The cached value of the '{@link #getChild_Connector() <em>Child Connector</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getChild_Connector()
   * @generated
   * @ordered
   */
  protected Composite child_Connector;

  /**
   * The cached value of the '{@link #getMember_Connector() <em>Member Connector</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMember_Connector()
   * @generated
   * @ordered
   */
  protected DependencyModel member_Connector;

  /**
   * The cached value of the '{@link #getConnector_Dependent_Source() <em>Connector Dependent Source</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConnector_Dependent_Source()
   * @generated
   * @ordered
   */
  protected EList connector_Dependent_Source;

  /**
   * The cached value of the '{@link #getComponent_Depends_On_Connector() <em>Component Depends On Connector</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComponent_Depends_On_Connector()
   * @generated
   * @ordered
   */
  protected EList component_Depends_On_Connector;

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
  public Composite getChild_Connector()
  {
    if (child_Connector != null && child_Connector.eIsProxy())
    {
      InternalEObject oldChild_Connector = (InternalEObject)child_Connector;
      child_Connector = (Composite)eResolveProxy(oldChild_Connector);
      if (child_Connector != oldChild_Connector)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.CONNECTORS__CHILD_CONNECTOR, oldChild_Connector, child_Connector));
      }
    }
    return child_Connector;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composite basicGetChild_Connector()
  {
    return child_Connector;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setChild_Connector(Composite newChild_Connector)
  {
    Composite oldChild_Connector = child_Connector;
    child_Connector = newChild_Connector;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.CONNECTORS__CHILD_CONNECTOR, oldChild_Connector, child_Connector));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel getMember_Connector()
  {
    if (member_Connector != null && member_Connector.eIsProxy())
    {
      InternalEObject oldMember_Connector = (InternalEObject)member_Connector;
      member_Connector = (DependencyModel)eResolveProxy(oldMember_Connector);
      if (member_Connector != oldMember_Connector)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.CONNECTORS__MEMBER_CONNECTOR, oldMember_Connector, member_Connector));
      }
    }
    return member_Connector;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel basicGetMember_Connector()
  {
    return member_Connector;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setMember_Connector(DependencyModel newMember_Connector)
  {
    DependencyModel oldMember_Connector = member_Connector;
    member_Connector = newMember_Connector;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.CONNECTORS__MEMBER_CONNECTOR, oldMember_Connector, member_Connector));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getConnector_Dependent_Source()
  {
    if (connector_Dependent_Source == null)
    {
      connector_Dependent_Source = new EObjectResolvingEList(OutputDependencyConnection.class, this, SmartfrogPackage.CONNECTORS__CONNECTOR_DEPENDENT_SOURCE);
    }
    return connector_Dependent_Source;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getComponent_Depends_On_Connector()
  {
    if (component_Depends_On_Connector == null)
    {
      component_Depends_On_Connector = new EObjectResolvingEList(InputDependencyConnection.class, this, SmartfrogPackage.CONNECTORS__COMPONENT_DEPENDS_ON_CONNECTOR);
    }
    return component_Depends_On_Connector;
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
      case SmartfrogPackage.CONNECTORS__CHILD_CONNECTOR:
        if (resolve) return getChild_Connector();
        return basicGetChild_Connector();
      case SmartfrogPackage.CONNECTORS__MEMBER_CONNECTOR:
        if (resolve) return getMember_Connector();
        return basicGetMember_Connector();
      case SmartfrogPackage.CONNECTORS__CONNECTOR_DEPENDENT_SOURCE:
        return getConnector_Dependent_Source();
      case SmartfrogPackage.CONNECTORS__COMPONENT_DEPENDS_ON_CONNECTOR:
        return getComponent_Depends_On_Connector();
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
      case SmartfrogPackage.CONNECTORS__CHILD_CONNECTOR:
        setChild_Connector((Composite)newValue);
        return;
      case SmartfrogPackage.CONNECTORS__MEMBER_CONNECTOR:
        setMember_Connector((DependencyModel)newValue);
        return;
      case SmartfrogPackage.CONNECTORS__CONNECTOR_DEPENDENT_SOURCE:
        getConnector_Dependent_Source().clear();
        getConnector_Dependent_Source().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.CONNECTORS__COMPONENT_DEPENDS_ON_CONNECTOR:
        getComponent_Depends_On_Connector().clear();
        getComponent_Depends_On_Connector().addAll((Collection)newValue);
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
      case SmartfrogPackage.CONNECTORS__CHILD_CONNECTOR:
        setChild_Connector((Composite)null);
        return;
      case SmartfrogPackage.CONNECTORS__MEMBER_CONNECTOR:
        setMember_Connector((DependencyModel)null);
        return;
      case SmartfrogPackage.CONNECTORS__CONNECTOR_DEPENDENT_SOURCE:
        getConnector_Dependent_Source().clear();
        return;
      case SmartfrogPackage.CONNECTORS__COMPONENT_DEPENDS_ON_CONNECTOR:
        getComponent_Depends_On_Connector().clear();
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
      case SmartfrogPackage.CONNECTORS__CHILD_CONNECTOR:
        return child_Connector != null;
      case SmartfrogPackage.CONNECTORS__MEMBER_CONNECTOR:
        return member_Connector != null;
      case SmartfrogPackage.CONNECTORS__CONNECTOR_DEPENDENT_SOURCE:
        return connector_Dependent_Source != null && !connector_Dependent_Source.isEmpty();
      case SmartfrogPackage.CONNECTORS__COMPONENT_DEPENDS_ON_CONNECTOR:
        return component_Depends_On_Connector != null && !component_Depends_On_Connector.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //ConnectorsImpl
