package Main;

import java.util.ArrayList;

public class TextManager {

    public final int LANGUAGE_ENG = 0;
    private static int currentLanguage = 0;

    private static ArrayList<String> unitMenuTexts;

    public static void init(){
        unitMenuTexts = new ArrayList<>();
        unitMenuTexts.add("Deselect");
        unitMenuTexts.add("Move");
        unitMenuTexts.add("Attack");
        unitMenuTexts.add("Info");
        unitMenuTexts.add("Wait");
    }

    public static ArrayList<String> getUnitMenuTexts(){
        return unitMenuTexts;
    }
}
