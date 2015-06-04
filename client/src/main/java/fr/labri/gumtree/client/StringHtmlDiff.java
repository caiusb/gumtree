package fr.labri.gumtree.client;

import org.rendersnake.HtmlCanvas;

import fr.labri.gumtree.client.ui.web.views.DiffView;

public class StringHtmlDiff {
	
	public String getHtmlOfDiff(String urlFolder, String aContent, String bContent, String aName, String bName) throws Exception {
		DiffView diffView = new DiffView(aContent, bContent, aName, bName);
		diffView.setURLFolder(urlFolder);
		HtmlCanvas html = new HtmlCanvas();
		diffView.renderOn(html);
		return html.toHtml();
	}
}
