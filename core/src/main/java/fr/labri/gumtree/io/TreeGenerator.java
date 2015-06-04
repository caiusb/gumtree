package fr.labri.gumtree.io;

import java.io.IOException;

import fr.labri.gumtree.tree.Tree;
import fr.labri.gumtree.tree.TreeUtils;

public abstract class TreeGenerator {
	
	public Tree fromFile(String file) throws IOException {
		Tree tree = generate(file);
		return processTree(tree);
	}

	private Tree processTree(Tree tree) {
		tree.refresh();
		TreeUtils.postOrderNumbering(tree);
		return tree;
	}
	
	public Tree fromString(String contents) throws IOException {
		Tree tree = generateFromString(contents);
		return processTree(tree);
	}
	
	public abstract Tree generate(String file) throws IOException;
	
	public Tree generateFromString(String contents){
		return null;
	}
	
	public abstract boolean handleFile(String file);
	
	public abstract String getName();

}
