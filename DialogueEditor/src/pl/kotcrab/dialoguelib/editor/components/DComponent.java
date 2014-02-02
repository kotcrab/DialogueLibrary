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

package pl.kotcrab.dialoguelib.editor.components;

import java.util.ArrayList;

import pl.kotcrab.dialoguelib.editor.Assets;
import pl.kotcrab.dialoguelib.editor.KotcrabText;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public abstract class DComponent
{
	
	private KotcrabText title;
	
	private int id; // id of this component, -1 for start, other in order 0,1,2,3,4...
	
	private int x, y;
	private int ry; // bottom, left point of background
	private int height, width;
	private Rectangle boundingRectangle = new Rectangle(0, 0, 0, 0);
	
	private Connector[] inputs;
	private Connector[] outputs;
	
	protected ComponentTableModel tableModel;
	
	private boolean visible;
	
	public DComponent(String title, int x, int y, int inputs, int outputs, int id)
	{
		this.x = x;
		this.y = y;
		this.id = id;
		
		this.title = new KotcrabText(Assets.consolasFont, title, false, 0, 0);
		this.title.setScale(0.7f);
		
		calcSize(inputs, outputs);
		
		this.inputs = new Connector[inputs];
		this.outputs = new Connector[outputs];
		
		for(int i = 0; i < this.inputs.length; i++)
			this.inputs[i] = new Connector(this, true);
		
		for(int i = 0; i < this.outputs.length; i++)
			this.outputs[i] = new Connector(this, false);
		
		ry = y - height / 2;
		
		distributeConnections();
		calcTextPos();
	}
	
	/**
	 * Finishes desarialization of this object
	 */
	public void setup()
	{
		calcSize(inputs.length, outputs.length);
		distributeConnections();
		calcTextPos();
	}
	
	private void distributeConnections()
	{
		float avY = height - 30; // avaiable space in y coordinate
		
		// i have no idea what i'm doing, no seriously why is this working?
		float avYIn = (avY - (12 * (inputs.length))) / (inputs.length);
		for(int i = 0; i < inputs.length; i++)
			inputs[i].setPosition(x, ry + avYIn / 2 + ((avYIn + 12) * (inputs.length - 1 - i)));
		
		float avYOut = (avY - (12 * (outputs.length))) / (outputs.length);
		for(int i = 0; i < outputs.length; i++)
			outputs[i].setPosition(x + width - 12, ry + avYOut / 2 + ((avYOut + 12) * (outputs.length - 1 - i)));
		
		// notes:
		// 12 is connection height
		// outputs.length - 1 - i to reverse render order, important when we resize component to disconnect connection from bottom, not from top
		// but still i don't know why is this working...
	}
	
	private void calcSize(int inputs, int outputs)
	{
		width = (int) (title.getTextBounds().width + 30);
		if(inputs > outputs)
			height = (inputs + 1) * 20 + 20;
		else
			height = (outputs + 1) * 20 + 20;
		
		boundingRectangle.set(x, y - height / 2, width, height);
		
	}
	
	public void render(SpriteBatch batch)
	{
		title.draw(batch);
	}
	
	public void renderShapes(ShapeRenderer shapeRenderer)
	{
		shapeRenderer.setColor(Color.GRAY);
		shapeRenderer.rect(x, ry, width, height);
		
		for(int i = 0; i < inputs.length; i++)
			inputs[i].render(shapeRenderer);
		
		for(int i = 0; i < outputs.length; i++)
			outputs[i].render(shapeRenderer);
		
	}
	
	public void renderOutline(ShapeRenderer shapeRenderer)
	{
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(x, ry, width, height); // outline
		shapeRenderer.line(x, ry + height - 30, x + width, ry + height - 30); // line under text
	}
	
	public void renderSelectionOutline(ShapeRenderer shapeRenderer, Color color)
	{
		shapeRenderer.setColor(color);
		shapeRenderer.rect(x, y - height / 2, width, height);// outline
		shapeRenderer.line(x, ry + height - 30, x + width, ry + height - 30); // line under text
	}
	
	public void renderDebug(ShapeRenderer shapeRenderer)
	{
		shapeRenderer.rect(boundingRectangle.x, boundingRectangle.y, boundingRectangle.width, boundingRectangle.height);
	}
	
	/**
	 * Detaches this component from others
	 */
	public void detachAll()
	{
		for(int i = 0; i < inputs.length; i++)
			inputs[i].detach();
		
		for(int i = 0; i < outputs.length; i++)
			outputs[i].detach();
	}
	
	public void detachAllNotOnList(ArrayList<DComponent> componentList)
	{
		for(int i = 0; i < inputs.length; i++)
			inputs[i].detachNotOnList(componentList);
		
		for(int i = 0; i < outputs.length; i++)
			outputs[i].detachNotOnList(componentList);
	}
	
	/**
	 * Resizes the component, if new number is smaller than current, connection will be detached
	 * 
	 * @param nInputs
	 *            New number of inputs
	 * @param nOutputs
	 *            New number of outputs
	 */
	public void resize(int nInputs, int nOutputs)
	{
		if(nInputs != inputs.length)
		{
			if(nInputs < inputs.length) // is new number is smaller than previous
			{
				for(int i = nInputs; i < inputs.length; i++)
					inputs[i].detach();
				
				Connector[] newInputs = new Connector[nInputs];
				
				System.arraycopy(inputs, 0, newInputs, 0, nInputs);
				
				inputs = newInputs;
			}
			
			if(nInputs > inputs.length)
			{
				
				Connector[] newInputs = new Connector[nInputs];
				
				System.arraycopy(inputs, 0, newInputs, 0, inputs.length);
				
				for(int i = inputs.length; i < newInputs.length; i++)
					newInputs[i] = new Connector(this, true);
				
				inputs = newInputs;
			}
		}
		
		if(nOutputs != outputs.length)
		{
			if(nOutputs < outputs.length)
			{
				for(int i = nOutputs; i < outputs.length; i++)
					outputs[i].detach();
				
				Connector[] newOutputs = new Connector[nOutputs];
				
				System.arraycopy(outputs, 0, newOutputs, 0, nOutputs);
				
				outputs = newOutputs;
			}
			
			if(nOutputs > outputs.length)
			{
				Connector[] newOutputs = new Connector[nOutputs];
				
				System.arraycopy(outputs, 0, newOutputs, 0, outputs.length);
				
				for(int i = outputs.length; i < newOutputs.length; i++)
					newOutputs[i] = new Connector(this, false);
				
				outputs = newOutputs;
			}
		}
		
		calcSize(inputs.length, outputs.length);
		
		ry = y - height / 2;
		
		calcTextPos();
		distributeConnections();
		
	}
	
	public boolean contains(float x, float y) // is given point inside component
	{
		return this.x <= x && this.x + this.width >= x && this.y - this.height / 2 <= y && this.y + this.height / 2 >= y;
	}
	
	public boolean contains(Rectangle rectangle)
	{
		float xmin = rectangle.x;
		float xmax = xmin + rectangle.width;
		
		float ymin = rectangle.y;
		float ymax = ymin + rectangle.height;
		
		return ((xmin > x && xmin < x + width) && (xmax > x && xmax < x + width)) && ((ymin > y && ymin < y + height / 2) && (ymax > y && ymax < y + height / 2));
	}
	
	public Connector connectionContains(float x, float y)
	{
		for(int i = 0; i < inputs.length; i++)
		{
			if(inputs[i].contains(x, y)) return inputs[i];
		}
		
		for(int i = 0; i < outputs.length; i++)
		{
			if(outputs[i].contains(x, y)) return outputs[i];
		}
		
		return null;
	}
	
	private void calcTextPos()
	{
		this.title.center(width);
		this.title.setPosition(x + this.title.getPosition().x, ry + height - 30);
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
		ry = y - height / 2;
		
		calcTextPos();
		distributeConnections();
		boundingRectangle.set(x, ry, width, height);
		
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public Connector[] getOutputs()
	{
		return outputs;
	}
	
	public Connector[] getInputs()
	{
		return inputs;
	}
	
	public void setInputs(Connector[] inputs)
	{
		this.inputs = inputs;
	}
	
	public void setOutputs(Connector[] outputs)
	{
		this.outputs = outputs;
	}
	
	public void setId(int id)
	{
		this.id = id;
		Object[][] data = tableModel.data;
		
		if(data[0][0].equals("ID")) data[0][1] = new Integer(id);
	}
	
	public int getId()
	{
		return id;
	}
	
	public ComponentTableModel getTableModel()
	{
		return tableModel;
	}
	
	public void setTableModelData(Object[][] data)
	{
		tableModel.data = data;
	}
	
	public Rectangle getBoundingRectangle()
	{
		return boundingRectangle;
	}
	
	protected void setListeners()
	{
		
	}
	
	public void calcVisible(Rectangle cameraRect)
	{
		if(cameraRect.overlaps(boundingRectangle))
			visible = true;
		else
			visible = false;
	}
	
	public boolean isVisible()
	{
		return visible;
	}
}