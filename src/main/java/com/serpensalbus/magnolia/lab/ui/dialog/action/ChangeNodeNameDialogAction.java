package com.serpensalbus.magnolia.lab.ui.dialog.action;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.wrapper.MgnlPropertySettingNodeWrapper;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.dialog.action.SaveDialogAction;
import info.magnolia.ui.dialog.action.SaveDialogActionDefinition;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.ModelConstants;

import java.util.GregorianCalendar;

import javax.inject.Inject;
import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.Item;

/**
 * Unwrap the node when saving to avoid setting the status of all child nodes.
 *
 * @author Lars Fischer
 */
public class ChangeNodeNameDialogAction extends SaveDialogAction {

    private static final Logger log = LoggerFactory.getLogger(ChangeNodeNameDialogAction.class);

    private final Item item;
    private final EditorCallback callback;
    private final EditorValidator validator;

    @Inject
    public ChangeNodeNameDialogAction(SaveDialogActionDefinition definition, Item item, EditorValidator validator, EditorCallback callback, Item item1, EditorCallback callback1, EditorValidator validator1) {
        super(definition, item, validator, callback);
        this.item = item1;
        this.callback = callback1;
        this.validator = validator1;
    }

    private Node setNodeProperties(Node node) throws Exception {
        Node updatedNode = NodeUtil.deepUnwrap(node, MgnlPropertySettingNodeWrapper.class);
        PropertyUtil.updateOrCreate(node, NodeTypes.LastModified.LAST_MODIFIED, new GregorianCalendar());
        PropertyUtil.setProperty(node, NodeTypes.LastModified.LAST_MODIFIED_BY, MgnlContext.getUser().getName());

        return updatedNode;
    }

    private boolean isNewNodeName(JcrNodeAdapter item) {
        String original = StringUtils.defaultString(item.getNodeName(), "");
        String current = StringUtils.defaultString(item.getItemProperty(ModelConstants.JCR_NAME).toString(), "");
        return !StringUtils.equals(original, current);
    }

    @Override
    public void execute() throws ActionExecutionException {
        if (validateForm()) {
            final JcrNodeAdapter item = (JcrNodeAdapter) this.item;
            try {
                Node node = item.applyChanges();
                setNodeName(node, item);

                if (!node.isNew() && isNewNodeName(item)) {
                    node = setNodeProperties(node);
                }
                node.getSession().save();
            } catch (final Exception e) {
                log.error("Problem while setting node properties.", e);
                throw new ActionExecutionException(e);
            }
            callback.onSuccess(getDefinition().getName());
        }
    }
    
}
