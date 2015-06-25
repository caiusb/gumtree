package fr.labri.gumtree.gen.jdt;

import org.eclipse.jdt.core.dom.ASTNode;

import fr.labri.gumtree.tree.Tree;

public class JdtTree extends Tree {

	private ASTNode containedNode;

	public JdtTree(int type, String label, ASTNode n) {
		super(type, label, n.getClass().getSimpleName());
		this.containedNode = n;
	}
	
	public ASTNode getContainedNode() {
		return containedNode;
	}
	
	
}
