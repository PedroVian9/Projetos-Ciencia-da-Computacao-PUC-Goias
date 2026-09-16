package util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Classe utilitaria de internacionalizacao (i18n).
 * Carrega o ResourceBundle "resources/messages" no idioma corrente
 * e expoe metodos estaticos para troca de idioma e leitura de textos.
 */
public class Internacionalizacao {

    private static Locale locale = new Locale("pt", "BR");
    private static ResourceBundle bundle = ResourceBundle.getBundle("resources.messages", locale);

    public static void setIdioma(String codigo) {
        if (codigo == null) codigo = "pt_BR";
        if (codigo.equalsIgnoreCase("en_US")) {
            locale = new Locale("en", "US");
        } else {
            locale = new Locale("pt", "BR");
        }
        bundle = ResourceBundle.getBundle("resources.messages", locale);
    }

    public static String getIdiomaAtual() {
        return locale.getLanguage().equals("en") ? "en_US" : "pt_BR";
    }

    public static String get(String chave) {
        try {
            return bundle.getString(chave);
        } catch (Exception e) {
            return chave;
        }
    }
}
