package com.isaac.lexsub.representation;

import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class ContextInstance {

	private final List<String> validPOS = Arrays.asList("r", "j", "a", "v", "n");
	private final int CONTEXT_TEXT_BEGIN_INDEX = 3;
	private final int TARGET_INDEX = 2;
	
	private String line;
	private int targetInd;
	private String[] words;
	private String target;
	private String fullTargetKey;
	private String pos;
	private String targetKey;
	private String targetLemma;
	private int targetId;
	private String targetPos;
	
	public ContextInstance(String line, boolean noPosFlag) {
		this.line = line;
		String[] tokens = line.split("\t");
		this.targetInd = Integer.parseInt(tokens[TARGET_INDEX]);
		this.words = tokens[3].split(" ");
		this.target = this.words[this.targetInd];
		this.fullTargetKey = tokens[0];
		this.pos = this.fullTargetKey.split(".")[this.fullTargetKey.split(".").length - 1];
		String[] tmp = this.fullTargetKey.split(".");
		this.targetKey = String.join(".", Arrays.asList(tmp[0], tmp[1])); // remove suffix in cases of bar.n.v
		this.targetLemma = this.fullTargetKey.split(".")[0];
		this.targetId = Integer.parseInt(tokens[1]);
		if (validPOS.contains(this.pos)) this.pos = WordUtils.capitalize(this.pos);
		if (noPosFlag) this.targetPos = String.join(".", Arrays.asList(this.target, "*"));
		else this.targetPos = String.join(".", Arrays.asList(this.target, this.pos));
	}
	
	public List<String> getNeighbors(int windowSize) {
		String[] tokens = this.line.split("\t")[CONTEXT_TEXT_BEGIN_INDEX].split(" ");
		int startPos = -1;
		int endPos = -1;
		if (windowSize > 0) {
			startPos = Math.max(0, this.targetInd - windowSize);
			endPos = Math.min(this.targetInd + windowSize + 1, tokens.length);
		} else {
			startPos = 0;
			endPos = tokens.length;
		}
		List<String> neighbors = new ArrayList<>();
		for (int i = startPos; i < endPos; i++) {
			if (i == this.targetInd) continue;
			neighbors.add(tokens[i]);
		}
		return neighbors;
	}
	
	public String decorateContext() {
		String[] tokens = this.line.split("\t");
		String[] words = tokens[CONTEXT_TEXT_BEGIN_INDEX].split(" ");
		words[this.targetInd] = "__" + words[this.targetInd] + "__";
		tokens[CONTEXT_TEXT_BEGIN_INDEX] = String.join(" ", words);
		return String.join("\t", tokens) + "\n";
	}

	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}

	public int getTargetInd() {
		return targetInd;
	}
	public void setTargetInd(int targetInd) {
		this.targetInd = targetInd;
	}

	public String[] getWords() {
		return words;
	}
	public void setWords(String[] words) {
		this.words = words;
	}

	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

	public String getFullTargetKey() {
		return fullTargetKey;
	}
	public void setFullTargetKey(String fullTargetKey) {
		this.fullTargetKey = fullTargetKey;
	}

	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getTargetKey() {
		return targetKey;
	}
	public void setTargetKey(String targetKey) {
		this.targetKey = targetKey;
	}

	public String getTargetLemma() {
		return targetLemma;
	}
	public void setTargetLemma(String targetLemma) {
		this.targetLemma = targetLemma;
	}

	public int getTargetId() {
		return targetId;
	}
	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	public String getTargetPos() {
		return targetPos;
	}
	public void setTargetPos(String targetPos) {
		this.targetPos = targetPos;
	}
}
