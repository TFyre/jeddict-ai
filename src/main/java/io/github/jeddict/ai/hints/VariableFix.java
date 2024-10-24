/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.github.jeddict.ai.hints;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import io.github.jeddict.ai.completion.Action;
import io.github.jeddict.ai.JeddictChatModel;
import io.github.jeddict.ai.util.SourceUtil;
import static io.github.jeddict.ai.util.StringUtil.removeCodeBlockMarkers;
import javax.lang.model.element.Element;
import org.json.JSONArray;
import org.json.JSONObject;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.spi.java.hints.JavaFix;
import org.openide.util.NbBundle;

/**
 *
 * @author Shiwani Gupta
 */
public class VariableFix extends JavaFix {

    private ElementHandle classType;
    private final Action action;
    private String actionTitleParam;
    private String compliationError;

    public VariableFix(TreePathHandle tpHandle, Action action, ElementHandle classType) {
        super(tpHandle);
        this.classType = classType;
        this.action = action;
    }

    public VariableFix(TreePathHandle tpHandle, String compliationError, String actionTitleParam) {
        super(tpHandle);
        this.compliationError = compliationError;
        this.actionTitleParam = actionTitleParam;
        this.action = Action.COMPILATION_ERROR;
    }

    @Override
    protected String getText() {
        return NbBundle.getMessage(JeddictChatModel.class, "HINT_VARIABLE_COMPILATION_ERROR", actionTitleParam);
    }

    @Override
    protected void performRewrite(JavaFix.TransformationContext tc) throws Exception {
        WorkingCopy copy = tc.getWorkingCopy();
        if (copy.toPhase(JavaSource.Phase.RESOLVED).compareTo(JavaSource.Phase.RESOLVED) < 0) {
            return;
        }

        TreePath treePath = tc.getPath();
        Tree leaf = treePath.getLeaf();

        Element elm = copy.getTrees().getElement(treePath);
        if (elm == null) {
            return;
        }

        String content = null;

        // Check if it's a variable and there's an error to fix
        if (leaf.getKind() == Tree.Kind.VARIABLE && action == Action.COMPILATION_ERROR) {
            content = new JeddictChatModel().fixVariableError(leaf.toString(), compliationError);
        }

        if (content == null) {
            return;
        }

        // Parse the content as JSON
        JSONObject json = new JSONObject(removeCodeBlockMarkers(content));
        JSONArray imports = json.getJSONArray("imports");
        String variableContent = json.getString("variableContent");

        SourceUtil.addImports(copy, imports);
        copy.rewrite(leaf, copy.getTreeMaker().QualIdent(variableContent));
    }

}
