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

import org.smartfrog.authoringtool.emf.Component;
import org.smartfrog.authoringtool.emf.Composite;
import org.smartfrog.authoringtool.emf.Connectors;
import org.smartfrog.authoringtool.emf.DependencyModel;
import org.smartfrog.authoringtool.emf.SmartfrogPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Dependency Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.DependencyModelImpl#isRun <em>Run</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.DependencyModelImpl#getModel_composite_Container <em>Model composite Container</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.DependencyModelImpl#getModel_Component_Container <em>Model Component Container</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.DependencyModelImpl#getModel_Connector_Container <em>Model Connector Container</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DependencyModelImpl extends ModelObjectImpl implements DependencyModel
{
  /**
   * The default value of the '{@link #isRun() <em>Run</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isRun()
   * @generated
   * @ordered
   */
  protected static final boolean RUN_EDEFAULT = true;

  /**
   * The cached value of the '{@link #isRun() <em>Run</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isRun()
   * @generated
   * @ordered
   */
  protected boolean run = RUN_EDEFAULT;

  /**
   * The cached value of the '{@link #getModel_composite_Container() <em>Model composite Container</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getModel_composite_Container()
   * @generated
   * @ordered
   */
  protected EList model_composite_Container;

  /**
   * The cached value of the '{@link #getModel_Component_Container() <em>Model Component Container</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getModel_Component_Container()
   * @generated
   * @ordered
   */
  protected EList model_Component_Container;

  /**
   * The cached value of the '{@link #getModel_Connector_Container() <em>Model Connector Container</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getModel_Connector_Container()
   * @generated
   * @ordered
   */
  protected EList model_Connector_Container;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DependencyModelImpl()
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
    return SmartfrogPackage.Literals.DEPENDENCY_MODEL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isRun()
  {
    return run;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRun(boolean newRun)
  {
    boolean oldRun = run;
    run = newRun;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.DEPENDENCY_MODEL__RUN, oldRun, run));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getModel_composite_Container()
  {
    if (model_composite_Container == null)
    {
      model_composite_Container = new EObjectContainmentEList(Composite.class, this, SmartfrogPackage.DEPENDENCY_MODEL__MODEL_COMPOSITE_CONTAINER);
    }
    return model_composite_Container;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getModel_Component_Container()
  {
    if (model_Component_Container == null)
    {
      model_Component_Container = new EObjectContainmentEList(Component.class, this, SmartfrogPackage.DEPENDENCY_MODEL__MODEL_COMPONENT_CONTAINER);
    }
    return model_Component_Container;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getModel_Connector_Container()
  {
    if (model_Connector_Container == null)
    {
      model_Connector_Container = new EObjectContainmentEList(Connectors.class, this, SmartfrogPackage.DEPENDENCY_MODEL__MODEL_CONNECTOR_CONTAINER);
    }
    return model_Connector_Container;
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
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_COMPOSITE_CONTAINER:
        return ((InternalEList)getModel_composite_Container()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_COMPONENT_CONTAINER:
        return ((InternalEList)getModel_Component_Container()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_CONNECTOR_CONTAINER:
        return ((InternalEList)getModel_Connector_Container()).basicRemove(otherEnd, msgs);
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
      case SmartfrogPackage.DEPENDENCY_MODEL__RUN:
        return isRun() ? Boolean.TRUE : Boolean.FALSE;
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_COMPOSITE_CONTAINER:
        return getModel_composite_Container();
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_COMPONENT_CONTAINER:
        return getModel_Component_Container();
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_CONNECTOR_CONTAINER:
        return getModel_Connector_Container();
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
      case SmartfrogPackage.DEPENDENCY_MODEL__RUN:
        setRun(((Boolean)newValue).booleanValue());
        return;
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_COMPOSITE_CONTAINER:
        getModel_composite_Container().clear();
        getModel_composite_Container().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_COMPONENT_CONTAINER:
        getModel_Component_Container().clear();
        getModel_Component_Container().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_CONNECTOR_CONTAINER:
        getModel_Connector_Container().clear();
        getModel_Connector_Container().addAll((Collection)newValue);
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
      case SmartfrogPackage.DEPENDENCY_MODEL__RUN:
        setRun(RUN_EDEFAULT);
        return;
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_COMPOSITE_CONTAINER:
        getModel_composite_Container().clear();
        return;
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_COMPONENT_CONTAINER:
        getModel_Component_Container().clear();
        return;
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_CONNECTOR_CONTAINER:
        getModel_Connector_Container().clear();
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
      case SmartfrogPackage.DEPENDENCY_MODEL__RUN:
        return run != RUN_EDEFAULT;
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_COMPOSITE_CONTAINER:
        return model_composite_Container != null && !model_composite_Container.isEmpty();
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_COMPONENT_CONTAINER:
        return model_Component_Container != null && !model_Component_Container.isEmpty();
      case SmartfrogPackage.DEPENDENCY_MODEL__MODEL_CONNECTOR_CONTAINER:
        return model_Connector_Container != null && !model_Connector_Container.isEmpty();
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
    result.append(" (Run: ");
    result.append(run);
    result.append(')');
    return result.toString();
  }

} //DependencyModelImpl
