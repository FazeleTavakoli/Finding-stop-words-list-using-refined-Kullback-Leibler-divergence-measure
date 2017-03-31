package edu.stanford.nlp.com.company;

import java.io.*;
import java.util.*;


public class TermFrequencyDetector {


    private static int numberOfCorpusTerms = 0;
    private static int numberOfSampledDOcTerms = 0;
    private static File file = new File("C:\\Irsa\\mainInput\\Input\\TFIDF\\TFIDFOutput.txt");
    private static Map<String, Integer> serializeHash = new HashMap<>();

    public double TF(List<Map<String, Integer>> corpus, Map<String, Integer> document, String term) {
        int corpusLength = countCorpusTerms(corpus);
        double termFreq = document.get(term);
        double termFreqProportion = termFreq / corpusLength;
        return termFreqProportion;
    }

    public double IDF(List<Map<String, Integer>> corpus, String term) {
        int DocFreq = 0;
        int inverseDocFreqProportion = 0;
        for (Map<String, Integer> doc : corpus) {
            if (doc.containsKey(term)) {
                DocFreq += 1;
            }
        }
        inverseDocFreqProportion = corpus.size() / DocFreq;
        return Math.log(inverseDocFreqProportion);
    }


    public double TFIDF(List<Map<String, Integer>> corpus, Map<String, Integer> document, String term) {

        double tfIdf = 0;
        tfIdf = TF(corpus, document, term) * IDF(corpus, term);
        return tfIdf;

    }

    public void modifyTermFreqHash(String inputPhrase, Map<String, Integer> inputHash) {
        Integer counter = 0;
        if (inputHash.containsKey(inputPhrase)) {
            counter = inputHash.get(inputPhrase);
            counter += 1;
            inputHash.replace(inputPhrase, counter);
        } else {
            counter += 1;
            inputHash.put(inputPhrase, counter);
        }
    }

    public static void writeInTFIDFFile(String fileInput) {
        BufferedWriter bw = null;
        try {
            FileWriter fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            bw.write(fileInput);
            bw.write("\r\n");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
            } catch (Exception ex) {
                System.out.println("Error in closing the BufferedWriter" + ex);
            }

        }
    }


    public static int countCorpusTerms(List<Map<String, Integer>> corpus) {
        if (numberOfCorpusTerms == 0) {
            for (Map<String, Integer> doc : corpus) {
                numberOfCorpusTerms += doc.size();
            }
        }
        return numberOfCorpusTerms;
    }

    public int countSampledDocTerms(List<Map<String, Integer>> corpus, List<Integer> hashIndex) {
        if (numberOfSampledDOcTerms == 0) {
            for (int index : hashIndex) {
                numberOfSampledDOcTerms += corpus.get(index).size();
            }
        }
        return numberOfSampledDOcTerms;
    }

    public Map<String, Double> computKLMeasure(List<Map<String, Integer>> corpus, List<Integer> hashIndex) {
        double weight = 0;
        double sampledDocLength = countSampledDocTerms(corpus, hashIndex);
        double corpusLength = countCorpusTerms(corpus);
        double corpusTermFerq = 0;
        double sampledDocTermFreq = 0; //frequency of a term in the sampled document
        double Px = 0;
        double Pc = 0;
        Map<String, Integer> termHash = new HashMap<String, Integer>();
        Map<String, Double> sampledDoctermWeight = new HashMap<String, Double>();

        //computing sampledDocTermFreq
        for (int i = 0; i < hashIndex.size(); i++) {
            termHash = new HashMap<>(corpus.get(hashIndex.get(i)));
            for (String term : termHash.keySet()) {
                weight = 0;
                corpusTermFerq = 0;
                sampledDocTermFreq = 0;
                if (!sampledDoctermWeight.containsKey(term)) {
                    sampledDocTermFreq = termHash.get(term);
                    for (int j = i + 1; j < hashIndex.size(); j++) {
                        if (corpus.get(hashIndex.get(j)).containsKey(term)) {
                            sampledDocTermFreq += corpus.get(hashIndex.get(j)).get(term);
                        }
                    }

                    //computing corpusTermFerq
                    for (Map<String, Integer> doc : corpus) {
                        if (doc.containsKey(term)) {
                            corpusTermFerq += doc.get(term);
                        }
                    }

                    Px = sampledDocTermFreq / sampledDocLength;
                    Pc = corpusTermFerq / corpusLength;
                    weight = Px * log((Px / Pc), 2);
                    sampledDoctermWeight.put(term, weight);
                }
            }
        }
        return sampledDoctermWeight;
    }

    public static double log(double x, int base) {
        return (Math.log(x) / Math.log(base));
    }

    public double findMaxTermWeight(Map<String, Double> inputMap) {
        double maxWeight = Collections.max(inputMap.values());
        return maxWeight;
    }

    public HashMap sortByValues(Map<String, ? extends Object> map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }



    public static Map<String,Integer> readSerialize(String fileName) {
        Map<String,Integer> serializeHash = new HashMap<>();
        List<Map<String, Integer>> serializeHashList = new ArrayList<>();
            try {
                FileInputStream fileIn = new FileInputStream(fileName);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                serializeHash = (Map) in.readObject();
                in.close();
                fileIn.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ClassNotFoundException c) {
                System.out.println("hashes not found");
                c.printStackTrace();
                return null;
            }
        return serializeHash;
    }

    public static void writeSeveralFiles(Map<String, Integer> termFreqHash,String fileName) {
        BufferedWriter bw = null;
            try {
                FileOutputStream fos = new FileOutputStream(fileName);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(termFreqHash);
                oos.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static List<String> getFiles(File f) {
        List <String> filePaths = new ArrayList<>();
        File files[];
        files = f.listFiles();
        for (File file : files ){
            filePaths.add(file.getAbsolutePath());
        }
        return filePaths;


    }

    public static void fileCleaner(){
        // empty the current content
        try {
            FileWriter fw = new FileWriter(file);
            fw.write("");
            fw.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public static double precisionDeterminer(List<String> obtainedList, List<String> referenceList ){
        double precision = 0;
        double numberOfMatchedElements = 0;
        double numberOfTotalElements = 0;
        for (String term: obtainedList){
            if (referenceList.contains(term)){
                numberOfMatchedElements += 1;
            }
        }
        numberOfTotalElements = (obtainedList.size()+referenceList.size()) - numberOfMatchedElements;
        precision = numberOfMatchedElements / numberOfTotalElements;
        return precision;
    }

    public static List addToList(String filePath) {
        List<String> list = new ArrayList<>();
        try {
            String line = "";
            BufferedReader br = null;
            InputStream inputStream = new FileInputStream(filePath);
            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null)
                list.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }



}
