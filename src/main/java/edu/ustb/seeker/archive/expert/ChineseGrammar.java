package edu.ustb.seeker.archive.expert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.international.pennchinese.ChineseGrammaticalStructure;
import edu.ustb.seeker.model.data.*;

public class ChineseGrammar {
    private Properties props;
    private CRFClassifier<CoreLabel> solver;

    public ChineseGrammar() {
        props = new Properties();
        props.setProperty("sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese");
        props.setProperty("model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
        props.setProperty("normTableEncoding", "UTF-8");
        props.setProperty("serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
        props.setProperty("inputEncoding", "UTF-8");
        props.setProperty("sighanPostProcessing", "true");
        solver = new CRFClassifier<CoreLabel>(props);
        solver.loadClassifierNoExceptions( "edu/stanford/nlp/models/segmenter/chinese/ctb.gz", props);

        props.setProperty("parse.model", "edu/stanford/nlp/models/lexparser/xinhuaFactoredSegmenting.ser.gz");
        props.setProperty("depparse.model", "edu/stanford/nlp/models/parser/nndep/CTB_CoNLL_params.txt.gz");

        props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/chinese-distsim/chinese-distsim.tagger");
        props.setProperty("ner.model", "edu/stanford/nlp/models/ner/chinese.misc.distsim.crf.ser.gz");
        props.setProperty("ner.useSUTime", "false");
    }

    public ChineseGrammar(Properties prop) {
        this.props = prop;
        this.solver = new CRFClassifier<CoreLabel>(prop);
        solver.loadClassifierNoExceptions( "edu/stanford/nlp/models/segmenter/chinese/ctb.gz", prop);
    }

    public String segmentString(String text) {
        List<String> segmented = solver.segmentString(text);
        String ret = "";
        for (String token: segmented) { ret = ret + token + ' '; }
        return ret;
    }

    public List<String> segmentStringList(String text) {
        return solver.segmentString(text);
    }

    public ChineseSentence parseSentence(String sentence) {
        Sentence sent = new Sentence(segmentString(sentence));
        List<String> words = segmentStringList(sentence);
        List<String> posTags = sent.posTags(props);
        List<String> nerTags = sent.nerTags(props);
        List<ChineseToken> tokens = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            tokens.add(new ChineseToken(i+1, words.get(i), posTags.get(i), nerTags.get(i)));
        }
        Tree tree = sent.parse(props);
        ChineseGrammaticalStructure gs = new ChineseGrammaticalStructure(tree);
        Collection<TypedDependency> tdl = gs.typedDependenciesCollapsed();
        DependentTree dependentTree = new DependentTree(tokens, tdl);
        return new ChineseSentence(tokens, dependentTree);
    }

    public static void main(String[] args) {
//        ChineseGrammar cg = new ChineseGrammar();
//        ChineseSentence cs = cg.parseSentence("");
//        System.out.println(cs);
    }
}

