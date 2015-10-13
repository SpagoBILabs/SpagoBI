/**
 SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0. If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/.
 
**/
package it.eng.spagobi.commons.utils;

import org.eclipse.core.runtime.QualifiedName;

public class SpagoBIMetaConstants {

	
	public static final String FOLDER_DATASET = "Business Queries";
	
	public static QualifiedName MODEL_NAME = new QualifiedName("it.eng.spagobi.meta.editor.modelId", "modelId");
	public static QualifiedName MODEL_FILE_NAME = new QualifiedName("it.eng.spagobi.meta.editor.modelFileName", "modelFileName");
	public static QualifiedName MODEL_FILE_NAME_FULL_PATH = new QualifiedName("it.eng.spagobi.meta.editor.modelFileNameFullPath", "modelFileNameFullPath");
	public static QualifiedName MODEL_FILE_NAME_REL_PATH = new QualifiedName("it.eng.spagobi.meta.editor.modelFileNameRelPath", "modelFileNameRelPath");
	public static QualifiedName DIRTY_MODEL = new QualifiedName("it.eng.spagobi.meta.mode.dirty", "dirty");
}
