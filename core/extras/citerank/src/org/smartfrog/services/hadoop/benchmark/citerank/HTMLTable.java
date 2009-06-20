/** (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.benchmark.citerank;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class HTMLTable extends CiteRankTool {

    private static final boolean PIXELS_ONLY = false;
    private static final String NAME = "HTMLTable";

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 3) {
            return usage("<input path> <rows> <columns>");
        }

        int rows = Integer.parseInt(args[1]);
        int columns = Integer.parseInt(args[2]);

        String[][] ranks = new String[rows][columns];
        ArrayList<String> correct = new ArrayList<String>();

        FileSystem fs = FileSystem.get(getConf());
        String inputDir = args[0];
        PrintWriter out = new PrintWriter(fs.create(new Path(inputDir + File.separator
                + CiteRankTool.RANKS_HTML)).getWrappedStream());
        BufferedReader in;

        try {
            in = new BufferedReader(new InputStreamReader(fs.open(new Path(inputDir + File.separator
                    + CiteRankTool.SORTED_RANKS + File.separator + "part-00000"))));
            try {
                for (int i = 0; i < rows; i++) {
                    StringTokenizer st = new StringTokenizer(in.readLine());
                    correct.add(st.nextToken());
                }
            } finally {
                in.close();
            }


            for (int j = 0; j < columns; j++) {
                in = new BufferedReader(new InputStreamReader(
                        fs.open(new Path(
                                inputDir + File.separator + CiteRankTool.SORTED_RANKS + "-" + j + ".dat"))));
                try {
                    for (int i = 0; i < rows; i++) {
                        ranks[i][j] = in.readLine();
                    }
                } finally {
                    in.close();
                }

            }

            out.println("<table cellspacing=\"2\" cellpadding=\"2\">");

            if (PIXELS_ONLY) {
                for (int i = 0; i < rows; i++) {
                    out.print("<tr>");
                    for (int j = 0; j < columns; j++) {
                        String color = "#cc0000";
                        if (correct.get(i).equals(ranks[i][j])) {
                            color = "#00cc00";
                        } else if (correct.contains(ranks[i][j])) {
                            color = "#cccc00";
                        }
                        out.print("<td bgcolor=\"" + color + "\"><img src=\"pixel.gif\" width=4 height=4 /></td>");
                    }
                    out.println("</tr>");
                }
            } else {
                out.print("<tr><td>&nbsp;</td>");
                for (int j = 0; j < columns; j++) {
                    out.print("<td>" + (j + 1) + "</td>");
                }
                out.println("</tr>");

                for (int i = 0; i < rows; i++) {
                    out.print("<tr><td>" + (i + 1) + "</td>");
                    for (int j = 0; j < columns; j++) {
                        String color = "#cc0000";
                        if (correct.get(i).equals(ranks[i][j])) {
                            color = "#00cc00";
                        } else if (correct.contains(ranks[i][j])) {
                            color = "#cccc00";
                        }

                        out.print("<td bgcolor=\"" + color + "\">" + ranks[i][j] + "</td>");
                    }
                    out.println("</tr>");
                }
            }

            out.println("</table>");
        } finally {
            out.close();
        }


        return 0;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new Configuration(), new HTMLTable(), args);
        System.exit(exitCode);
    }

}
