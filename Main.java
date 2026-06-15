import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import manager.PengelolaSurat;
import view.AppFrame;

public class Main {
    public static void main(String[] args) {
        // Set System Look and Feel to match native OS style
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Gagal mengatur Look and Feel: " + e.getMessage());
        }

        // Run Swing application on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            PengelolaSurat pengelola = new PengelolaSurat();
            AppFrame app = new AppFrame(pengelola);
            app.setVisible(true);
        });
    }
}
