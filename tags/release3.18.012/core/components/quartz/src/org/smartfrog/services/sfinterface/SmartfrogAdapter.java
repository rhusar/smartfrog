package org.smartfrog.services.sfinterface;

import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

import java.util.Map;

/**
 * @author sanjay, May 5, 2005
 *         <p/>
 *         This interface is implemented by class providing smartfrog description
 *         submission capability. These methods take an InputStream for smartfrog description
 *         because all the descriptions are stored in Avalanche server's database. While submitting
 *         a description is fetched from the database and then passed over to smartfrog submission API.
 */
public interface SmartfrogAdapter {

    /**
     * Submits a deployment request on a single host.
     * No monitoring parent component is
     * created on the calling node. . Description file need not contain values of all the needed attributes,
     * some of the attribute whose value needs to be dynamically decided on Avalanche server before
     * submission, can be passed in this map.
     *
     * @param descriptionFile .sf file content to submit.
     * @param attributes      map of all dynamically resolved attributes.
     * @param host            host to submit this description.
     * @throws SFParseException  if the description is invalid.
     * @throws SFSubmitException if the submission itself fails, this is not when the
     *                           description is successfully sent to host and while execution some error comes. This
     *                           exception is throws only if the description could not be sent successfully to host.
     */
    public Map submit(String descriptionFile, Map attributes, String host)
            throws SFParseException, SFSubmitException, SmartFrogRuntimeException;

    /**
     * Submits a deployment request on multiple host.
     * It validates the description file,
     * performs the necessary attribute replacements and submits the description directly
     * to the smartfrog daemon running on these hosts. No monitoring parent component is
     * created on the calling node. It is not necessary to have a smartfrog daemon running
     * on the calling node.
     * Description file need not contain value of all the needed attributes, some of the attribute
     * whose value needs to be dynamically decided on Avalanche server before submission, can
     * be passed in this map. (If some mendatory attribute doesnt contain a default value in the
     * sf file and map also doesnt contain a value, then SFParseException is thrown.) After resolving
     * the values on the submitting node the same description is submitted to all the hosts.
     * If submission fails on one host then it should try to submit on all remaining hosts before
     * throwing exception. SFMultiHostSubmitException contains a list of all hosts that failed
     * with the SfSubmitException for each host.
     * An implementing class can choose to submit on multiple hosts parellely in different threads but
     * it is important that exceptions are accumulated until all hosts are done.
     *
     * @param descriptionFile .sf file name to submit on the repository.
     * @param attributes      Attributes to replace in the description file dynamically.
     * @param hosts
     * @throws SFParseException          if the SF in the inputStream is invalid, or if map doesnt contain
     *                                   some mendatory attribute.
     * @throws SFMultiHostSubmitException if the file and attributes are proper but runtime submission fails
     *                                   on one or more nodes due to any error.
     */
    public Map submit(String descriptionFile, Map attributes, String[] hosts)
            throws SFParseException, SFMultiHostSubmitException;


    /**
     * submits a deployment request on the local host.
     * The description is submitted to a
     * local smartfrog daemon
     * -- This method will primarily be used in host ignition using sfSelfInstaller. --
     *
     * @param descriptionFile description file to submit.
     * @param attributes      map of dynamically resolved attributes.
     * @throws SFParseException  if the file is invalid.
     * @throws SFSubmitException if the execution of the description fails.
     */

    public Map submit(String descriptionFile, Map attributes)
            throws SFParseException, SFSubmitException, SmartFrogRuntimeException;

    /**
     * Submit a complete description to the local smartfrog daemon running in a different JVM.
     *
     * @param descriptionFile file to submit
     * @throws SFParseException  if the description file is invalid
     * @throws SFSubmitException if the submission or execution of the component fails.
     */
    public Map submit(String descriptionFile)
            throws SFParseException, SFSubmitException, SmartFrogRuntimeException;

    public void enableDyanamicClassLoading(String codebase);

    public void disableDyanamicClassLoading();


}
