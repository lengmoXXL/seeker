package edu.ustb.seeker.archive.valuerules;

import edu.ustb.seeker.archive.expert.ChineseGrammar;
import edu.ustb.seeker.archive.expert.ChinesePhraseLib;
import edu.ustb.seeker.archive.expert.PhraseLibExtendedHowNet;
import edu.ustb.seeker.model.data.ChineseSentence;
import edu.ustb.seeker.model.data.ChineseToken;
import edu.ustb.seeker.model.data.SchemaField;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExtractorByRule {
    private String numberPath, stringPath;
    private RuleGraph numberGraph, stringGraph;

    private ChinesePhraseLib chinesePhraseLib;

    public ExtractorByRule(ChinesePhraseLib chinesePhraseLib) throws IOException {
        numberPath = "settings/valueGenerateRules/NUMBER.txt";
        stringPath = "settings/valueGenerateRules/STRING.txt";
        File numberFile = new File(numberPath);
        File stringFile = new File(stringPath);

        this.chinesePhraseLib = chinesePhraseLib;
        numberGraph = new RuleGraph();
        stringGraph = new RuleGraph();
        RuleGraphNode.initChinesePhraseLib(chinesePhraseLib);

        Scanner scan = new Scanner(numberFile);
        while (scan.hasNext()) {
            String rule = scan.nextLine();
//            System.out.println(parse(rule));
            numberGraph.addRule(parse(rule));
        }
        scan.close();

        scan = new Scanner(stringFile);
        while (scan.hasNext()) {
            String rule = scan.nextLine();
            stringGraph.addRule(parse(rule));
        }
        scan.close();
    }

    private List<String> parse(String line) {
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
                if (left != right && line.charAt(left) == '>') ret.add(line.substring(left, right));
                ret.add("?");
                right ++; left = right;
            } else if ('*' == line.charAt(right)) {
                ret.add("*");
                right ++; left = right;
            } else if ("()".contains("" + line.charAt(right))) {
                if (left != right && line.charAt(left) == '>') ret.add(line.substring(left, right));
                ret.add("" + line.charAt(right));
                right ++; left = right;
            } else if ('<' == line.charAt(right)) {
                if (left != right && line.charAt(left) == '>') ret.add(line.substring(left, right));
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

    public JSONObject mapping(List<ChineseToken> tokens, int type) {
        ExtractVariablePool extractVariablePool = null;
        if (type == SchemaField.STRING) {
            extractVariablePool = stringGraph.mapping(tokens);
        } else if (type == SchemaField.NUMBER) {
            extractVariablePool = numberGraph.mapping(tokens);
        }
        if (extractVariablePool == null) return new JSONObject();
        else return extractVariablePool.toJSONObject();
    }

    public static void main(String[] args) throws IOException {
        ChineseGrammar chineseGrammar = new ChineseGrammar();
        ChinesePhraseLib chinesePhraseLib = new PhraseLibExtendedHowNet(chineseGrammar);
        ExtractorByRule extractorByRule = new ExtractorByRule(chinesePhraseLib);
        ChineseSentence sentence = chineseGrammar.parseSentence("在10到100之间");
        chinesePhraseLib.annotateSemanticType(sentence);
        System.out.println(extractorByRule.mapping(sentence.getTokens(), SchemaField.NUMBER));
        Scanner scan = new Scanner(System.in);
        while (true) {
            String input = scan.nextLine();
            System.out.println(input);
            sentence = chineseGrammar.parseSentence(input);
            chinesePhraseLib.annotateSemanticType(sentence);
            System.out.println(extractorByRule.mapping(sentence.getTokens(), SchemaField.NUMBER));
        }
    }
}
