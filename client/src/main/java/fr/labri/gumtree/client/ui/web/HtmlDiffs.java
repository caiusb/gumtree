package fr.labri.gumtree.client.ui.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import fr.labri.gumtree.actions.RootAndLeavesClassifier;
import fr.labri.gumtree.actions.TreeClassifier;
import fr.labri.gumtree.algo.StringAlgorithms;
import fr.labri.gumtree.matchers.MappingStore;
import fr.labri.gumtree.matchers.Matcher;
import fr.labri.gumtree.tree.Tree;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

public final class HtmlDiffs {
	
	private static final String SRC_MV_SPAN = "<span class=\"%s\" id=\"move-src-%d\" data-title=\"%s\">";
	private static final String DST_MV_SPAN = "<span class=\"%s\" id=\"move-dst-%d\" data-title=\"%s\">";
	private static final String ADD_DEL_SPAN = "<span class=\"%s\" data-title=\"%s\">";
	private static final String UPD_SPAN = "<span class=\"cupd\">";
	private static final String ID_SPAN = "<span class=\"marker\" id=\"mapping-%d\"></span>";
	private static final String END_SPAN = "</span>";
	
	private String srcDiff;
	
	private String dstDiff;
	
	private Tree src;
	
	private Tree dst;
	
	private Reader fSrcReader;
	
	private Reader fDstReader;
	
	private Matcher matcher;
	
	private MappingStore mappings;
	
	public HtmlDiffs(File fSrc, File fDst, Tree src, Tree dst, Matcher matcher) throws FileNotFoundException {
		this(new FileReader(fSrc), new FileReader(fDst), src, dst, matcher);
	}
	
	public HtmlDiffs(Reader fSrc, Reader fDst, Tree src, Tree dst, Matcher matcher) {
		this.fSrcReader = fSrc;
		this.fDstReader = fDst;
		this.src = src;
		this.dst = dst;
		this.matcher = matcher;
		this.mappings = matcher.getMappings();
	}
	
	public void produce() throws IOException {
		TreeClassifier c = new RootAndLeavesClassifier(src, dst, matcher);
		TIntIntMap mappingIds = new TIntIntHashMap();
		
		int uId = 1;
		int mId = 1;
		
		TagIndex ltags = new TagIndex();
		for (Tree t: src.getTrees()) {
			if (c.getSrcMvTrees().contains(t)) {
				mappingIds.put(mappings.getDst(t).getId(), mId);
				ltags.addStartTag(t.getPos(), String.format(ID_SPAN, uId++));
				ltags.addTags(t.getPos(), String.format(SRC_MV_SPAN, "token mv", mId++, tooltip(t)), t.getEndPos(), END_SPAN);
			} if (c.getSrcUpdTrees().contains(t)) {
				mappingIds.put(mappings.getDst(t).getId(), mId);
				ltags.addStartTag(t.getPos(), String.format(ID_SPAN, uId++));
				ltags.addTags(t.getPos(), String.format(SRC_MV_SPAN, "token upd", mId++, tooltip(t)), t.getEndPos(), END_SPAN);
				
				List<int[]> hunks = StringAlgorithms.hunks(t.getLabel(), mappings.getDst(t).getLabel());
				for(int[] hunk: hunks)
					ltags.addTags(t.getPos() + hunk[0], UPD_SPAN, t.getPos() + hunk[1], END_SPAN);
				
			} if (c.getSrcDelTrees().contains(t)) {
				ltags.addStartTag(t.getPos(), String.format(ID_SPAN, uId++));
				ltags.addTags(t.getPos(), String.format(ADD_DEL_SPAN, "token del", tooltip(t)), t.getEndPos(), END_SPAN);
			}
		}

		TagIndex rtags = new TagIndex();
		for (Tree t: dst.getTrees()) {
			if (c.getDstMvTrees().contains(t)) {
				int dId = mappingIds.get(t.getId());
				rtags.addStartTag(t.getPos(), String.format(ID_SPAN, uId++));
				rtags.addTags(t.getPos(), String.format(DST_MV_SPAN, "token mv", dId, tooltip(t)), t.getEndPos(), END_SPAN);
			} if (c.getDstUpdTrees().contains(t)) {
				int dId = mappingIds.get(t.getId());
				rtags.addStartTag(t.getPos(), String.format(ID_SPAN, uId++));
				rtags.addTags(t.getPos(), String.format(DST_MV_SPAN, "token upd", dId, tooltip(t)), t.getEndPos(), END_SPAN);
				List<int[]> hunks = StringAlgorithms.hunks(mappings.getSrc(t).getLabel(), t.getLabel());
				for(int[] hunk: hunks)
					rtags.addTags(t.getPos() + hunk[2], UPD_SPAN, t.getPos() + hunk[3], END_SPAN);
			} if (c.getDstAddTrees().contains(t)) {
				rtags.addStartTag(t.getPos(), String.format(ID_SPAN, uId++));
				rtags.addTags(t.getPos(), String.format(ADD_DEL_SPAN, "token add", tooltip(t)), t.getEndPos(), END_SPAN);
			}
		}

		StringWriter w1 = new StringWriter();
		BufferedReader r = new BufferedReader(fSrcReader);
		int cursor = 0;
		
		while (r.ready()) {
			char cr = (char) r.read();
			w1.append(ltags.getEndTags(cursor));
			w1.append(ltags.getStartTags(cursor));
			append(cr, w1);
			cursor++;
		}
		w1.append(ltags.getEndTags(cursor));
		r.close();
		srcDiff = w1.toString();
		
		StringWriter w2 = new StringWriter();
		r = new BufferedReader(fDstReader);
		cursor = 0;
	
		while (r.ready()) {
			char cr = (char) r.read();
			w2.append(rtags.getEndTags(cursor));
			w2.append(rtags.getStartTags(cursor));
			append(cr, w2);
			cursor++;
		}
		w2.append(rtags.getEndTags(cursor));
		r.close();
		
		dstDiff = w2.toString();
	}
	
	public String getSrcDiff() {
		return srcDiff;
	}

	public String getDstDiff() {
		return dstDiff;
	}

	private static String tooltip(Tree t) {
		return (t.getParent() != null) ? t.getParent().getTypeLabel() + "/" + t.getTypeLabel() : t.getTypeLabel();
	}

	private static void append(char cr, Writer w) throws IOException {
		if (cr == '<') w.append("&lt;");
		else if (cr == '>') w.append("&gt;");
		else if (cr == '&') w.append("&amp;");
		else w.append(cr);
	}
	
}
