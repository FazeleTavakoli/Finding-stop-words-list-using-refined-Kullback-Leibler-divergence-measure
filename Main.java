package edu.stanford.nlp.com.company;

import java.io.*;
import java.util.*;



public class TFIDFMain {

    public static void main(String args[]) throws InterruptedException {

        long startTime0 = System.nanoTime();
        System.out.print("time 0 in nanoseconds = ");
        System.out.println(startTime0);

        String splittedArray[];
        Spliter spliter = new Spliter();
        String tokenizedTerm = "";
        String stemmedTerm = "";
        ArrayList finalTermType = new ArrayList();
        List<String> txtfilePaths = new ArrayList<>();
        List<Map<String, Integer>> termFreqHashList = new ArrayList<>();
        List<Integer> freqHashIndex = new ArrayList<>(); //This list contains the index of termFreqHashes that have a specific random term.
        List<String> allWords = new ArrayList<>();
        List<String> documentTokPhrases = new ArrayList<>();
        List<Map<String, Double>> partialStopWordsList = new ArrayList<>(); // this list contains all of partialStopWordHashes
        List<String> finalStopWords = new ArrayList<>();
        Map<String, Integer> termFreqHash = new HashMap<>();
        Map<String, Double> termWeightHash = new HashMap<>();
        LinkedHashMap<String, Double> sortedTermWeightHash = new LinkedHashMap<>();
        LinkedHashMap<String, Double> partialStopWordHash = new LinkedHashMap<>();
        Map<String, Double> totalStopWordHash = new HashMap<>(); //this hashMap contains all terms of partialStopWordHashes
        Map.Entry<String, Double> me;
        Map<String, List<Double>> repeatedTermHash = new HashMap<>();
        String serializedFilePath = "C:\\Irsa\\mainInput\\Input\\TFIDF\\serializedWrittenFiles\\";
        TermFrequencyDetector tfd = new TermFrequencyDetector();
        Double termWeight = 0.0;

        File txtFolder = new File("C:\\Irsa\\mainInput\\Input\\TFIDF\\news_input\\Files");
        txtfilePaths = new ArrayList<>(TermFrequencyDetector.getFiles(txtFolder));
        TermFrequencyDetector.fileCleaner();
        for (int k = 0; k < txtfilePaths.size(); k++) {
            String fileName = "file" + k + ".ser";
            tfd.writeInTFIDFFile(txtfilePaths.get(k));
            termFreqHash = new HashMap<String, Integer>();
            documentTokPhrases = new ArrayList<String>();
            try {
                String line = "";
                BufferedReader br = null;
                InputStream inputStream = new FileInputStream(txtfilePaths.get(k));
                br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                while ((line = br.readLine()) != null) {
                    line = line.replaceAll("[\uFEFF]", "");
                    line = line.replaceAll("[&nbsp;]+", "");
                    line = line.trim();
                    splittedArray = spliter.lineSplitter(line);
                    int v = splittedArray.length;
                    for (int i = 0; i < v; i++) {
                        if (Corpus.pJustU200b.matcher(splittedArray[i]).find() || Corpus.pJustU200c.matcher(splittedArray[i]).find() ||
                                Corpus.pJustU200f.matcher(splittedArray[i]).find() || Corpus.pUfffd.matcher(splittedArray[i]).find() ||
                                Corpus.pU200a.matcher(splittedArray[i]).find()) {
                            splittedArray[i] = Corpus.pJustU200b.matcher(splittedArray[i]).replaceAll(" ");
                            splittedArray[i] = Corpus.pJustU200c.matcher(splittedArray[i]).replaceAll(" ");
                            splittedArray[i] = Corpus.pJustU200f.matcher(splittedArray[i]).replaceAll(" ");
                            splittedArray[i] = Corpus.pUfffd.matcher(splittedArray[i]).replaceAll(" ");
                            splittedArray[i] = Corpus.pU200a.matcher(splittedArray[i]).replaceAll(" ");
                        }
                        splittedArray[i] = Corpus.pSeveralSpace.matcher(splittedArray[i]).replaceAll(" ");
                        splittedArray[i] = splittedArray[i].trim();
                        String sentence = splittedArray[i];
                        if (!Corpus.pSingleSpace.matcher(sentence).find() && !Corpus.pEmpty.matcher(sentence).find()) {
                            TextTokenizer textTokenizer = new TextTokenizer();
                            textTokenizer.innerMain(sentence);

                            if (textTokenizer.gettokenizedPhraseLength() != 0) {
                                //storing stems of terms in a hash
                                TextStemmer textStemmer = new TextStemmer();
                                finalTermType = textTokenizer.getFinalTermType();
                                stemmedTerm = textStemmer.runStemmer(finalTermType);
                                while (!stemmedTerm.equals("")) {
                                    tfd.modifyTermFreqHash(stemmedTerm, termFreqHash);
                                    if (!allWords.contains(stemmedTerm)) {
                                        allWords.add(stemmedTerm);
                                    }
                                    stemmedTerm = textStemmer.runStemmer(finalTermType);
                                }
                                textStemmer = null;

                            }
                        }
                    }

                }

                inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (termFreqHash.size() != 0)
                TermFrequencyDetector.writeSeveralFiles(termFreqHash, serializedFilePath + fileName);
        }

        String allSerFolderPath = "C:\\Irsa\\mainInput\\Input\\TFIDF\\serializedWrittenFiles";
        File allSerFolder = new File(allSerFolderPath);
        int folderSize = allSerFolder.listFiles().length;
        for (int i = 0; i < folderSize; i++) {
            Map<String, Integer> serHash = new HashMap<>();
            String serFilePath = allSerFolderPath + "\\" + "file" + i + ".ser";
            File f = new File(serFilePath);
            if (f.exists() && !f.isDirectory()) {
                serHash = TermFrequencyDetector.readSerialize(serFilePath);
                if (!termFreqHashList.contains(serHash))
                    termFreqHashList.add(serHash);
                for (String item : serHash.keySet())
                    if (!allWords.contains(item))
                        allWords.add(item);
            }
            //}
        }

        long endTime0 = System.nanoTime();
        System.out.print("end Time in nanoseconds = ");
        System.out.println(endTime0);
        long duration4 = (startTime0 - endTime0);
        System.out.print("duration time in nanoseconds = ");
        System.out.println(duration4);


        for (int y = 0; y < 1000; y++) {
            System.out.println(y);
            String randomWord = "";
            double normalizedTermWeight = 0;
            partialStopWordHash = new LinkedHashMap<>();
            freqHashIndex = new ArrayList<>();

            randomWord = allWords.get((int) (Math.random() * allWords.size()));
            for (Map<String, Integer> item : termFreqHashList) {
                if (item.containsKey(randomWord)) {
                    freqHashIndex.add(termFreqHashList.indexOf(item));
                }
            }
            termWeightHash = new HashMap<>(tfd.computKLMeasure(termFreqHashList, freqHashIndex));
            double maxTermWeight = tfd.findMaxTermWeight(termWeightHash);
            for (String term : termWeightHash.keySet()) {
                normalizedTermWeight = termWeightHash.get(term) / maxTermWeight;
                termWeightHash.replace(term, normalizedTermWeight);
            }


            sortedTermWeightHash = new LinkedHashMap<>(tfd.sortByValues(termWeightHash));

            //Extract the top X top-ranked (i.e. least weighted)
            int X = 200;
            Set<Map.Entry<String, Double>> set = sortedTermWeightHash.entrySet();
            Iterator<Map.Entry<String, Double>> iterator = set.iterator();
            int counter = 0;
            while (iterator.hasNext()) {
                if (counter < X) {
                    me = iterator.next();
                    partialStopWordHash.put(me.getKey(), me.getValue());
                    counter += 1;
                } else
                    break;
            }
            partialStopWordsList.add(partialStopWordHash);
        }

        List<Double> repeatedTermWeightList = new ArrayList<Double>();
        for (int i = 0; i < partialStopWordsList.size(); i++) {
            LinkedHashMap<String, Double> lHash = new LinkedHashMap<>(partialStopWordsList.get(i));
            for (String term : lHash.keySet()) {
                if (!totalStopWordHash.containsKey(term))
                    totalStopWordHash.put(term, lHash.get(term));
                else {
                    if (repeatedTermHash.containsKey(term)) {
                        repeatedTermWeightList = new ArrayList<Double>(repeatedTermHash.get(term));
                        repeatedTermWeightList.add(lHash.get(term));
                        repeatedTermHash.replace(term, repeatedTermWeightList);
                    } else {
                        repeatedTermWeightList.add(lHash.get(term));
                        repeatedTermHash.put(term, repeatedTermWeightList);
                    }
                }
            }
        }

        Double totalWeight = 0.0;
        for (String term : repeatedTermHash.keySet()) {
            totalWeight = totalStopWordHash.get(term);
            for (Double w : repeatedTermHash.get(term)) {
                totalWeight += w;
            }
            Double averageWeight = totalWeight / (repeatedTermHash.get(term).size() + 1);
            totalStopWordHash.replace(term, averageWeight);
        }
        sortedTermWeightHash = new LinkedHashMap<>(tfd.sortByValues(totalStopWordHash));


        //Extract the L top-ranked terms as stopword list for the collection.
        int L = 400; //final number of sto
        // p words
        int counter = 0;
        for (String term : sortedTermWeightHash.keySet()) {
            if (counter < L) {
                finalStopWords.add(term);
                counter += 1;
            } else
                break;
        }

        String stopWordsFilePath = "C:\\Irsa\\mainInput\\Input\\TFIDF\\stopWords.txt";
        List<String> refernceStopWordList = new ArrayList<>(TermFrequencyDetector.addToList(stopWordsFilePath));
        double precision = TermFrequencyDetector.precisionDeterminer(finalStopWords, refernceStopWordList);
    }
}
