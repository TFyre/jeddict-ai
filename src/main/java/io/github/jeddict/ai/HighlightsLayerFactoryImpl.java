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
package io.github.jeddict.ai;

import io.github.jeddict.ai.completion.JeddictCompletionProvider;
import org.netbeans.spi.editor.highlighting.HighlightsLayerFactory;
import org.netbeans.spi.editor.highlighting.HighlightsLayer;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.highlighting.ZOrder;

@MimeRegistration(mimeType = "", service = HighlightsLayerFactory.class)
public class HighlightsLayerFactoryImpl implements HighlightsLayerFactory {

    @Override
    public HighlightsLayer[] createLayers(Context context) {
        return new HighlightsLayer[] {
            HighlightsLayer.create(JeddictCompletionProvider.class.getName() + "-3", ZOrder.SYNTAX_RACK.forPosition(1600), false, JeddictCompletionProvider.getPreTextBag(context.getDocument(), context.getComponent())),
        };
    }
}
