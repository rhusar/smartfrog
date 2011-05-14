package org.smartfrog.services.hadoop.benchmark.citerank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class HTMLTableLocal {

    private static final boolean PIXELS_ONLY = true;

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: HTMLTableLocal <input path> <rows> <columns>");
            System.exit(-1);
        }

        String inputDir = args[0];
        int rows = Integer.parseInt(args[1]);
        int columns = Integer.parseInt(args[2]);

        String[][] ranks = new String[rows][columns];
        ArrayList<String> correct = new ArrayList<String>();


        PrintWriter out = new PrintWriter(new FileWriter(inputDir + File.separator + CiteRankTool.RANKS_HTML));

        {
            BufferedReader in = new BufferedReader(new FileReader(
                    inputDir + File.separator + CiteRankTool.SORTED_RANKS + File.separator + "part-00000"));
            for (int i = 0; i < rows; i++) {
                StringTokenizer st = new StringTokenizer(in.readLine());
                correct.add(st.nextToken());
            }
            in.close();
        }


        for (int j = 0; j < columns; j++) {
            BufferedReader in = new BufferedReader(
                    new FileReader(inputDir + File.separator + CiteRankTool.SORTED_RANKS + "-" + j + ".dat"));
            for (int i = 0; i < rows; i++) {
                ranks[i][j] = in.readLine();
            }
            in.close();
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

            for (int k = 0; k < correct.size(); k++) {
                PrintWriter out2 =
                        new PrintWriter(new FileWriter(inputDir + File.separator + "ranks-" + (k + 1) + ".html"));
                out2.println("<a href=\"ranks-" + k + ".html\">prev</a> <a href=\"ranks-" + (k + 2) +
                        ".html\">next</a>");
                out2.println("<table cellspacing=\"2\" cellpadding=\"2\">");
                for (int i = 0; i < rows; i++) {
                    out2.print("<tr>");
                    for (int j = 0; j < columns; j++) {
                        String color = "#cc0000";
                        if (correct.get(k).equals(ranks[i][j])) {
                            color = "#000000";
                        } else if (correct.get(i).equals(ranks[i][j])) {
                            color = "#00cc00";
                        } else if (correct.contains(ranks[i][j])) {
                            color = "#cccc00";
                        }
                        out2.print("<td bgcolor=\"" + color + "\"><img src=\"pixel.gif\" width=4 height=4 /></td>");
                    }
                    out2.println("</tr>");
                }
                out2.println("</table>");
                out2.close();
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

        out.close();
    }

}
