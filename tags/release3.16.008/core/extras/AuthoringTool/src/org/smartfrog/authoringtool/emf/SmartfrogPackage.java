/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.smartfrog.authoringtool.emf;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.smartfrog.authoringtool.emf.SmartfrogFactory
 * @model kind="package"
 * @generated
 */
public interface SmartfrogPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "Smartfrog";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.smartfrog.org/sfml";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "smartfrog";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  SmartfrogPackage eINSTANCE = org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl.init();

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.MementoValueImpl <em>Memento Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.MementoValueImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getMementoValue()
   * @generated
   */
  int MEMENTO_VALUE = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMENTO_VALUE__NAME = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMENTO_VALUE__VALUE = 1;

  /**
   * The number of structural features of the '<em>Memento Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMENTO_VALUE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.MementoImpl <em>Memento</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.MementoImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getMemento()
   * @generated
   */
  int MEMENTO = 1;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMENTO__ID = 0;

  /**
   * The feature id for the '<em><b>Data</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMENTO__DATA = 1;

  /**
   * The number of structural features of the '<em>Memento</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMENTO_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.SubtypeImpl <em>Subtype</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.SubtypeImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getSubtype()
   * @generated
   */
  int SUBTYPE = 2;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUBTYPE__NAME = 0;

  /**
   * The feature id for the '<em><b>Base</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUBTYPE__BASE = 1;

  /**
   * The feature id for the '<em><b>Instances</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUBTYPE__INSTANCES = 2;

  /**
   * The feature id for the '<em><b>Links</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUBTYPE__LINKS = 3;

  /**
   * The number of structural features of the '<em>Subtype</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUBTYPE_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.SubtypeLinkImpl <em>Subtype Link</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.SubtypeLinkImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getSubtypeLink()
   * @generated
   */
  int SUBTYPE_LINK = 3;

  /**
   * The feature id for the '<em><b>Base</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUBTYPE_LINK__BASE = 0;

  /**
   * The feature id for the '<em><b>Instance</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUBTYPE_LINK__INSTANCE = 1;

  /**
   * The number of structural features of the '<em>Subtype Link</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUBTYPE_LINK_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.ModelObjectImpl <em>Model Object</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.ModelObjectImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getModelObject()
   * @generated
   */
  int MODEL_OBJECT = 4;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OBJECT__NAME = 0;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OBJECT__ID = 1;

  /**
   * The feature id for the '<em><b>X</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OBJECT__X = 2;

  /**
   * The feature id for the '<em><b>Y</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OBJECT__Y = 3;

  /**
   * The feature id for the '<em><b>Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OBJECT__WIDTH = 4;

  /**
   * The feature id for the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OBJECT__HEIGHT = 5;

  /**
   * The feature id for the '<em><b>Expanded Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OBJECT__EXPANDED_WIDTH = 6;

  /**
   * The feature id for the '<em><b>Expanded Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OBJECT__EXPANDED_HEIGHT = 7;

  /**
   * The feature id for the '<em><b>Expanded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OBJECT__EXPANDED = 8;

  /**
   * The feature id for the '<em><b>Subtype</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OBJECT__SUBTYPE = 9;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OBJECT__VISIBLE = 10;

  /**
   * The feature id for the '<em><b>Model Link Target</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OBJECT__MODEL_LINK_TARGET = 11;

  /**
   * The number of structural features of the '<em>Model Object</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OBJECT_FEATURE_COUNT = 12;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.ConnectorsImpl <em>Connectors</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.ConnectorsImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getConnectors()
   * @generated
   */
  int CONNECTORS = 13;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__NAME = MODEL_OBJECT__NAME;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__ID = MODEL_OBJECT__ID;

  /**
   * The feature id for the '<em><b>X</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__X = MODEL_OBJECT__X;

  /**
   * The feature id for the '<em><b>Y</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__Y = MODEL_OBJECT__Y;

  /**
   * The feature id for the '<em><b>Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__WIDTH = MODEL_OBJECT__WIDTH;

  /**
   * The feature id for the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__HEIGHT = MODEL_OBJECT__HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__EXPANDED_WIDTH = MODEL_OBJECT__EXPANDED_WIDTH;

  /**
   * The feature id for the '<em><b>Expanded Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__EXPANDED_HEIGHT = MODEL_OBJECT__EXPANDED_HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__EXPANDED = MODEL_OBJECT__EXPANDED;

  /**
   * The feature id for the '<em><b>Subtype</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__SUBTYPE = MODEL_OBJECT__SUBTYPE;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__VISIBLE = MODEL_OBJECT__VISIBLE;

  /**
   * The feature id for the '<em><b>Model Link Target</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__MODEL_LINK_TARGET = MODEL_OBJECT__MODEL_LINK_TARGET;

  /**
   * The feature id for the '<em><b>Child Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__CHILD_CONNECTOR = MODEL_OBJECT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Member Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__MEMBER_CONNECTOR = MODEL_OBJECT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Connector Dependent Source</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__CONNECTOR_DEPENDENT_SOURCE = MODEL_OBJECT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Component Depends On Connector</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__COMPONENT_DEPENDS_ON_CONNECTOR = MODEL_OBJECT_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Connectors</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS_FEATURE_COUNT = MODEL_OBJECT_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.AndImpl <em>And</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.AndImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getAnd()
   * @generated
   */
  int AND = 5;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__NAME = CONNECTORS__NAME;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__ID = CONNECTORS__ID;

  /**
   * The feature id for the '<em><b>X</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__X = CONNECTORS__X;

  /**
   * The feature id for the '<em><b>Y</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__Y = CONNECTORS__Y;

  /**
   * The feature id for the '<em><b>Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__WIDTH = CONNECTORS__WIDTH;

  /**
   * The feature id for the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__HEIGHT = CONNECTORS__HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__EXPANDED_WIDTH = CONNECTORS__EXPANDED_WIDTH;

  /**
   * The feature id for the '<em><b>Expanded Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__EXPANDED_HEIGHT = CONNECTORS__EXPANDED_HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__EXPANDED = CONNECTORS__EXPANDED;

  /**
   * The feature id for the '<em><b>Subtype</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__SUBTYPE = CONNECTORS__SUBTYPE;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__VISIBLE = CONNECTORS__VISIBLE;

  /**
   * The feature id for the '<em><b>Model Link Target</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__MODEL_LINK_TARGET = CONNECTORS__MODEL_LINK_TARGET;

  /**
   * The feature id for the '<em><b>Child Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__CHILD_CONNECTOR = CONNECTORS__CHILD_CONNECTOR;

  /**
   * The feature id for the '<em><b>Member Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__MEMBER_CONNECTOR = CONNECTORS__MEMBER_CONNECTOR;

  /**
   * The feature id for the '<em><b>Connector Dependent Source</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__CONNECTOR_DEPENDENT_SOURCE = CONNECTORS__CONNECTOR_DEPENDENT_SOURCE;

  /**
   * The feature id for the '<em><b>Component Depends On Connector</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__COMPONENT_DEPENDS_ON_CONNECTOR = CONNECTORS__COMPONENT_DEPENDS_ON_CONNECTOR;

  /**
   * The number of structural features of the '<em>And</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_FEATURE_COUNT = CONNECTORS_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.OrImpl <em>Or</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.OrImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getOr()
   * @generated
   */
  int OR = 6;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__NAME = CONNECTORS__NAME;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__ID = CONNECTORS__ID;

  /**
   * The feature id for the '<em><b>X</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__X = CONNECTORS__X;

  /**
   * The feature id for the '<em><b>Y</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__Y = CONNECTORS__Y;

  /**
   * The feature id for the '<em><b>Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__WIDTH = CONNECTORS__WIDTH;

  /**
   * The feature id for the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__HEIGHT = CONNECTORS__HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__EXPANDED_WIDTH = CONNECTORS__EXPANDED_WIDTH;

  /**
   * The feature id for the '<em><b>Expanded Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__EXPANDED_HEIGHT = CONNECTORS__EXPANDED_HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__EXPANDED = CONNECTORS__EXPANDED;

  /**
   * The feature id for the '<em><b>Subtype</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__SUBTYPE = CONNECTORS__SUBTYPE;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__VISIBLE = CONNECTORS__VISIBLE;

  /**
   * The feature id for the '<em><b>Model Link Target</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__MODEL_LINK_TARGET = CONNECTORS__MODEL_LINK_TARGET;

  /**
   * The feature id for the '<em><b>Child Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__CHILD_CONNECTOR = CONNECTORS__CHILD_CONNECTOR;

  /**
   * The feature id for the '<em><b>Member Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__MEMBER_CONNECTOR = CONNECTORS__MEMBER_CONNECTOR;

  /**
   * The feature id for the '<em><b>Connector Dependent Source</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__CONNECTOR_DEPENDENT_SOURCE = CONNECTORS__CONNECTOR_DEPENDENT_SOURCE;

  /**
   * The feature id for the '<em><b>Component Depends On Connector</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__COMPONENT_DEPENDS_ON_CONNECTOR = CONNECTORS__COMPONENT_DEPENDS_ON_CONNECTOR;

  /**
   * The number of structural features of the '<em>Or</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_FEATURE_COUNT = CONNECTORS_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.NorImpl <em>Nor</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.NorImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getNor()
   * @generated
   */
  int NOR = 7;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__NAME = CONNECTORS__NAME;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__ID = CONNECTORS__ID;

  /**
   * The feature id for the '<em><b>X</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__X = CONNECTORS__X;

  /**
   * The feature id for the '<em><b>Y</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__Y = CONNECTORS__Y;

  /**
   * The feature id for the '<em><b>Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__WIDTH = CONNECTORS__WIDTH;

  /**
   * The feature id for the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__HEIGHT = CONNECTORS__HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__EXPANDED_WIDTH = CONNECTORS__EXPANDED_WIDTH;

  /**
   * The feature id for the '<em><b>Expanded Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__EXPANDED_HEIGHT = CONNECTORS__EXPANDED_HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__EXPANDED = CONNECTORS__EXPANDED;

  /**
   * The feature id for the '<em><b>Subtype</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__SUBTYPE = CONNECTORS__SUBTYPE;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__VISIBLE = CONNECTORS__VISIBLE;

  /**
   * The feature id for the '<em><b>Model Link Target</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__MODEL_LINK_TARGET = CONNECTORS__MODEL_LINK_TARGET;

  /**
   * The feature id for the '<em><b>Child Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__CHILD_CONNECTOR = CONNECTORS__CHILD_CONNECTOR;

  /**
   * The feature id for the '<em><b>Member Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__MEMBER_CONNECTOR = CONNECTORS__MEMBER_CONNECTOR;

  /**
   * The feature id for the '<em><b>Connector Dependent Source</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__CONNECTOR_DEPENDENT_SOURCE = CONNECTORS__CONNECTOR_DEPENDENT_SOURCE;

  /**
   * The feature id for the '<em><b>Component Depends On Connector</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR__COMPONENT_DEPENDS_ON_CONNECTOR = CONNECTORS__COMPONENT_DEPENDS_ON_CONNECTOR;

  /**
   * The number of structural features of the '<em>Nor</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NOR_FEATURE_COUNT = CONNECTORS_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.NandImpl <em>Nand</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.NandImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getNand()
   * @generated
   */
  int NAND = 8;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__NAME = CONNECTORS__NAME;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__ID = CONNECTORS__ID;

  /**
   * The feature id for the '<em><b>X</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__X = CONNECTORS__X;

  /**
   * The feature id for the '<em><b>Y</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__Y = CONNECTORS__Y;

  /**
   * The feature id for the '<em><b>Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__WIDTH = CONNECTORS__WIDTH;

  /**
   * The feature id for the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__HEIGHT = CONNECTORS__HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__EXPANDED_WIDTH = CONNECTORS__EXPANDED_WIDTH;

  /**
   * The feature id for the '<em><b>Expanded Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__EXPANDED_HEIGHT = CONNECTORS__EXPANDED_HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__EXPANDED = CONNECTORS__EXPANDED;

  /**
   * The feature id for the '<em><b>Subtype</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__SUBTYPE = CONNECTORS__SUBTYPE;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__VISIBLE = CONNECTORS__VISIBLE;

  /**
   * The feature id for the '<em><b>Model Link Target</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__MODEL_LINK_TARGET = CONNECTORS__MODEL_LINK_TARGET;

  /**
   * The feature id for the '<em><b>Child Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__CHILD_CONNECTOR = CONNECTORS__CHILD_CONNECTOR;

  /**
   * The feature id for the '<em><b>Member Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__MEMBER_CONNECTOR = CONNECTORS__MEMBER_CONNECTOR;

  /**
   * The feature id for the '<em><b>Connector Dependent Source</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__CONNECTOR_DEPENDENT_SOURCE = CONNECTORS__CONNECTOR_DEPENDENT_SOURCE;

  /**
   * The feature id for the '<em><b>Component Depends On Connector</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND__COMPONENT_DEPENDS_ON_CONNECTOR = CONNECTORS__COMPONENT_DEPENDS_ON_CONNECTOR;

  /**
   * The number of structural features of the '<em>Nand</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAND_FEATURE_COUNT = CONNECTORS_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl <em>Component</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.ComponentImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getComponent()
   * @generated
   */
  int COMPONENT = 9;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__NAME = MODEL_OBJECT__NAME;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__ID = MODEL_OBJECT__ID;

  /**
   * The feature id for the '<em><b>X</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__X = MODEL_OBJECT__X;

  /**
   * The feature id for the '<em><b>Y</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__Y = MODEL_OBJECT__Y;

  /**
   * The feature id for the '<em><b>Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__WIDTH = MODEL_OBJECT__WIDTH;

  /**
   * The feature id for the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__HEIGHT = MODEL_OBJECT__HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__EXPANDED_WIDTH = MODEL_OBJECT__EXPANDED_WIDTH;

  /**
   * The feature id for the '<em><b>Expanded Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__EXPANDED_HEIGHT = MODEL_OBJECT__EXPANDED_HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__EXPANDED = MODEL_OBJECT__EXPANDED;

  /**
   * The feature id for the '<em><b>Subtype</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__SUBTYPE = MODEL_OBJECT__SUBTYPE;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__VISIBLE = MODEL_OBJECT__VISIBLE;

  /**
   * The feature id for the '<em><b>Model Link Target</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__MODEL_LINK_TARGET = MODEL_OBJECT__MODEL_LINK_TARGET;

  /**
   * The feature id for the '<em><b>Extends</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__EXTENDS = MODEL_OBJECT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Component Attribute Container</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__COMPONENT_ATTRIBUTE_CONTAINER = MODEL_OBJECT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Child Components</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__CHILD_COMPONENTS = MODEL_OBJECT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Model Member Components</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__MODEL_MEMBER_COMPONENTS = MODEL_OBJECT_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Simple Dependent Source</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__SIMPLE_DEPENDENT_SOURCE = MODEL_OBJECT_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Component Dependent Source</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__COMPONENT_DEPENDENT_SOURCE = MODEL_OBJECT_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Simple Depend On</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__SIMPLE_DEPEND_ON = MODEL_OBJECT_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Connector Depend On Component</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__CONNECTOR_DEPEND_ON_COMPONENT = MODEL_OBJECT_FEATURE_COUNT + 7;

  /**
   * The number of structural features of the '<em>Component</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT_FEATURE_COUNT = MODEL_OBJECT_FEATURE_COUNT + 8;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.CompositeImpl <em>Composite</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.CompositeImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getComposite()
   * @generated
   */
  int COMPOSITE = 10;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__NAME = MODEL_OBJECT__NAME;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__ID = MODEL_OBJECT__ID;

  /**
   * The feature id for the '<em><b>X</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__X = MODEL_OBJECT__X;

  /**
   * The feature id for the '<em><b>Y</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__Y = MODEL_OBJECT__Y;

  /**
   * The feature id for the '<em><b>Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__WIDTH = MODEL_OBJECT__WIDTH;

  /**
   * The feature id for the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__HEIGHT = MODEL_OBJECT__HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__EXPANDED_WIDTH = MODEL_OBJECT__EXPANDED_WIDTH;

  /**
   * The feature id for the '<em><b>Expanded Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__EXPANDED_HEIGHT = MODEL_OBJECT__EXPANDED_HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__EXPANDED = MODEL_OBJECT__EXPANDED;

  /**
   * The feature id for the '<em><b>Subtype</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__SUBTYPE = MODEL_OBJECT__SUBTYPE;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__VISIBLE = MODEL_OBJECT__VISIBLE;

  /**
   * The feature id for the '<em><b>Model Link Target</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__MODEL_LINK_TARGET = MODEL_OBJECT__MODEL_LINK_TARGET;

  /**
   * The feature id for the '<em><b>Super Composite</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__SUPER_COMPOSITE = MODEL_OBJECT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Composite Component Container</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__COMPOSITE_COMPONENT_CONTAINER = MODEL_OBJECT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Composite Attribute Container</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__COMPOSITE_ATTRIBUTE_CONTAINER = MODEL_OBJECT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Composite Connector Container</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__COMPOSITE_CONNECTOR_CONTAINER = MODEL_OBJECT_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Child Composite</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__CHILD_COMPOSITE = MODEL_OBJECT_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Model Member Composites</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE__MODEL_MEMBER_COMPOSITES = MODEL_OBJECT_FEATURE_COUNT + 5;

  /**
   * The number of structural features of the '<em>Composite</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSITE_FEATURE_COUNT = MODEL_OBJECT_FEATURE_COUNT + 6;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.DependencyModelImpl <em>Dependency Model</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.DependencyModelImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getDependencyModel()
   * @generated
   */
  int DEPENDENCY_MODEL = 11;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__NAME = MODEL_OBJECT__NAME;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__ID = MODEL_OBJECT__ID;

  /**
   * The feature id for the '<em><b>X</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__X = MODEL_OBJECT__X;

  /**
   * The feature id for the '<em><b>Y</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__Y = MODEL_OBJECT__Y;

  /**
   * The feature id for the '<em><b>Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__WIDTH = MODEL_OBJECT__WIDTH;

  /**
   * The feature id for the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__HEIGHT = MODEL_OBJECT__HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__EXPANDED_WIDTH = MODEL_OBJECT__EXPANDED_WIDTH;

  /**
   * The feature id for the '<em><b>Expanded Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__EXPANDED_HEIGHT = MODEL_OBJECT__EXPANDED_HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__EXPANDED = MODEL_OBJECT__EXPANDED;

  /**
   * The feature id for the '<em><b>Subtype</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__SUBTYPE = MODEL_OBJECT__SUBTYPE;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__VISIBLE = MODEL_OBJECT__VISIBLE;

  /**
   * The feature id for the '<em><b>Model Link Target</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__MODEL_LINK_TARGET = MODEL_OBJECT__MODEL_LINK_TARGET;

  /**
   * The feature id for the '<em><b>Run</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__RUN = MODEL_OBJECT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Model composite Container</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__MODEL_COMPOSITE_CONTAINER = MODEL_OBJECT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Model Component Container</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__MODEL_COMPONENT_CONTAINER = MODEL_OBJECT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Model Connector Container</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__MODEL_CONNECTOR_CONTAINER = MODEL_OBJECT_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Dependency Model</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL_FEATURE_COUNT = MODEL_OBJECT_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.AttributeImpl <em>Attribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.AttributeImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getAttribute()
   * @generated
   */
  int ATTRIBUTE = 12;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__NAME = MODEL_OBJECT__NAME;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__ID = MODEL_OBJECT__ID;

  /**
   * The feature id for the '<em><b>X</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__X = MODEL_OBJECT__X;

  /**
   * The feature id for the '<em><b>Y</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__Y = MODEL_OBJECT__Y;

  /**
   * The feature id for the '<em><b>Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__WIDTH = MODEL_OBJECT__WIDTH;

  /**
   * The feature id for the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__HEIGHT = MODEL_OBJECT__HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__EXPANDED_WIDTH = MODEL_OBJECT__EXPANDED_WIDTH;

  /**
   * The feature id for the '<em><b>Expanded Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__EXPANDED_HEIGHT = MODEL_OBJECT__EXPANDED_HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__EXPANDED = MODEL_OBJECT__EXPANDED;

  /**
   * The feature id for the '<em><b>Subtype</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__SUBTYPE = MODEL_OBJECT__SUBTYPE;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__VISIBLE = MODEL_OBJECT__VISIBLE;

  /**
   * The feature id for the '<em><b>Model Link Target</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__MODEL_LINK_TARGET = MODEL_OBJECT__MODEL_LINK_TARGET;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__VALUE = MODEL_OBJECT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>State Data</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__STATE_DATA = MODEL_OBJECT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>State Listen</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__STATE_LISTEN = MODEL_OBJECT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>State Notify</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__STATE_NOTIFY = MODEL_OBJECT_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Is Lazy Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__IS_LAZY_VALUE = MODEL_OBJECT_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Component Attribute</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__COMPONENT_ATTRIBUTE = MODEL_OBJECT_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Composite Arrtibute</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__COMPOSITE_ARRTIBUTE = MODEL_OBJECT_FEATURE_COUNT + 6;

  /**
   * The number of structural features of the '<em>Attribute</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_FEATURE_COUNT = MODEL_OBJECT_FEATURE_COUNT + 7;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.SimpleDependencyConnectionImpl <em>Simple Dependency Connection</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.SimpleDependencyConnectionImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getSimpleDependencyConnection()
   * @generated
   */
  int SIMPLE_DEPENDENCY_CONNECTION = 14;

  /**
   * The feature id for the '<em><b>Source</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SIMPLE_DEPENDENCY_CONNECTION__SOURCE = 0;

  /**
   * The feature id for the '<em><b>Target</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SIMPLE_DEPENDENCY_CONNECTION__TARGET = 1;

  /**
   * The feature id for the '<em><b>Relevant</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SIMPLE_DEPENDENCY_CONNECTION__RELEVANT = 2;

  /**
   * The feature id for the '<em><b>Enabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SIMPLE_DEPENDENCY_CONNECTION__ENABLED = 3;

  /**
   * The feature id for the '<em><b>Dependency Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SIMPLE_DEPENDENCY_CONNECTION__DEPENDENCY_NAME = 4;

  /**
   * The number of structural features of the '<em>Simple Dependency Connection</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SIMPLE_DEPENDENCY_CONNECTION_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.InputDependencyConnectionImpl <em>Input Dependency Connection</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.InputDependencyConnectionImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getInputDependencyConnection()
   * @generated
   */
  int INPUT_DEPENDENCY_CONNECTION = 15;

  /**
   * The feature id for the '<em><b>Source</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INPUT_DEPENDENCY_CONNECTION__SOURCE = 0;

  /**
   * The feature id for the '<em><b>Target</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INPUT_DEPENDENCY_CONNECTION__TARGET = 1;

  /**
   * The feature id for the '<em><b>Relevant</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INPUT_DEPENDENCY_CONNECTION__RELEVANT = 2;

  /**
   * The feature id for the '<em><b>Enabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INPUT_DEPENDENCY_CONNECTION__ENABLED = 3;

  /**
   * The feature id for the '<em><b>Input Dependency Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INPUT_DEPENDENCY_CONNECTION__INPUT_DEPENDENCY_NAME = 4;

  /**
   * The number of structural features of the '<em>Input Dependency Connection</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INPUT_DEPENDENCY_CONNECTION_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.OutputDependencyConnectionImpl <em>Output Dependency Connection</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.OutputDependencyConnectionImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getOutputDependencyConnection()
   * @generated
   */
  int OUTPUT_DEPENDENCY_CONNECTION = 16;

  /**
   * The feature id for the '<em><b>Source</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OUTPUT_DEPENDENCY_CONNECTION__SOURCE = 0;

  /**
   * The feature id for the '<em><b>Target</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OUTPUT_DEPENDENCY_CONNECTION__TARGET = 1;

  /**
   * The feature id for the '<em><b>Relevant</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OUTPUT_DEPENDENCY_CONNECTION__RELEVANT = 2;

  /**
   * The feature id for the '<em><b>Enabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OUTPUT_DEPENDENCY_CONNECTION__ENABLED = 3;

  /**
   * The feature id for the '<em><b>Output Dependency Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OUTPUT_DEPENDENCY_CONNECTION__OUTPUT_DEPENDENCY_NAME = 4;

  /**
   * The number of structural features of the '<em>Output Dependency Connection</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OUTPUT_DEPENDENCY_CONNECTION_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.RootImpl <em>Root</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.RootImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getRoot()
   * @generated
   */
  int ROOT = 17;

  /**
   * The feature id for the '<em><b>Mementos</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__MEMENTOS = 0;

  /**
   * The feature id for the '<em><b>Subtypes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__SUBTYPES = 1;

  /**
   * The feature id for the '<em><b>Real Root</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__REAL_ROOT = 2;

  /**
   * The feature id for the '<em><b>And</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__AND = 3;

  /**
   * The feature id for the '<em><b>Or</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__OR = 4;

  /**
   * The feature id for the '<em><b>Nor</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__NOR = 5;

  /**
   * The feature id for the '<em><b>Nand</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__NAND = 6;

  /**
   * The feature id for the '<em><b>Component</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__COMPONENT = 7;

  /**
   * The feature id for the '<em><b>Composite</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__COMPOSITE = 8;

  /**
   * The feature id for the '<em><b>Dependency Model</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__DEPENDENCY_MODEL = 9;

  /**
   * The feature id for the '<em><b>Attribute</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__ATTRIBUTE = 10;

  /**
   * The feature id for the '<em><b>Connectors</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__CONNECTORS = 11;

  /**
   * The feature id for the '<em><b>Simple Dependency Connection</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__SIMPLE_DEPENDENCY_CONNECTION = 12;

  /**
   * The feature id for the '<em><b>Input Dependency Connection</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__INPUT_DEPENDENCY_CONNECTION = 13;

  /**
   * The feature id for the '<em><b>Output Dependency Connection</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__OUTPUT_DEPENDENCY_CONNECTION = 14;

  /**
   * The number of structural features of the '<em>Root</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT_FEATURE_COUNT = 15;


  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.MementoValue <em>Memento Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Memento Value</em>'.
   * @see org.smartfrog.authoringtool.emf.MementoValue
   * @generated
   */
  EClass getMementoValue();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.MementoValue#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.smartfrog.authoringtool.emf.MementoValue#getName()
   * @see #getMementoValue()
   * @generated
   */
  EAttribute getMementoValue_Name();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.MementoValue#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.smartfrog.authoringtool.emf.MementoValue#getValue()
   * @see #getMementoValue()
   * @generated
   */
  EAttribute getMementoValue_Value();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.Memento <em>Memento</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Memento</em>'.
   * @see org.smartfrog.authoringtool.emf.Memento
   * @generated
   */
  EClass getMemento();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.Memento#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.smartfrog.authoringtool.emf.Memento#getId()
   * @see #getMemento()
   * @generated
   */
  EAttribute getMemento_Id();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Memento#getData <em>Data</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Data</em>'.
   * @see org.smartfrog.authoringtool.emf.Memento#getData()
   * @see #getMemento()
   * @generated
   */
  EReference getMemento_Data();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.Subtype <em>Subtype</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Subtype</em>'.
   * @see org.smartfrog.authoringtool.emf.Subtype
   * @generated
   */
  EClass getSubtype();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.Subtype#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.smartfrog.authoringtool.emf.Subtype#getName()
   * @see #getSubtype()
   * @generated
   */
  EAttribute getSubtype_Name();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Subtype#getBase <em>Base</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Base</em>'.
   * @see org.smartfrog.authoringtool.emf.Subtype#getBase()
   * @see #getSubtype()
   * @generated
   */
  EReference getSubtype_Base();

  /**
   * Returns the meta object for the reference list '{@link org.smartfrog.authoringtool.emf.Subtype#getInstances <em>Instances</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Instances</em>'.
   * @see org.smartfrog.authoringtool.emf.Subtype#getInstances()
   * @see #getSubtype()
   * @generated
   */
  EReference getSubtype_Instances();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Subtype#getLinks <em>Links</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Links</em>'.
   * @see org.smartfrog.authoringtool.emf.Subtype#getLinks()
   * @see #getSubtype()
   * @generated
   */
  EReference getSubtype_Links();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.SubtypeLink <em>Subtype Link</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Subtype Link</em>'.
   * @see org.smartfrog.authoringtool.emf.SubtypeLink
   * @generated
   */
  EClass getSubtypeLink();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.SubtypeLink#getBase <em>Base</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Base</em>'.
   * @see org.smartfrog.authoringtool.emf.SubtypeLink#getBase()
   * @see #getSubtypeLink()
   * @generated
   */
  EReference getSubtypeLink_Base();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.SubtypeLink#getInstance <em>Instance</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Instance</em>'.
   * @see org.smartfrog.authoringtool.emf.SubtypeLink#getInstance()
   * @see #getSubtypeLink()
   * @generated
   */
  EReference getSubtypeLink_Instance();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.ModelObject <em>Model Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Model Object</em>'.
   * @see org.smartfrog.authoringtool.emf.ModelObject
   * @generated
   */
  EClass getModelObject();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.ModelObject#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.smartfrog.authoringtool.emf.ModelObject#getName()
   * @see #getModelObject()
   * @generated
   */
  EAttribute getModelObject_Name();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.ModelObject#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.smartfrog.authoringtool.emf.ModelObject#getId()
   * @see #getModelObject()
   * @generated
   */
  EAttribute getModelObject_Id();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.ModelObject#getX <em>X</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>X</em>'.
   * @see org.smartfrog.authoringtool.emf.ModelObject#getX()
   * @see #getModelObject()
   * @generated
   */
  EAttribute getModelObject_X();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.ModelObject#getY <em>Y</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Y</em>'.
   * @see org.smartfrog.authoringtool.emf.ModelObject#getY()
   * @see #getModelObject()
   * @generated
   */
  EAttribute getModelObject_Y();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.ModelObject#getWidth <em>Width</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Width</em>'.
   * @see org.smartfrog.authoringtool.emf.ModelObject#getWidth()
   * @see #getModelObject()
   * @generated
   */
  EAttribute getModelObject_Width();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.ModelObject#getHeight <em>Height</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Height</em>'.
   * @see org.smartfrog.authoringtool.emf.ModelObject#getHeight()
   * @see #getModelObject()
   * @generated
   */
  EAttribute getModelObject_Height();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.ModelObject#getExpandedWidth <em>Expanded Width</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Expanded Width</em>'.
   * @see org.smartfrog.authoringtool.emf.ModelObject#getExpandedWidth()
   * @see #getModelObject()
   * @generated
   */
  EAttribute getModelObject_ExpandedWidth();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.ModelObject#getExpandedHeight <em>Expanded Height</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Expanded Height</em>'.
   * @see org.smartfrog.authoringtool.emf.ModelObject#getExpandedHeight()
   * @see #getModelObject()
   * @generated
   */
  EAttribute getModelObject_ExpandedHeight();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.ModelObject#isExpanded <em>Expanded</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Expanded</em>'.
   * @see org.smartfrog.authoringtool.emf.ModelObject#isExpanded()
   * @see #getModelObject()
   * @generated
   */
  EAttribute getModelObject_Expanded();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.ModelObject#isSubtype <em>Subtype</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Subtype</em>'.
   * @see org.smartfrog.authoringtool.emf.ModelObject#isSubtype()
   * @see #getModelObject()
   * @generated
   */
  EAttribute getModelObject_Subtype();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.ModelObject#isVisible <em>Visible</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Visible</em>'.
   * @see org.smartfrog.authoringtool.emf.ModelObject#isVisible()
   * @see #getModelObject()
   * @generated
   */
  EAttribute getModelObject_Visible();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.ModelObject#getModelLinkTarget <em>Model Link Target</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Model Link Target</em>'.
   * @see org.smartfrog.authoringtool.emf.ModelObject#getModelLinkTarget()
   * @see #getModelObject()
   * @generated
   */
  EAttribute getModelObject_ModelLinkTarget();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.And <em>And</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>And</em>'.
   * @see org.smartfrog.authoringtool.emf.And
   * @generated
   */
  EClass getAnd();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.Or <em>Or</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Or</em>'.
   * @see org.smartfrog.authoringtool.emf.Or
   * @generated
   */
  EClass getOr();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.Nor <em>Nor</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Nor</em>'.
   * @see org.smartfrog.authoringtool.emf.Nor
   * @generated
   */
  EClass getNor();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.Nand <em>Nand</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Nand</em>'.
   * @see org.smartfrog.authoringtool.emf.Nand
   * @generated
   */
  EClass getNand();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.Component <em>Component</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Component</em>'.
   * @see org.smartfrog.authoringtool.emf.Component
   * @generated
   */
  EClass getComponent();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.Component#getExtends <em>Extends</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Extends</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getExtends()
   * @see #getComponent()
   * @generated
   */
  EAttribute getComponent_Extends();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Component#getComponent_Attribute_Container <em>Component Attribute Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Component Attribute Container</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getComponent_Attribute_Container()
   * @see #getComponent()
   * @generated
   */
  EReference getComponent_Component_Attribute_Container();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Component#getChild_Components <em>Child Components</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Child Components</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getChild_Components()
   * @see #getComponent()
   * @generated
   */
  EReference getComponent_Child_Components();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Component#getModel_Member_Components <em>Model Member Components</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Model Member Components</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getModel_Member_Components()
   * @see #getComponent()
   * @generated
   */
  EReference getComponent_Model_Member_Components();

  /**
   * Returns the meta object for the reference list '{@link org.smartfrog.authoringtool.emf.Component#getSimple_Dependent_Source <em>Simple Dependent Source</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Simple Dependent Source</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getSimple_Dependent_Source()
   * @see #getComponent()
   * @generated
   */
  EReference getComponent_Simple_Dependent_Source();

  /**
   * Returns the meta object for the reference list '{@link org.smartfrog.authoringtool.emf.Component#getComponent_Dependent_Source <em>Component Dependent Source</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Component Dependent Source</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getComponent_Dependent_Source()
   * @see #getComponent()
   * @generated
   */
  EReference getComponent_Component_Dependent_Source();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Component#getSimple_Depend_On <em>Simple Depend On</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Simple Depend On</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getSimple_Depend_On()
   * @see #getComponent()
   * @generated
   */
  EReference getComponent_Simple_Depend_On();

  /**
   * Returns the meta object for the reference list '{@link org.smartfrog.authoringtool.emf.Component#getConnector_Depend_On_Component <em>Connector Depend On Component</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Connector Depend On Component</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getConnector_Depend_On_Component()
   * @see #getComponent()
   * @generated
   */
  EReference getComponent_Connector_Depend_On_Component();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.Composite <em>Composite</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Composite</em>'.
   * @see org.smartfrog.authoringtool.emf.Composite
   * @generated
   */
  EClass getComposite();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Composite#getSuperComposite <em>Super Composite</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Super Composite</em>'.
   * @see org.smartfrog.authoringtool.emf.Composite#getSuperComposite()
   * @see #getComposite()
   * @generated
   */
  EReference getComposite_SuperComposite();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Composite#getComposite_Component_Container <em>Composite Component Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Composite Component Container</em>'.
   * @see org.smartfrog.authoringtool.emf.Composite#getComposite_Component_Container()
   * @see #getComposite()
   * @generated
   */
  EReference getComposite_Composite_Component_Container();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Composite#getComposite_Attribute_Container <em>Composite Attribute Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Composite Attribute Container</em>'.
   * @see org.smartfrog.authoringtool.emf.Composite#getComposite_Attribute_Container()
   * @see #getComposite()
   * @generated
   */
  EReference getComposite_Composite_Attribute_Container();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Composite#getComposite_Connector_Container <em>Composite Connector Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Composite Connector Container</em>'.
   * @see org.smartfrog.authoringtool.emf.Composite#getComposite_Connector_Container()
   * @see #getComposite()
   * @generated
   */
  EReference getComposite_Composite_Connector_Container();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Composite#getChildComposite <em>Child Composite</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Child Composite</em>'.
   * @see org.smartfrog.authoringtool.emf.Composite#getChildComposite()
   * @see #getComposite()
   * @generated
   */
  EReference getComposite_ChildComposite();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Composite#getModel_Member_Composites <em>Model Member Composites</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Model Member Composites</em>'.
   * @see org.smartfrog.authoringtool.emf.Composite#getModel_Member_Composites()
   * @see #getComposite()
   * @generated
   */
  EReference getComposite_Model_Member_Composites();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.DependencyModel <em>Dependency Model</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Dependency Model</em>'.
   * @see org.smartfrog.authoringtool.emf.DependencyModel
   * @generated
   */
  EClass getDependencyModel();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.DependencyModel#isRun <em>Run</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Run</em>'.
   * @see org.smartfrog.authoringtool.emf.DependencyModel#isRun()
   * @see #getDependencyModel()
   * @generated
   */
  EAttribute getDependencyModel_Run();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.DependencyModel#getModel_composite_Container <em>Model composite Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Model composite Container</em>'.
   * @see org.smartfrog.authoringtool.emf.DependencyModel#getModel_composite_Container()
   * @see #getDependencyModel()
   * @generated
   */
  EReference getDependencyModel_Model_composite_Container();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.DependencyModel#getModel_Component_Container <em>Model Component Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Model Component Container</em>'.
   * @see org.smartfrog.authoringtool.emf.DependencyModel#getModel_Component_Container()
   * @see #getDependencyModel()
   * @generated
   */
  EReference getDependencyModel_Model_Component_Container();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.DependencyModel#getModel_Connector_Container <em>Model Connector Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Model Connector Container</em>'.
   * @see org.smartfrog.authoringtool.emf.DependencyModel#getModel_Connector_Container()
   * @see #getDependencyModel()
   * @generated
   */
  EReference getDependencyModel_Model_Connector_Container();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.Attribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute</em>'.
   * @see org.smartfrog.authoringtool.emf.Attribute
   * @generated
   */
  EClass getAttribute();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.Attribute#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.smartfrog.authoringtool.emf.Attribute#getValue()
   * @see #getAttribute()
   * @generated
   */
  EAttribute getAttribute_Value();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.Attribute#isStateData <em>State Data</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>State Data</em>'.
   * @see org.smartfrog.authoringtool.emf.Attribute#isStateData()
   * @see #getAttribute()
   * @generated
   */
  EAttribute getAttribute_StateData();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.Attribute#isStateListen <em>State Listen</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>State Listen</em>'.
   * @see org.smartfrog.authoringtool.emf.Attribute#isStateListen()
   * @see #getAttribute()
   * @generated
   */
  EAttribute getAttribute_StateListen();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.Attribute#isStateNotify <em>State Notify</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>State Notify</em>'.
   * @see org.smartfrog.authoringtool.emf.Attribute#isStateNotify()
   * @see #getAttribute()
   * @generated
   */
  EAttribute getAttribute_StateNotify();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.Attribute#isIsLazyValue <em>Is Lazy Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Is Lazy Value</em>'.
   * @see org.smartfrog.authoringtool.emf.Attribute#isIsLazyValue()
   * @see #getAttribute()
   * @generated
   */
  EAttribute getAttribute_IsLazyValue();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Attribute#getComponent_Attribute <em>Component Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Component Attribute</em>'.
   * @see org.smartfrog.authoringtool.emf.Attribute#getComponent_Attribute()
   * @see #getAttribute()
   * @generated
   */
  EReference getAttribute_Component_Attribute();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Attribute#getComposite_Arrtibute <em>Composite Arrtibute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Composite Arrtibute</em>'.
   * @see org.smartfrog.authoringtool.emf.Attribute#getComposite_Arrtibute()
   * @see #getAttribute()
   * @generated
   */
  EReference getAttribute_Composite_Arrtibute();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.Connectors <em>Connectors</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Connectors</em>'.
   * @see org.smartfrog.authoringtool.emf.Connectors
   * @generated
   */
  EClass getConnectors();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Connectors#getChild_Connector <em>Child Connector</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Child Connector</em>'.
   * @see org.smartfrog.authoringtool.emf.Connectors#getChild_Connector()
   * @see #getConnectors()
   * @generated
   */
  EReference getConnectors_Child_Connector();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Connectors#getMember_Connector <em>Member Connector</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Member Connector</em>'.
   * @see org.smartfrog.authoringtool.emf.Connectors#getMember_Connector()
   * @see #getConnectors()
   * @generated
   */
  EReference getConnectors_Member_Connector();

  /**
   * Returns the meta object for the reference list '{@link org.smartfrog.authoringtool.emf.Connectors#getConnector_Dependent_Source <em>Connector Dependent Source</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Connector Dependent Source</em>'.
   * @see org.smartfrog.authoringtool.emf.Connectors#getConnector_Dependent_Source()
   * @see #getConnectors()
   * @generated
   */
  EReference getConnectors_Connector_Dependent_Source();

  /**
   * Returns the meta object for the reference list '{@link org.smartfrog.authoringtool.emf.Connectors#getComponent_Depends_On_Connector <em>Component Depends On Connector</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Component Depends On Connector</em>'.
   * @see org.smartfrog.authoringtool.emf.Connectors#getComponent_Depends_On_Connector()
   * @see #getConnectors()
   * @generated
   */
  EReference getConnectors_Component_Depends_On_Connector();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.SimpleDependencyConnection <em>Simple Dependency Connection</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Simple Dependency Connection</em>'.
   * @see org.smartfrog.authoringtool.emf.SimpleDependencyConnection
   * @generated
   */
  EClass getSimpleDependencyConnection();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.SimpleDependencyConnection#getSource <em>Source</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Source</em>'.
   * @see org.smartfrog.authoringtool.emf.SimpleDependencyConnection#getSource()
   * @see #getSimpleDependencyConnection()
   * @generated
   */
  EReference getSimpleDependencyConnection_Source();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.SimpleDependencyConnection#getTarget <em>Target</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Target</em>'.
   * @see org.smartfrog.authoringtool.emf.SimpleDependencyConnection#getTarget()
   * @see #getSimpleDependencyConnection()
   * @generated
   */
  EReference getSimpleDependencyConnection_Target();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.SimpleDependencyConnection#getRelevant <em>Relevant</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Relevant</em>'.
   * @see org.smartfrog.authoringtool.emf.SimpleDependencyConnection#getRelevant()
   * @see #getSimpleDependencyConnection()
   * @generated
   */
  EAttribute getSimpleDependencyConnection_Relevant();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.SimpleDependencyConnection#getEnabled <em>Enabled</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Enabled</em>'.
   * @see org.smartfrog.authoringtool.emf.SimpleDependencyConnection#getEnabled()
   * @see #getSimpleDependencyConnection()
   * @generated
   */
  EAttribute getSimpleDependencyConnection_Enabled();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.SimpleDependencyConnection#getDependency_Name <em>Dependency Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Dependency Name</em>'.
   * @see org.smartfrog.authoringtool.emf.SimpleDependencyConnection#getDependency_Name()
   * @see #getSimpleDependencyConnection()
   * @generated
   */
  EAttribute getSimpleDependencyConnection_Dependency_Name();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.InputDependencyConnection <em>Input Dependency Connection</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Input Dependency Connection</em>'.
   * @see org.smartfrog.authoringtool.emf.InputDependencyConnection
   * @generated
   */
  EClass getInputDependencyConnection();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getSource <em>Source</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Source</em>'.
   * @see org.smartfrog.authoringtool.emf.InputDependencyConnection#getSource()
   * @see #getInputDependencyConnection()
   * @generated
   */
  EReference getInputDependencyConnection_Source();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getTarget <em>Target</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Target</em>'.
   * @see org.smartfrog.authoringtool.emf.InputDependencyConnection#getTarget()
   * @see #getInputDependencyConnection()
   * @generated
   */
  EReference getInputDependencyConnection_Target();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getRelevant <em>Relevant</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Relevant</em>'.
   * @see org.smartfrog.authoringtool.emf.InputDependencyConnection#getRelevant()
   * @see #getInputDependencyConnection()
   * @generated
   */
  EAttribute getInputDependencyConnection_Relevant();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getEnabled <em>Enabled</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Enabled</em>'.
   * @see org.smartfrog.authoringtool.emf.InputDependencyConnection#getEnabled()
   * @see #getInputDependencyConnection()
   * @generated
   */
  EAttribute getInputDependencyConnection_Enabled();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.InputDependencyConnection#getInput_Dependency_Name <em>Input Dependency Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Input Dependency Name</em>'.
   * @see org.smartfrog.authoringtool.emf.InputDependencyConnection#getInput_Dependency_Name()
   * @see #getInputDependencyConnection()
   * @generated
   */
  EAttribute getInputDependencyConnection_Input_Dependency_Name();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.OutputDependencyConnection <em>Output Dependency Connection</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Output Dependency Connection</em>'.
   * @see org.smartfrog.authoringtool.emf.OutputDependencyConnection
   * @generated
   */
  EClass getOutputDependencyConnection();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.OutputDependencyConnection#getSource <em>Source</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Source</em>'.
   * @see org.smartfrog.authoringtool.emf.OutputDependencyConnection#getSource()
   * @see #getOutputDependencyConnection()
   * @generated
   */
  EReference getOutputDependencyConnection_Source();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.OutputDependencyConnection#getTarget <em>Target</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Target</em>'.
   * @see org.smartfrog.authoringtool.emf.OutputDependencyConnection#getTarget()
   * @see #getOutputDependencyConnection()
   * @generated
   */
  EReference getOutputDependencyConnection_Target();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.OutputDependencyConnection#getRelevant <em>Relevant</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Relevant</em>'.
   * @see org.smartfrog.authoringtool.emf.OutputDependencyConnection#getRelevant()
   * @see #getOutputDependencyConnection()
   * @generated
   */
  EAttribute getOutputDependencyConnection_Relevant();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.OutputDependencyConnection#getEnabled <em>Enabled</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Enabled</em>'.
   * @see org.smartfrog.authoringtool.emf.OutputDependencyConnection#getEnabled()
   * @see #getOutputDependencyConnection()
   * @generated
   */
  EAttribute getOutputDependencyConnection_Enabled();

  /**
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.OutputDependencyConnection#getOutput_Dependency_Name <em>Output Dependency Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Output Dependency Name</em>'.
   * @see org.smartfrog.authoringtool.emf.OutputDependencyConnection#getOutput_Dependency_Name()
   * @see #getOutputDependencyConnection()
   * @generated
   */
  EAttribute getOutputDependencyConnection_Output_Dependency_Name();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.Root <em>Root</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Root</em>'.
   * @see org.smartfrog.authoringtool.emf.Root
   * @generated
   */
  EClass getRoot();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getMementos <em>Mementos</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Mementos</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getMementos()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_Mementos();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getSubtypes <em>Subtypes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Subtypes</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getSubtypes()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_Subtypes();

  /**
   * Returns the meta object for the containment reference '{@link org.smartfrog.authoringtool.emf.Root#getRealRoot <em>Real Root</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Real Root</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getRealRoot()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_RealRoot();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getAnd <em>And</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>And</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getAnd()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_And();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getOr <em>Or</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Or</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getOr()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_Or();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getNor <em>Nor</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Nor</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getNor()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_Nor();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getNand <em>Nand</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Nand</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getNand()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_Nand();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getComponent <em>Component</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Component</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getComponent()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_Component();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getComposite <em>Composite</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Composite</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getComposite()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_Composite();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getDependencyModel <em>Dependency Model</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Dependency Model</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getDependencyModel()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_DependencyModel();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getAttribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attribute</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getAttribute()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_Attribute();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getConnectors <em>Connectors</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Connectors</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getConnectors()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_Connectors();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getSimpleDependencyConnection <em>Simple Dependency Connection</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Simple Dependency Connection</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getSimpleDependencyConnection()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_SimpleDependencyConnection();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getInputDependencyConnection <em>Input Dependency Connection</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Input Dependency Connection</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getInputDependencyConnection()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_InputDependencyConnection();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getOutputDependencyConnection <em>Output Dependency Connection</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Output Dependency Connection</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getOutputDependencyConnection()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_OutputDependencyConnection();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  SmartfrogFactory getSmartfrogFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.MementoValueImpl <em>Memento Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.MementoValueImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getMementoValue()
     * @generated
     */
    EClass MEMENTO_VALUE = eINSTANCE.getMementoValue();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MEMENTO_VALUE__NAME = eINSTANCE.getMementoValue_Name();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MEMENTO_VALUE__VALUE = eINSTANCE.getMementoValue_Value();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.MementoImpl <em>Memento</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.MementoImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getMemento()
     * @generated
     */
    EClass MEMENTO = eINSTANCE.getMemento();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MEMENTO__ID = eINSTANCE.getMemento_Id();

    /**
     * The meta object literal for the '<em><b>Data</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MEMENTO__DATA = eINSTANCE.getMemento_Data();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.SubtypeImpl <em>Subtype</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.SubtypeImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getSubtype()
     * @generated
     */
    EClass SUBTYPE = eINSTANCE.getSubtype();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SUBTYPE__NAME = eINSTANCE.getSubtype_Name();

    /**
     * The meta object literal for the '<em><b>Base</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SUBTYPE__BASE = eINSTANCE.getSubtype_Base();

    /**
     * The meta object literal for the '<em><b>Instances</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SUBTYPE__INSTANCES = eINSTANCE.getSubtype_Instances();

    /**
     * The meta object literal for the '<em><b>Links</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SUBTYPE__LINKS = eINSTANCE.getSubtype_Links();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.SubtypeLinkImpl <em>Subtype Link</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.SubtypeLinkImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getSubtypeLink()
     * @generated
     */
    EClass SUBTYPE_LINK = eINSTANCE.getSubtypeLink();

    /**
     * The meta object literal for the '<em><b>Base</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SUBTYPE_LINK__BASE = eINSTANCE.getSubtypeLink_Base();

    /**
     * The meta object literal for the '<em><b>Instance</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SUBTYPE_LINK__INSTANCE = eINSTANCE.getSubtypeLink_Instance();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.ModelObjectImpl <em>Model Object</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.ModelObjectImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getModelObject()
     * @generated
     */
    EClass MODEL_OBJECT = eINSTANCE.getModelObject();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL_OBJECT__NAME = eINSTANCE.getModelObject_Name();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL_OBJECT__ID = eINSTANCE.getModelObject_Id();

    /**
     * The meta object literal for the '<em><b>X</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL_OBJECT__X = eINSTANCE.getModelObject_X();

    /**
     * The meta object literal for the '<em><b>Y</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL_OBJECT__Y = eINSTANCE.getModelObject_Y();

    /**
     * The meta object literal for the '<em><b>Width</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL_OBJECT__WIDTH = eINSTANCE.getModelObject_Width();

    /**
     * The meta object literal for the '<em><b>Height</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL_OBJECT__HEIGHT = eINSTANCE.getModelObject_Height();

    /**
     * The meta object literal for the '<em><b>Expanded Width</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL_OBJECT__EXPANDED_WIDTH = eINSTANCE.getModelObject_ExpandedWidth();

    /**
     * The meta object literal for the '<em><b>Expanded Height</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL_OBJECT__EXPANDED_HEIGHT = eINSTANCE.getModelObject_ExpandedHeight();

    /**
     * The meta object literal for the '<em><b>Expanded</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL_OBJECT__EXPANDED = eINSTANCE.getModelObject_Expanded();

    /**
     * The meta object literal for the '<em><b>Subtype</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL_OBJECT__SUBTYPE = eINSTANCE.getModelObject_Subtype();

    /**
     * The meta object literal for the '<em><b>Visible</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL_OBJECT__VISIBLE = eINSTANCE.getModelObject_Visible();

    /**
     * The meta object literal for the '<em><b>Model Link Target</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL_OBJECT__MODEL_LINK_TARGET = eINSTANCE.getModelObject_ModelLinkTarget();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.AndImpl <em>And</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.AndImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getAnd()
     * @generated
     */
    EClass AND = eINSTANCE.getAnd();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.OrImpl <em>Or</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.OrImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getOr()
     * @generated
     */
    EClass OR = eINSTANCE.getOr();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.NorImpl <em>Nor</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.NorImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getNor()
     * @generated
     */
    EClass NOR = eINSTANCE.getNor();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.NandImpl <em>Nand</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.NandImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getNand()
     * @generated
     */
    EClass NAND = eINSTANCE.getNand();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl <em>Component</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.ComponentImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getComponent()
     * @generated
     */
    EClass COMPONENT = eINSTANCE.getComponent();

    /**
     * The meta object literal for the '<em><b>Extends</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMPONENT__EXTENDS = eINSTANCE.getComponent_Extends();

    /**
     * The meta object literal for the '<em><b>Component Attribute Container</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT__COMPONENT_ATTRIBUTE_CONTAINER = eINSTANCE.getComponent_Component_Attribute_Container();

    /**
     * The meta object literal for the '<em><b>Child Components</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT__CHILD_COMPONENTS = eINSTANCE.getComponent_Child_Components();

    /**
     * The meta object literal for the '<em><b>Model Member Components</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT__MODEL_MEMBER_COMPONENTS = eINSTANCE.getComponent_Model_Member_Components();

    /**
     * The meta object literal for the '<em><b>Simple Dependent Source</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT__SIMPLE_DEPENDENT_SOURCE = eINSTANCE.getComponent_Simple_Dependent_Source();

    /**
     * The meta object literal for the '<em><b>Component Dependent Source</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT__COMPONENT_DEPENDENT_SOURCE = eINSTANCE.getComponent_Component_Dependent_Source();

    /**
     * The meta object literal for the '<em><b>Simple Depend On</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT__SIMPLE_DEPEND_ON = eINSTANCE.getComponent_Simple_Depend_On();

    /**
     * The meta object literal for the '<em><b>Connector Depend On Component</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT__CONNECTOR_DEPEND_ON_COMPONENT = eINSTANCE.getComponent_Connector_Depend_On_Component();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.CompositeImpl <em>Composite</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.CompositeImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getComposite()
     * @generated
     */
    EClass COMPOSITE = eINSTANCE.getComposite();

    /**
     * The meta object literal for the '<em><b>Super Composite</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPOSITE__SUPER_COMPOSITE = eINSTANCE.getComposite_SuperComposite();

    /**
     * The meta object literal for the '<em><b>Composite Component Container</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPOSITE__COMPOSITE_COMPONENT_CONTAINER = eINSTANCE.getComposite_Composite_Component_Container();

    /**
     * The meta object literal for the '<em><b>Composite Attribute Container</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPOSITE__COMPOSITE_ATTRIBUTE_CONTAINER = eINSTANCE.getComposite_Composite_Attribute_Container();

    /**
     * The meta object literal for the '<em><b>Composite Connector Container</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPOSITE__COMPOSITE_CONNECTOR_CONTAINER = eINSTANCE.getComposite_Composite_Connector_Container();

    /**
     * The meta object literal for the '<em><b>Child Composite</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPOSITE__CHILD_COMPOSITE = eINSTANCE.getComposite_ChildComposite();

    /**
     * The meta object literal for the '<em><b>Model Member Composites</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPOSITE__MODEL_MEMBER_COMPOSITES = eINSTANCE.getComposite_Model_Member_Composites();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.DependencyModelImpl <em>Dependency Model</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.DependencyModelImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getDependencyModel()
     * @generated
     */
    EClass DEPENDENCY_MODEL = eINSTANCE.getDependencyModel();

    /**
     * The meta object literal for the '<em><b>Run</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DEPENDENCY_MODEL__RUN = eINSTANCE.getDependencyModel_Run();

    /**
     * The meta object literal for the '<em><b>Model composite Container</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DEPENDENCY_MODEL__MODEL_COMPOSITE_CONTAINER = eINSTANCE.getDependencyModel_Model_composite_Container();

    /**
     * The meta object literal for the '<em><b>Model Component Container</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DEPENDENCY_MODEL__MODEL_COMPONENT_CONTAINER = eINSTANCE.getDependencyModel_Model_Component_Container();

    /**
     * The meta object literal for the '<em><b>Model Connector Container</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DEPENDENCY_MODEL__MODEL_CONNECTOR_CONTAINER = eINSTANCE.getDependencyModel_Model_Connector_Container();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.AttributeImpl <em>Attribute</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.AttributeImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getAttribute()
     * @generated
     */
    EClass ATTRIBUTE = eINSTANCE.getAttribute();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE__VALUE = eINSTANCE.getAttribute_Value();

    /**
     * The meta object literal for the '<em><b>State Data</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE__STATE_DATA = eINSTANCE.getAttribute_StateData();

    /**
     * The meta object literal for the '<em><b>State Listen</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE__STATE_LISTEN = eINSTANCE.getAttribute_StateListen();

    /**
     * The meta object literal for the '<em><b>State Notify</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE__STATE_NOTIFY = eINSTANCE.getAttribute_StateNotify();

    /**
     * The meta object literal for the '<em><b>Is Lazy Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE__IS_LAZY_VALUE = eINSTANCE.getAttribute_IsLazyValue();

    /**
     * The meta object literal for the '<em><b>Component Attribute</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE__COMPONENT_ATTRIBUTE = eINSTANCE.getAttribute_Component_Attribute();

    /**
     * The meta object literal for the '<em><b>Composite Arrtibute</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE__COMPOSITE_ARRTIBUTE = eINSTANCE.getAttribute_Composite_Arrtibute();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.ConnectorsImpl <em>Connectors</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.ConnectorsImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getConnectors()
     * @generated
     */
    EClass CONNECTORS = eINSTANCE.getConnectors();

    /**
     * The meta object literal for the '<em><b>Child Connector</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONNECTORS__CHILD_CONNECTOR = eINSTANCE.getConnectors_Child_Connector();

    /**
     * The meta object literal for the '<em><b>Member Connector</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONNECTORS__MEMBER_CONNECTOR = eINSTANCE.getConnectors_Member_Connector();

    /**
     * The meta object literal for the '<em><b>Connector Dependent Source</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONNECTORS__CONNECTOR_DEPENDENT_SOURCE = eINSTANCE.getConnectors_Connector_Dependent_Source();

    /**
     * The meta object literal for the '<em><b>Component Depends On Connector</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONNECTORS__COMPONENT_DEPENDS_ON_CONNECTOR = eINSTANCE.getConnectors_Component_Depends_On_Connector();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.SimpleDependencyConnectionImpl <em>Simple Dependency Connection</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.SimpleDependencyConnectionImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getSimpleDependencyConnection()
     * @generated
     */
    EClass SIMPLE_DEPENDENCY_CONNECTION = eINSTANCE.getSimpleDependencyConnection();

    /**
     * The meta object literal for the '<em><b>Source</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SIMPLE_DEPENDENCY_CONNECTION__SOURCE = eINSTANCE.getSimpleDependencyConnection_Source();

    /**
     * The meta object literal for the '<em><b>Target</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SIMPLE_DEPENDENCY_CONNECTION__TARGET = eINSTANCE.getSimpleDependencyConnection_Target();

    /**
     * The meta object literal for the '<em><b>Relevant</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SIMPLE_DEPENDENCY_CONNECTION__RELEVANT = eINSTANCE.getSimpleDependencyConnection_Relevant();

    /**
     * The meta object literal for the '<em><b>Enabled</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SIMPLE_DEPENDENCY_CONNECTION__ENABLED = eINSTANCE.getSimpleDependencyConnection_Enabled();

    /**
     * The meta object literal for the '<em><b>Dependency Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SIMPLE_DEPENDENCY_CONNECTION__DEPENDENCY_NAME = eINSTANCE.getSimpleDependencyConnection_Dependency_Name();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.InputDependencyConnectionImpl <em>Input Dependency Connection</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.InputDependencyConnectionImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getInputDependencyConnection()
     * @generated
     */
    EClass INPUT_DEPENDENCY_CONNECTION = eINSTANCE.getInputDependencyConnection();

    /**
     * The meta object literal for the '<em><b>Source</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference INPUT_DEPENDENCY_CONNECTION__SOURCE = eINSTANCE.getInputDependencyConnection_Source();

    /**
     * The meta object literal for the '<em><b>Target</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference INPUT_DEPENDENCY_CONNECTION__TARGET = eINSTANCE.getInputDependencyConnection_Target();

    /**
     * The meta object literal for the '<em><b>Relevant</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INPUT_DEPENDENCY_CONNECTION__RELEVANT = eINSTANCE.getInputDependencyConnection_Relevant();

    /**
     * The meta object literal for the '<em><b>Enabled</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INPUT_DEPENDENCY_CONNECTION__ENABLED = eINSTANCE.getInputDependencyConnection_Enabled();

    /**
     * The meta object literal for the '<em><b>Input Dependency Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INPUT_DEPENDENCY_CONNECTION__INPUT_DEPENDENCY_NAME = eINSTANCE.getInputDependencyConnection_Input_Dependency_Name();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.OutputDependencyConnectionImpl <em>Output Dependency Connection</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.OutputDependencyConnectionImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getOutputDependencyConnection()
     * @generated
     */
    EClass OUTPUT_DEPENDENCY_CONNECTION = eINSTANCE.getOutputDependencyConnection();

    /**
     * The meta object literal for the '<em><b>Source</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OUTPUT_DEPENDENCY_CONNECTION__SOURCE = eINSTANCE.getOutputDependencyConnection_Source();

    /**
     * The meta object literal for the '<em><b>Target</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OUTPUT_DEPENDENCY_CONNECTION__TARGET = eINSTANCE.getOutputDependencyConnection_Target();

    /**
     * The meta object literal for the '<em><b>Relevant</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OUTPUT_DEPENDENCY_CONNECTION__RELEVANT = eINSTANCE.getOutputDependencyConnection_Relevant();

    /**
     * The meta object literal for the '<em><b>Enabled</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OUTPUT_DEPENDENCY_CONNECTION__ENABLED = eINSTANCE.getOutputDependencyConnection_Enabled();

    /**
     * The meta object literal for the '<em><b>Output Dependency Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute OUTPUT_DEPENDENCY_CONNECTION__OUTPUT_DEPENDENCY_NAME = eINSTANCE.getOutputDependencyConnection_Output_Dependency_Name();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.RootImpl <em>Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.RootImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getRoot()
     * @generated
     */
    EClass ROOT = eINSTANCE.getRoot();

    /**
     * The meta object literal for the '<em><b>Mementos</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__MEMENTOS = eINSTANCE.getRoot_Mementos();

    /**
     * The meta object literal for the '<em><b>Subtypes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__SUBTYPES = eINSTANCE.getRoot_Subtypes();

    /**
     * The meta object literal for the '<em><b>Real Root</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__REAL_ROOT = eINSTANCE.getRoot_RealRoot();

    /**
     * The meta object literal for the '<em><b>And</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__AND = eINSTANCE.getRoot_And();

    /**
     * The meta object literal for the '<em><b>Or</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__OR = eINSTANCE.getRoot_Or();

    /**
     * The meta object literal for the '<em><b>Nor</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__NOR = eINSTANCE.getRoot_Nor();

    /**
     * The meta object literal for the '<em><b>Nand</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__NAND = eINSTANCE.getRoot_Nand();

    /**
     * The meta object literal for the '<em><b>Component</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__COMPONENT = eINSTANCE.getRoot_Component();

    /**
     * The meta object literal for the '<em><b>Composite</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__COMPOSITE = eINSTANCE.getRoot_Composite();

    /**
     * The meta object literal for the '<em><b>Dependency Model</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__DEPENDENCY_MODEL = eINSTANCE.getRoot_DependencyModel();

    /**
     * The meta object literal for the '<em><b>Attribute</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__ATTRIBUTE = eINSTANCE.getRoot_Attribute();

    /**
     * The meta object literal for the '<em><b>Connectors</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__CONNECTORS = eINSTANCE.getRoot_Connectors();

    /**
     * The meta object literal for the '<em><b>Simple Dependency Connection</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__SIMPLE_DEPENDENCY_CONNECTION = eINSTANCE.getRoot_SimpleDependencyConnection();

    /**
     * The meta object literal for the '<em><b>Input Dependency Connection</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__INPUT_DEPENDENCY_CONNECTION = eINSTANCE.getRoot_InputDependencyConnection();

    /**
     * The meta object literal for the '<em><b>Output Dependency Connection</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__OUTPUT_DEPENDENCY_CONNECTION = eINSTANCE.getRoot_OutputDependencyConnection();

  }

} //SmartfrogPackage
