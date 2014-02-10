/*******************************************************************************
    DialogueEditor
    Copyright (C) 2013-2014 Pawel Pastuszak

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package pl.kotcrab.jdialogue.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import pl.kotcrab.jdialogue.editor.components.DComponentType;
import pl.kotcrab.jdialogue.editor.gui.AddComponentMenuItem;
import pl.kotcrab.jdialogue.editor.project.Project;

public class Editor extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	public static Editor window;
	
	private EditorLogic logic;
	
	private JPanel contentPane;
	
	private JSplitPane rendererSplitPane;
	private PropertyTable table;

	/**
	 * Create the frame.
	 */
	public Editor()
	{
		super("Dialogue Editor");
		
		logic = new EditorLogic(this);
		
		addWindowListener(logic.winAdapater);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		setMinimumSize(new Dimension(950, 425));
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		createMenuBar();
		createToolbar();
		createPopupMenu();
		
		logic.createRenderer();
		
		rendererSplitPane = new JSplitPane();
		rendererSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		rendererSplitPane.setResizeWeight(0.8);
		rendererSplitPane.setContinuousLayout(true);
		contentPane.add(rendererSplitPane, BorderLayout.CENTER);
		
		JSplitPane propertiesSplitPane = new JSplitPane();
		propertiesSplitPane.setResizeWeight(0.7);
		
		JPanel propertyPanel = new JPanel();
		propertyPanel.setLayout(new BorderLayout());
		
		table = new PropertyTable(new DefaultTableModel());
		logic.table = table;
		
		propertyPanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		propertyPanel.add(table, BorderLayout.CENTER);
		
		JPanel buttonsPanel = new JPanel();
		
		JButton btnSequences = new JButton("Sequences");
		JButton btnCharacters = new JButton("Characters");
		
		btnSequences.addActionListener(logic.sequencesBtnListner);
		btnCharacters.addActionListener(logic.charactersBtnListner);
		
		buttonsPanel.add(btnCharacters);
		buttonsPanel.add(btnSequences);
		
		propertiesSplitPane.setRightComponent(buttonsPanel);
		propertiesSplitPane.setLeftComponent(propertyPanel);
		rendererSplitPane.setRightComponent(propertiesSplitPane);
		rendererSplitPane.setLeftComponent(logic.canvas.getCanvas());

	}
	
	private void createToolbar()
	{
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setBorder(new LineBorder(new Color(0, 0, 0), 0));
		contentPane.add(toolBar, BorderLayout.NORTH);
		
		JButton btnSave = new JButton("Save");
		JButton btnLoad = new JButton("Load");
		JButton btnUndo = new JButton("Undo");
		JButton btnRedo = new JButton("Redo");
		JButton btnRun = new JButton("Run");
		
		btnSave.addActionListener(logic.saveButtonListener);
		btnLoad.addActionListener(logic.toolbarLoadListener);
		btnUndo.addActionListener(logic.toolbarUndoListener);
		btnRedo.addActionListener(logic.toolbarRedoListener);
		btnRun.addActionListener(null); //TODO setup listener
		
		toolBar.add(btnSave);
		toolBar.add(btnLoad);
		toolBar.add(btnUndo);
		toolBar.add(btnRedo);
		toolBar.add(btnRun);
	}
	
	private void createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.getPopupMenu().setLightWeightPopupEnabled(false); // without this menu will render under canvas
		menuBar.add(fileMenu);
		
		JMenuItem menuNewProject = new JMenuItem("New Project");
		JMenuItem menuLoadProject = new JMenuItem("Load Project");
		JMenuItem menuSaveProject = new JMenuItem("Save Project");
		JMenuItem menuExportProject = new JMenuItem("Export Project");
		JMenuItem menuExit = new JMenuItem("Exit");
		
		menuNewProject.addActionListener(logic.menubarNewProjectListener);
		menuSaveProject.addActionListener(logic.saveButtonListener);
		menuExportProject.addActionListener(logic.menubarExportProjectListener);
		
		fileMenu.add(menuNewProject);
		fileMenu.add(menuLoadProject);
		fileMenu.add(menuSaveProject);
		fileMenu.add(new JSeparator());
		fileMenu.add(menuExportProject);
		fileMenu.add(new JSeparator());
		fileMenu.add(menuExit);
		
		JMenu rendererMenu = new JMenu("Renderer");
		rendererMenu.getPopupMenu().setLightWeightPopupEnabled(false); // without this menu will render under canvas
		menuBar.add(rendererMenu);
		
		JCheckBoxMenuItem chckRenderDebug = new JCheckBoxMenuItem("Render Debug Info");
		JCheckBoxMenuItem chckRenderCurves = new JCheckBoxMenuItem("Render Curves");
		JMenuItem chckResetCamera = new JMenuItem("Reset Camera");
		
		chckRenderCurves.setSelected(true);
		
		chckRenderDebug.addActionListener(logic.menubarRenderDebugListener);
		chckRenderCurves.addActionListener(logic.menubarRenderCurvesListener);
		chckResetCamera.addActionListener(logic.menubarResetCameraListener);
		
		rendererMenu.add(chckRenderDebug);
		rendererMenu.add(chckRenderCurves);
		rendererMenu.add(chckResetCamera);
	}
	
	public void createPopupMenu()
	{
		PopupMenu popupMenu = new PopupMenu();
		
		MenuItem mAddText = new AddComponentMenuItem("Add 'Text'", DComponentType.TEXT);
		MenuItem mAddChoice = new AddComponentMenuItem("Add 'Choice'", DComponentType.CHOICE);
		MenuItem mAddRandom = new AddComponentMenuItem("Add 'Random'", DComponentType.RANDOM);
		MenuItem mAddCallback = new AddComponentMenuItem("Add 'Callback'", DComponentType.CALLBACK);
		MenuItem mAddRelay = new AddComponentMenuItem("Add 'Relay'", DComponentType.RELAY);
		MenuItem mAddEnd = new AddComponentMenuItem("Add 'End'", DComponentType.END);
		
		mAddText.addActionListener(logic.popupMenuListener);
		mAddChoice.addActionListener(logic.popupMenuListener);
		mAddRandom.addActionListener(logic.popupMenuListener);
		mAddCallback.addActionListener(logic.popupMenuListener);
		mAddRelay.addActionListener(logic.popupMenuListener);
		mAddEnd.addActionListener(logic.popupMenuListener);
		
		popupMenu.add(mAddText);
		popupMenu.add(mAddChoice);
		popupMenu.add(mAddRandom);
		popupMenu.add(mAddCallback);
		popupMenu.add(mAddRelay);
		popupMenu.add(mAddEnd);
		
		logic.popupMenu = popupMenu;
	}
	
	public void loadProject(File projectConfigFile)
	{
		logic.loadProject(projectConfigFile);
	}
	
	public void newProject(Project project)
	{
		logic.newProject(project);
	}
	
	@Override
	public void dispose()
	{
		logic.renderer.dispose(); // we have to manulay dispose renderer from this thread, or we will get "No OpenGL context found in the current thread."
		super.dispose();
	}
}
