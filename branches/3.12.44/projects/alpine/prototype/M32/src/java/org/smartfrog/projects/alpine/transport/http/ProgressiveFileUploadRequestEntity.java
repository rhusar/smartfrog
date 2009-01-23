package org.smartfrog.projects.alpine.transport.http;

import org.apache.commons.httpclient.methods.RequestEntity;
import org.smartfrog.projects.alpine.transport.ProgressFeedback;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.projects.alpine.transport.ProgressCancelledFault;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Handle restartable uploads
 */
public class ProgressiveFileUploadRequestEntity implements RequestEntity {

    private File file = null;


    /**
     * any progress indicator
     */
    ProgressFeedback progress;
    private long length;
    private int blocksize;
    Transmission tx;
    MessageDocument message;
    String mimeType;

    public ProgressiveFileUploadRequestEntity(Transmission tx,
                                              MessageDocument message,
                                              File file,
                                              String mimeType,
                                              ProgressFeedback progress,
                                              int blocksize) {
        super();
        this.tx=tx;
        this.message=message;
        this.file = file;
        this.mimeType=mimeType;
        this.blocksize = blocksize;
        length = file.length();
    }

    public boolean isRepeatable() {
        return true;
    }

    public String getContentType() {
        return mimeType;
    }

    public void writeRequest(OutputStream out) throws IOException {
        InputStream in = new FileInputStream(this.file);
        try {
            int l;
            long count=0;
            byte[] buffer = new byte[blocksize];
            while ((l = in.read(buffer)) != -1) {
                out.write(buffer, 0, l);
                count+=l;
                if(progress!=null && !progress.tick(tx,message,count,length)) {
                    throw new ProgressCancelledFault("Upload cancelled");
                }
            }
        } finally {
            in.close();
        }
    }

    public long getContentLength() {
        return length;
    }
}


