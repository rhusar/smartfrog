/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/

package org.smartfrog.services.www.bulkio.server;

import org.apache.log4j.helpers.ISO8601DateFormat;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

/**
 * This servlet does long lived GET operations
 */

public class SlowGetServlet extends AbstractBulkioServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long hours = getParameterAsLong(request, "hours");
        if (hours <= 0) {
            hours = 0;
        }
        long minutes = getParameterAsLong(request, "minutes");
        if (minutes <= 0) {
            minutes = 0;
        }
        long seconds = getParameterAsLong(request, "seconds");
        if (seconds <= 0) {
            seconds = 0;
        }
        long millis = getParameterAsLong(request, "milliseconds");
        if (millis <= 0) {
            millis = 0;
        }

        long events = getParameterAsLong(request, "events");
        if (events <= 0) {
            throw new ServletException("Missing or wrong events parameter " + events);
        }

        long time = millis + 1000 * (seconds + minutes * 60 + hours * 3600);
        long interval = time / events;
        if (interval <= 0) {
            throw new ServletException("Bad time interval " + interval);
        }
        getLog().info("Serving an event every " + interval + " milliseconds "
                + " for " + events + " events; "
                + " total time:" + time + " milliseconds");
        response.setContentType(TEXT_PLAIN);

        ServletOutputStream outputStream = null;
        outputStream = response.getOutputStream();
        DateFormat dateFormatter = new ISO8601DateFormat();
        long count = 0;
        try {
            for (count = 1; count <= events; count++) {
                Thread.sleep(interval);
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                StringBuilder message = new StringBuilder();
                message.append(now).append(", ");
                message.append(count).append(", ");
                message.append(events).append(", ");
                message.append('"').append(dateFormatter.format(date)).append("\"");
                String text = message.toString();
                getLog().info(text);
                outputStream.println(text);
                outputStream.flush();
                response.flushBuffer();
            }
        } catch (InterruptedException ie) {
            String message = "Interrupted at event " + count;
            getLog().info(message);
            throw new ServletException(message, ie);
        } finally {
            closeQuietly(outputStream);
        }
    }
}
