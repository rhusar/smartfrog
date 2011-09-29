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
import org.smartfrog.authoringtool.emf.DependencyModel;
import org.smartfrog.authoringtool.emf.InputDependencyConnection;
import org.smartfrog.authoringtool.emf.OutputDependencyConnection;
import org.smartfrog.authoringtool.emf.SimpleDependencyConnection;
import org.smartfrog.authoringtool.emf.SmartfrogPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Component</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getExtends <em>Extends</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getComponent_Attribute_Container <em>Component Attribute Container</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getChild_Components <em>Child Components</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getModel_Member_Components <em>Model Member Components</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getSimple_Dependent_Source <em>Simple Dependent Source</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getComponent_Dependent_Source <em>Component Dependent Source</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getSimple_Depend_On <em>Simple Depend On</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl#getConnector_Depend_On_Component <em>Connector Depend On Component</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ComponentImpl extends ModelObjectImpl implements Component
{
  /**
   * The default value of the '{@link #getExtends() <em>Extends</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExtends()
   * @generated
   * @ordered
   */
  protected static final String EXTENDS_EDEFAULT = "0";

  /**
   * The cached value of the '{@link #getExtends() <em>Extends</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExtends()
   * @generated
   * @ordered
   */
  protected String extends_ = EXTENDS_EDEFAULT;

  /**
   * The cached value of the '{@link #getComponent_Attribute_Container() <em>Component Attribute Container</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComponent_Attribute_Container()
   * @generated
   * @ordered
   */
  protected EList component_Attribute_Container;

  /**
   * The cached value of the '{@link #getChild_Components() <em>Child Components</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getChild_Components()
   * @generated
   * @ordered
   */
  protected Composite child_Components;

  /**
   * The cached value of the '{@link #getModel_Member_Components() <em>Model Member Components</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getModel_Member_Components()
   * @generated
   * @ordered
   */
  protected DependencyModel model_Member_Components;

  /**
   * The cached value of the '{@link #getSimple_Dependent_Source() <em>Simple Dependent Source</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSimple_Dependent_Source()
   * @generated
   * @ordered
   */
  protected EList simple_Dependent_Source;

  /**
   * The cached value of the '{@link #getComponent_Dependent_Source() <em>Component Dependent Source</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComponent_Dependent_Source()
   * @generated
   * @ordered
   */
  protected EList component_Dependent_Source;

  /**
   * The cached value of the '{@link #getSimple_Depend_On() <em>Simple Depend On</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSimple_Depend_On()
   * @generated
   * @ordered
   */
  protected SimpleDependencyConnection simple_Depend_On;

  /**
   * The cached value of the '{@link #getConnector_Depend_On_Component() <em>Connector Depend On Component</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConnector_Depend_On_Component()
   * @generated
   * @ordered
   */
  protected EList connector_Depend_On_Component;

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
  public String getExtends()
  {
    return extends_;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setExtends(String newExtends)
  {
    String oldExtends = extends_;
    extends_ = newExtends;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.COMPONENT__EXTENDS, oldExtends, extends_));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getComponent_Attribute_Container()
  {
    if (component_Attribute_Container == null)
    {
      component_Attribute_Container = new EObjectContainmentEList(Attribute.class, this, SmartfrogPackage.COMPONENT__COMPONENT_ATTRIBUTE_CONTAINER);
    }
    return component_Attribute_Container;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composite getChild_Components()
  {
    if (child_Components != null && child_Components.eIsProxy())
    {
      InternalEObject oldChild_Components = (InternalEObject)child_Components;
      child_Components = (Composite)eResolveProxy(oldChild_Components);
      if (child_Components != oldChild_Components)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.COMPONENT__CHILD_COMPONENTS, oldChild_Components, child_Components));
      }
    }
    return child_Components;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composite basicGetChild_Components()
  {
    return child_Components;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setChild_Components(Composite newChild_Components)
  {
    Composite oldChild_Components = child_Components;
    child_Components = newChild_Components;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.COMPONENT__CHILD_COMPONENTS, oldChild_Components, child_Components));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel getModel_Member_Components()
  {
    if (model_Member_Components != null && model_Member_Components.eIsProxy())
    {
      InternalEObject oldModel_Member_Components = (InternalEObject)model_Member_Components;
      model_Member_Components = (DependencyModel)eResolveProxy(oldModel_Member_Components);
      if (model_Member_Components != oldModel_Member_Components)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.COMPONENT__MODEL_MEMBER_COMPONENTS, oldModel_Member_Components, model_Member_Components));
      }
    }
    return model_Member_Components;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel basicGetModel_Member_Components()
  {
    return model_Member_Components;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setModel_Member_Components(DependencyModel newModel_Member_Components)
  {
    DependencyModel oldModel_Member_Components = model_Member_Components;
    model_Member_Components = newModel_Member_Components;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.COMPONENT__MODEL_MEMBER_COMPONENTS, oldModel_Member_Components, model_Member_Components));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getSimple_Dependent_Source()
  {
    if (simple_Dependent_Source == null)
    {
      simple_Dependent_Source = new EObjectResolvingEList(SimpleDependencyConnection.class, this, SmartfrogPackage.COMPONENT__SIMPLE_DEPENDENT_SOURCE);
    }
    return simple_Dependent_Source;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getComponent_Dependent_Source()
  {
    if (component_Dependent_Source == null)
    {
      component_Dependent_Source = new EObjectResolvingEList(InputDependencyConnection.class, this, SmartfrogPackage.COMPONENT__COMPONENT_DEPENDENT_SOURCE);
    }
    return component_Dependent_Source;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SimpleDependencyConnection getSimple_Depend_On()
  {
    if (simple_Depend_On != null && simple_Depend_On.eIsProxy())
    {
      InternalEObject oldSimple_Depend_On = (InternalEObject)simple_Depend_On;
      simple_Depend_On = (SimpleDependencyConnection)eResolveProxy(oldSimple_Depend_On);
      if (simple_Depend_On != oldSimple_Depend_On)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.COMPONENT__SIMPLE_DEPEND_ON, oldSimple_Depend_On, simple_Depend_On));
      }
    }
    return simple_Depend_On;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SimpleDependencyConnection basicGetSimple_Depend_On()
  {
    return simple_Depend_On;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSimple_Depend_On(SimpleDependencyConnection newSimple_Depend_On)
  {
    SimpleDependencyConnection oldSimple_Depend_On = simple_Depend_On;
    simple_Depend_On = newSimple_Depend_On;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.COMPONENT__SIMPLE_DEPEND_ON, oldSimple_Depend_On, simple_Depend_On));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getConnector_Depend_On_Component()
  {
    if (connector_Depend_On_Component == null)
    {
      connector_Depend_On_Component = new EObjectResolvingEList(OutputDependencyConnection.class, this, SmartfrogPackage.COMPONENT__CONNECTOR_DEPEND_ON_COMPONENT);
    }
    return connector_Depend_On_Component;
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
      case SmartfrogPackage.COMPONENT__COMPONENT_ATTRIBUTE_CONTAINER:
        return ((InternalEList)getComponent_Attribute_Container()).basicRemove(otherEnd, msgs);
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
      case SmartfrogPackage.COMPONENT__EXTENDS:
        return getExtends();
      case SmartfrogPackage.COMPONENT__COMPONENT_ATTRIBUTE_CONTAINER:
        return getComponent_Attribute_Container();
      case SmartfrogPackage.COMPONENT__CHILD_COMPONENTS:
        if (resolve) return getChild_Components();
        return basicGetChild_Components();
      case SmartfrogPackage.COMPONENT__MODEL_MEMBER_COMPONENTS:
        if (resolve) return getModel_Member_Components();
        return basicGetModel_Member_Components();
      case SmartfrogPackage.COMPONENT__SIMPLE_DEPENDENT_SOURCE:
        return getSimple_Dependent_Source();
      case SmartfrogPackage.COMPONENT__COMPONENT_DEPENDENT_SOURCE:
        return getComponent_Dependent_Source();
      case SmartfrogPackage.COMPONENT__SIMPLE_DEPEND_ON:
        if (resolve) return getSimple_Depend_On();
        return basicGetSimple_Depend_On();
      case SmartfrogPackage.COMPONENT__CONNECTOR_DEPEND_ON_COMPONENT:
        return getConnector_Depend_On_Component();
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
      case SmartfrogPackage.COMPONENT__EXTENDS:
        setExtends((String)newValue);
        return;
      case SmartfrogPackage.COMPONENT__COMPONENT_ATTRIBUTE_CONTAINER:
        getComponent_Attribute_Container().clear();
        getComponent_Attribute_Container().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPONENT__CHILD_COMPONENTS:
        setChild_Components((Composite)newValue);
        return;
      case SmartfrogPackage.COMPONENT__MODEL_MEMBER_COMPONENTS:
        setModel_Member_Components((DependencyModel)newValue);
        return;
      case SmartfrogPackage.COMPONENT__SIMPLE_DEPENDENT_SOURCE:
        getSimple_Dependent_Source().clear();
        getSimple_Dependent_Source().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPONENT__COMPONENT_DEPENDENT_SOURCE:
        getComponent_Dependent_Source().clear();
        getComponent_Dependent_Source().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPONENT__SIMPLE_DEPEND_ON:
        setSimple_Depend_On((SimpleDependencyConnection)newValue);
        return;
      case SmartfrogPackage.COMPONENT__CONNECTOR_DEPEND_ON_COMPONENT:
        getConnector_Depend_On_Component().clear();
        getConnector_Depend_On_Component().addAll((Collection)newValue);
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
      case SmartfrogPackage.COMPONENT__EXTENDS:
        setExtends(EXTENDS_EDEFAULT);
        return;
      case SmartfrogPackage.COMPONENT__COMPONENT_ATTRIBUTE_CONTAINER:
        getComponent_Attribute_Container().clear();
        return;
      case SmartfrogPackage.COMPONENT__CHILD_COMPONENTS:
        setChild_Components((Composite)null);
        return;
      case SmartfrogPackage.COMPONENT__MODEL_MEMBER_COMPONENTS:
        setModel_Member_Components((DependencyModel)null);
        return;
      case SmartfrogPackage.COMPONENT__SIMPLE_DEPENDENT_SOURCE:
        getSimple_Dependent_Source().clear();
        return;
      case SmartfrogPackage.COMPONENT__COMPONENT_DEPENDENT_SOURCE:
        getComponent_Dependent_Source().clear();
        return;
      case SmartfrogPackage.COMPONENT__SIMPLE_DEPEND_ON:
        setSimple_Depend_On((SimpleDependencyConnection)null);
        return;
      case SmartfrogPackage.COMPONENT__CONNECTOR_DEPEND_ON_COMPONENT:
        getConnector_Depend_On_Component().clear();
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
      case SmartfrogPackage.COMPONENT__EXTENDS:
        return EXTENDS_EDEFAULT == null ? extends_ != null : !EXTENDS_EDEFAULT.equals(extends_);
      case SmartfrogPackage.COMPONENT__COMPONENT_ATTRIBUTE_CONTAINER:
        return component_Attribute_Container != null && !component_Attribute_Container.isEmpty();
      case SmartfrogPackage.COMPONENT__CHILD_COMPONENTS:
        return child_Components != null;
      case SmartfrogPackage.COMPONENT__MODEL_MEMBER_COMPONENTS:
        return model_Member_Components != null;
      case SmartfrogPackage.COMPONENT__SIMPLE_DEPENDENT_SOURCE:
        return simple_Dependent_Source != null && !simple_Dependent_Source.isEmpty();
      case SmartfrogPackage.COMPONENT__COMPONENT_DEPENDENT_SOURCE:
        return component_Dependent_Source != null && !component_Dependent_Source.isEmpty();
      case SmartfrogPackage.COMPONENT__SIMPLE_DEPEND_ON:
        return simple_Depend_On != null;
      case SmartfrogPackage.COMPONENT__CONNECTOR_DEPEND_ON_COMPONENT:
        return connector_Depend_On_Component != null && !connector_Depend_On_Component.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (Extends: ");
    result.append(extends_);
    result.append(')');
    return result.toString();
  }

} //ComponentImpl
