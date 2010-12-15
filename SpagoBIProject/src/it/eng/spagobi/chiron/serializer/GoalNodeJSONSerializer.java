package it.eng.spagobi.chiron.serializer;


import it.eng.spagobi.kpi.goal.metadata.bo.GoalNode;

import java.util.Locale;

import org.json.JSONObject;

public class GoalNodeJSONSerializer implements Serializer {
	
	private static final String NODE_ID = "nodeId";
	private static final String OU = "ou";
	private static final String GOAL = "goal";
	private static final String NAME = "name";
	private static final String LABEL = "label";
	private static final String GOAL_DESC = "goalDesc";

	
	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof GoalNode) ) {
			throw new SerializationException("ModelNodeJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			GoalNode goalNode = (GoalNode) o;
			result = new JSONObject();
			
			result.put(NODE_ID, goalNode.getId());
			result.put(OU, goalNode.getOuId());
			result.put(NAME, goalNode.getName());
			result.put(LABEL, goalNode.getLabel());
			result.put(GOAL_DESC, goalNode.getGoalDescr());
		
			
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}

}
