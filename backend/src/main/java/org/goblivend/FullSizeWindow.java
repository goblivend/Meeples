package org.goblivend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class FullSizeWindow {
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Custom Ctrl + Key Example");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(400, 300);
//
//            // Create a text area for demonstration
//            JTextArea textArea = new JTextArea();
//            frame.add(new JScrollPane(textArea));
//
//            // Define the custom key combination (Ctrl + Y)
//            KeyStroke keyStroke = KeyStroke.getKeyStroke("control Y");
//            InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
//            ActionMap actionMap = textArea.getActionMap();
//
//            // Map the key stroke to an action name
//            inputMap.put(keyStroke, "gfhfghfghf");
//
//            // Define the custom action
//            actionMap.put("gfhfghfghf", new AbstractAction() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    System.out.println("Ctrl+Y pressed: Custom action triggered!");
//                }
//            });
//
//            frame.setVisible(true);
//        });
//    }

//    public static void main(String[] args) {
//        // Create the dialog
//        JDialog dialog = new JDialog();
//        dialog.setTitle("Press Escape to close");
//        dialog.setSize(300, 200);
//        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//
//        // Ensure the root pane is used for binding keys
//        InputMap inputMap = dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
//        ActionMap actionMap = dialog.getRootPane().getActionMap();
//
//        // Bind the Escape key to an action
//        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "closeDialog");
//        actionMap.put("closeDialog", new AbstractAction() {
//            @Override
//            public void actionPerformed(java.awt.event.ActionEvent e) {
//                System.out.println("Escape key pressed!");
//                dialog.dispose();
//            }
//        });
//
//        // Display the dialog
//        dialog.setVisible(true);
//    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Full-Screen Resize Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300); // Initial size of the frame

        // Add a label to show the current size
        JLabel sizeLabel = new JLabel("Current size: 400x300");
        sizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(sizeLabel);

        // Get the GraphicsDevice to handle full-screen
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        // Button to toggle full-screen mode
//        JButton toggleFullScreenButton = new JButton("Toggle Full-Screen");
//        toggleFullScreenButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (gd.isFullScreenSupported()) {
//                    if (frame.isUndecorated()) {
//                        gd.setFullScreenWindow(null);  // Exit full-screen mode
//                        frame.setUndecorated(false);
//                        frame.setSize(400, 300);  // Restore the size
//                    } else {
//                        frame.setUndecorated(true);
//                        gd.setFullScreenWindow(frame);  // Enter full-screen mode
//                    }
//                }
//            }
//        });
//        frame.add(toggleFullScreenButton, BorderLayout.SOUTH);

        // Add a component listener to handle resizing events
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = frame.getWidth();
                int height = frame.getHeight();
                sizeLabel.setText("Current size: " + width + "x" + height);
                System.out.println("Resized to: " + width + "x" + height);
            }
        });

        frame.setMinimumSize(new Dimension(300, 200));

        frame.setLayout(new BorderLayout());
        frame.setVisible(true);
    }
}
