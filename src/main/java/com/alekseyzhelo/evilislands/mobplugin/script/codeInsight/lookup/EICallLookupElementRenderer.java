package com.alekseyzhelo.evilislands.mobplugin.script.codeInsight.lookup;

import com.alekseyzhelo.evilislands.mobplugin.script.psi.EIFunctionDeclaration;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.lookup.LookupElementRenderer;

/**
 * Created by Aleks on 02-08-2015.
 */
public class EICallLookupElementRenderer<T extends LookupElement> extends LookupElementRenderer<T> {
    @Override
    public void renderElement(T element, LookupElementPresentation presentation) {
        presentation.setItemText(element.getLookupString());
        if(element.getPsiElement() instanceof EIFunctionDeclaration) {
            EIFunctionDeclaration declaration = (EIFunctionDeclaration) element.getPsiElement();
            presentation.setTypeText(declaration.getType() != null ? declaration.getType().getText() : "void");
        }
    }
}
