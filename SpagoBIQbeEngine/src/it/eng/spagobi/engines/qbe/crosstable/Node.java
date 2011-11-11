/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.qbe.crosstable;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class Node implements Cloneable{
		public static final String CROSSTAB_NODE_JSON_CHILDS = "node_childs";
		public static final String CROSSTAB_NODE_JSON_KEY = "node_key";
	
		private String value;
		private List<Node> childs;
		private int leafPosition =-1;
		private List<Integer> leafPositionsForCF;//Uset for the CF
		private Node fatherNode; //!= from null only if we need the value
		
		public Node(String value){
			this.value = value;
			childs = new ArrayList<Node>();
		}

		public String getValue() {
			return value;
		}

		public List<Node> getChilds() {
			return childs;
		}
		
		public void setChilds(List<Node> childs) {
			this.childs = childs;
		}
		
		public void addChild(Node child){
			childs.add(child);
		}
		
		public boolean isChild(Node child){
			return childs.contains(child);
		}
		
		public int getLeafsNumber(){
			if(childs.size()==0){
				return 1;
			}else{
				int leafsNumber=0;
				for(int i=0; i<childs.size(); i++){
					leafsNumber = leafsNumber + childs.get(i).getLeafsNumber();
				}
				return leafsNumber;
			}
		}
		
		public JSONObject toJSONObject() throws JSONException{
			JSONObject thisNode = new JSONObject();
			
			thisNode.put(CROSSTAB_NODE_JSON_KEY, value);
			
			if(childs.size()>0){
				JSONArray nodeChilds = new JSONArray();
				for(int i=0; i<childs.size(); i++){
					nodeChilds.put(childs.get(i).toJSONObject());
				}
				thisNode.put(CROSSTAB_NODE_JSON_CHILDS, nodeChilds);
			}
					
			return thisNode;
		}
		
		@Override
		public String toString(){
			String string;
			
			if(childs.size()==0){
				return "["+value.toString()+"]";
			}else{
				string = "["+value.toString()+",[";
				for(int i=0; i<childs.size()-1; i++){
					string = string+childs.get(i).toString()+",";
				}
				string=string+childs.get(childs.size()-1).toString()+"]]";
			}
			return string;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}


		public int getLeafPosition() {
			return leafPosition;
		}
		
		public void setLeafPositions(){
			setLeafPositions(0);
		}
		
		private int setLeafPositions(int pos){
			if(childs.size()==0){
				leafPosition = pos;
				pos++;
			}else{
				for(int i=0; i<childs.size(); i++){
					pos = childs.get(i).setLeafPositions(pos);
				}
			}
			return pos;
		}
		
		public Node clone(){
			Node n = new Node(value);
			if(childs.size()>0){
				for (int j = 0; j < childs.size(); j++) {
					n.addChild(childs.get(j).clone());
				}
			}
			return n;
		}

		public List<Integer> getLeafPositionsForCF() {
			return leafPositionsForCF;
		}

		public void setLeafPositionsForCF(List<Integer> leafPositionsForCF) {
			this.leafPositionsForCF = leafPositionsForCF;
		}

		public List<Node> getLevel(int level){
			List<Node> nodes = new ArrayList<Node>();
			if(level==0){
				nodes.add(this);
			}else{
				if(childs.size()==0){
					return null;
				}
				for(int i=0; i<childs.size(); i++){
					nodes.addAll(childs.get(i).getLevel(level-1));
				}
			}
			return nodes;
		}
		
		public List<Node> getLeafs(){
			List<Node> list = new ArrayList<Node>();
			if(childs.size()==0){
				list.add(this);
			}else{
				for(int i=0; i<childs.size(); i++){
					list.addAll(childs.get(i).getLeafs());
				}
			}
			return list;
		}
		
		public void updateFathers(){
			for(int i=0; i<childs.size(); i++ ){
				childs.get(i).fatherNode = this;
				childs.get(i).updateFathers();
			}
		}
		
		public int getSubTreeDepth(){
			if(childs.size()==0){
				return 1;
			}else{
				return 1 + childs.get(0).getSubTreeDepth();
			}
		}
		
		public void removeNodeFromTree(){
			if(fatherNode!=null){
				List<Node> fatherChilds = fatherNode.getChilds();
				for(int i=0;i<fatherChilds.size(); i++){
					if(fatherChilds.get(i)==this){
						fatherChilds.remove(i);
						break;
					}
				}
				if(fatherChilds.size()==0){
					fatherNode.removeNodeFromTree();
				}
			}
		}
		
		
		public int getRightMostLeafPositionCF(){
			if(childs.size()==0){
				return leafPosition;
			}
			return childs.get(childs.size()-1).getRightMostLeafPositionCF();
		}

		/**
		 * For test
		 * @param height
		 * @param branch
		 */
		public void buildSubTree(int height, int branch){
			if(height<2){
				for(int i=0; i<branch; i++){
					addChild(new Node(""+i));
				}
			}else{
				for(int i=0; i<branch; i++){
					Node n = new Node(value+"_"+i);
					addChild(n);
					n.buildSubTree(height-1, branch);
				}
			}
		}
		
	
}
