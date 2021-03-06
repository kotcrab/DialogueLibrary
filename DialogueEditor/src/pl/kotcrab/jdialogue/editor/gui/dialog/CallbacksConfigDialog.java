/*******************************************************************************
 * DialogueEditor
 * Copyright (C) 2013-2014 Pawel Pastuszak
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package pl.kotcrab.jdialogue.editor.gui.dialog;

import pl.kotcrab.jdialogue.editor.Editor;
import pl.kotcrab.jdialogue.editor.project.PCallback;
import pl.kotcrab.jdialogue.editor.project.Project;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CallbacksConfigDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private CallbacksConfigDialog instance;
	private final JPanel contentPanel = new JPanel();
	private JList<String> list;
	private Project project;

	/**
	 * Create the dialog.
	 */
	public CallbacksConfigDialog (final Editor parrent, final Project project) {
		super(parrent, true);
		instance = this;
		this.project = project;
		setTitle("Callbacks");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(parrent.getX() + (parrent.getWidth() / 2) - (450 / 2), parrent.getY() + (parrent.getHeight() / 2) - (300 / 2), 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		list = new JList<String>();
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		JScrollPane listScroller = new JScrollPane(list);

		updateList();

		contentPanel.add(listScroller);
		JLabel lblSeq1 = new JLabel("Callbacks:");
		contentPanel.add(lblSeq1, BorderLayout.NORTH);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton btnCreateNew = new JButton("Create New");
		btnCreateNew.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				new CallbackCreationDialog(parrent, project, null, true);
				updateList();
			}
		});
		buttonPane.add(btnCreateNew);
		JButton okButton = new JButton("Edit");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				if (isSelectionValid()) {
					new CallbackCreationDialog(parrent, project, getCallbackByName(list.getSelectedValue()), false);
					updateList();
				}
			}

		});

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				if (isSelectionValid()) {
					new CallbackDeleteDialog(parrent, project, getCallbackByName(list.getSelectedValue()));
					updateList();
				}
			}
		});
		buttonPane.add(btnDelete);
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton("Close");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void updateList () {
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		ArrayList<PCallback> callbacks = project.getCallbacks();
		for (PCallback cb : callbacks)
			listModel.addElement(cb.getName());

		list.setModel(listModel);
	}

	private boolean isSelectionValid () {
		if (list.getSelectedValue() == null) {
			JOptionPane.showMessageDialog(instance, "Please select callback.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (list.getSelectedIndex() == 0) {
			JOptionPane.showMessageDialog(instance, "This callback cannot be edited or deleted.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	private PCallback getCallbackByName (String name) {
		ArrayList<PCallback> cbList = project.getCallbacks();

		for (PCallback cb : cbList)
			if (cb.getName().equals(name)) return cb;

		return null;
	}
}
