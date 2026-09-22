package com.aiassistant.knowledge.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TextChunkerService {

    private final EmbeddingModel embeddingModel;

    /*
     * Minimum amount of text we try to keep in a chunk.
     */
    private static final int MIN_CHUNK_SIZE = 300;

    /*
     * Maximum amount of text allowed in one chunk.
     *
     * We keep this reasonably large so related information
     * such as a complete project description stays together.
     */
    private static final int MAX_CHUNK_SIZE = 1200;

    /*
     * Semantic similarity threshold.
     *
     * Higher value = stricter grouping.
     * Lower value = more text grouped together.
     */
    private static final double SIMILARITY_THRESHOLD = 0.55;

    public TextChunkerService(
            EmbeddingModel embeddingModel
    ) {
        this.embeddingModel = embeddingModel;
    }

    public List<String> chunkText(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        /*
         * First divide the document using paragraphs.
         *
         * This is much better than blindly cutting every
         * 500 characters because paragraphs usually represent
         * meaningful pieces of information.
         */
        List<String> paragraphs = splitIntoParagraphs(text);

        if (paragraphs.isEmpty()) {
            return List.of();
        }

        List<String> chunks = new ArrayList<>();

        StringBuilder currentChunk =
                new StringBuilder();

        float[] previousEmbedding = null;

        for (String paragraph : paragraphs) {

            if (paragraph.isBlank()) {
                continue;
            }

            /*
             * If a single paragraph is extremely large,
             * divide it into smaller pieces.
             */
            if (paragraph.length() > MAX_CHUNK_SIZE) {

                if (!currentChunk.isEmpty()) {
                    chunks.add(
                            currentChunk
                                    .toString()
                                    .trim()
                    );

                    currentChunk.setLength(0);
                    previousEmbedding = null;
                }

                List<String> largeParagraphParts =
                        splitLargeParagraph(paragraph);

                chunks.addAll(
                        largeParagraphParts
                );

                continue;
            }

            /*
             * Generate embedding for the current paragraph.
             */
            float[] currentEmbedding =
                    embeddingModel.embed(paragraph);

            /*
             * First paragraph always starts
             * the first semantic chunk.
             */
            if (currentChunk.isEmpty()) {

                currentChunk
                        .append(paragraph);

                previousEmbedding =
                        currentEmbedding;

                continue;
            }

            /*
             * Calculate semantic similarity between
             * the previous paragraph and current paragraph.
             */
            double similarity =
                    cosineSimilarity(
                            previousEmbedding,
                            currentEmbedding
                    );

            int currentLength =
                    currentChunk.length();

            /*
             * Decide whether the paragraph belongs
             * to the current semantic chunk.
             */
            boolean semanticallyRelated =
                    similarity >= SIMILARITY_THRESHOLD;

            boolean canFit =
                    currentLength
                            + paragraph.length()
                            + 2
                            <= MAX_CHUNK_SIZE;

            /*
             * Keep related paragraphs together.
             */
            if (
                    semanticallyRelated
                            && canFit
            ) {

                currentChunk
                        .append("\n\n")
                        .append(paragraph);

            } else if (
                    currentLength
                            < MIN_CHUNK_SIZE
                            && canFit
            ) {

                /*
                 * If the current chunk is still too small,
                 * keep the paragraph with it.
                 *
                 * This prevents many tiny chunks.
                 */
                currentChunk
                        .append("\n\n")
                        .append(paragraph);

            } else {

                /*
                 * Semantic boundary detected.
                 *
                 * Finish the current chunk and
                 * start a new one.
                 */
                chunks.add(
                        currentChunk
                                .toString()
                                .trim()
                );

                currentChunk.setLength(0);

                currentChunk
                        .append(paragraph);
            }

            previousEmbedding =
                    currentEmbedding;
        }

        /*
         * Add the final chunk.
         */
        if (!currentChunk.isEmpty()) {

            chunks.add(
                    currentChunk
                            .toString()
                            .trim()
            );
        }

        return chunks;
    }


    /*
     * -------------------------------------------------------
     * PARAGRAPH SPLITTING
     * -------------------------------------------------------
     */

    private List<String> splitIntoParagraphs(
            String text
    ) {

        return Arrays.stream(
                        text.split("\\n\\s*\\n")
                )
                .map(String::trim)
                .filter(paragraph ->
                        !paragraph.isBlank()
                )
                .toList();
    }


    /*
     * -------------------------------------------------------
     * LARGE PARAGRAPH SPLITTING
     * -------------------------------------------------------
     */

    private List<String> splitLargeParagraph(
            String paragraph
    ) {

        List<String> parts =
                new ArrayList<>();

        int start = 0;

        while (start < paragraph.length()) {

            int end =
                    Math.min(
                            start + MAX_CHUNK_SIZE,
                            paragraph.length()
                    );

            /*
             * Try to end the chunk at a sentence
             * instead of cutting in the middle.
             */
            if (end < paragraph.length()) {

                int sentenceEnd =
                        findLastSentenceBoundary(
                                paragraph,
                                start,
                                end
                        );

                if (sentenceEnd > start) {
                    end = sentenceEnd;
                }
            }

            String part =
                    paragraph
                            .substring(start, end)
                            .trim();

            if (!part.isBlank()) {
                parts.add(part);
            }

            start = end;
        }

        return parts;
    }


    /*
     * Find a natural sentence boundary.
     */
    private int findLastSentenceBoundary(
            String text,
            int start,
            int end
    ) {

        for (int i = end - 1; i > start; i--) {

            char current =
                    text.charAt(i);

            if (
                    current == '.'
                            || current == '?'
                            || current == '!'
            ) {

                return i + 1;
            }
        }

        return -1;
    }


    /*
     * -------------------------------------------------------
     * COSINE SIMILARITY
     * -------------------------------------------------------
     */

    private double cosineSimilarity(
            float[] vectorA,
            float[] vectorB
    ) {

        if (
                vectorA == null
                        || vectorB == null
                        || vectorA.length != vectorB.length
        ) {
            return 0.0;
        }

        double dotProduct = 0.0;

        double magnitudeA = 0.0;

        double magnitudeB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {

            dotProduct +=
                    vectorA[i] * vectorB[i];

            magnitudeA +=
                    vectorA[i] * vectorA[i];

            magnitudeB +=
                    vectorB[i] * vectorB[i];
        }

        if (
                magnitudeA == 0
                        || magnitudeB == 0
        ) {
            return 0.0;
        }

        return dotProduct /
                (
                        Math.sqrt(magnitudeA)
                                * Math.sqrt(magnitudeB)
                );
    }
}