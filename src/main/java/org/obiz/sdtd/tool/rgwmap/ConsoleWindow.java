package org.obiz.sdtd.tool.rgwmap;

import javafx.scene.layout.Pane;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ConsoleWindow extends JFrame {
    private final JScrollPane scrollPane;
    private JPanel root;
    private JTextArea textArea;

    PrintStream sout;
    PrintStream serr;
    PrintStream wsout;
    PrintStream wserr;

    public ConsoleWindow() throws HeadlessException {
        super("RGW Map builder <CONSOLE>");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        root = new JPanel();
        root.setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setFont(new Font("Courier New", 0, 14));
        textArea.setBackground(new Color(36, 17, 11));
        textArea.setForeground(new Color(214, 203, 176));
        textArea.setSelectedTextColor(new Color(74, 247, 51));
        textArea.setSelectionColor(new Color(77, 95, 114));

        scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(scrollPane, BorderLayout.CENTER);
//        root.add(textArea);
        setResizable(false);

        sout = System.out;
        serr = System.err;

        try {
            PipedOutputStream soutpout =  new PipedOutputStream();
            PipedInputStream soutpin = new PipedInputStream(soutpout);
            wsout = new PrintStream(soutpout);
            System.setOut(wsout);
            BufferedReader reader = new BufferedReader(new InputStreamReader(soutpin));

            new Thread(() -> {
                String line;
                try {
                    while ((line = reader.readLine())!=null) {
                        String finalLine = line;
                        EventQueue.invokeLater(() -> {
                            textArea.setText(textArea.getText() + "\n" + finalLine);
                            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
                        });
                        sout.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            })
                    .start()
            ;

//            new InputStreamReader(soutpin).

        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentPane(root);
        this.setVisible(true);
    }
}
