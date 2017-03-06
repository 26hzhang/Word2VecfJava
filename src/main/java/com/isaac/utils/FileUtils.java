package com.isaac.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class FileUtils {

	private final static long ONE_GB = 1024 * 1024 * 1024;

	public static void saveEnsembleModel(String directory, EnsembleW2VfModel ensembleModel) {
		saveWord2VecfModel(directory + "/words", ensembleModel.wordModel);
		saveWord2VecfModel(directory + "/contexts", ensembleModel.contextModel);
	}

	public static EnsembleW2VfModel readEnsembleModelFromBinary(String directory) {
		String wordFilename = directory + "/words";
		Word2VecfModel wordModel = readWord2VecfModelFromBinary(wordFilename);
		String contextFilename = directory + "/contexts";
		Word2VecfModel contextModel = readWord2VecfModelFromBinary(contextFilename);
		return new EnsembleW2VfModel(wordModel, contextModel);
	}

	public static EnsembleW2VfModel readEnsembleModelFromText(String directory) {
		String wordFilename = directory + "/words";
		Word2VecfModel wordModel = readWord2VecfModelFromText(wordFilename);
		String contextFilename = directory + "/contexts";
		Word2VecfModel contextModel = readWord2VecfModelFromText(contextFilename);
		return new EnsembleW2VfModel(wordModel, contextModel);
	}

	public static void saveWord2VecfModel(String filename, Word2VecfModel model) {
		String vocabFilename = filename + ".vocab";
		saveVocabulary(vocabFilename, model.words);
		String vectorFilename = filename + ".matrix";
		saveVectors(vectorFilename, model.vectors);
	}

	public static Word2VecfModel readWord2VecfModelFromBinary(String filename) {
		String vocabFilename = filename + ".vocab";
		List<String> words = readVocabulary(vocabFilename);
		String vectorFilename = filename + ".matrix";
		float[][] matrix = readVectors(vectorFilename);
		int layerSize = matrix[0].length;
		return new Word2VecfModel(layerSize, words, matrix);
	}

	public static Word2VecfModel readWord2VecfModelFromText(String filename) {
		String[] words = null;
		float[][] matrix = null;
		int layerSize = 0;
		try {
			File file = new File(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = br.readLine();
			int vocabSize = Integer.parseInt(line.split(" ")[0]);
			layerSize = Integer.parseInt(line.split(" ")[1]);
			words = new String[vocabSize];
			matrix = new float[vocabSize][layerSize];
			int index = 0;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(" ");
				words[index] = values[0];
				float[] row = new float[layerSize];
				for (int i = 1; i < values.length; i++)
					row[i - 1] = Float.parseFloat(values[i]);
				matrix[index] = row;
				index++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Word2VecfModel(layerSize, Arrays.asList(words), matrix);
	}

	public static void convertTextToBinary(String filename) {
		List<Map.Entry<String, float[]>> list = new ArrayList<Map.Entry<String, float[]>>(readFromTextToMap(filename).entrySet());
		list = list.stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toList());
		List<String> words = new ArrayList<String>();
		List<float[]> matList = new ArrayList<float[]>();
		Iterator<Map.Entry<String, float[]>> it = list.iterator();
		while (it.hasNext()) {
			Map.Entry<String, float[]> entry = it.next();
			words.add(entry.getKey());
			matList.add(entry.getValue());
		}
		float[][] matrix = matList.toArray(new float[0][]);
		saveVocabulary(filename + ".vocab", words);
		saveVectors(filename + ".matrix", matrix);
	}

	private static Map<String, float[]> readFromTextToMap(String filename) {
		Map<String, float[]> map = new HashMap<String, float[]>();
		try {
			File file = new File(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = br.readLine();
			@SuppressWarnings("unused")
			int vocabSize = Integer.parseInt(line.split(" ")[0]);
			int layerSize = Integer.parseInt(line.split(" ")[1]);
			String[] values;
			while ((line = br.readLine()) != null) {
				values = line.split(" ");
				String word = values[0];
				float[] matrix = new float[layerSize];
				for (int i = 1; i < values.length; i++)
					matrix[i - 1] = Float.parseFloat(values[i]);
				map.put(word, matrix);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public static List<String> readVocabulary(String filename) {
		List<String> words = new ArrayList<String>();
		try {
			File file = new File(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = "";
			while ((line = br.readLine()) != null)
				words.add(line);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return words;
	}

	public static float[][] readVectors(String filename) {
		float[][] matrix = null;
		try {
			FileInputStream fis = new FileInputStream(new File(filename));
			FileChannel channel = fis.getChannel();
			MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, Math.min(channel.size(), Integer.MAX_VALUE));
			// load vocabSize and layerSize
			final float[] sizes = new float[2];
			final FloatBuffer sizeBuffer = buffer.asFloatBuffer();
			sizeBuffer.get(sizes);
			buffer.position(buffer.position() + 4 * 2);
			int vocabSize = (int) sizes[0];
			int layerSize = (int) sizes[1];
			// load float vectors from matrix file
			matrix = new float[vocabSize][layerSize];
			int bufferCount = 1;
			final float[] floats = new float[layerSize];
			for (int lineno = 0; lineno < vocabSize; lineno++) {
				final FloatBuffer floatBuffer = buffer.asFloatBuffer();
				floatBuffer.get(floats);
				for (int i = 0; i < floats.length; ++i) {
					matrix[lineno][i] = floats[i];
				}
				buffer.position(buffer.position() + 4 * layerSize);
				// Java's NIO only allows memory-mapping up to 2GB. To work around this problem, we re-map
				// every gigabyte. To calculate offsets correctly, we have to keep track how many gigabytes
				// we've already skipped. That's what this is for remapping file.
				if (buffer.position() > ONE_GB) {
					final int newPosition = (int) (buffer.position() - ONE_GB);
					final long size = Math.min(channel.size() - ONE_GB * bufferCount, Integer.MAX_VALUE);
					buffer = channel.map(FileChannel.MapMode.READ_ONLY, ONE_GB * bufferCount, size);
					buffer.position(newPosition);
					bufferCount += 1;
				}
			}
			channel.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return matrix;
	}

	public static void saveVocabulary(String filename, String[] words) {
		File wordFile = new File(filename + ".vocab");
		try {
			if (wordFile.exists())
				wordFile.delete();
			wordFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(wordFile)));
			// Write words to file
			for (int i = 0; i < words.length; i++) {
				bw.write(words[i] + "\n");
				bw.flush();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveVocabulary(String filename, List<String> words) {
		File wordFile = new File(filename + ".vocab");
		try {
			if (wordFile.exists())
				wordFile.delete();
			wordFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(wordFile)));
			// Write words to file
			for (int i = 0; i < words.size(); i++) {
				bw.write(words.get(i) + "\n");
				bw.flush();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveVectors(String filename, float[][] matrix) {
		int vocabSize = matrix.length;
		int layerSize = matrix[0].length;
		File matrixFile = new File(filename + ".matrix");
		if (matrixFile.exists())
			matrixFile.delete();
		try {
			matrixFile.createNewFile();
			// Write vocabSize and layerSize to file
			FileOutputStream fos = new FileOutputStream(matrixFile);
			ByteBuffer buffer = ByteBuffer.allocate(4 * 2);
			buffer.clear();
			buffer.putFloat(vocabSize).putFloat(layerSize);
			fos.write(buffer.array());
			fos.flush();
			// Write matrix to file
			buffer = ByteBuffer.allocate(4 * layerSize);
			for (int i = 0; i < vocabSize; i++) {
				buffer.clear();
				for (int j = 0; j < layerSize; j++) {
					buffer.putFloat(matrix[i][j]);
				}
				fos.write(buffer.array());
			}
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
