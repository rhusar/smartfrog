/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.smartfrog.authoringtool.emf.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.smartfrog.authoringtool.emf.And;
import org.smartfrog.authoringtool.emf.Attribute;
import org.smartfrog.authoringtool.emf.Component;
import org.smartfrog.authoringtool.emf.Composite;
import org.smartfrog.authoringtool.emf.Connectors;
import org.smartfrog.authoringtool.emf.DependencyModel;
import org.smartfrog.authoringtool.emf.InputDependencyConnection;
import org.smartfrog.authoringtool.emf.Memento;
import org.smartfrog.authoringtool.emf.MementoValue;
import org.smartfrog.authoringtool.emf.ModelObject;
import org.smartfrog.authoringtool.emf.Nand;
import org.smartfrog.authoringtool.emf.Nor;
import org.smartfrog.authoringtool.emf.Or;
import org.smartfrog.authoringtool.emf.OutputDependencyConnection;
import org.smartfrog.authoringtool.emf.Root;
import org.smartfrog.authoringtool.emf.SimpleDependencyConnection;
import org.smartfrog.authoringtool.emf.SmartfrogFactory;
import org.smartfrog.authoringtool.emf.SmartfrogPackage;
import org.smartfrog.authoringtool.emf.Subtype;
import org.smartfrog.authoringtool.emf.SubtypeLink;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SmartfrogPackageImpl extends EPackageImpl implements SmartfrogPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mementoValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass mementoEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass subtypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass subtypeLinkEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass modelObjectEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass andEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass orEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass norEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass nandEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass componentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass compositeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dependencyModelEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass connectorsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass simpleDependencyConnectionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass inputDependencyConnectionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass outputDependencyConnectionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass rootEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.smartfrog.authoringtool.emf.SmartfrogPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private SmartfrogPackageImpl()
  {
    super(eNS_URI, SmartfrogFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this
   * model, and for any others upon which it depends.  Simple
   * dependencies are satisfied by calling this method on all
   * dependent packages before doing anything else.  This method drives
   * initialization for interdependent packages directly, in parallel
   * with this package, itself.
   * <p>Of this package and its interdependencies, all packages which
   * have not yet been registered by their URI values are first created
   * and registered.  The packages are then initialized in two steps:
   * meta-model objects for all of the packages are created before any
   * are initialized, since one package's meta-model objects may refer to
   * those of another.
   * <p>Invocation of this method will not affect any packages that have
   * already been initialized.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static SmartfrogPackage init()
  {
    if (isInited) return (SmartfrogPackage)EPackage.Registry.INSTANCE.getEPackage(SmartfrogPackage.eNS_URI);

    // Obtain or create and register package
    SmartfrogPackageImpl theSmartfrogPackage = (SmartfrogPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof SmartfrogPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new SmartfrogPackageImpl());

    isInited = true;

    // Create package meta-data objects
    theSmartfrogPackage.createPackageContents();

    // Initialize created meta-data
    theSmartfrogPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theSmartfrogPackage.freeze();

    return theSmartfrogPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMementoValue()
  {
    return mementoValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMementoValue_Name()
  {
    return (EAttribute)mementoValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMementoValue_Value()
  {
    return (EAttribute)mementoValueEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMemento()
  {
    return mementoEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMemento_Id()
  {
    return (EAttribute)mementoEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMemento_Data()
  {
    return (EReference)mementoEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSubtype()
  {
    return subtypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSubtype_Name()
  {
    return (EAttribute)subtypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSubtype_Base()
  {
    return (EReference)subtypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSubtype_Instances()
  {
    return (EReference)subtypeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSubtype_Links()
  {
    return (EReference)subtypeEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSubtypeLink()
  {
    return subtypeLinkEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSubtypeLink_Base()
  {
    return (EReference)subtypeLinkEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSubtypeLink_Instance()
  {
    return (EReference)subtypeLinkEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getModelObject()
  {
    return modelObjectEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModelObject_Name()
  {
    return (EAttribute)modelObjectEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModelObject_Id()
  {
    return (EAttribute)modelObjectEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModelObject_X()
  {
    return (EAttribute)modelObjectEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModelObject_Y()
  {
    return (EAttribute)modelObjectEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModelObject_Width()
  {
    return (EAttribute)modelObjectEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModelObject_Height()
  {
    return (EAttribute)modelObjectEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModelObject_ExpandedWidth()
  {
    return (EAttribute)modelObjectEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModelObject_ExpandedHeight()
  {
    return (EAttribute)modelObjectEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModelObject_Expanded()
  {
    return (EAttribute)modelObjectEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModelObject_Subtype()
  {
    return (EAttribute)modelObjectEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModelObject_Visible()
  {
    return (EAttribute)modelObjectEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModelObject_ModelLinkTarget()
  {
    return (EAttribute)modelObjectEClass.getEStructuralFeatures().get(11);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAnd()
  {
    return andEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOr()
  {
    return orEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getNor()
  {
    return norEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getNand()
  {
    return nandEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getComponent()
  {
    return componentEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getComponent_Extends()
  {
    return (EAttribute)componentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComponent_Component_Attribute_Container()
  {
    return (EReference)componentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComponent_Child_Components()
  {
    return (EReference)componentEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComponent_Model_Member_Components()
  {
    return (EReference)componentEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComponent_Simple_Dependent_Source()
  {
    return (EReference)componentEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComponent_Component_Dependent_Source()
  {
    return (EReference)componentEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComponent_Simple_Depend_On()
  {
    return (EReference)componentEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComponent_Connector_Depend_On_Component()
  {
    return (EReference)componentEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getComposite()
  {
    return compositeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComposite_SuperComposite()
  {
    return (EReference)compositeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComposite_Composite_Component_Container()
  {
    return (EReference)compositeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComposite_Composite_Attribute_Container()
  {
    return (EReference)compositeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComposite_Composite_Connector_Container()
  {
    return (EReference)compositeEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComposite_ChildComposite()
  {
    return (EReference)compositeEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComposite_Model_Member_Composites()
  {
    return (EReference)compositeEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDependencyModel()
  {
    return dependencyModelEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDependencyModel_Run()
  {
    return (EAttribute)dependencyModelEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDependencyModel_Model_composite_Container()
  {
    return (EReference)dependencyModelEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDependencyModel_Model_Component_Container()
  {
    return (EReference)dependencyModelEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDependencyModel_Model_Connector_Container()
  {
    return (EReference)dependencyModelEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttribute()
  {
    return attributeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttribute_Value()
  {
    return (EAttribute)attributeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttribute_StateData()
  {
    return (EAttribute)attributeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttribute_StateListen()
  {
    return (EAttribute)attributeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttribute_StateNotify()
  {
    return (EAttribute)attributeEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttribute_IsLazyValue()
  {
    return (EAttribute)attributeEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttribute_Component_Attribute()
  {
    return (EReference)attributeEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttribute_Composite_Arrtibute()
  {
    return (EReference)attributeEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConnectors()
  {
    return connectorsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConnectors_Child_Connector()
  {
    return (EReference)connectorsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConnectors_Member_Connector()
  {
    return (EReference)connectorsEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConnectors_Connector_Dependent_Source()
  {
    return (EReference)connectorsEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConnectors_Component_Depends_On_Connector()
  {
    return (EReference)connectorsEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSimpleDependencyConnection()
  {
    return simpleDependencyConnectionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSimpleDependencyConnection_Source()
  {
    return (EReference)simpleDependencyConnectionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSimpleDependencyConnection_Target()
  {
    return (EReference)simpleDependencyConnectionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSimpleDependencyConnection_Relevant()
  {
    return (EAttribute)simpleDependencyConnectionEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSimpleDependencyConnection_Enabled()
  {
    return (EAttribute)simpleDependencyConnectionEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSimpleDependencyConnection_Dependency_Name()
  {
    return (EAttribute)simpleDependencyConnectionEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getInputDependencyConnection()
  {
    return inputDependencyConnectionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getInputDependencyConnection_Source()
  {
    return (EReference)inputDependencyConnectionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getInputDependencyConnection_Target()
  {
    return (EReference)inputDependencyConnectionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getInputDependencyConnection_Relevant()
  {
    return (EAttribute)inputDependencyConnectionEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getInputDependencyConnection_Enabled()
  {
    return (EAttribute)inputDependencyConnectionEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getInputDependencyConnection_Input_Dependency_Name()
  {
    return (EAttribute)inputDependencyConnectionEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOutputDependencyConnection()
  {
    return outputDependencyConnectionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOutputDependencyConnection_Source()
  {
    return (EReference)outputDependencyConnectionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOutputDependencyConnection_Target()
  {
    return (EReference)outputDependencyConnectionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getOutputDependencyConnection_Relevant()
  {
    return (EAttribute)outputDependencyConnectionEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getOutputDependencyConnection_Enabled()
  {
    return (EAttribute)outputDependencyConnectionEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getOutputDependencyConnection_Output_Dependency_Name()
  {
    return (EAttribute)outputDependencyConnectionEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRoot()
  {
    return rootEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_Mementos()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_Subtypes()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_RealRoot()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_And()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_Or()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_Nor()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_Nand()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_Component()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_Composite()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_DependencyModel()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_Attribute()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_Connectors()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(11);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_SimpleDependencyConnection()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(12);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_InputDependencyConnection()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(13);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_OutputDependencyConnection()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(14);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SmartfrogFactory getSmartfrogFactory()
  {
    return (SmartfrogFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    mementoValueEClass = createEClass(MEMENTO_VALUE);
    createEAttribute(mementoValueEClass, MEMENTO_VALUE__NAME);
    createEAttribute(mementoValueEClass, MEMENTO_VALUE__VALUE);

    mementoEClass = createEClass(MEMENTO);
    createEAttribute(mementoEClass, MEMENTO__ID);
    createEReference(mementoEClass, MEMENTO__DATA);

    subtypeEClass = createEClass(SUBTYPE);
    createEAttribute(subtypeEClass, SUBTYPE__NAME);
    createEReference(subtypeEClass, SUBTYPE__BASE);
    createEReference(subtypeEClass, SUBTYPE__INSTANCES);
    createEReference(subtypeEClass, SUBTYPE__LINKS);

    subtypeLinkEClass = createEClass(SUBTYPE_LINK);
    createEReference(subtypeLinkEClass, SUBTYPE_LINK__BASE);
    createEReference(subtypeLinkEClass, SUBTYPE_LINK__INSTANCE);

    modelObjectEClass = createEClass(MODEL_OBJECT);
    createEAttribute(modelObjectEClass, MODEL_OBJECT__NAME);
    createEAttribute(modelObjectEClass, MODEL_OBJECT__ID);
    createEAttribute(modelObjectEClass, MODEL_OBJECT__X);
    createEAttribute(modelObjectEClass, MODEL_OBJECT__Y);
    createEAttribute(modelObjectEClass, MODEL_OBJECT__WIDTH);
    createEAttribute(modelObjectEClass, MODEL_OBJECT__HEIGHT);
    createEAttribute(modelObjectEClass, MODEL_OBJECT__EXPANDED_WIDTH);
    createEAttribute(modelObjectEClass, MODEL_OBJECT__EXPANDED_HEIGHT);
    createEAttribute(modelObjectEClass, MODEL_OBJECT__EXPANDED);
    createEAttribute(modelObjectEClass, MODEL_OBJECT__SUBTYPE);
    createEAttribute(modelObjectEClass, MODEL_OBJECT__VISIBLE);
    createEAttribute(modelObjectEClass, MODEL_OBJECT__MODEL_LINK_TARGET);

    andEClass = createEClass(AND);

    orEClass = createEClass(OR);

    norEClass = createEClass(NOR);

    nandEClass = createEClass(NAND);

    componentEClass = createEClass(COMPONENT);
    createEAttribute(componentEClass, COMPONENT__EXTENDS);
    createEReference(componentEClass, COMPONENT__COMPONENT_ATTRIBUTE_CONTAINER);
    createEReference(componentEClass, COMPONENT__CHILD_COMPONENTS);
    createEReference(componentEClass, COMPONENT__MODEL_MEMBER_COMPONENTS);
    createEReference(componentEClass, COMPONENT__SIMPLE_DEPENDENT_SOURCE);
    createEReference(componentEClass, COMPONENT__COMPONENT_DEPENDENT_SOURCE);
    createEReference(componentEClass, COMPONENT__SIMPLE_DEPEND_ON);
    createEReference(componentEClass, COMPONENT__CONNECTOR_DEPEND_ON_COMPONENT);

    compositeEClass = createEClass(COMPOSITE);
    createEReference(compositeEClass, COMPOSITE__SUPER_COMPOSITE);
    createEReference(compositeEClass, COMPOSITE__COMPOSITE_COMPONENT_CONTAINER);
    createEReference(compositeEClass, COMPOSITE__COMPOSITE_ATTRIBUTE_CONTAINER);
    createEReference(compositeEClass, COMPOSITE__COMPOSITE_CONNECTOR_CONTAINER);
    createEReference(compositeEClass, COMPOSITE__CHILD_COMPOSITE);
    createEReference(compositeEClass, COMPOSITE__MODEL_MEMBER_COMPOSITES);

    dependencyModelEClass = createEClass(DEPENDENCY_MODEL);
    createEAttribute(dependencyModelEClass, DEPENDENCY_MODEL__RUN);
    createEReference(dependencyModelEClass, DEPENDENCY_MODEL__MODEL_COMPOSITE_CONTAINER);
    createEReference(dependencyModelEClass, DEPENDENCY_MODEL__MODEL_COMPONENT_CONTAINER);
    createEReference(dependencyModelEClass, DEPENDENCY_MODEL__MODEL_CONNECTOR_CONTAINER);

    attributeEClass = createEClass(ATTRIBUTE);
    createEAttribute(attributeEClass, ATTRIBUTE__VALUE);
    createEAttribute(attributeEClass, ATTRIBUTE__STATE_DATA);
    createEAttribute(attributeEClass, ATTRIBUTE__STATE_LISTEN);
    createEAttribute(attributeEClass, ATTRIBUTE__STATE_NOTIFY);
    createEAttribute(attributeEClass, ATTRIBUTE__IS_LAZY_VALUE);
    createEReference(attributeEClass, ATTRIBUTE__COMPONENT_ATTRIBUTE);
    createEReference(attributeEClass, ATTRIBUTE__COMPOSITE_ARRTIBUTE);

    connectorsEClass = createEClass(CONNECTORS);
    createEReference(connectorsEClass, CONNECTORS__CHILD_CONNECTOR);
    createEReference(connectorsEClass, CONNECTORS__MEMBER_CONNECTOR);
    createEReference(connectorsEClass, CONNECTORS__CONNECTOR_DEPENDENT_SOURCE);
    createEReference(connectorsEClass, CONNECTORS__COMPONENT_DEPENDS_ON_CONNECTOR);

    simpleDependencyConnectionEClass = createEClass(SIMPLE_DEPENDENCY_CONNECTION);
    createEReference(simpleDependencyConnectionEClass, SIMPLE_DEPENDENCY_CONNECTION__SOURCE);
    createEReference(simpleDependencyConnectionEClass, SIMPLE_DEPENDENCY_CONNECTION__TARGET);
    createEAttribute(simpleDependencyConnectionEClass, SIMPLE_DEPENDENCY_CONNECTION__RELEVANT);
    createEAttribute(simpleDependencyConnectionEClass, SIMPLE_DEPENDENCY_CONNECTION__ENABLED);
    createEAttribute(simpleDependencyConnectionEClass, SIMPLE_DEPENDENCY_CONNECTION__DEPENDENCY_NAME);

    inputDependencyConnectionEClass = createEClass(INPUT_DEPENDENCY_CONNECTION);
    createEReference(inputDependencyConnectionEClass, INPUT_DEPENDENCY_CONNECTION__SOURCE);
    createEReference(inputDependencyConnectionEClass, INPUT_DEPENDENCY_CONNECTION__TARGET);
    createEAttribute(inputDependencyConnectionEClass, INPUT_DEPENDENCY_CONNECTION__RELEVANT);
    createEAttribute(inputDependencyConnectionEClass, INPUT_DEPENDENCY_CONNECTION__ENABLED);
    createEAttribute(inputDependencyConnectionEClass, INPUT_DEPENDENCY_CONNECTION__INPUT_DEPENDENCY_NAME);

    outputDependencyConnectionEClass = createEClass(OUTPUT_DEPENDENCY_CONNECTION);
    createEReference(outputDependencyConnectionEClass, OUTPUT_DEPENDENCY_CONNECTION__SOURCE);
    createEReference(outputDependencyConnectionEClass, OUTPUT_DEPENDENCY_CONNECTION__TARGET);
    createEAttribute(outputDependencyConnectionEClass, OUTPUT_DEPENDENCY_CONNECTION__RELEVANT);
    createEAttribute(outputDependencyConnectionEClass, OUTPUT_DEPENDENCY_CONNECTION__ENABLED);
    createEAttribute(outputDependencyConnectionEClass, OUTPUT_DEPENDENCY_CONNECTION__OUTPUT_DEPENDENCY_NAME);

    rootEClass = createEClass(ROOT);
    createEReference(rootEClass, ROOT__MEMENTOS);
    createEReference(rootEClass, ROOT__SUBTYPES);
    createEReference(rootEClass, ROOT__REAL_ROOT);
    createEReference(rootEClass, ROOT__AND);
    createEReference(rootEClass, ROOT__OR);
    createEReference(rootEClass, ROOT__NOR);
    createEReference(rootEClass, ROOT__NAND);
    createEReference(rootEClass, ROOT__COMPONENT);
    createEReference(rootEClass, ROOT__COMPOSITE);
    createEReference(rootEClass, ROOT__DEPENDENCY_MODEL);
    createEReference(rootEClass, ROOT__ATTRIBUTE);
    createEReference(rootEClass, ROOT__CONNECTORS);
    createEReference(rootEClass, ROOT__SIMPLE_DEPENDENCY_CONNECTION);
    createEReference(rootEClass, ROOT__INPUT_DEPENDENCY_CONNECTION);
    createEReference(rootEClass, ROOT__OUTPUT_DEPENDENCY_CONNECTION);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Add supertypes to classes
    andEClass.getESuperTypes().add(this.getConnectors());
    orEClass.getESuperTypes().add(this.getConnectors());
    norEClass.getESuperTypes().add(this.getConnectors());
    nandEClass.getESuperTypes().add(this.getConnectors());
    componentEClass.getESuperTypes().add(this.getModelObject());
    compositeEClass.getESuperTypes().add(this.getModelObject());
    dependencyModelEClass.getESuperTypes().add(this.getModelObject());
    attributeEClass.getESuperTypes().add(this.getModelObject());
    connectorsEClass.getESuperTypes().add(this.getModelObject());

    // Initialize classes and features; add operations and parameters
    initEClass(mementoValueEClass, MementoValue.class, "MementoValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMementoValue_Name(), ecorePackage.getEString(), "name", "0", 0, 1, MementoValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMementoValue_Value(), ecorePackage.getEString(), "value", "0", 0, 1, MementoValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mementoEClass, Memento.class, "Memento", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMemento_Id(), ecorePackage.getEString(), "id", "0", 0, 1, Memento.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMemento_Data(), this.getMementoValue(), null, "data", null, 0, 2000, Memento.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(subtypeEClass, Subtype.class, "Subtype", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSubtype_Name(), ecorePackage.getEString(), "name", "AnonymousSubtype", 0, 1, Subtype.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSubtype_Base(), this.getModelObject(), null, "base", null, 0, 1, Subtype.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSubtype_Instances(), this.getModelObject(), null, "instances", null, 0, 2000, Subtype.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSubtype_Links(), this.getSubtypeLink(), null, "links", null, 0, 20000, Subtype.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(subtypeLinkEClass, SubtypeLink.class, "SubtypeLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSubtypeLink_Base(), this.getModelObject(), null, "base", null, 0, 1, SubtypeLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSubtypeLink_Instance(), this.getModelObject(), null, "instance", null, 0, 1, SubtypeLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(modelObjectEClass, ModelObject.class, "ModelObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getModelObject_Name(), ecorePackage.getEString(), "Name", "0", 0, 1, ModelObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModelObject_Id(), ecorePackage.getEString(), "Id", "0", 0, 1, ModelObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModelObject_X(), ecorePackage.getEInt(), "X", "0", 0, 1, ModelObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModelObject_Y(), ecorePackage.getEInt(), "Y", "0", 0, 1, ModelObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModelObject_Width(), ecorePackage.getEInt(), "Width", "100", 0, 1, ModelObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModelObject_Height(), ecorePackage.getEInt(), "Height", "100", 0, 1, ModelObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModelObject_ExpandedWidth(), ecorePackage.getEInt(), "ExpandedWidth", "200", 0, 1, ModelObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModelObject_ExpandedHeight(), ecorePackage.getEInt(), "ExpandedHeight", "200", 0, 1, ModelObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModelObject_Expanded(), ecorePackage.getEBoolean(), "Expanded", "false", 0, 1, ModelObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModelObject_Subtype(), ecorePackage.getEBoolean(), "Subtype", "false", 0, 1, ModelObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModelObject_Visible(), ecorePackage.getEBoolean(), "Visible", "true", 0, 1, ModelObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModelObject_ModelLinkTarget(), ecorePackage.getEString(), "ModelLinkTarget", "", 0, 1, ModelObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(andEClass, And.class, "And", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(orEClass, Or.class, "Or", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(norEClass, Nor.class, "Nor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(nandEClass, Nand.class, "Nand", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(componentEClass, Component.class, "Component", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getComponent_Extends(), ecorePackage.getEString(), "Extends", "0", 0, 1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComponent_Component_Attribute_Container(), this.getAttribute(), null, "Component_Attribute_Container", null, 0, 2000, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComponent_Child_Components(), this.getComposite(), null, "Child_Components", null, 0, 1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComponent_Model_Member_Components(), this.getDependencyModel(), null, "Model_Member_Components", null, 0, 1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComponent_Simple_Dependent_Source(), this.getSimpleDependencyConnection(), null, "Simple_Dependent_Source", null, 0, 2147483647, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComponent_Component_Dependent_Source(), this.getInputDependencyConnection(), null, "Component_Dependent_Source", null, 0, 2147483647, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComponent_Simple_Depend_On(), this.getSimpleDependencyConnection(), null, "Simple_Depend_On", null, 1, 1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComponent_Connector_Depend_On_Component(), this.getOutputDependencyConnection(), null, "Connector_Depend_On_Component", null, 0, 2147483647, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(compositeEClass, Composite.class, "Composite", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getComposite_SuperComposite(), this.getComposite(), null, "SuperComposite", null, 0, 2000, Composite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComposite_Composite_Component_Container(), this.getComponent(), null, "Composite_Component_Container", null, 0, 2000, Composite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComposite_Composite_Attribute_Container(), this.getAttribute(), null, "Composite_Attribute_Container", null, 0, 2000, Composite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComposite_Composite_Connector_Container(), this.getConnectors(), null, "Composite_Connector_Container", null, 0, 2000, Composite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComposite_ChildComposite(), this.getComposite(), null, "ChildComposite", null, 0, 1, Composite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComposite_Model_Member_Composites(), this.getDependencyModel(), null, "Model_Member_Composites", null, 0, 1, Composite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(dependencyModelEClass, DependencyModel.class, "DependencyModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDependencyModel_Run(), ecorePackage.getEBoolean(), "Run", "true", 0, 1, DependencyModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDependencyModel_Model_composite_Container(), this.getComposite(), null, "Model_composite_Container", null, 0, 2000, DependencyModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDependencyModel_Model_Component_Container(), this.getComponent(), null, "Model_Component_Container", null, 0, 2000, DependencyModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDependencyModel_Model_Connector_Container(), this.getConnectors(), null, "Model_Connector_Container", null, 0, 2000, DependencyModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeEClass, Attribute.class, "Attribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAttribute_Value(), ecorePackage.getEString(), "Value", "0", 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttribute_StateData(), ecorePackage.getEBoolean(), "StateData", "false", 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttribute_StateListen(), ecorePackage.getEBoolean(), "StateListen", "false", 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttribute_StateNotify(), ecorePackage.getEBoolean(), "StateNotify", "false", 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttribute_IsLazyValue(), ecorePackage.getEBoolean(), "IsLazyValue", "false", 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAttribute_Component_Attribute(), this.getComponent(), null, "Component_Attribute", null, 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAttribute_Composite_Arrtibute(), this.getComposite(), null, "Composite_Arrtibute", null, 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(connectorsEClass, Connectors.class, "Connectors", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConnectors_Child_Connector(), this.getComposite(), null, "Child_Connector", null, 0, 1, Connectors.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getConnectors_Member_Connector(), this.getDependencyModel(), null, "Member_Connector", null, 0, 1, Connectors.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getConnectors_Connector_Dependent_Source(), this.getOutputDependencyConnection(), null, "Connector_Dependent_Source", null, 0, 2147483647, Connectors.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getConnectors_Component_Depends_On_Connector(), this.getInputDependencyConnection(), null, "Component_Depends_On_Connector", null, 0, 2147483647, Connectors.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(simpleDependencyConnectionEClass, SimpleDependencyConnection.class, "SimpleDependencyConnection", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSimpleDependencyConnection_Source(), this.getComponent(), null, "Source", null, 0, 1, SimpleDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSimpleDependencyConnection_Target(), this.getComponent(), null, "Target", null, 0, 1, SimpleDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSimpleDependencyConnection_Relevant(), ecorePackage.getEString(), "Relevant", "0", 0, 1, SimpleDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSimpleDependencyConnection_Enabled(), ecorePackage.getEString(), "Enabled", "0", 0, 1, SimpleDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSimpleDependencyConnection_Dependency_Name(), ecorePackage.getEString(), "Dependency_Name", "0", 0, 1, SimpleDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(inputDependencyConnectionEClass, InputDependencyConnection.class, "InputDependencyConnection", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getInputDependencyConnection_Source(), this.getComponent(), null, "Source", null, 0, 1, InputDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getInputDependencyConnection_Target(), this.getConnectors(), null, "Target", null, 0, 1, InputDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getInputDependencyConnection_Relevant(), ecorePackage.getEString(), "Relevant", "0", 0, 1, InputDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getInputDependencyConnection_Enabled(), ecorePackage.getEString(), "Enabled", "0", 0, 1, InputDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getInputDependencyConnection_Input_Dependency_Name(), ecorePackage.getEString(), "Input_Dependency_Name", "0", 0, 1, InputDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(outputDependencyConnectionEClass, OutputDependencyConnection.class, "OutputDependencyConnection", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getOutputDependencyConnection_Source(), this.getConnectors(), null, "Source", null, 0, 1, OutputDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOutputDependencyConnection_Target(), this.getComponent(), null, "Target", null, 0, 1, OutputDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getOutputDependencyConnection_Relevant(), ecorePackage.getEString(), "Relevant", "0", 0, 1, OutputDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getOutputDependencyConnection_Enabled(), ecorePackage.getEString(), "Enabled", "0", 0, 1, OutputDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getOutputDependencyConnection_Output_Dependency_Name(), ecorePackage.getEString(), "Output_Dependency_Name", "0", 0, 1, OutputDependencyConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(rootEClass, Root.class, "Root", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRoot_Mementos(), this.getMemento(), null, "mementos", null, 0, 2000, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_Subtypes(), this.getSubtype(), null, "subtypes", null, 0, 2000, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_RealRoot(), this.getDependencyModel(), null, "RealRoot", null, 0, 1, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_And(), this.getAnd(), null, "And", null, 0, 2000, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_Or(), this.getOr(), null, "Or", null, 0, 2000, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_Nor(), this.getNor(), null, "Nor", null, 0, 2000, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_Nand(), this.getNand(), null, "Nand", null, 0, 2000, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_Component(), this.getComponent(), null, "Component", null, 0, 2000, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_Composite(), this.getComposite(), null, "Composite", null, 0, 2000, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_DependencyModel(), this.getDependencyModel(), null, "DependencyModel", null, 0, 2000, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_Attribute(), this.getAttribute(), null, "Attribute", null, 0, 2000, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_Connectors(), this.getConnectors(), null, "Connectors", null, 0, 2000, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_SimpleDependencyConnection(), this.getSimpleDependencyConnection(), null, "SimpleDependencyConnection", null, 0, 2000, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_InputDependencyConnection(), this.getInputDependencyConnection(), null, "InputDependencyConnection", null, 0, 2000, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_OutputDependencyConnection(), this.getOutputDependencyConnection(), null, "OutputDependencyConnection", null, 0, 2000, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} //SmartfrogPackageImpl
