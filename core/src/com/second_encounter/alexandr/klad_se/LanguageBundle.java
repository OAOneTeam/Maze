package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashMap;

public class LanguageBundle {

    private HashMap<String, String> languageMap = new HashMap<String, String>();

    public LanguageBundle(String file) {
        XmlReader xmlReader = new XmlReader();
        XmlReader.Element element = xmlReader.parse(Gdx.files.internal(file).reader("UTF-8"));
        Array<XmlReader.Element> languages = element.getChildrenByName("entry");
        for (int i = 0; i < languages.size; i++) {
            XmlReader.Element language = languages.get(i);
            languageMap.put(language.getAttribute("key"), language.getText());
        }
    }

    public String get(String key) {
        String string = languageMap.get(key);
        if (string != null)
            return string;
        else
            return key.toUpperCase();
    }

    public void dispose() {
        languageMap.clear();
    }
}
