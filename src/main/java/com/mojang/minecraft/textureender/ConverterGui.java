package com.mojang.minecraft.textureender;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ConverterGui extends JPanel {
    private static final Font MONOSPACED = new Font("Monospaced", Font.PLAIN, 12);

    private static ConverterGui instance;
    private final JTextArea console = new JTextArea();
    private final JScrollPane scroll = new JScrollPane(console, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    public ConverterGui() {
        super(true);
        instance = this;

        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        console.setFont(MONOSPACED);
        console.setEditable(false);
        console.setMargin(null);
        console.setWrapStyleWord(true);
        console.setLineWrap(true);

        add(scroll);
    }

    public static void logLine(String line, Throwable throwable) {
        logLine(line);
        logLine(throwable);
    }

    public static void logLine(Throwable throwable) {
        StringWriter writer = null;
        PrintWriter printWriter = null;
        String result = throwable.toString();

        try {
            writer = new StringWriter();
            printWriter = new PrintWriter(writer);
            throwable.printStackTrace(printWriter);
            result = writer.toString();
        } finally {
            try {
                if (writer != null) writer.close();
                if (printWriter != null) printWriter.close();
            } catch (IOException ex) {}
        }

        logLine(result);
    }

    public static void logLine(final String line) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    logLine(line);
                }
            });
            return;
        }

        final Document document = instance.console.getDocument();
        final JScrollBar scrollBar = instance.scroll.getVerticalScrollBar();
        boolean shouldScroll = false;
        System.out.println(line);

        if (instance.scroll.getViewport().getView() == instance.console) {
            shouldScroll = scrollBar.getValue() + scrollBar.getSize().getHeight() + MONOSPACED.getSize() * 4 > scrollBar.getMaximum();
        }

        try {
            document.insertString(document.getLength(), line + "\n", null);
        } catch (BadLocationException ignored) {}

        if (shouldScroll) {
            scrollBar.setValue(Integer.MAX_VALUE);
        }
    }

    public static String relativize(File root, File file) {
        return root.toURI().relativize(file.toURI()).toString();
    }
}
