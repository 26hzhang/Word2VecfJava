package com.isaac.lexsub.representation;

import java.util.Arrays;
import java.util.regex.Pattern;

public class Token {
	
	private int id = 0;
	private String form = "*root*";
	private String lemma = "_";
	private String cpostag = "_";
	private String postag = "_";
	private String feats = "_";
	private int head = -1;
	private String deptype = "rroot";
	private int phead = -1;
	private String pdeptype = "_";
	
	// Default constructor
	public Token() { }
	
	// Constructor
	public Token(String[] tokens) {
		this.id = Integer.parseInt(tokens[0]);
		this.form = tokens[1];
		this.lemma = tokens[2];
		this.cpostag = tokens[3];
		this.postag = tokens[4];
		this.feats = tokens[5];
		this.head = Integer.parseInt(tokens[6]);
		this.deptype = tokens[7];
		if (tokens.length > 8) {
			if (tokens[8].equals("_")) this.phead = -1;
			else this.phead = Integer.parseInt(tokens[8]);
			this.pdeptype = tokens[9];
		} else {
			this.phead = -1;
			this.deptype = "_";
		}
	}
	
	@Override
	public String toString() {
		return String.join("\t", Arrays.asList(String.valueOf(id), form, lemma, cpostag, feats, String.valueOf(head), deptype, String.valueOf(phead), pdeptype));
	}
	
	// From tree line
	public static final Pattern treeLineExtractor = Pattern.compile(""); //TODO
	
	public void fromTreeLine() {
		// TODO
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}

	public String getLemma() {
		return lemma;
	}
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public String getCpostag() {
		return cpostag;
	}
	public void setCpostag(String cpostag) {
		this.cpostag = cpostag;
	}

	public String getPostag() {
		return postag;
	}
	public void setPostag(String postag) {
		this.postag = postag;
	}

	public String getFeats() {
		return feats;
	}
	public void setFeats(String feats) {
		this.feats = feats;
	}

	public int getHead() {
		return head;
	}
	public void setHead(int head) {
		this.head = head;
	}

	public String getDeptype() {
		return deptype;
	}
	public void setDeptype(String deptype) {
		this.deptype = deptype;
	}

	public int getPhead() {
		return phead;
	}
	public void setPhead(int phead) {
		this.phead = phead;
	}

	public String getPdeptype() {
		return pdeptype;
	}
	public void setPdeptype(String pdeptype) {
		this.pdeptype = pdeptype;
	}

}
