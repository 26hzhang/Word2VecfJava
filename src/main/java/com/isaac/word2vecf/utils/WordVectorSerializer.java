package com.isaac.word2vecf.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Bytes;
import com.isaac.word2vecf.Word2Vec;
import com.isaac.word2vecf.Word2Vecf;
import javafx.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanghao on 5/6/17.
 * Modified by zhanghao on 10/10/17
 * @author  ZHANG HAO
 * email: isaac.changhau@gmail.com
 */
public class WordVectorSerializer {
	/** For file process usage */
	private final static long ONE_GB = 1024 * 1024 * 1024;
	private final static byte LINE_SEPARATOR = 10;
	private final static byte COLUMN_SEPARATOR = 32;
	private final static ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

	/** @return {@link Word2Vec} */
	public static Word2Vec loadWord2VecModel (String wordFilePath, boolean binary) {
		Word2Vec model = null;
		try {
			Pair<List<String>, INDArray> pair;
			if (binary) pair = fromBinary(wordFilePath);
			else pair = fromText(wordFilePath);
			model = new Word2Vec(pair.getValue().columns(), pair.getKey(), pair.getValue(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}

	/** @return {@link Word2Vecf} */
	public static Word2Vecf loadWord2VecfModel (String wordFilePath, String contextFilePath, boolean binary) {
		Word2Vecf model = null;
		try {
			Pair<List<String>, INDArray> wordPair;
			Pair<List<String>, INDArray> contextPair;
			if (binary) {
				wordPair = fromBinary(wordFilePath);
				contextPair = fromBinary(contextFilePath);
			} else {
				wordPair = fromText(wordFilePath);
				contextPair = fromText(contextFilePath);
			}
			model = new Word2Vecf(wordPair.getValue().columns(), wordPair.getKey(), wordPair.getValue(), contextPair.getKey(), contextPair.getValue(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}

	private static Pair<List<String>, INDArray> fromText(String wordFilePath) throws IOException {
		BufferedReader reader = new BufferedReader(Common.asReaderUTF8Lenient(new FileInputStream(new File(wordFilePath))));
		String fstLine = reader.readLine();
		int vocabSize = Integer.parseInt(fstLine.split(" ")[0]);
		int layerSize = Integer.parseInt(fstLine.split(" ")[1]);
		List<String> wordVocab = Lists.newArrayList();
		INDArray wordVectors = Nd4j.create(vocabSize, layerSize);
		int n = 1;
		String line;
		while ((line = reader.readLine()) != null) {
			String[] values = line.split(" ");
			wordVocab.add(values[0]);
			Preconditions.checkArgument(layerSize == values.length - 1, "For file '%s', on line %s, layer size is %s, but found %s values in the word vector",
					wordFilePath, n, layerSize, values.length - 1); // Sanity check
			for (int d = 1; d < values.length; d++) wordVectors.putScalar(n - 1, d - 1, Float.parseFloat(values[d]));
			n++;
		}
		return new Pair<>(wordVocab, wordVectors);
	}

	private static Pair<List<String>, INDArray> fromBinary(String filename) throws IOException {
		if (filename == null || filename.isEmpty())
			return null;
		try (final FileInputStream fis = new FileInputStream((new File(filename))); final FileChannel channel = fis.getChannel()) {
			final Charset cs = StandardCharsets.UTF_8;
			MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, Math.min(channel.size(), Integer.MAX_VALUE));
			buffer.order(byteOrder);
			int bufferCount = 1;
			// Java's NIO only allows memory-mapping up to 2GB. To work around this problem, we re-map every gigabyte.
			// To calculate offsets correctly, we have to keep track how many gigabytes we've already skipped. That's
			// what this is for.
			List<Byte> list = new ArrayList<>(); // read the first line
			byte c = buffer.get();
			while (c != LINE_SEPARATOR) {
				list.add(c);
				c = buffer.get();
			}
			String firstLine = new String(Bytes.toArray(list), cs);
			int index = firstLine.indexOf(COLUMN_SEPARATOR);
			Preconditions.checkState(index != -1, "Expected a space in the first line of file '%s': '%s'", filename, firstLine);
			final int vocabSize = Integer.parseInt(firstLine.substring(0, index));
			final int layerSize = Integer.parseInt(firstLine.substring(index + 1));
			List<String> vocab = new ArrayList<>(vocabSize);
			INDArray vectors = Nd4j.create(vocabSize, layerSize);
			final float[] floats = new float[layerSize];
			for (int lineno = 0; lineno < vocabSize; lineno++) {
				// read to vocab
				list.clear();
				c = buffer.get();
				while (c != COLUMN_SEPARATOR) { // ignore newlines in front of words (some binary files have newline, some don't)
					if (c != LINE_SEPARATOR) list.add(c);
					c = buffer.get();
				}
				vocab.add(new String(Bytes.toArray(list), cs));
				// read to vectors
				final FloatBuffer floatBuffer = buffer.asFloatBuffer();
				floatBuffer.get(floats);
				for (int i = 0; i < floats.length; ++i) vectors.putScalar(lineno, i, floats[i]);
				buffer.position(buffer.position() + 4 * layerSize);
				if (buffer.position() > ONE_GB) { // remap file
					final int newPosition = (int) (buffer.position() - ONE_GB);
					final long size = Math.min(channel.size() - ONE_GB * bufferCount, Integer.MAX_VALUE);
					buffer = channel.map(FileChannel.MapMode.READ_ONLY, ONE_GB * bufferCount, size);
					buffer.order(byteOrder);
					buffer.position(newPosition);
					bufferCount += 1;
				}
			}
			return new Pair<>(vocab, vectors);
		}
	}

	/** Save the word2vec model as binary file */
	@SuppressWarnings("unused")
	public static void saveWord2VecToBinary(String toPath, Word2Vec w2v){
		final Charset cs = StandardCharsets.UTF_8;
		try {
			final OutputStream os = new FileOutputStream(new File(toPath));
			final String header = String.format("%d %d\n", w2v.wordVocabSize(), w2v.getLayerSize());
			os.write(header.getBytes(cs));
			final ByteBuffer buffer = ByteBuffer.allocate(4 * w2v.getLayerSize());
			buffer.order(byteOrder);
			for (int i = 0; i < w2v.wordVocabSize(); ++i) {
				os.write(String.format("%s ", w2v.getWordVocab().get(i)).getBytes(cs)); // Write one word in byte format, add a space.
				buffer.clear();
				for (int j = 0; j < w2v.getLayerSize(); ++j) {
					buffer.putFloat(w2v.getWordVectors().getFloat(i, j));
				}
				os.write(buffer.array()); // Write all float values of one vector in byte format.
				os.write('\n'); // Add a newline.
			}
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Save the word2vecf model as binary file */
	public static void saveWord2VecfToBinary (String wordToPath, String contextToPath, Word2Vecf w2vf) {
		final Charset cs = StandardCharsets.UTF_8;
		try {
			final OutputStream wos = new FileOutputStream(new File(wordToPath));
			final String wheader = String.format("%d %d\n", w2vf.wordVocabSize(), w2vf.getLayerSize());
			wos.write(wheader.getBytes(cs));
			final ByteBuffer wbuffer = ByteBuffer.allocate(4 * w2vf.getLayerSize());
			wbuffer.order(byteOrder);
			for (int i = 0; i < w2vf.wordVocabSize(); ++i) {
				wos.write(String.format("%s ", w2vf.getWordVocab().get(i)).getBytes(cs));
				wbuffer.clear();
				for (int j = 0; j < w2vf.getLayerSize(); ++j) wbuffer.putFloat(w2vf.getWordVectors().getFloat(i, j));
				wos.write(wbuffer.array());
				wos.write('\n');
			}
			wos.flush();
			wos.close();
			final OutputStream cos = new FileOutputStream(new File(contextToPath));
			final String cheader = String.format("%d %d\n", w2vf.contextVocabSize(), w2vf.getLayerSize());
			cos.write(cheader.getBytes(cs));
			final ByteBuffer cbuffer = ByteBuffer.allocate(4 * w2vf.getLayerSize());
			cbuffer.order(byteOrder);
			for (int i = 0; i < w2vf.contextVocabSize(); ++i) {
				cos.write(String.format("%s ", w2vf.getContextVocab().get(i)).getBytes(cs));
				cbuffer.clear();
				for (int j = 0; j < w2vf.getLayerSize(); ++j) cbuffer.putFloat(w2vf.getContextVectors().getFloat(i, j));
				cos.write(cbuffer.array());
				cos.write('\n');
			}
			cos.flush();
			cos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
