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

package pl.kotcrab.jdialogue.editor.gui;

import pl.kotcrab.jdialogue.editor.components.DComponentType;

import java.awt.HeadlessException;
import java.awt.MenuItem;
import java.awt.MenuShortcut;

public class AddComponentMenuItem extends MenuItem {
	private static final long serialVersionUID = 1L;

	private DComponentType type;

	public AddComponentMenuItem (String label, DComponentType type) throws HeadlessException {
		super(label);
		this.type = type;
	}

	public AddComponentMenuItem (String label, MenuShortcut s, DComponentType type) throws HeadlessException {
		super(label, s);
		this.type = type;
	}

	public DComponentType getType () {
		return type;
	}
}
