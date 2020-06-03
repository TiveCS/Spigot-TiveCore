package com.github.tivecs.tivecore;

import com.github.tivecs.tivecore.lab.language.LanguageHandler;
import com.github.tivecs.tivecore.lab.language.Placeholder;

public class TivePlaceholder extends Placeholder {

    private static TiveCorePlugin plugin = TiveCorePlugin.getPlugin(TiveCorePlugin.class);

    public TivePlaceholder(){
        LanguageHandler langHandler = plugin.getLanguageHandler();

        getReplacer().put("system_language", langHandler.hasDefaultLanguage() ? langHandler.getDefaultLanguage().getLanguageId() : "None");
        getReplacer().put("system_language_amount", String.valueOf(langHandler.getRegisteredLanguages().size()));
        getReplacer().put("system_language_size", String.valueOf(langHandler.getRegisteredLanguages().size()));
    }

}
