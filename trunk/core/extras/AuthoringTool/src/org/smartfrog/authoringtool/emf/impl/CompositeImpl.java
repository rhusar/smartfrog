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
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositeImpl#getComposite_Component_Container <em>Composite Component Container</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositeImpl#getComposite_Attribute_Container <em>Composite Attribute Container</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositeImpl#getComposite_Connector_Container <em>Composite Connector Container</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositeImpl#getChildComposite <em>Child Composite</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.CompositeImpl#getModel_Member_Composites <em>Model Member Composites</em>}</li>
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
   * The cached value of the '{@link #getComposite_Component_Container() <em>Composite Component Container</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComposite_Component_Container()
   * @generated
   * @ordered
   */
  protected EList composite_Component_Container;

  /**
   * The cached value of the '{@link #getComposite_Attribute_Container() <em>Composite Attribute Container</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComposite_Attribute_Container()
   * @generated
   * @ordered
   */
  protected EList composite_Attribute_Container;

  /**
   * The cached value of the '{@link #getComposite_Connector_Container() <em>Composite Connector Container</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComposite_Connector_Container()
   * @generated
   * @ordered
   */
  protected EList composite_Connector_Container;

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
   * The cached value of the '{@link #getModel_Member_Composites() <em>Model Member Composites</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getModel_Member_Composites()
   * @generated
   * @ordered
   */
  protected DependencyModel model_Member_Composites;

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
  public EList getComposite_Component_Container()
  {
    if (composite_Component_Container == null)
    {
      composite_Component_Container = new EObjectContainmentEList(Component.class, this, SmartfrogPackage.COMPOSITE__COMPOSITE_COMPONENT_CONTAINER);
    }
    return composite_Component_Container;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getComposite_Attribute_Container()
  {
    if (composite_Attribute_Container == null)
    {
      composite_Attribute_Container = new EObjectContainmentEList(Attribute.class, this, SmartfrogPackage.COMPOSITE__COMPOSITE_ATTRIBUTE_CONTAINER);
    }
    return composite_Attribute_Container;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getComposite_Connector_Container()
  {
    if (composite_Connector_Container == null)
    {
      composite_Connector_Container = new EObjectContainmentEList(Connectors.class, this, SmartfrogPackage.COMPOSITE__COMPOSITE_CONNECTOR_CONTAINER);
    }
    return composite_Connector_Container;
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
  public DependencyModel getModel_Member_Composites()
  {
    if (model_Member_Composites != null && model_Member_Composites.eIsProxy())
    {
      InternalEObject oldModel_Member_Composites = (InternalEObject)model_Member_Composites;
      model_Member_Composites = (DependencyModel)eResolveProxy(oldModel_Member_Composites);
      if (model_Member_Composites != oldModel_Member_Composites)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.COMPOSITE__MODEL_MEMBER_COMPOSITES, oldModel_Member_Composites, model_Member_Composites));
      }
    }
    return model_Member_Composites;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DependencyModel basicGetModel_Member_Composites()
  {
    return model_Member_Composites;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setModel_Member_Composites(DependencyModel newModel_Member_Composites)
  {
    DependencyModel oldModel_Member_Composites = model_Member_Composites;
    model_Member_Composites = newModel_Member_Composites;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.COMPOSITE__MODEL_MEMBER_COMPOSITES, oldModel_Member_Composites, model_Member_Composites));
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
      case SmartfrogPackage.COMPOSITE__COMPOSITE_COMPONENT_CONTAINER:
        return ((InternalEList)getComposite_Component_Container()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.COMPOSITE__COMPOSITE_ATTRIBUTE_CONTAINER:
        return ((InternalEList)getComposite_Attribute_Container()).basicRemove(otherEnd, msgs);
      case SmartfrogPackage.COMPOSITE__COMPOSITE_CONNECTOR_CONTAINER:
        return ((InternalEList)getComposite_Connector_Container()).basicRemove(otherEnd, msgs);
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
      case SmartfrogPackage.COMPOSITE__COMPOSITE_COMPONENT_CONTAINER:
        return getComposite_Component_Container();
      case SmartfrogPackage.COMPOSITE__COMPOSITE_ATTRIBUTE_CONTAINER:
        return getComposite_Attribute_Container();
      case SmartfrogPackage.COMPOSITE__COMPOSITE_CONNECTOR_CONTAINER:
        return getComposite_Connector_Container();
      case SmartfrogPackage.COMPOSITE__CHILD_COMPOSITE:
        if (resolve) return getChildComposite();
        return basicGetChildComposite();
      case SmartfrogPackage.COMPOSITE__MODEL_MEMBER_COMPOSITES:
        if (resolve) return getModel_Member_Composites();
        return basicGetModel_Member_Composites();
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
      case SmartfrogPackage.COMPOSITE__COMPOSITE_COMPONENT_CONTAINER:
        getComposite_Component_Container().clear();
        getComposite_Component_Container().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPOSITE__COMPOSITE_ATTRIBUTE_CONTAINER:
        getComposite_Attribute_Container().clear();
        getComposite_Attribute_Container().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPOSITE__COMPOSITE_CONNECTOR_CONTAINER:
        getComposite_Connector_Container().clear();
        getComposite_Connector_Container().addAll((Collection)newValue);
        return;
      case SmartfrogPackage.COMPOSITE__CHILD_COMPOSITE:
        setChildComposite((Composite)newValue);
        return;
      case SmartfrogPackage.COMPOSITE__MODEL_MEMBER_COMPOSITES:
        setModel_Member_Composites((DependencyModel)newValue);
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
      case SmartfrogPackage.COMPOSITE__COMPOSITE_COMPONENT_CONTAINER:
        getComposite_Component_Container().clear();
        return;
      case SmartfrogPackage.COMPOSITE__COMPOSITE_ATTRIBUTE_CONTAINER:
        getComposite_Attribute_Container().clear();
        return;
      case SmartfrogPackage.COMPOSITE__COMPOSITE_CONNECTOR_CONTAINER:
        getComposite_Connector_Container().clear();
        return;
      case SmartfrogPackage.COMPOSITE__CHILD_COMPOSITE:
        setChildComposite((Composite)null);
        return;
      case SmartfrogPackage.COMPOSITE__MODEL_MEMBER_COMPOSITES:
        setModel_Member_Composites((DependencyModel)null);
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
      case SmartfrogPackage.COMPOSITE__COMPOSITE_COMPONENT_CONTAINER:
        return composite_Component_Container != null && !composite_Component_Container.isEmpty();
      case SmartfrogPackage.COMPOSITE__COMPOSITE_ATTRIBUTE_CONTAINER:
        return composite_Attribute_Container != null && !composite_Attribute_Container.isEmpty();
      case SmartfrogPackage.COMPOSITE__COMPOSITE_CONNECTOR_CONTAINER:
        return composite_Connector_Container != null && !composite_Connector_Container.isEmpty();
      case SmartfrogPackage.COMPOSITE__CHILD_COMPOSITE:
        return childComposite != null;
      case SmartfrogPackage.COMPOSITE__MODEL_MEMBER_COMPOSITES:
        return model_Member_Composites != null;
    }
    return super.eIsSet(featureID);
  }

} //CompositeImpl
