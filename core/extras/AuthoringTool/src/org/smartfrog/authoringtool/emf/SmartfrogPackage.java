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
  int CONNECTORS = 11;

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
   * The feature id for the '<em><b>Dep Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__DEP_CONNECTOR = MODEL_OBJECT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Gen Dep Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__GEN_DEP_CONNECTOR = MODEL_OBJECT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>By</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__BY = MODEL_OBJECT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>On</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONNECTORS__ON = MODEL_OBJECT_FEATURE_COUNT + 3;

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
   * The feature id for the '<em><b>Dep Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__DEP_CONNECTOR = CONNECTORS__DEP_CONNECTOR;

  /**
   * The feature id for the '<em><b>Gen Dep Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__GEN_DEP_CONNECTOR = CONNECTORS__GEN_DEP_CONNECTOR;

  /**
   * The feature id for the '<em><b>By</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__BY = CONNECTORS__BY;

  /**
   * The feature id for the '<em><b>On</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__ON = CONNECTORS__ON;

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
   * The feature id for the '<em><b>Dep Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__DEP_CONNECTOR = CONNECTORS__DEP_CONNECTOR;

  /**
   * The feature id for the '<em><b>Gen Dep Connector</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__GEN_DEP_CONNECTOR = CONNECTORS__GEN_DEP_CONNECTOR;

  /**
   * The feature id for the '<em><b>By</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__BY = CONNECTORS__BY;

  /**
   * The feature id for the '<em><b>On</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__ON = CONNECTORS__ON;

  /**
   * The number of structural features of the '<em>Or</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_FEATURE_COUNT = CONNECTORS_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl <em>Component</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.ComponentImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getComponent()
   * @generated
   */
  int COMPONENT = 7;

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
   * The feature id for the '<em><b>Comp</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__COMP = MODEL_OBJECT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Group of components</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__GROUP_OF_COMPONENTS = MODEL_OBJECT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Comps</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__COMPS = MODEL_OBJECT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Depends By</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__DEPENDS_BY = MODEL_OBJECT_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>By</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__BY = MODEL_OBJECT_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Depend On</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__DEPEND_ON = MODEL_OBJECT_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>On</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT__ON = MODEL_OBJECT_FEATURE_COUNT + 6;

  /**
   * The number of structural features of the '<em>Component</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPONENT_FEATURE_COUNT = MODEL_OBJECT_FEATURE_COUNT + 7;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.CompositImpl <em>Composit</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.CompositImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getComposit()
   * @generated
   */
  int COMPOSIT = 8;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__NAME = MODEL_OBJECT__NAME;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__ID = MODEL_OBJECT__ID;

  /**
   * The feature id for the '<em><b>X</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__X = MODEL_OBJECT__X;

  /**
   * The feature id for the '<em><b>Y</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__Y = MODEL_OBJECT__Y;

  /**
   * The feature id for the '<em><b>Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__WIDTH = MODEL_OBJECT__WIDTH;

  /**
   * The feature id for the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__HEIGHT = MODEL_OBJECT__HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__EXPANDED_WIDTH = MODEL_OBJECT__EXPANDED_WIDTH;

  /**
   * The feature id for the '<em><b>Expanded Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__EXPANDED_HEIGHT = MODEL_OBJECT__EXPANDED_HEIGHT;

  /**
   * The feature id for the '<em><b>Expanded</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__EXPANDED = MODEL_OBJECT__EXPANDED;

  /**
   * The feature id for the '<em><b>Subtype</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__SUBTYPE = MODEL_OBJECT__SUBTYPE;

  /**
   * The feature id for the '<em><b>Visible</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__VISIBLE = MODEL_OBJECT__VISIBLE;

  /**
   * The feature id for the '<em><b>Model Link Target</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__MODEL_LINK_TARGET = MODEL_OBJECT__MODEL_LINK_TARGET;

  /**
   * The feature id for the '<em><b>Super Composit</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__SUPER_COMPOSIT = MODEL_OBJECT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Components</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__COMPONENTS = MODEL_OBJECT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Compos</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__COMPOS = MODEL_OBJECT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Compo</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__COMPO = MODEL_OBJECT_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Chile Composit</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__CHILE_COMPOSIT = MODEL_OBJECT_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Composits</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT__COMPOSITS = MODEL_OBJECT_FEATURE_COUNT + 5;

  /**
   * The number of structural features of the '<em>Composit</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPOSIT_FEATURE_COUNT = MODEL_OBJECT_FEATURE_COUNT + 6;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.DependencyModelImpl <em>Dependency Model</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.DependencyModelImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getDependencyModel()
   * @generated
   */
  int DEPENDENCY_MODEL = 9;

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
   * The feature id for the '<em><b>SF Model</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__SF_MODEL = MODEL_OBJECT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Depmodel</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__DEPMODEL = MODEL_OBJECT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Root Model</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL__ROOT_MODEL = MODEL_OBJECT_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Dependency Model</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_MODEL_FEATURE_COUNT = MODEL_OBJECT_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.AttributeImpl <em>Attribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.AttributeImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getAttribute()
   * @generated
   */
  int ATTRIBUTE = 10;

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
   * The feature id for the '<em><b>Attri Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__ATTRI_NAME = MODEL_OBJECT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__VALUE = MODEL_OBJECT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__ATTRIBUTES = MODEL_OBJECT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Composite arrtibutes</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__COMPOSITE_ARRTIBUTES = MODEL_OBJECT_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Attribute</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_FEATURE_COUNT = MODEL_OBJECT_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.SimpleDependencyConnectionImpl <em>Simple Dependency Connection</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.SimpleDependencyConnectionImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getSimpleDependencyConnection()
   * @generated
   */
  int SIMPLE_DEPENDENCY_CONNECTION = 12;

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
   * The number of structural features of the '<em>Simple Dependency Connection</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SIMPLE_DEPENDENCY_CONNECTION_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.InputDependencyConnectionImpl <em>Input Dependency Connection</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.InputDependencyConnectionImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getInputDependencyConnection()
   * @generated
   */
  int INPUT_DEPENDENCY_CONNECTION = 13;

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
   * The number of structural features of the '<em>Input Dependency Connection</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INPUT_DEPENDENCY_CONNECTION_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.OutputDependencyConnectionImpl <em>Output Dependency Connection</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.OutputDependencyConnectionImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getOutputDependencyConnection()
   * @generated
   */
  int OUTPUT_DEPENDENCY_CONNECTION = 14;

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
   * The number of structural features of the '<em>Output Dependency Connection</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OUTPUT_DEPENDENCY_CONNECTION_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.smartfrog.authoringtool.emf.impl.RootImpl <em>Root</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.smartfrog.authoringtool.emf.impl.RootImpl
   * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getRoot()
   * @generated
   */
  int ROOT = 15;

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
   * The feature id for the '<em><b>Component</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__COMPONENT = 5;

  /**
   * The feature id for the '<em><b>Composit</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__COMPOSIT = 6;

  /**
   * The feature id for the '<em><b>Dependency Model</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__DEPENDENCY_MODEL = 7;

  /**
   * The feature id for the '<em><b>Attribute</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__ATTRIBUTE = 8;

  /**
   * The feature id for the '<em><b>Connectors</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__CONNECTORS = 9;

  /**
   * The feature id for the '<em><b>Simple Dependency Connection</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__SIMPLE_DEPENDENCY_CONNECTION = 10;

  /**
   * The feature id for the '<em><b>Input Dependency Connection</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__INPUT_DEPENDENCY_CONNECTION = 11;

  /**
   * The feature id for the '<em><b>Output Dependency Connection</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__OUTPUT_DEPENDENCY_CONNECTION = 12;

  /**
   * The number of structural features of the '<em>Root</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT_FEATURE_COUNT = 13;

int ATTRIBUTE__ATTRI__NAME = 0;


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
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.Component <em>Component</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Component</em>'.
   * @see org.smartfrog.authoringtool.emf.Component
   * @generated
   */
  EClass getComponent();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Component#getComp <em>Comp</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Comp</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getComp()
   * @see #getComponent()
   * @generated
   */
  EReference getComponent_Comp();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Component#getGroup_of_components <em>Group of components</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Group of components</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getGroup_of_components()
   * @see #getComponent()
   * @generated
   */
  EReference getComponent_Group_of_components();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Component#getComps <em>Comps</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Comps</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getComps()
   * @see #getComponent()
   * @generated
   */
  EReference getComponent_Comps();

  /**
   * Returns the meta object for the reference list '{@link org.smartfrog.authoringtool.emf.Component#getDepends_By <em>Depends By</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Depends By</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getDepends_By()
   * @see #getComponent()
   * @generated
   */
  EReference getComponent_Depends_By();

  /**
   * Returns the meta object for the reference list '{@link org.smartfrog.authoringtool.emf.Component#getBy <em>By</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>By</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getBy()
   * @see #getComponent()
   * @generated
   */
  EReference getComponent_By();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Component#getDependOn <em>Depend On</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Depend On</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getDependOn()
   * @see #getComponent()
   * @generated
   */
  EReference getComponent_DependOn();

  /**
   * Returns the meta object for the reference list '{@link org.smartfrog.authoringtool.emf.Component#getOn <em>On</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>On</em>'.
   * @see org.smartfrog.authoringtool.emf.Component#getOn()
   * @see #getComponent()
   * @generated
   */
  EReference getComponent_On();

  /**
   * Returns the meta object for class '{@link org.smartfrog.authoringtool.emf.Composit <em>Composit</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Composit</em>'.
   * @see org.smartfrog.authoringtool.emf.Composit
   * @generated
   */
  EClass getComposit();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Composit#getSuperComposit <em>Super Composit</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Super Composit</em>'.
   * @see org.smartfrog.authoringtool.emf.Composit#getSuperComposit()
   * @see #getComposit()
   * @generated
   */
  EReference getComposit_SuperComposit();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Composit#getComponents <em>Components</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Components</em>'.
   * @see org.smartfrog.authoringtool.emf.Composit#getComponents()
   * @see #getComposit()
   * @generated
   */
  EReference getComposit_Components();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Composit#getCompos <em>Compos</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Compos</em>'.
   * @see org.smartfrog.authoringtool.emf.Composit#getCompos()
   * @see #getComposit()
   * @generated
   */
  EReference getComposit_Compos();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Composit#getCompo <em>Compo</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Compo</em>'.
   * @see org.smartfrog.authoringtool.emf.Composit#getCompo()
   * @see #getComposit()
   * @generated
   */
  EReference getComposit_Compo();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Composit#getChileComposit <em>Chile Composit</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Chile Composit</em>'.
   * @see org.smartfrog.authoringtool.emf.Composit#getChileComposit()
   * @see #getComposit()
   * @generated
   */
  EReference getComposit_ChileComposit();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Composit#getComposits <em>Composits</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Composits</em>'.
   * @see org.smartfrog.authoringtool.emf.Composit#getComposits()
   * @see #getComposit()
   * @generated
   */
  EReference getComposit_Composits();

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
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.DependencyModel#getSFModel <em>SF Model</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>SF Model</em>'.
   * @see org.smartfrog.authoringtool.emf.DependencyModel#getSFModel()
   * @see #getDependencyModel()
   * @generated
   */
  EReference getDependencyModel_SFModel();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.DependencyModel#getDepmodel <em>Depmodel</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Depmodel</em>'.
   * @see org.smartfrog.authoringtool.emf.DependencyModel#getDepmodel()
   * @see #getDependencyModel()
   * @generated
   */
  EReference getDependencyModel_Depmodel();

  /**
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.DependencyModel#getRootModel <em>Root Model</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Root Model</em>'.
   * @see org.smartfrog.authoringtool.emf.DependencyModel#getRootModel()
   * @see #getDependencyModel()
   * @generated
   */
  EReference getDependencyModel_RootModel();

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
   * Returns the meta object for the attribute '{@link org.smartfrog.authoringtool.emf.Attribute#getAttri_Name <em>Attri Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attri Name</em>'.
   * @see org.smartfrog.authoringtool.emf.Attribute#getAttri_Name()
   * @see #getAttribute()
   * @generated
   */
  EAttribute getAttribute_Attri_Name();

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
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Attribute#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Attributes</em>'.
   * @see org.smartfrog.authoringtool.emf.Attribute#getAttributes()
   * @see #getAttribute()
   * @generated
   */
  EReference getAttribute_Attributes();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Attribute#getComposite_arrtibutes <em>Composite arrtibutes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Composite arrtibutes</em>'.
   * @see org.smartfrog.authoringtool.emf.Attribute#getComposite_arrtibutes()
   * @see #getAttribute()
   * @generated
   */
  EReference getAttribute_Composite_arrtibutes();

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
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Connectors#getDepConnector <em>Dep Connector</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Dep Connector</em>'.
   * @see org.smartfrog.authoringtool.emf.Connectors#getDepConnector()
   * @see #getConnectors()
   * @generated
   */
  EReference getConnectors_DepConnector();

  /**
   * Returns the meta object for the reference '{@link org.smartfrog.authoringtool.emf.Connectors#getGenDepConnector <em>Gen Dep Connector</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Gen Dep Connector</em>'.
   * @see org.smartfrog.authoringtool.emf.Connectors#getGenDepConnector()
   * @see #getConnectors()
   * @generated
   */
  EReference getConnectors_GenDepConnector();

  /**
   * Returns the meta object for the reference list '{@link org.smartfrog.authoringtool.emf.Connectors#getBy <em>By</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>By</em>'.
   * @see org.smartfrog.authoringtool.emf.Connectors#getBy()
   * @see #getConnectors()
   * @generated
   */
  EReference getConnectors_By();

  /**
   * Returns the meta object for the reference list '{@link org.smartfrog.authoringtool.emf.Connectors#getOn <em>On</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>On</em>'.
   * @see org.smartfrog.authoringtool.emf.Connectors#getOn()
   * @see #getConnectors()
   * @generated
   */
  EReference getConnectors_On();

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
   * Returns the meta object for the containment reference list '{@link org.smartfrog.authoringtool.emf.Root#getComposit <em>Composit</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Composit</em>'.
   * @see org.smartfrog.authoringtool.emf.Root#getComposit()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_Composit();

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
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.ComponentImpl <em>Component</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.ComponentImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getComponent()
     * @generated
     */
    EClass COMPONENT = eINSTANCE.getComponent();

    /**
     * The meta object literal for the '<em><b>Comp</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT__COMP = eINSTANCE.getComponent_Comp();

    /**
     * The meta object literal for the '<em><b>Group of components</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT__GROUP_OF_COMPONENTS = eINSTANCE.getComponent_Group_of_components();

    /**
     * The meta object literal for the '<em><b>Comps</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT__COMPS = eINSTANCE.getComponent_Comps();

    /**
     * The meta object literal for the '<em><b>Depends By</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT__DEPENDS_BY = eINSTANCE.getComponent_Depends_By();

    /**
     * The meta object literal for the '<em><b>By</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT__BY = eINSTANCE.getComponent_By();

    /**
     * The meta object literal for the '<em><b>Depend On</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT__DEPEND_ON = eINSTANCE.getComponent_DependOn();

    /**
     * The meta object literal for the '<em><b>On</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPONENT__ON = eINSTANCE.getComponent_On();

    /**
     * The meta object literal for the '{@link org.smartfrog.authoringtool.emf.impl.CompositImpl <em>Composit</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.smartfrog.authoringtool.emf.impl.CompositImpl
     * @see org.smartfrog.authoringtool.emf.impl.SmartfrogPackageImpl#getComposit()
     * @generated
     */
    EClass COMPOSIT = eINSTANCE.getComposit();

    /**
     * The meta object literal for the '<em><b>Super Composit</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPOSIT__SUPER_COMPOSIT = eINSTANCE.getComposit_SuperComposit();

    /**
     * The meta object literal for the '<em><b>Components</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPOSIT__COMPONENTS = eINSTANCE.getComposit_Components();

    /**
     * The meta object literal for the '<em><b>Compos</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPOSIT__COMPOS = eINSTANCE.getComposit_Compos();

    /**
     * The meta object literal for the '<em><b>Compo</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPOSIT__COMPO = eINSTANCE.getComposit_Compo();

    /**
     * The meta object literal for the '<em><b>Chile Composit</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPOSIT__CHILE_COMPOSIT = eINSTANCE.getComposit_ChileComposit();

    /**
     * The meta object literal for the '<em><b>Composits</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPOSIT__COMPOSITS = eINSTANCE.getComposit_Composits();

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
     * The meta object literal for the '<em><b>SF Model</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DEPENDENCY_MODEL__SF_MODEL = eINSTANCE.getDependencyModel_SFModel();

    /**
     * The meta object literal for the '<em><b>Depmodel</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DEPENDENCY_MODEL__DEPMODEL = eINSTANCE.getDependencyModel_Depmodel();

    /**
     * The meta object literal for the '<em><b>Root Model</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DEPENDENCY_MODEL__ROOT_MODEL = eINSTANCE.getDependencyModel_RootModel();

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
     * The meta object literal for the '<em><b>Attri Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE__ATTRI_NAME = eINSTANCE.getAttribute_Attri_Name();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE__VALUE = eINSTANCE.getAttribute_Value();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE__ATTRIBUTES = eINSTANCE.getAttribute_Attributes();

    /**
     * The meta object literal for the '<em><b>Composite arrtibutes</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE__COMPOSITE_ARRTIBUTES = eINSTANCE.getAttribute_Composite_arrtibutes();

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
     * The meta object literal for the '<em><b>Dep Connector</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONNECTORS__DEP_CONNECTOR = eINSTANCE.getConnectors_DepConnector();

    /**
     * The meta object literal for the '<em><b>Gen Dep Connector</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONNECTORS__GEN_DEP_CONNECTOR = eINSTANCE.getConnectors_GenDepConnector();

    /**
     * The meta object literal for the '<em><b>By</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONNECTORS__BY = eINSTANCE.getConnectors_By();

    /**
     * The meta object literal for the '<em><b>On</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONNECTORS__ON = eINSTANCE.getConnectors_On();

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
     * The meta object literal for the '<em><b>Component</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__COMPONENT = eINSTANCE.getRoot_Component();

    /**
     * The meta object literal for the '<em><b>Composit</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__COMPOSIT = eINSTANCE.getRoot_Composit();

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
