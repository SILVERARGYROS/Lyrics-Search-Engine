/* 
 * This is just the first attempt, to see the final result plz scroll further 
 * down. Or don't... No being capable of basic human comprehension is capable 
 * of understanding this monstrosity. God had no hand in the implementation of 
 * tf-idf cosine similarity. The fact that this Similarity method exists even 
 * on paper proves that God is either impotent to alter His universe or ignorant 
 * to the horrors taking place in His kingdom. The tf-idf cosine similarity is 
 * more than a mathematical algorithm. It is the declaration of humankind's 
 * contempt for the natural order. It is hubris manifest.
 */

// package com.example.Lucene;

// import org.apache.lucene.index.FieldInvertState;
// import org.apache.lucene.index.IndexOptions;
// import org.apache.lucene.search.similarities.TFIDFSimilarity;

// public class CTFIDFSimilarity extends TFIDFSimilarity {

//     public float tf(float freq) {
//         if (freq > 0) {
//             return 1 + (float) Math.log(freq);
//         } else {
//             return 0;
//         }
//     }

//     public float idf(long docFreq, long numDocs) {
//         return (float)Math.log(numDocs/(float)docFreq);
//     }

//     // normalization factor so that queries can be compared
//     public float queryNorm(float sumOfSquaredWeights) {
//         return (float)Math.sqrt(sumOfSquaredWeights);
//     }

//     // number of terms in the query that were found in the document
//     public float coord(int overlap, int maxOverlap) {
//         return overlap / (float) maxOverlap;
//     }

//     public float computeNorm(String fieldName, FieldInvertState state) {
//         final int numTerms;
//         if (state.getIndexOptions() == IndexOptions.DOCS && state.getIndexCreatedVersionMajor() >= 8) {
//             numTerms = state.getUniqueTermCount();
//         } else if (discountOverlaps) {
//             numTerms = state.getLength() - state.getNumOverlap();
//         } else {
//             numTerms = state.getLength();
//         }

//         // Compute the Euclidean length (L2 norm) of the vector
//         float euclideanNorm = (float) Math.sqrt(numTerms);

//         return euclideanNorm;
//     }

//     public float lengthNorm(int length){
//         return (float) (1.0 / Math.sqrt(length));
//     }
// }

package com.example.Lucene;


import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.SmallFloat;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;

public class CTFIDFSimilarity extends Similarity {
    protected boolean discountOverlaps = true;

    public float coord(int overlap, int maxOverlap) {
        // Override if needed
        return overlap / (float) maxOverlap;
    }

    public float tf(float freq) {
        return 1 + (float)Math.log(freq);
    }

    public float idf(long docFreq, long numDocs) {
        return (float)Math.log(numDocs/docFreq);
    }

    // normalization factor so that queries can be compared
    public float queryNorm(float sumOfSquaredWeights) {
        return (float)Math.sqrt(sumOfSquaredWeights);
    }

    public final long computeNorm(FieldInvertState state) {
        final int numTerms;
        if (state.getIndexOptions() == IndexOptions.DOCS && state.getIndexCreatedVersionMajor() >= 8) {
        numTerms = state.getUniqueTermCount();
        } else if (discountOverlaps) {
        numTerms = state.getLength() - state.getNumOverlap();
        } else {
        numTerms = state.getLength();
        }
        return SmallFloat.intToByte4(numTerms);
    }
    
    public float lengthNorm(int state) {
        // Override if needed
        return state;
    }

    public Explanation idfExplain(CollectionStatistics collectionStats, TermStatistics termStats) {
        final long df = termStats.docFreq();
        final long docCount = collectionStats.docCount();
        final float idf = idf(df, docCount);
        return Explanation.match(idf, "idf(docFreq, docCount)", 
            Explanation.match(df, "docFreq, number of documents containing term"),
            Explanation.match(docCount, "docCount, total number of documents with field"));
    }

    public Explanation idfExplain(CollectionStatistics collectionStats, TermStatistics termStats[]) {
        double idf = 0d; // sum into a double before casting into a float
        List<Explanation> subs = new ArrayList<>();
        for (final TermStatistics stat : termStats ) {
          Explanation idfExplain = idfExplain(collectionStats, stat);
          subs.add(idfExplain);
          idf += idfExplain.getValue().floatValue();
        }
        return Explanation.match((float) idf, "idf(), sum of:", subs);
    }

    public final SimScorer scorer(float boost, CollectionStatistics collectionStats, TermStatistics... termStats) {
        final Explanation idf = termStats.length == 1
        ? idfExplain(collectionStats, termStats[0])
        : idfExplain(collectionStats, termStats);
        float[] normTable = new float[256];
        for (int i = 1; i < 256; ++i) {
        int length = SmallFloat.byte4ToInt((byte) i);
        float norm = lengthNorm(length);
        normTable[i] = norm;
        }
        normTable[0] = 1f / normTable[255];
        return new CTFIDFScorer(boost, idf, normTable);
    }


    public class CTFIDFScorer extends SimScorer {
        /** The idf and its explanation */
        private final Explanation idf;
        private final float boost;
        private final float queryWeight;
        final float[] normTable;
        
        public CTFIDFScorer(float boost, Explanation idf, float[] normTable) {
            // TODO: Validate?
            this.idf = idf;
            this.boost = boost;
            this.queryWeight = boost * idf.getValue().floatValue();
            this.normTable = normTable;
        }

        @Override
        public float score(float freq, long norm) {
            final float raw = tf(freq) * queryWeight; // compute tf(f)*weight
            float normValue = normTable[(int) (norm & 0xFF)];
        
            // Compute the cosine similarity by dividing the dot product of query and document vectors
            float score = (raw * normValue / (idf.getValue().floatValue() * normValue)) / 2;
            // float score = raw / (idf.getValue().floatValue() * normValue);

            return score;
        }

        @Override
        public Explanation explain(Explanation freq, long norm) {
            return explainScore(freq, norm, normTable);
        }

        private Explanation explainScore(Explanation freq, long encodedNorm, float[] normTable) {
            List<Explanation> subs = new ArrayList<Explanation>();
            if (boost != 1F) {
                subs.add(Explanation.match(boost, "boost"));
            }
            subs.add(idf);
            Explanation tf = Explanation.match(tf(freq.getValue().floatValue()), "tf(freq="+freq.getValue()+"), with freq of:", freq);
            subs.add(tf);

            float norm = normTable[(int) (encodedNorm & 0xFF)];
            
            Explanation fieldNorm = Explanation.match(norm, "fieldNorm");
            subs.add(fieldNorm);
            
            return Explanation.match(
                queryWeight * tf.getValue().floatValue() * norm,
                "score(freq="+freq.getValue()+"), product of:",
                subs);
        }
    }
}