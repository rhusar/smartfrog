package org.smartfrog;

import org.smartfrog.sfcore.deployer.CodeRepository;
import org.smartfrog.sfcore.deployer.CoreClassesClassLoadingEnvironment;
import org.smartfrog.sfcore.security.SFSecurity;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.MessageKeys;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.URL;

public class SFLoader {

    private SFLoader() {}

    /**
     * Loads a resource, either directly or from a code repository, checking security if needed.
     * <br/>
     * The resource name can be of the following forms :
     * <ul>
     * <li>A properly formatted URL.</li>
     * <li>A file name, absolute or relative to the current directory of the running SF daemon.</li>
     * <li>A path to a resource in the given CodeRepository.</li>
     * </ul>
     * @param resource The name of the resource.
     * @param repository The CodeRepository to look in. Can be null, in this case the core classes repository is used.
     * @return An InputStream to the resource.
     */
    public static InputStream getInputStream(String resource, CodeRepository repository) throws IOException {
        URL resourceURL;

        try {
            // Try first to directly generate a URL from resource
            resourceURL = new URL(resource);

            return SFSecurity.getSecureInputStream(resourceURL);
        } catch (Throwable e) {
            // Didn't work, the input is a malformed url or it is inside a
            // jar file and this is not explicit in the url.
        }

        try {
            // Let's use a relative file path...
            resourceURL = SFClassLoader.stringToURL(resource);

            return SFSecurity.getSecureInputStream(resourceURL);
        } catch (Throwable e) {
            // Still in trouble, cannot obtain the resource from the file
            // system directly.
        }

        // The forward / does not work when using ClassLoader.getResourceAsStream
        String resourceInJar = (resource.startsWith("/")
                ? resource.substring(1) : resource);

        // Try the class loaders
        if (repository != null) return repository.getResourceAsStream(resourceInJar);
        else return defaultRepository().getResourceAsStream(resourceInJar);
    }

    static CodeRepository defaultRepository() {
        return new CoreClassesClassLoadingEnvironment();
    }

    /**
     * Gets ByteArray for the given resource. Throws exception if stream is
     * null.
     * @param resourceSFURL Name of the resource. SF url valid.
     * @return ByteArray (byte []) with the resource data
     * @throws org.smartfrog.sfcore.common.SmartFrogException if input stream could not be created for the
     * resource
     * @see org.smartfrog.sfcore.security.SFClassLoader
     */
    public static byte[] getByteArrayForResource(String resourceSFURL) throws SmartFrogException {
        ByteArrayOutputStream bStrm = null;
        DataInputStream iStrm = null;
        try {
            iStrm = new DataInputStream(SFSystem.getInputStreamForResource(resourceSFURL));
            byte resourceData[];
            bStrm = new ByteArrayOutputStream();
            int ch;
            while ((ch = iStrm.read()) != -1) {
                bStrm.write(ch);
            }
            resourceData = bStrm.toByteArray();
            bStrm.close();
            return resourceData;
        } catch (IOException ex) {
            throw SmartFrogException.forward(ex);
        } finally {
            if (bStrm != null) { try { bStrm.close();} catch (IOException swallowed) { } }
            if (iStrm != null) { try { iStrm.close();} catch (IOException swallowed) { } }
        }
    }

    public static InputStream getInputStreamSFException(String url) throws SmartFrogParseException {
        InputStream is;
        try {
            // TODO: Check if access to a user code repository is needed
            is = getInputStream(url, null);
        } catch (IOException e) {
            String msg = MessageUtil.
                    formatMessage(MessageKeys.MSG_URL_TO_PARSE_NOT_FOUND, url);
            throw new SmartFrogParseException(msg, e);
        }
        return is;
    }
}
