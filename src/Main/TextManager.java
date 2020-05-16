package Main;

import java.util.ArrayList;

public class TextManager {

    public final int LANGUAGE_ENG = 0;
    private static int currentLanguage = 0;

    private static ArrayList<String> actionMenuTexts;

    public static void init(){
        actionMenuTexts = new ArrayList<>();
        actionMenuTexts.add("Back");
        actionMenuTexts.add("Attack");
        actionMenuTexts.add("Wait");
    }

    public static ArrayList<String> getActionMenuTexts(){
        return actionMenuTexts;
    }
}
