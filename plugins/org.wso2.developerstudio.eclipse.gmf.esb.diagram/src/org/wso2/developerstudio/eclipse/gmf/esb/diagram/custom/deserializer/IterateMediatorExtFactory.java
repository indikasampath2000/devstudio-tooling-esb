/*
*  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.developerstudio.eclipse.gmf.esb.diagram.custom.deserializer;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.IterateMediatorFactory;
import org.apache.synapse.config.xml.OMElementUtils;
import org.apache.synapse.config.xml.SynapseXPathFactory;
import org.apache.synapse.config.xml.TargetFactory;
import org.apache.synapse.config.xml.XMLConfigConstants;
import org.apache.synapse.mediators.eip.Target;
import org.apache.synapse.mediators.eip.splitter.IterateMediator;
import org.apache.synapse.util.xpath.SynapseXPath;
import org.jaxen.JaxenException;

public class IterateMediatorExtFactory extends IterateMediatorFactory {

    protected Mediator createSpecificMediator(OMElement omElement) {

	Mediator mediator = new IterateMediator();

	QName ATT_CONTPAR = new QName("continueParent");
	QName ATT_PREPLD = new QName("preservePayload");
	QName ATT_ATTACHPATH = new QName("attachPath");
	QName ATT_SEQUENCIAL = new QName("sequential");
	QName ID_Q = new QName(XMLConfigConstants.NULL_NAMESPACE, "id");

	processAuditStatus(mediator, omElement);

	OMAttribute id = omElement.getAttribute(ID_Q);
	if (id != null) {
	    ((IterateMediator) mediator).setId(id.getAttributeValue());
	}

	OMAttribute continueParent = omElement.getAttribute(ATT_CONTPAR);
	if (continueParent != null) {
	    ((IterateMediator) mediator).setContinueParent(Boolean.valueOf(continueParent.getAttributeValue()));
	}

	OMAttribute preservePayload = omElement.getAttribute(ATT_PREPLD);
	if (preservePayload != null) {
	    ((IterateMediator) mediator).setPreservePayload(Boolean.valueOf(preservePayload.getAttributeValue()));
	}

	OMAttribute expression = omElement.getAttribute(ATT_EXPRN);
	if (expression != null) {
	    try {
		((IterateMediator) mediator).setExpression(SynapseXPathFactory.getSynapseXPath(omElement, ATT_EXPRN));
	    } catch (JaxenException e) {
		// ignore
	    }
	}

	OMAttribute attachPath = omElement.getAttribute(ATT_ATTACHPATH);
	String attachPathValue = ".";
	if (attachPath != null) {
	    attachPathValue = attachPath.getAttributeValue();
	}

	try {
	    SynapseXPath xp = new SynapseXPath(attachPathValue);
	    Log log = LogFactory.getLog(IterateMediatorFactory.class);
	    OMElementUtils.addNameSpaces(xp, omElement, log);
	    ((IterateMediator) mediator).setAttachPath(xp);
	} catch (JaxenException e) {
	    // ignore
	}

	boolean asynchronous = true;
	OMAttribute asynchronousAttr = omElement.getAttribute(ATT_SEQUENCIAL);
	if (asynchronousAttr != null && asynchronousAttr.getAttributeValue().equals("true")) {
	    asynchronous = false;
	}

	OMElement targetElement = omElement.getFirstChildWithName(TARGET_Q);
	if (targetElement != null) {
	    Target target = TargetFactory.createTarget(targetElement, null);
	    if (target != null) {
		target.setAsynchronous(asynchronous);
		((IterateMediator) mediator).setTarget(target);
	    }
	}

	addAllCommentChildrenToList(omElement, ((IterateMediator) mediator).getCommentsList());

	return mediator;
    }

}
