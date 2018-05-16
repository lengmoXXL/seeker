package edu.ustb.seeker.archive.expert;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class ChineseSegment {
    private Properties props;
    private CRFClassifier<CoreLabel> solver;
    public ChineseSegment() {
        props = new Properties();
        props.setProperty("sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese");
        props.setProperty("model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
        props.setProperty("normTableEncoding", "UTF-8");
        props.setProperty("serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
        props.setProperty("inputEncoding", "UTF-8");
        props.setProperty("sighanPostProcessing", "true");
        solver = new CRFClassifier<CoreLabel>(props);
        solver.loadClassifierNoExceptions( "edu/stanford/nlp/models/segmenter/chinese/ctb.gz", props);
    }

    public ChineseSegment(Properties prop) {
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

    public static void main(String[] args) {
        ChineseSegment seg = new ChineseSegment();
        System.out.println(seg.segmentString("今天的委托函"));
    }
}

