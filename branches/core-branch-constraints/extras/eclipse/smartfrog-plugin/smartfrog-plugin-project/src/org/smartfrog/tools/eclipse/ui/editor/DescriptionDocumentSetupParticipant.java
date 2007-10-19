
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


package org.smartfrog.tools.eclipse.ui.editor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;


/**
 * Link document to the editor
 */
public class DescriptionDocumentSetupParticipant
    implements IDocumentSetupParticipant
{
    /*
     * @see org.eclipse.core.filebuffers.IDocumentSetupParticipant#setup(org.eclipse.jface.text.IDocument)
     */
    public void setup(IDocument document)
    {
        if (document instanceof IDocumentExtension3) {
            IDocumentExtension3 extension3 = (IDocumentExtension3)document;
            IDocumentPartitioner partitioner = new DefaultPartitioner(
                    DescriptionPartitionScanner.getInstance(),
                    DescriptionPartitionScanner.SMARTFROG_PARTITION_TYPES);
            extension3.setDocumentPartitioner(
                SmartFrogPlugin.SMARTFROG_PARTITIONING, partitioner);
            partitioner.connect(document);
        }
    }
}
