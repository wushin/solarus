/*
 * Copyright (C) 2006-2014 Christopho, Solarus - http://www.solarus-games.org
 *
 * Solarus Quest Editor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Solarus Quest Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.solarus.editor.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.solarus.editor.DialogSection;
import org.solarus.editor.Dialogs;
import org.solarus.editor.Project;
import org.solarus.editor.ResourceType;
import org.solarus.editor.QuestEditorException;

/**
 * Main window of the dialogs editor
 */
public class DialogsEditorPanel extends AbstractEditorPanel implements ListSelectionListener {

    /**
     * The current Dialogs.
     */
    private Dialogs dialogs;
    /**
     * A list wich contains the sections of the current dialogs
     */
    protected JList<DialogSection> sectionsList;
    /**
     * A textfield for filter the section list
     */
    protected JTextField filter;
    /**
     * Table for section edition
     */
    protected DialogSection currentSection;

    protected SectionListModel model;
    //private DialogSectionTableModel tableModel;
    private JTextField name;
    private JTextArea comments;
    private JTextField icon;
    private JTextField line1;
    private JTextField line2;
    private JTextField line3;
    private JCheckBox question;
    private JComboBox<String> skipAction;
    private JTextField nextSection;
    private JTextField nextSection2;
    /**
     * A text area for the dialogs description
     */
    private JTextArea dialogsDesc;
    /**
     * Buttons for list management
     */
    private JButton moveSectionUp;
    private JButton moveSectionDown;
    private JButton newSection;
    private JButton removeSection;
    private final JScrollPane listScrollPane;

