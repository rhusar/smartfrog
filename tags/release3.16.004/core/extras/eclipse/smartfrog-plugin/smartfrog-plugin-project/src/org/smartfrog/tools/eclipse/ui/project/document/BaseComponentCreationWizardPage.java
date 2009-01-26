
/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.tools.eclipse.ui.project.document;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import java.util.List;


/**
 * Basic component creation wizard
 */
/**
 */
public class BaseComponentCreationWizardPage
    extends NewClassWizardPage
{
    private static final String SFTERMINATEWITH__METHOD_DESCRIPTION = "public synchronized void sfTerminateWith(...) ..."; //$NON-NLS-1$
    private static final String SFDEPLOY__METHOD_DESCRIPTION = "public synchronized void sfDeploy() ..."; //$NON-NLS-1$
    private static final String SFSTART_METHOD_DESCRIPTION = "public synchronized void sfStart() ..."; //$NON-NLS-1$
    private static final String TERMINATIONRECORD_VAR = "TerminationRecord"; //$NON-NLS-1$
    private static final String REMOTEEXCEPTION_NAME = "RemoteException"; //$NON-NLS-1$
    private static final String SMARTFROGEXCEPTION_NAME = "SmartFrogException"; //$NON-NLS-1$
    private static final String SFTERMINATE_STATUS_ARGUMENT_NAME = "status"; //$NON-NLS-1$
    private static final String SFTERMINATEWITH_METHOD_NAME = "sfTerminateWith"; //$NON-NLS-1$
    private static final String SFDEPLOY_METHOD_NAME = "sfDeploy"; //$NON-NLS-1$
    private static final String SFSTART_METHOD_NAME = "sfStart"; //$NON-NLS-1$
    public static final String DEFAULT_DESCRIPTION_EXT = ".java"; //$NON-NLS-1$
    private Button[] mButtons = new Button[ BUTTON_NUM ];
    private boolean[] fButtonsSelected = new boolean[ BUTTON_NUM ];
    private static final int BUTTON_NUM = 3;

    /**
     * Constructor
     * @param  workbench         The current workbench
     * @param  selection         The current resource selection
     * @param  selectedProject   The project that is highlighted or contain the highlighted folder
     *
     */
    public BaseComponentCreationWizardPage()
    {
        super();

        for (int i = 0; i < fButtonsSelected.length; i++) {
            fButtonsSelected[ i ] = true;
        }
    }

    /** (non-Javadoc)
     * Method declared on IDialogPage.
     */
    public void createControl(Composite parent)
    {
        super.createControl(parent);

        Composite composite = (Composite)getControl();
        createSeparator(composite, 4); //this is number get from parent
        createSmartFrogMethodsGroup(composite);
        setMethodStubSelection(false, true, true, true);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#createType(org.eclipse.core.runtime.IProgressMonitor)
     * Create the SmartFrog method stubs base on user selection
     */
    public void createType(IProgressMonitor monitor)
        throws CoreException, InterruptedException
    {
        super.createType(monitor);

        ICompilationUnit cu = ( this.getCreatedType().getCompilationUnit() );
        cu.createImport("org.smartfrog.sfcore.common.SmartFrogException", null, //$NON-NLS-1$
            null);
        cu.createImport("org.smartfrog.sfcore.prim.TerminationRecord", null, //$NON-NLS-1$
            null);

        cu.becomeWorkingCopy(null, null);

        ASTParser parser = ASTParser.newParser(AST.JLS2);
        parser.setSource(cu);

        CompilationUnit astRoot = (CompilationUnit)parser.createAST(null);
        AST ast = astRoot.getAST();
        astRoot.recordModifications();

        TypeDeclaration typeDeclaration = (TypeDeclaration)astRoot.types().get(
                0);

        boolean hasChange = false;

        if (fButtonsSelected[ 0 ]) {
            addSfDeployMethod(cu, astRoot, typeDeclaration, SFSTART_METHOD_NAME,
                true, false);
            hasChange = true;
        }

        if (fButtonsSelected[ 1 ]) {
            addSfDeployMethod(cu, astRoot, typeDeclaration,
                SFDEPLOY_METHOD_NAME, true, false);
            hasChange = true;
        }

        if (fButtonsSelected[ 2 ]) {
            addSfDeployMethod(cu, astRoot, typeDeclaration,
                SFTERMINATEWITH_METHOD_NAME, false, true);
            hasChange = true;
        }

        if (hasChange) {
            // apply the change, if any methods are added
            String source = cu.getBuffer().getContents();
            Document document = new Document(source);
            TextEdit edit = astRoot.rewrite(document, null);

            try {
                edit.apply(document);
            } catch (MalformedTreeException e) {
                e.printStackTrace();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            String st = document.get();
            cu.getBuffer().setContents(document.get());
        }

        cu.commitWorkingCopy(false, new SubProgressMonitor(monitor, 1));
        cu.discardWorkingCopy();
    }

    /**
     * add smartFrog methods to template (memory only)
     * @param cu
     * @param unit
     * @param typeDeclaration
     * @param methodName
     * @param needToThrow
     * @param needParam
     * @throws CoreException
     * @throws InterruptedException
     */
    private void addSfDeployMethod(ICompilationUnit cu, CompilationUnit unit,
        TypeDeclaration typeDeclaration, String methodName, boolean needToThrow,
        boolean needParam)
        throws CoreException, InterruptedException
    {
        AST localAst = typeDeclaration.getAST();
        MethodDeclaration methodDeclaration = localAst.newMethodDeclaration();
        methodDeclaration.setConstructor(false);
        methodDeclaration.setModifiers(Modifier.PUBLIC | Modifier.SYNCHRONIZED);
        methodDeclaration.setName(localAst.newSimpleName(methodName));
        methodDeclaration.setReturnType(localAst.newPrimitiveType(
                PrimitiveType.VOID));

        if (needParam) {
            SingleVariableDeclaration variableDeclaration = localAst
                .newSingleVariableDeclaration();
            variableDeclaration.setModifiers(Modifier.NONE);
            variableDeclaration.setType(localAst.newSimpleType(
                    localAst.newSimpleName(TERMINATIONRECORD_VAR)));
            variableDeclaration.setName(localAst.newSimpleName(
                    SFTERMINATE_STATUS_ARGUMENT_NAME));
            methodDeclaration.parameters().add(variableDeclaration);
        }

        Block block = localAst.newBlock();
        methodDeclaration.setBody(block);

        SuperMethodInvocation superMethodInvocation = localAst
            .newSuperMethodInvocation();
        superMethodInvocation.setName(localAst.newSimpleName(methodName));

        if (needParam) {
            List list = superMethodInvocation.arguments();
            list.add(localAst.newSimpleName(SFTERMINATE_STATUS_ARGUMENT_NAME));
        }

        ExpressionStatement expressionStatement = localAst
            .newExpressionStatement(superMethodInvocation);
        block.statements().add(expressionStatement);

        if (needToThrow) {
            List throwsExceptions = methodDeclaration.thrownExceptions();
            throwsExceptions.add(localAst.newSimpleName(
                    SMARTFROGEXCEPTION_NAME));
            throwsExceptions.add(localAst.newSimpleName(REMOTEEXCEPTION_NAME));
        }

        typeDeclaration.bodyDeclarations().add(methodDeclaration);
    }

    private void createSmartFrogMethodsGroup(Composite parent)
    {
        Label smartFrogLabel = new Label(parent, SWT.LEFT | SWT.WRAP);
        smartFrogLabel.setFont(parent.getFont());
        LayoutUtil.setHorizontalSpan(smartFrogLabel, 4);
        smartFrogLabel.setText(
            ( Messages.getString("BaseComponentCreationWizardPage.test.SmartFrogStubs") )); //$NON-NLS-1$

        Composite selectionButtonsGroup = getSelectionButtonsGroup(parent);
        LayoutUtil.setHorizontalSpan(selectionButtonsGroup, 3);
    }

    /**
     * Create the SmartFrog selection group, the alignment is base on Eclipse 3.0
     * @param parent
     * @return
     */
    public Composite getSelectionButtonsGroup(Composite parent)
    {
        GridLayout layout = new GridLayout();
        layout.makeColumnsEqualWidth = false;
        layout.numColumns = 2;

        Composite fButtonComposite = new Composite(parent, SWT.NULL);
        layout.marginHeight = 0;
        layout.marginWidth = 0;

        fButtonComposite.setLayout(layout);

        SelectionListener listener = new SelectionListener() {
                public void widgetDefaultSelected(SelectionEvent e)
                {
                    doWidgetSelected(e);
                }

                public void widgetSelected(SelectionEvent e)
                {
                    doWidgetSelected(e);
                }
            };

        mButtons[ 0 ] = createButton(fButtonComposite,
                SFSTART_METHOD_DESCRIPTION, listener);
        mButtons[ 1 ] = createButton(fButtonComposite,
                SFDEPLOY__METHOD_DESCRIPTION, listener);
        mButtons[ 2 ] = createButton(fButtonComposite,
                SFTERMINATEWITH__METHOD_DESCRIPTION, listener);

        return fButtonComposite;
    }

    /**
     * @param fButtonComposite
     */
    private Button createButton(Composite fButtonComposite, String label,
        SelectionListener listener)
    {
        Label emptySpace = new Label(fButtonComposite, SWT.LEFT);
        emptySpace.setFont(fButtonComposite.getFont());
        emptySpace.setText("                               "); //$NON-NLS-1$

        Button button = new Button(fButtonComposite, SWT.LEFT | SWT.CHECK);
        button.setLayoutData(new GridData());
        button.setText(label);
        button.setSelection(true);
        button.addSelectionListener(listener);

        return button;
    }

    private void doWidgetSelected(SelectionEvent e)
    {
        Button button = (Button)e.widget;

        for (int i = 0; i < mButtons.length; i++) {
            if (mButtons[ i ] == button) {
                fButtonsSelected[ i ] = button.getSelection();

                return;
            }
        }
    }

    private static Control createEmptySpace(Composite parent, int span)
    {
        Label label = new Label(parent, SWT.LEFT);
        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = false;
        gd.horizontalSpan = span;
        gd.horizontalIndent = 0;
        gd.widthHint = 0;
        gd.heightHint = 0;
        label.setLayoutData(gd);

        return label;
    }

   
}
