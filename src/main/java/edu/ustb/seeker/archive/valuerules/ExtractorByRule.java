package edu.ustb.seeker.archive.valuerules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExtractorByRule {
    private String libPath;
    private RuleGraph ruleGraph;

    public ExtractorByRule() throws IOException {
        libPath = "settings/valueGenerateRules.txt";
        File file = new File(libPath);

        ruleGraph = new RuleGraph();

        Scanner scan = new Scanner(file);
        while (scan.hasNext()) {
            String rule = scan.nextLine();
//            System.out.println(parse(rule));
            ruleGraph.addRule(parse(rule));
        }
        scan.close();
    }

    public List<String> parse(String line) {
        List<String> ret = new ArrayList<>();
        int left = 0;
        int right = 0;
        for (; right < line.length();) {
            if ("\"{[]}".contains("" + line.charAt(right))) {
                if (left == right) {
                    right ++;
                } else {
                    if (line.charAt(left) != '>') {
                        right ++;
                    }
                    ret.add(line.substring(left, right));
                    left = right;
                }
            } else if ('?' == line.charAt(right)) {
                ret.add("?");
                right ++; left = right;
            } else if ('*' == line.charAt(right)) {
                ret.add("*");
                right ++; left = right;
            } else if ("()".contains("" + line.charAt(right))) {
                ret.add("" + line.charAt(right));
                right ++; left = right;
            } else if ('<' == line.charAt(right)) {
                ret.add("" + line.charAt(right));
                right ++; left = right;
            } else if ('>' == line.charAt(right)) {
                right ++;
            } else {
                if (left == right) left++;
                right++;
            }
        }
        if (left < right) {
            ret.add(line.substring(left, right));
        }

        return ret;
    }


    public static void main(String[] args) throws IOException {
        ExtractorByRule extractorByRule = new ExtractorByRule();
    }
}
