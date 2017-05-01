/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 * 
 * Copyright 2017 The ZAP Development Team
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.zaproxy.zap.extension.history;

import java.awt.Component;
import java.io.File;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.model.HistoryReference;
import org.parosproxy.paros.model.SiteNode;
import org.parosproxy.paros.view.SiteMapPanel;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.model.Context;

public class PopupMenuExportContextURLs extends PopupMenuExportURLs {

	private static final long serialVersionUID = -4426560452505908380L;
	
	private static Logger LOG = Logger.getLogger(PopupMenuExportURLs.class);

	public PopupMenuExportContextURLs(String menuItem) {
		super(menuItem);
	}
	
	@Override
	public boolean isEnableForComponent(Component invoker) {
		if (SiteMapPanel.CONTEXT_TREE_COMPONENT_NAME.equals(invoker.getName())) {
			Context ctx = View.getSingleton().getSiteTreePanel().getSelectedContext();
			if (ctx != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void performAction() {
		Context ctx = extension.getView().getSiteTreePanel().getSelectedContext(); 
		if (ctx == null) {
			View.getSingleton().showWarningDialog(Constant.messages.getString("exportUrls.popup.context.error"));
			if (LOG.isDebugEnabled()) {
				LOG.debug("No context selected, when trying to export URLs for a context.");
			}
			return;
		}
		
		File file = super.getOutputFile();
		if (file == null) {
			return;
		}
		super.writeURLs(file, this.getOutputSet(ctx));
	}

	private SortedSet<String> getOutputSet(Context ctx) {
		SortedSet<String> outputSet = new TreeSet<String>();

		for (SiteNode node : ctx.getNodesInContextFromSiteTree()) {
			HistoryReference nodeHR = node.getHistoryReference();
			if (nodeHR != null && !HistoryReference.getTemporaryTypes().contains(nodeHR.getHistoryType())) {
				outputSet.add(nodeHR.getURI().toString());
			}
		}
		return outputSet;
	}

}
