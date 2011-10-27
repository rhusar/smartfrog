package org.smartfrog.services.jetty.examples.servlets;

import org.smartfrog.services.jetty.contexts.delegates.DelegateHelper;
import org.smartfrog.services.jetty.utils.ServletUtils;
import org.smartfrog.services.www.HttpHeaders;
import org.smartfrog.services.www.ServletContextComponent;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.RemoteToString;
import org.smartfrog.sfcore.reference.Reference;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;

/**
 * This is a servlet designed to cause trouble
 * Options
 *  -failOnStartup
 *  -startupDelay in mS = time to sleep in the init() phase
 *  -all ops are logged
 *
 */

public class TroublesomeServlet extends HttpServlet {

    private static final Reference STARTUP_TIME = new Reference("startupTime");
    private Log log = LogFactory.getLog(this.getClass());
    protected boolean failOnStartup;

    protected volatile boolean initialised;

    @Override
    public void init() throws ServletException {
        try {
            log.info("initalizing");
            ServletContext ctx = getServletContext();
            Prim owner = DelegateHelper.retrieveOwner(ctx);
            log.info("owner :" + ((RemoteToString) owner).sfRemoteToString());
            Prim container = owner.sfResolve(ServletContextComponent.ATTR_SERVLET_CONTEXT, (Prim) null, true);
            int startupTime;
            startupTime = (Integer) container.sfResolve(STARTUP_TIME, true);
            if (startupTime > 0) {
                Thread.sleep(startupTime);
            }
            failOnStartup = (Boolean) container.sfResolve(new Reference("failOnStartup"), true);
            initialised = true;
            if (failOnStartup) {
                log.info("Failing");
                throw new ServletException("Explicit Fail on Startup");
            }
        } catch (InterruptedException e) {
            log.error(e.toString(), e);
            throw new ServletException(e);
        } catch (RemoteException e) {
            log.error(e.toString(), e);
            throw new ServletException(e);
        } catch (SmartFrogException e) {
            log.error(e.toString(), e);
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        log.info("incoming request" + request.getPathTranslated());
        try {
            if (!initialised) {
                throw new ServletException("Not yet fully initialised");
            }
            if (failOnStartup) {
                throw new ServletException("Fail on startup engaged");
            }
        } catch (ServletException e) {
            log.error(e.toString(), e);
            PrintWriter out = beginResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeHtmlPage(out, "Troublesome Servlet: ERROR", "Error + "+ e.toString());
            endResponse(out);
        }

        PrintWriter out = beginResponse(response, HttpServletResponse.SC_OK);
        writeHtmlPage(out, "Troublesome Servlet", "we are live");
        endResponse(out);
    }

    private void endResponse(final PrintWriter out) {
        out.flush();
        out.close();
    }

    private void writeHtmlPage(final PrintWriter out, final String h1Text, final String bodytext) {
        ServletUtils.open(out, "html");
        ServletUtils.element(out, "head", "Context Attributes");
        ServletUtils.open(out, "body");
        ServletUtils.element(out, "h1", h1Text);
        ServletUtils.element(out, "p", bodytext);
        ServletUtils.close(out, "body");
        ServletUtils.close(out, "html");
    }

    private PrintWriter beginResponse(final HttpServletResponse response, final int status) throws IOException {
        response.setStatus(status);
        response.setContentType(HttpHeaders.TEXT_HTML);
        return response.getWriter();
    }
}
