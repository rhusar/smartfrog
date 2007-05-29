/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.nbm;

import javax.swing.*;
import java.io.*;
import java.util.prefs.*;

final class SmartfrogsvcPanel extends javax.swing.JPanel {
    
    private final SmartfrogsvcOptionsPanelController controller;
    
    SmartfrogsvcPanel(SmartfrogsvcOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        // TODO listen to changes in form fields and call controller.changed()
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jTextFieldSFHOME = new javax.swing.JTextField();
        jButtonShowFC = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButtonAddPath = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaSfUserHome = new javax.swing.JTextArea();
        jCheckBoxRestrictToIncludes = new javax.swing.JCheckBox();
        jTextFieldQuietTime = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        setBackground(java.awt.Color.white);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "SmartFrog Home");

        jTextFieldSFHOME.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTextFieldSFHOMEPropertyChange(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonShowFC, "...");
        jButtonShowFC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonShowFCActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "SmartFrogUserHome");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonAddPath, "Add...");
        jButtonAddPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddPathActionPerformed(evt);
            }
        });

        jTextAreaSfUserHome.setColumns(20);
        jTextAreaSfUserHome.setLineWrap(true);
        jTextAreaSfUserHome.setRows(5);
        jScrollPane1.setViewportView(jTextAreaSfUserHome);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxRestrictToIncludes, "Restrict extends completion to components from include files");
        jCheckBoxRestrictToIncludes.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxRestrictToIncludes.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jTextFieldQuietTime.setText("1");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "Quiet time delay before auto reparse in seconds");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jCheckBoxRestrictToIncludes)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jTextFieldSFHOME, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jButtonShowFC)
                            .add(jButtonAddPath)))
                    .add(layout.createSequentialGroup()
                        .add(jTextFieldQuietTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel3)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jButtonShowFC)
                    .add(jTextFieldSFHOME, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(25, 25, 25)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel2)
                        .add(9, 9, 9)
                        .add(jButtonAddPath))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 207, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(23, 23, 23)
                .add(jCheckBoxRestrictToIncludes)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextFieldQuietTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addContainerGap(18, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
        
    private void jButtonAddPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddPathActionPerformed
        javax.swing.JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.OPEN_DIALOG) {
            File f = fc.getSelectedFile();
            if (jTextAreaSfUserHome.getText().length() > 0) {
                jTextAreaSfUserHome.setText(jTextAreaSfUserHome.getText() + File.pathSeparator+ f.getAbsolutePath());
            } else {
                jTextAreaSfUserHome.setText(f.getAbsolutePath());
            }
            controller.changed();
        }
    }//GEN-LAST:event_jButtonAddPathActionPerformed
    
    private void jTextFieldSFHOMEPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTextFieldSFHOMEPropertyChange
        controller.changed();
    }//GEN-LAST:event_jTextFieldSFHOMEPropertyChange
    
    private void jButtonShowFCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShowFCActionPerformed
        javax.swing.JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.OPEN_DIALOG) {
            File f = fc.getSelectedFile();
            this.jTextFieldSFHOME.setText(f.getAbsolutePath());
            controller.changed();
        }
    }//GEN-LAST:event_jButtonShowFCActionPerformed
    
    void load() {
        jTextFieldSFHOME.setText(Preferences.userNodeForPackage(SmartfrogsvcPanel.class).get(SmartfrogsvcAdvancedOption.SFHOMEVAR,""));
        jTextAreaSfUserHome.setText(Preferences.userNodeForPackage(SmartfrogsvcPanel.class).get(SmartfrogsvcAdvancedOption.SFUSERHOMEVAR,""));
        jCheckBoxRestrictToIncludes.setSelected(Preferences.userNodeForPackage(SmartfrogsvcPanel.class).getBoolean(SmartfrogsvcAdvancedOption.SFRESTRICTINCLUDE,true));
        jTextFieldQuietTime.setText(Preferences.userNodeForPackage(SmartfrogsvcPanel.class).get(SmartfrogsvcAdvancedOption.SFQUIETTIME,"1"));
    }
    
    void store() {
        Preferences.userNodeForPackage(SmartfrogsvcPanel.class).put(SmartfrogsvcAdvancedOption.SFHOMEVAR,jTextFieldSFHOME.getText());
        Preferences.userNodeForPackage(SmartfrogsvcPanel.class).put(SmartfrogsvcAdvancedOption.SFUSERHOMEVAR,jTextAreaSfUserHome.getText());
        Preferences.userNodeForPackage(SmartfrogsvcPanel.class).put(SmartfrogsvcAdvancedOption.SFQUIETTIME,jTextFieldQuietTime.getText());
        if (this.jCheckBoxRestrictToIncludes.isSelected()) {
            Preferences.userNodeForPackage(SmartfrogsvcPanel.class).putBoolean(SmartfrogsvcAdvancedOption.SFRESTRICTINCLUDE,true);
        } else {
            Preferences.userNodeForPackage(SmartfrogsvcPanel.class).putBoolean(SmartfrogsvcAdvancedOption.SFRESTRICTINCLUDE,false);
        }
        SmartFrogSvcUtil.rebuildInfo();
    }
    
    boolean valid() {
        return true;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddPath;
    private javax.swing.JButton jButtonShowFC;
    private javax.swing.JCheckBox jCheckBoxRestrictToIncludes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaSfUserHome;
    private javax.swing.JTextField jTextFieldQuietTime;
    private javax.swing.JTextField jTextFieldSFHOME;
    // End of variables declaration//GEN-END:variables
    
}
