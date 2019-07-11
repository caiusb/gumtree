package com.github.gumtreediff.client.diff;

import com.github.gumtreediff.client.diff.web.DiffView;
import org.rendersnake.HtmlCanvas;

import java.io.File;
import java.io.IOException;

public abstract class StringHtmlDiff {

    public static String getHtmlOfDiff(File aSrc, File bSrc) throws IOException {
        DiffView diffView = new DiffView(aSrc, bSrc);
        HtmlCanvas html = new HtmlCanvas();
        diffView.renderOn(html);
        return html.toHtml();
    }
}