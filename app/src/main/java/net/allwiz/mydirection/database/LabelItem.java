package net.allwiz.mydirection.database;

public class LabelItem {
    public static LabelItem newInstance() {
        LabelItem l = new LabelItem();

        l.category = 0;
        l.labelIndex = 0;
        l.name = "";
        return l;
    }

    public static LabelItem newInstance(int category, int label, String name) {
        LabelItem l = new LabelItem();
        l.category = category;
        l.labelIndex = label;
        l.name = name;
        return l;
    }


    public int      category;
    public long     labelIndex;
    public String   name;
}
