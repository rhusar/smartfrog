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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

public class CiteRank extends CiteRankTool {

    public static final int NUM_REDUCE_TASKS = 6;
    public static final DecimalFormat FORMATTER = new DecimalFormat("#.####################");
    private static final int HTML_TABLE_ROWS = 200;
    private static final boolean HTML_TABLE = true;

    @Override
    protected String getName() {
        return "CiteRank";
    }

    private static String read(FileSystem fs, String path) throws IOException {
        BufferedReader in = null;
        String result;
        try {
            Path part_0 = new Path(path + File.separator + "part-00000");
            in = new BufferedReader(new InputStreamReader(fs.open(part_0)));
            StringTokenizer st = new StringTokenizer(in.readLine());
            st.nextToken();
            result = st.nextToken();
        } finally {
            close(in);
        }
        return result;
    }

    private static void overwrite(FileSystem fs, Path src, Path dst) throws IOException {
        fs.delete(dst, true);
        fs.rename(src, dst);
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 4) {
            return usage("<input path> <output path> <iterations> <tolerance>");
        }

        FileSystem fs = FileSystem.get(getConf());
        String inpath = args[0];
        String outpath = args[1];
        String input = outpath + File.separator + CiteRankTool.PREVIOUS_RANKS;
        String output = outpath + File.separator + CiteRankTool.CURRENT_RANKS;
        int iterationLimit = Integer.parseInt(args[2]);
        final double toleranceArg = Double.parseDouble(args[3]);
        //clean the data up
        exec("Data cleanup", new CheckingData(), inpath, output);
        //count the data
        String countFile = outpath + File.separator + CiteRankTool.COUNT;
        exec("Page count", new CountPages(), output, countFile);
        String count = read(fs, countFile);
        exec("InitializeRanks", new InitializeRanks(), output, input, count);
        int iterations = 0;
        String sortedRanksDir = outpath + File.separator +
                CiteRankTool.SORTED_RANKS;
        while (iterations < iterationLimit) {
            String danglingFile = outpath + File.separator + CiteRankTool.DANGLING;
            exec("DanglingPages", new DanglingPages(), input, danglingFile);
            String dangling = read(fs, danglingFile);
            exec("UpdateRanks", new UpdateRanks(), input, output, count, dangling);
            overwrite(fs, new Path(output), new Path(input));
            if ((iterations > CiteRankTool.CHECK_CONVERGENCE_FREQUENCY)
                    && (iterations % CiteRankTool.CHECK_CONVERGENCE_FREQUENCY == 0)) {
                String convergenceFile = outpath + File.separator + CiteRankTool.CONVERGENCE;
                exec("CheckConvergence", new CheckConvergence(),
                        input,
                        convergenceFile);
                double tolerance = Double.parseDouble(read(fs, convergenceFile));

                if (tolerance <= toleranceArg) {
                    //exit the loop when we are happy
                    break;
                }
            }

            if (HTML_TABLE) {
                exec("SortRanks #"+(iterations+1), new SortRanks(), input, sortedRanksDir);
                Path htmlsource = new Path(sortedRanksDir + File.separator + "part-00000");
                BufferedReader in = null;
                PrintWriter out = null;
                Path outfile = new Path(sortedRanksDir + "-" + iterations + ".dat");
                try {
                    in = new BufferedReader(new InputStreamReader(fs.open(htmlsource)));
                    out = new PrintWriter(fs.create(outfile).getWrappedStream());
                    for (int j = 0; j < HTML_TABLE_ROWS; j++) {
                        StringTokenizer st = new StringTokenizer(in.readLine());
                        out.write(st.nextToken() + "\n");
                    }
                } finally {
                    close(in);
                    close(out);
                }
            }

            iterations++;
        }

        exec("SortRanks", new SortRanks(), input, sortedRanksDir);

        if (HTML_TABLE) {
            exec("HTMLTable", new HTMLTable(),
                    outpath, Integer.toString(HTML_TABLE_ROWS), Integer.toString(iterations));
        }
        return 0;
    }

    public static void main(String[] args) throws Exception {
        System.exit(ToolRunner.run(new Configuration(), new CiteRank(), args));
    }
}
