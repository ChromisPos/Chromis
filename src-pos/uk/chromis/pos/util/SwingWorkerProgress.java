/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c)2015-2016
**    http://www.chromis.co.uk
**
**    This file is part of Chromis POS Version V0.60.2 beta
**
**    Chromis POS is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    Chromis POS is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>
**
**
*/

package uk.chromis.pos.util;

/**
 *
 * 
 */
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

/**
 *
 * 
 */
public class SwingWorkerProgress {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        SwingWorkerProgress swingWorkerProgress = new SwingWorkerProgress();
    }

    /**
     *
     */
    public SwingWorkerProgress() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    /**
     *
     */
    public class TestPane extends JPanel {

        private JProgressBar pbProgress;
        private JButton start;

        /**
         *
         */
        public TestPane() {

            setBorder(new EmptyBorder(10, 10, 10, 10));
            pbProgress = new JProgressBar();
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(pbProgress, gbc);

            start = new JButton("Start");
            gbc.gridy++;
            add(start, gbc);

            start.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    start.setEnabled(false);
                    ProgressWorker pw = new ProgressWorker();
                    pw.addPropertyChangeListener(new PropertyChangeListener() {

                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            String name = evt.getPropertyName();
                            switch (name) {
                                case "progress":
                                    int progress = (int) evt.getNewValue();
                                    pbProgress.setValue(progress);
                                    repaint();
                                    break;
                                case "state":
                                    SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
                                    switch (state) {
                                        case DONE:
                                            start.setEnabled(true);
                                            break;
                                    }
                            }
                        }

                    });
                    pw.execute();
                }
            });

        }
    }

    /**
     *
     */
    public class ProgressWorker extends SwingWorker<Object, Object> {

        @Override
        protected Object doInBackground() throws Exception {
            int i = 0;
            int max = 2000;

            while (i < max) {
                i += 10;
                int progress = Math.round(((float)i / (float)max) * 100f);
                setProgress(progress);
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                }
            }

            return null;
        }
    }
}