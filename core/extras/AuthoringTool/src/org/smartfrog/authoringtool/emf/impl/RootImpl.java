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
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.smartfrog.authoringtool.emf.And;
import org.smartfrog.authoringtool.emf.Attribute;
import org.smartfrog.authoringtool.emf.Component;
import org.smartfrog.authoringtool.emf.Composit;
import org.smartfrog.authoringtool.emf.Connectors;
import org.smartfrog.authoringtool.emf.DependencyModel;
import org.smartfrog.authoringtool.emf.InputDependencyConnection;
import org.smartfrog.authoringtool.emf.Memento;
import org.smartfrog.authoringtool.emf.Or;
import org.smartfrog.authoringtool.emf.OutputDependencyConnection;
import org.smartfrog.authoringtool.emf.Root;
import org.smartfrog.authoringtool.emf.SimpleDependencyConnection;
import org.smartfrog.authoringtool.emf.SmartfrogPackage;
import org.smartfrog.authoringtool.emf.Subtype;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Root</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.RootImpl#getMementos <em>Mementos</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.RootImpl#getSubtypes <em>Subtypes</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.RootImpl#getRealRoot <em>Real Root</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.RootImpl#getAnd <em>And</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.RootImpl#getOr <em>Or</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.RootImpl#getComponent <em>Component</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.RootImpl#getComposit <em>Composit</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.RootImpl#getDependencyModel <em>Dependency Model</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.RootImpl#getAttribute <em>Attribute</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.RootImpl#getConnectors <em>Connectors</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.RootImpl#getSimpleDependencyConnection <em>Simple Dependency Connection</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.RootImpl#getInputDependencyConnection <em>Input Dependency Connection</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.RootImpl#getOutputDependencyConnection <em>Output Dependency Connection</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RootImpl extends EObjectImpl implements Root
{
  /**
   * The cached value of the '{@link #getMementos() <em>Mementos</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMementos()
   * @generated
   * @ordered
   */
  protected EList mementos;

  /**
   * The cached value of the '{@link #getSubtypes() <em>Subtypes</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSubtypes()
   * @generated
   * @ordered
   */
  protected EList subtypes;

  /**
   * The cached value of the '{@link #getRealRoot() <em>Real Root</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRealRoot()
   * @generated
   * @ordered
   */
  protected DependencyModel realRoot;

  /**
   * The cached value of the '{@link #getAnd() <em>And</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAnd()
   * @generated
   * @ordered
   */
  protected EList and;

  /**
   * The cached value of the '{@link #getOr() <em>Or</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOr()
   * @generated
   * @ordered
   */
  protected EList or;

  /**
   * The cached value of the '{@link #getComponent() <em>Component</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComponent()
   * @generated
   * @ordered
   */
  protected EList component;

  /**
   * The cached value of the '{@link #getComposit() <em>Composit</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComposit()
   * @generated
   * @ordered
   */
  protected EList composit;

  /**
   * The cached value of the '{@link #getDependencyModel() <em>Dependency Model</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDependencyModel()
   * @generated
   * @ordered
   */
  protected EList dependencyModel;

  /**
   * The cached value of the '{@link #getAttribute() <em>Attribute</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttribute()
   * @generated
   * @ordered
   */
  protected EList attribute;

  /**
   * The cached value of the '{@link #getConnectors() <em>Connectors</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConnectors()
   * @generated
   * @ordered
   */
  protected EList connectors;

  /**
   * The cached value of the '{@link #getSimpleDependencyConnection() <em>Simple Dependency Connection</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSimpleDependencyConnection()
   * @generated
   * @ordered
   */
  protected EList simpleDependencyConnection;

  /**
   * The cached value of the '{@link #getInputDependencyConnection() <em>Input Dependency Connection</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInputDependencyConnection()
   * @generated
   * @ordered
   */
  protected EList inputDependencyConnection;

  /**
   * The cached value of the '{@link #getOutputDependencyConnection() <em>Output Dependency Connection</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOutputDependencyConnection()
   * @generated
   * @ordered
   */
  protected EList outputDependencyConnection;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected RootImpl()
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
    return SmartfrogPackage.Literals.ROOT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getMementos()
  {
    if (mementos == null)
    {
      mementos = new EObjectContainmentEList(Memento.class, this, SmartfrogPackage.ROOT__MEMENTOS);
    }
    return mementos;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getSubtypes()
  {
    if (subtypes == null)
    {
      subtypes = new EObjectContainmentEList(Subtype.class, this, SmartfrogPackage.ROOT__SUBTYPES);
    }
    return subtypes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel getRealRoot()
  {
    return realRoot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetRealRoot(DependencyModel newRealRoot, NotificationChain msgs)
  {
    DependencyModel oldRealRoot = realRoot;
    realRoot = newRealRoot;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, SmartfrogPackage.ROOT__REAL_ROOT, oldRealRoot, newRealRoot);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRealRoot(DependencyModel newRealRoot)
  {
    if (newRealRoot != realRoot)
    {
      NotificationChain msgs = null;
      if (realRoot != null)
        msgs = ((InternalEObject)realRoot).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - SmartfrogPackage.ROOT__REAL_ROOT, null, msgs);
      if (newRealRoot != null)
        msgs = ((InternalEObject)newRealRoot).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - SmartfrogPackage.ROOT__REAL_ROOT, null, msgs);
      msgs = basicSetRealRoot(newRealRoot, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.ROOT__REAL_ROOT, newRealRoot, newRealRoot));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getAnd()
  {
    if (and == null)
    {
      and = new EObjectContainmentEList(And.class, this, SmartfrogPackage.ROOT__AND);
    }
    return and;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getOr()
  {
    if (or == null)
    {
      or = new EObjectContainmentEList(Or.class, this, SmartfrogPackage.ROOT__OR);
    }
    return or;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getComponent()
  {
    if (component == null)
    {
      component = new EObjectContainmentEList(Component.class, this, SmartfrogPackage.ROOT__COMPONENT);
    }
    return component;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getComposit()
  {
    if (composit == null)
    {
      composit = new EObjectContainmentEList(Composit.class, this, SmartfrogPackage.ROOT__COMPOSIT);
    }
    return composit;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getDependencyModel()
  {
    if (dependencyModel == null)
    {
      dependencyModel = new EObjectContainmentEList(DependencyModel.class, this, SmartfrogPackage.ROOT__DEPENDENCY_MODEL);
    }
    return dependencyModel;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getAttribute()
  {
    if (attribute == null)
    {
      attribute = new EObjectContainmentEList(Attribute.class, this, SmartfrogPackage.ROOT__ATTRIBUTE);
    }
    return attribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getConnectors()
  {
    if (connectors == null)
    {
      connectors = new EObjectContainmentEList(Connectors.class, this, SmartfrogPackage.ROOT__CONNECTORS);
    }
    return connectors;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getSimpleDependencyConnection()
  {
    if (simpleDependencyConnection == null)
    {
      simpleDependencyConnection = new EObjectContainmentEList(SimpleDependencyConnection.class, this, SmartfrogPackage.ROOT__SIMPLE_DEPENDENCY_CONNECTION);
    }
    return simpleDependencyConnection;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getInputDependencyConnection()
  {
    if (inputDependencyConnection == null)
    {
      inputDependencyConnection = new EObjectContainmentEList(InputDependencyConnection.class, this, SmartfrogPackage.ROOT__INPUT_DEPENDENCY_CONNECTION);
    }
    return inputDependencyConnection;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getOutputDependencyConnection()
  {
    if (outputDependencyConnection == null)
    {
      outputDependencyConnection = new EObjectContainmentEList(OutputDependencyConnection.class, this, SmartfrogPackage.ROOT__OUTPUT_DEPENDENCY_CONNECTION);
    }
    return outputDependencyConnection;
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
      case SmartfrogPackage.ROOT__MEMENTOS:
        return ((InternalEList)getMementos()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.ROOT__SUBTYPES:
        return ((InternalEList)getSubtypes()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.ROOT__REAL_ROOT:
        return basicSetRealRoot(null, msgs);
      case SmartfrogPackage.ROOT__AND:
        return ((InternalEList)getAnd()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.ROOT__OR:
        return ((InternalEList)getOr()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.ROOT__COMPONENT:
        return ((InternalEList)getComponent()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.ROOT__COMPOSIT:
        return ((InternalEList)getComposit()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.ROOT__DEPENDENCY_MODEL:
        return ((InternalEList)getDependencyModel()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.ROOT__ATTRIBUTE:
        return ((InternalEList)getAttribute()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.ROOT__CONNECTORS:
        return ((InternalEList)getConnectors()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.ROOT__SIMPLE_DEPENDENCY_CONNECTION:
        return ((InternalEList)getSimpleDependencyConnection()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.ROOT__INPUT_DEPENDENCY_CONNECTION:
        return ((InternalEList)getInputDependencyConnection()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.ROOT__OUTPUT_DEPENDENCY_CONNECTION:
        return ((InternalEList)getOutputDependencyConnection()).basicRemove(otherEnd, msgs);
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
      case SmartfrogPackage.ROOT__MEMENTOS:
        return getMementos();
      case SmartfrogPackage.ROOT__SUBTYPES:
        return getSubtypes();
      case SmartfrogPackage.ROOT__REAL_ROOT:
        return getRealRoot();
      case SmartfrogPackage.ROOT__AND:
        return getAnd();
      case SmartfrogPackage.ROOT__OR:
        return getOr();
      case SmartfrogPackage.ROOT__COMPONENT:
        return getComponent();
      case SmartfrogPackage.ROOT__COMPOSIT:
        return getComposit();
      case SmartfrogPackage.ROOT__DEPENDENCY_MODEL:
        return getDependencyModel();
      case SmartfrogPackage.ROOT__ATTRIBUTE:
        return getAttribute();
      case SmartfrogPackage.ROOT__CONNECTORS:
        return getConnectors();
      case SmartfrogPackage.ROOT__SIMPLE_DEPENDENCY_CONNECTION:
        return getSimpleDependencyConnection();
      case SmartfrogPackage.ROOT__INPUT_DEPENDENCY_CONNECTION:
        return getInputDependencyConnection();
      case SmartfrogPackage.ROOT__OUTPUT_DEPENDENCY_CONNECTION:
        return getOutputDependencyConnection();
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
      case SmartfrogPackage.ROOT__MEMENTOS:
        getMementos().clear();
        getMementos().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.ROOT__SUBTYPES:
        getSubtypes().clear();
        getSubtypes().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.ROOT__REAL_ROOT:
        setRealRoot((DependencyModel)newValue);
        return;
      case SmartfrogPackage.ROOT__AND:
        getAnd().clear();
        getAnd().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.ROOT__OR:
        getOr().clear();
        getOr().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.ROOT__COMPONENT:
        getComponent().clear();
        getComponent().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.ROOT__COMPOSIT:
        getComposit().clear();
        getComposit().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.ROOT__DEPENDENCY_MODEL:
        getDependencyModel().clear();
        getDependencyModel().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.ROOT__ATTRIBUTE:
        getAttribute().clear();
        getAttribute().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.ROOT__CONNECTORS:
        getConnectors().clear();
        getConnectors().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.ROOT__SIMPLE_DEPENDENCY_CONNECTION:
        getSimpleDependencyConnection().clear();
        getSimpleDependencyConnection().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.ROOT__INPUT_DEPENDENCY_CONNECTION:
        getInputDependencyConnection().clear();
        getInputDependencyConnection().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.ROOT__OUTPUT_DEPENDENCY_CONNECTION:
        getOutputDependencyConnection().clear();
        getOutputDependencyConnection().addAll((Collection)newValue);
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
      case SmartfrogPackage.ROOT__MEMENTOS:
        getMementos().clear();
        return;
      case SmartfrogPackage.ROOT__SUBTYPES:
        getSubtypes().clear();
        return;
      case SmartfrogPackage.ROOT__REAL_ROOT:
        setRealRoot((DependencyModel)null);
        return;
      case SmartfrogPackage.ROOT__AND:
        getAnd().clear();
        return;
      case SmartfrogPackage.ROOT__OR:
        getOr().clear();
        return;
      case SmartfrogPackage.ROOT__COMPONENT:
        getComponent().clear();
        return;
      case SmartfrogPackage.ROOT__COMPOSIT:
        getComposit().clear();
        return;
      case SmartfrogPackage.ROOT__DEPENDENCY_MODEL:
        getDependencyModel().clear();
        return;
      case SmartfrogPackage.ROOT__ATTRIBUTE:
        getAttribute().clear();
        return;
      case SmartfrogPackage.ROOT__CONNECTORS:
        getConnectors().clear();
        return;
      case SmartfrogPackage.ROOT__SIMPLE_DEPENDENCY_CONNECTION:
        getSimpleDependencyConnection().clear();
        return;
      case SmartfrogPackage.ROOT__INPUT_DEPENDENCY_CONNECTION:
        getInputDependencyConnection().clear();
        return;
      case SmartfrogPackage.ROOT__OUTPUT_DEPENDENCY_CONNECTION:
        getOutputDependencyConnection().clear();
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
      case SmartfrogPackage.ROOT__MEMENTOS:
        return mementos != null && !mementos.isEmpty();
      case SmartfrogPackage.ROOT__SUBTYPES:
        return subtypes != null && !subtypes.isEmpty();
      case SmartfrogPackage.ROOT__REAL_ROOT:
        return realRoot != null;
      case SmartfrogPackage.ROOT__AND:
        return and != null && !and.isEmpty();
      case SmartfrogPackage.ROOT__OR:
        return or != null && !or.isEmpty();
      case SmartfrogPackage.ROOT__COMPONENT:
        return component != null && !component.isEmpty();
      case SmartfrogPackage.ROOT__COMPOSIT:
        return composit != null && !composit.isEmpty();
      case SmartfrogPackage.ROOT__DEPENDENCY_MODEL:
        return dependencyModel != null && !dependencyModel.isEmpty();
      case SmartfrogPackage.ROOT__ATTRIBUTE:
        return attribute != null && !attribute.isEmpty();
      case SmartfrogPackage.ROOT__CONNECTORS:
        return connectors != null && !connectors.isEmpty();
      case SmartfrogPackage.ROOT__SIMPLE_DEPENDENCY_CONNECTION:
        return simpleDependencyConnection != null && !simpleDependencyConnection.isEmpty();
      case SmartfrogPackage.ROOT__INPUT_DEPENDENCY_CONNECTION:
        return inputDependencyConnection != null && !inputDependencyConnection.isEmpty();
      case SmartfrogPackage.ROOT__OUTPUT_DEPENDENCY_CONNECTION:
        return outputDependencyConnection != null && !outputDependencyConnection.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //RootImpl
