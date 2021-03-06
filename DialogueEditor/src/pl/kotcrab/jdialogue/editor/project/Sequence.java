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

package pl.kotcrab.jdialogue.editor.project;

import com.thoughtworks.xstream.XStream;
import pl.kotcrab.jdialogue.editor.Editor;
import pl.kotcrab.jdialogue.editor.IOUtils;
import pl.kotcrab.jdialogue.editor.components.Connector;
import pl.kotcrab.jdialogue.editor.components.DComponent;
import pl.kotcrab.jdialogue.editor.components.types.EndComponent;
import pl.kotcrab.jdialogue.editor.components.types.StartComponent;

import javax.swing.JOptionPane;
import java.io.File;
import java.util.ArrayList;

public class Sequence {
	private File file;
	private String name;

	private ArrayList<DComponent> componentList = new ArrayList<DComponent>();

	private boolean loaded = false;
	private boolean saved = false;

	public Sequence (String path, String name) {
		file = new File(path);
		this.name = name;
		componentList.add(new StartComponent(200, 200));
		loaded = true;
	}

	public Sequence (File file) {
		this.file = file;
		name = file.getName().split("\\.")[0]; // because . is reserved for regular expression
	}

	@SuppressWarnings("unchecked")
	public void load (XStream xstream, boolean gzip) {
		if (loaded == false) {
			loaded = true;

			if (gzip)
				componentList = (ArrayList<DComponent>) IOUtils.loadGzip(xstream, file);
			else
				componentList = (ArrayList<DComponent>) IOUtils.loadNormal(xstream, file);
		}
	}

	public void save (XStream xstream, boolean gzip) {
		saved = true;
		if (gzip)
			IOUtils.saveGzip(xstream, file, componentList);
		else
			IOUtils.saveNormal(xstream, file, componentList);
	}

	public void rename (XStream xstream, boolean gzip, String name) {
		file.delete();
		this.name = name;
		file = new File(file.getParentFile().getPath() + File.separator + name + ".xml");
		save(xstream, gzip);
	}

	public boolean export (XStream xstream, boolean gzipExport, String exportPath) // TODO organize components list by id
	{

		if (checkForEnd() == false) {
			JOptionPane.showMessageDialog(Editor.window, "Could not find 'End' component, please fix errors before exporting. Skipping sequence: " + name, "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (checkConnectors() == false) {
			JOptionPane.showMessageDialog(Editor.window, "Some components are not connected, please fix errors before exporting. Skipping sequence: " + name, "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		optimizeIDs();

		if (gzipExport)
			IOUtils.saveGzip(xstream, new File(exportPath + name + ".xml"), componentList);
		else
			IOUtils.saveNormal(xstream, new File(exportPath + name + ".xml"), componentList);

		return true;
	}

	private void optimizeIDs () {
		int id = 0;
		for (DComponent comp : componentList) {
			comp.setId(id);
			id++;
		}
	}

	private boolean checkForEnd () {
		for (DComponent comp : componentList) {
			if (comp instanceof EndComponent) return true;
		}

		return false;
	}

	public boolean checkConnectors () {
		for (DComponent comp : componentList) {
			Connector[] inputs = comp.getInputs();

			for (int i = 0; i < inputs.length; i++)
				if (inputs[i].getTarget() == null) return false;

			Connector[] outputs = comp.getOutputs();

			for (int i = 0; i < outputs.length; i++)
				if (outputs[i].getTarget() == null) return false;
		}

		return true;
	}

	public ArrayList<DComponent> getComponentList () {
		return componentList;
	}

	public String getName () {
		return name;
	}

	@Override
	public String toString () {
		return name;
	}

	public boolean isSaved () {
		return saved;
	}

	public void setSaved (boolean saved) {
		this.saved = saved;
	}

	public boolean isLoaded () {
		return loaded;
	}
}
