/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.smartfrog.authoringtool.emf.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

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
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.DependencyModelImpl#getSFModel <em>SF Model</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.DependencyModelImpl#getDepmodel <em>Depmodel</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.DependencyModelImpl#getRootModel <em>Root Model</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DependencyModelImpl extends ModelObjectImpl implements DependencyModel
{
  /**
   * The cached value of the '{@link #getSFModel() <em>SF Model</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSFModel()
   * @generated
   * @ordered
   */
  protected EList sfModel;

  /**
   * The cached value of the '{@link #getDepmodel() <em>Depmodel</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDepmodel()
   * @generated
   * @ordered
   */
  protected EList depmodel;

  /**
   * The cached value of the '{@link #getRootModel() <em>Root Model</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRootModel()
   * @generated
   * @ordered
   */
  protected EList rootModel;

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
  public EList getSFModel()
  {
    if (sfModel == null)
    {
      sfModel = new EObjectContainmentEList(Composite.class, this, SmartfrogPackage.DEPENDENCY_MODEL__SF_MODEL);
    }
    return sfModel;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getDepmodel()
  {
    if (depmodel == null)
    {
      depmodel = new EObjectContainmentEList(Component.class, this, SmartfrogPackage.DEPENDENCY_MODEL__DEPMODEL);
    }
    return depmodel;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getRootModel()
  {
    if (rootModel == null)
    {
      rootModel = new EObjectContainmentEList(Connectors.class, this, SmartfrogPackage.DEPENDENCY_MODEL__ROOT_MODEL);
    }
    return rootModel;
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
      case SmartfrogPackage.DEPENDENCY_MODEL__SF_MODEL:
        return ((InternalEList)getSFModel()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.DEPENDENCY_MODEL__DEPMODEL:
        return ((InternalEList)getDepmodel()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.DEPENDENCY_MODEL__ROOT_MODEL:
        return ((InternalEList)getRootModel()).basicRemove(otherEnd, msgs);
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
      case SmartfrogPackage.DEPENDENCY_MODEL__SF_MODEL:
        return getSFModel();
      case SmartfrogPackage.DEPENDENCY_MODEL__DEPMODEL:
        return getDepmodel();
      case SmartfrogPackage.DEPENDENCY_MODEL__ROOT_MODEL:
        return getRootModel();
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
      case SmartfrogPackage.DEPENDENCY_MODEL__SF_MODEL:
        getSFModel().clear();
        getSFModel().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.DEPENDENCY_MODEL__DEPMODEL:
        getDepmodel().clear();
        getDepmodel().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.DEPENDENCY_MODEL__ROOT_MODEL:
        getRootModel().clear();
        getRootModel().addAll((Collection)newValue);
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
      case SmartfrogPackage.DEPENDENCY_MODEL__SF_MODEL:
        getSFModel().clear();
        return;
      case SmartfrogPackage.DEPENDENCY_MODEL__DEPMODEL:
        getDepmodel().clear();
        return;
      case SmartfrogPackage.DEPENDENCY_MODEL__ROOT_MODEL:
        getRootModel().clear();
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
      case SmartfrogPackage.DEPENDENCY_MODEL__SF_MODEL:
        return sfModel != null && !sfModel.isEmpty();
      case SmartfrogPackage.DEPENDENCY_MODEL__DEPMODEL:
        return depmodel != null && !depmodel.isEmpty();
      case SmartfrogPackage.DEPENDENCY_MODEL__ROOT_MODEL:
        return rootModel != null && !rootModel.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //DependencyModelImpl