    /**
     * Creates a new dialogs editor.
     * @param mainWindow The main window of the quest editor.
     * @param languageId Language of the dialogs to edit. 
     */
	public DialogsEditorPanel(EditorWindow mainWindow, String languageId) {
	    super(mainWindow, Project.getDialogsFile(languageId).getAbsolutePath());
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel(new BorderLayout());
        sectionsList = new JList<>();
        sectionsList.setPreferredSize(new Dimension(250, 800));
        model = new SectionListModel();

        sectionsList.setModel(model);
        sectionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sectionsList.addListSelectionListener(this);
        listScrollPane = new JScrollPane(sectionsList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        filter = new JTextField();
        filter.setBorder(BorderFactory.createTitledBorder("Filter"));
        filter.addKeyListener(new FilterList());
        leftPanel.add(filter, BorderLayout.NORTH);
        leftPanel.setBorder(BorderFactory.createTitledBorder("Dialogs sections"));

        JPanel buttonsPanel = new JPanel(new GridLayout(0, 1));
        moveSectionUp = new JButton("Move up selected section");
        moveSectionUp.setEnabled(false);
        moveSectionDown = new JButton("Move down selected section");
        moveSectionDown.setEnabled(false);
        newSection = new JButton("Create a new section");
        newSection.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                DialogsEditorPanel.this.model.addSection();
            }
        });
        removeSection = new JButton("Delete selected section");
        removeSection.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                DialogsEditorPanel.this.model.removeSection(currentSection);
            }
        });
        buttonsPanel.add(moveSectionUp);
        buttonsPanel.add(moveSectionDown);
        buttonsPanel.add(newSection);
        buttonsPanel.add(removeSection);
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Section management"));
        leftPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);


        JPanel centerPanel = new JPanel(new BorderLayout());
        dialogsDesc = new JTextArea();
        centerPanel.add(new JScrollPane(dialogsDesc), BorderLayout.CENTER);
        dialogsDesc.setFont(new Font("Courier New", Font.PLAIN, 12));

        JPanel p1 = new JPanel(new BorderLayout());
        JPanel sectionPanel = new JPanel(new GridLayout(0, 2));

        sectionPanel.add(new JLabel("Name"));
        name = new JTextField();
        name.setFont(new Font("Courier New", Font.PLAIN, 12));
        sectionPanel.add(name);


        sectionPanel.add(new JLabel("Icon"));
        icon = new JTextField();
        icon.setFont(new Font("Courier New", Font.PLAIN, 12));
        sectionPanel.add(icon);
        sectionPanel.add(new JLabel("Line 1"));
        line1 = new JTextField();
        line1.setFont(new Font("Courier New", Font.PLAIN, 12));
        sectionPanel.add(line1);
        sectionPanel.add(new JLabel("Line 2"));
        line2 = new JTextField();
        line2.setFont(new Font("Courier New", Font.PLAIN, 12));
        sectionPanel.add(line2);
        sectionPanel.add(new JLabel("Line 3"));
        line3 = new JTextField();
        line3.setFont(new Font("Courier New", Font.PLAIN, 12));
        sectionPanel.add(line3);
        sectionPanel.add(new JLabel("Question"));
        question = new JCheckBox();
        sectionPanel.add(question);
        sectionPanel.add(new JLabel("Skip Action"));
        skipAction = new JComboBox<>(new String[]{DialogSection.SKIP_ACTION_NONE, DialogSection.SKIP_ACTION_CURRENT, DialogSection.SKIP_ACTION_ALL});
        skipAction.setFont(new Font("Courier New", Font.PLAIN, 12));
        sectionPanel.add(skipAction);
        sectionPanel.add(new JLabel("Next Section"));
        nextSection = new JTextField();
        nextSection.setFont(new Font("Courier New", Font.PLAIN, 12));
        sectionPanel.add(nextSection);
        sectionPanel.add(new JLabel("Next Section 2"));
        nextSection2 = new JTextField();
        nextSection2.setFont(new Font("Courier New", Font.PLAIN, 12));
        sectionPanel.add(nextSection2);


        comments = new JTextArea(4, 25);
        comments.setFont(new Font("Courier New", Font.PLAIN, 12));
        comments.setBorder(BorderFactory.createTitledBorder("Comments"));
        p1.add(comments, BorderLayout.NORTH);
        p1.setBorder(BorderFactory.createTitledBorder("Section Data"));
        p1.add(sectionPanel, BorderLayout.CENTER);
        centerPanel.add(p1, BorderLayout.SOUTH);

        add(centerPanel);

        setDialogs(dialogs);
	}

    /**
     * Sets the current dialogs. This method is called when the user opens a dialogs,
     * closes the dialogs, or creates a new one.
     * @param dialogs the new dialogs, or null if no dialogs is loaded
     */
    private void setDialogs(Dialogs dialogs) {

        // If there was already dialogs, remove their observers.
        if (this.dialogs != null) {
            this.dialogs.deleteObservers();
        }

        this.dialogs = dialogs;

        model.setItems(dialogs.getSections());
        model.filterList("");
        sectionsList.setPreferredSize(new Dimension(250, sectionsList.getModel().getSize() * 18));
        sectionsList.repaint();
        dialogsDesc.setText(dialogs.getDescription());

        //setSection(dialogs.getSections().get(0));
        repaint();
    }

    /**
     * This function is called when the user wants to close the current dialogs.
     * If the dialogs is not saved, we propose to save it.
     * @return false if the user canceled.
     */
    @Override
	public boolean checkCurrentFileSaved() {
        boolean result = true;

        if (dialogs != null && !dialogs.isSaved()) {
            int answer = JOptionPane.showConfirmDialog(this,
                    "The dialogs has been modified. Do you want to save it?",
                    "Save the modifications",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                save();
            } else if (answer == JOptionPane.CANCEL_OPTION) {
                result = false;
            }
        }

        return result;
    }

    /**
     * Give the name of the resource opened in the editor
     * @return the name of the map
     */
    public Dialogs getDialogs() {
        return dialogs;
    }

    /**
     * Returns a name describing what is opened in the editor.
     * @return A name describing the file opened in the editor.
     */
    @Override
	public String getTitle() {
        return "Dialogs: " + getDialogs().getLanguageId();
    }

    /**
     * Creates a new dialogs in the project and sets it as the current dialogs.
     */
    protected void newDialogs() {

        if (!checkCurrentFileSaved()) {
            return;
        }
        try {
            Dialogs dialogs = new Dialogs();
            setDialogs(dialogs);
        } catch (QuestEditorException ex) {
            GuiTools.errorDialog("Cannot create the dialogs: " + ex.getMessage());
        }
    }

    /**
     * Loads a dialog file of the project and sets it as the current dialogs.
     */
    protected void openDialogs() {

        if (!checkCurrentFileSaved()) {
            return;
        }

        ResourceChooserDialog dialog = new ResourceChooserDialog(ResourceType.LANGUAGE);
        dialog.setLocationRelativeTo(DialogsEditorPanel.this);
        dialog.pack();
        dialog.setVisible(true);
        String dialogsId = dialog.getSelectedId();

        if (dialogsId.length() == 0) {
            return;
        }

        try {
            Dialogs dialogs = new Dialogs(dialogsId);
            setDialogs(dialogs);
        } catch (QuestEditorException ex) {
            GuiTools.errorDialog("Could not load the dialogs: " + ex.getMessage());
        }
    }

    /**
     * Saves the current dialogs.
     */
    @Override
	public void save() {
        try {
            saveCurrentSection();
            repaint();
            dialogs.save();
        } catch (QuestEditorException ex) {
            GuiTools.errorDialog("Could not save dialogs: " + ex.getMessage());
        }
    }

    /**
     * Closes this editor without confirmation. 
     */
    @Override
    public void close() {
        setDialogs(null);
    }

    private void setSection(DialogSection newSection) {
        if (currentSection != null) {
            saveCurrentSection();
        }
        if (newSection != null) {
            this.currentSection = newSection;
            name.setText(newSection.getName());
            comments.setText(newSection.getComment());
            icon.setText("" + newSection.getIcon());
            line1.setText(newSection.getLine1());
            line2.setText(newSection.getLine2());
            line3.setText(newSection.getLine3());
            question.setSelected(newSection.isQuestion());
            skipAction.setSelectedItem(newSection.getSkipAction());
            nextSection.setText(newSection.getNextSection());
            nextSection2.setText(newSection.getNextSection2());
        } else {
            sectionsList.setSelectedValue(model.elementAt(0), true);
            setSection( model.elementAt(0));
        }
    }

    @Override
	public void valueChanged(ListSelectionEvent lse) {
        setSection(sectionsList.getSelectedValue());
        repaint();
    }

    public void saveCurrentSection() {
        dialogs.setSaved(false);
        currentSection.setName(name.getText());
        currentSection.setComment(comments.getText());
        currentSection.setIcon(Integer.parseInt(icon.getText()));
        currentSection.setLine1(line1.getText());
        currentSection.setLine2(line2.getText());
        currentSection.setLine3(line3.getText());
        currentSection.setQuestion(question.isSelected());
        currentSection.setSkipAction((String) skipAction.getSelectedItem());
        currentSection.setNextSection(nextSection.getText());
        currentSection.setNextSection2(nextSection2.getText());
    }

    public class SectionListModel extends DefaultListModel<DialogSection> {

        ArrayList<DialogSection> elements = new ArrayList<DialogSection>();
        ArrayList<DialogSection> filteredElements = new ArrayList<DialogSection>();

        @Override
        public int getSize() {
            if (filteredElements == null) {
                return 0;
            }
            return filteredElements.size();
        }

        @Override
        public DialogSection getElementAt(int index) {
            return filteredElements.get(index);
        }

        protected void setItems(ArrayList<DialogSection> sections) {
            this.elements = sections;
            filterList("");
        }

        protected void filterList(String filter) {

            filteredElements.clear();
            if (filter.length() == 0) {
                filteredElements.addAll(elements);
            } else {
                for (DialogSection section : elements) {
                    if (section.getName().contains(filter)) {
                        filteredElements.add(section);
                    }
                }
            }
            fireContentsChanged(this, 0, getSize());

        }

        public void addSection() {
            DialogSection newSection = new DialogSection("New section");
            elements.add(newSection);
            filteredElements.add(newSection);
            sectionsList.setPreferredSize(new Dimension(250, sectionsList.getModel().getSize() * 18));
            setSection(newSection);
            fireContentsChanged(this, 0, getSize());
            sectionsList.repaint();
        }

        public void removeSection(DialogSection section) {
//            int idx = filteredElements.indexOf(section);
            sectionsList.setSelectedValue(getElementAt(0), true);
            elements.remove(section);
            filteredElements.remove(section);
            fireContentsChanged(this, 0, getSize());
//            if (size() -1 > idx) {
//                setSection((DialogSection) getElementAt(idx));
//            } else {
            //currentSection = newSection;

//            }
            sectionsList.setPreferredSize(new Dimension(250, sectionsList.getModel().getSize() * 18));
            setSection(getElementAt(0));
            sectionsList.repaint();

        }
    }

    public class FilterList implements KeyListener {

        @Override
		public void keyTyped(KeyEvent e) {
            //nothing to do

        }

        @Override
		public void keyPressed(KeyEvent e) {
            //nothing to do
        }

        @Override
		public void keyReleased(KeyEvent e) {
            model.filterList(filter.getText());
            try {
                sectionsList.setPreferredSize(new Dimension(250, sectionsList.getModel().getSize() * 18));
                sectionsList.setSelectedValue(currentSection, true);
                sectionsList.repaint();
            } catch (Exception ex) {
                //nothing to do
            }
            //sectionsList.repaint();
        }
    }
}
