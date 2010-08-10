/**
Copyright (c) 2005-2010, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.

 * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 **/
package it.eng.spagobi.engines.commonj.utils;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import commonj.work.WorkEvent;

public class GeneralUtils {

	static final String WORK_COMPLETED="Work completed";
	static final String WORK_STARTED="Work started";
	static final String WORK_ACCEPTED="Work accepted";
	static final String WORK_REJECTED="Work rejected";
	static final String WORK_NOT_STARTED="Work not started";


	static public String getEventMessage(int status){
		if(status==WorkEvent.WORK_COMPLETED){
			return WORK_COMPLETED;
		}
		else if(status==WorkEvent.WORK_STARTED){
			return WORK_STARTED;
		}
		else if(status==WorkEvent.WORK_ACCEPTED){
			return WORK_ACCEPTED;			
		}
		else if(status==WorkEvent.WORK_REJECTED){
			return WORK_REJECTED;			
		}
		else if(status==0){
			return WORK_NOT_STARTED;
		}
		return "";

	}

	static public JSONObject buildJSONObject (String pid,int statusCode) throws JSONException{
		String message=GeneralUtils.getEventMessage(statusCode);
		JSONObject info=new JSONObject();
		info.put("pid", pid);
		info.put("status_code", statusCode);
		info.put("status", message);
		info.put("time", (new Date()).toString());
		return info;

	}

}
