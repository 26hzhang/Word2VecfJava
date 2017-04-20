package com.isaac.word2vecf.vocabulary;

/**
 * Created by zhanghao on 19/4/17.
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class Token {
	private int index;
	private String word;
	private int relation;
	private String dependency;

	public Token (int index, String word, Integer relation, String dependency) {
		this.index = index;
		this.word = word;
		this.relation = relation;
		this.dependency = dependency;
	}

	public int getIndex() {
		return index;
	}

	public String getWord() {
		return word;
	}

	public int getRelation() {
		return relation;
	}

	public String getDependency() {
		return dependency;
	}
}
