package edu.ustb.seeker.model.data;


public class SemanticPhrase {
    public static final int Lt = 1;
    public static final int Gt = 2;
    public static final int Lte = 3;
    public static final int Gte = 4;
    public static final int Equ = 5;
    public static final int nEqu = 6;
    public static final int Contain = 7;
    public static final int nContain = 8;
    public static final int Not = 9;
    public static final int At = 10;
    public static final int And = 11;
    public static final int Or = 12;
    public static final int Range = 13;
    public static final int Other = 14;

    private int state;
    public SemanticPhrase(int state) {
        this.state = state;
    }

    public SemanticPhrase(String str) {
        if (str.equals("Lt")) {
            this.state = SemanticPhrase.Lt;
        } else if (str.equals("Gt")) {
            this.state = SemanticPhrase.Gt;
        } else if (str.equals("Not")) {
            this.state = SemanticPhrase.Not;
        } else if (str.equals("And")) {
            this.state = SemanticPhrase.And;
        } else if (str.equals("Or")) {
            this.state = SemanticPhrase.Or;
        } else if (str.equals("Equ")) {
            this.state = SemanticPhrase.Equ;
        } else if (str.equals("Contain")) {
            this.state = SemanticPhrase.Contain;
        } else if (str.equals("Range")) {
            this.state = SemanticPhrase.Range;
        } else if (str.equals("Other")) {
            this.state = SemanticPhrase.Other;
        }
    }

    @Override
    public String toString() {
        switch (this.state) {
            case Lt:
                return "Lt";
            case Gt:
                return "Gt";
            case Lte:
                return "Lte";
            case Gte:
                return "Gte";
            case Equ:
                return "Equ";
            case nEqu:
                return "nEqu";
            case Contain:
                return "Contain";
            case nContain:
                return "nContain";
            case Not:
                return "Not";
            case At:
                return "At";
            case And:
                return "And";
            case Or:
                return "Or";
            case Range:
                return "Range";
            case Other:
                return "Other";
            default:
                return "Other";
        }
    }
}
