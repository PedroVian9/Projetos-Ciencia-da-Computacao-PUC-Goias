import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import view.TelaLogin;

/**
 * Classe principal do SisBiblio. Aplica look and feel do sistema
 * e inicializa a tela de login. Mantida enxuta delegando a logica
 * de UI/dados as classes das camadas view/dao/model.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new TelaLogin().setVisible(true);
        });
    }
}
