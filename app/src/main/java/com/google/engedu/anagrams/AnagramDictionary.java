/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private ArrayList<String> wordList = new ArrayList<>();
    private HashSet<String> wordSet = new HashSet<>();
    private HashMap<String, ArrayList<String>> lettersToWord = new HashMap<>();
    private HashMap<Integer, ArrayList<String>> sizeToWords = new HashMap<>();
    private int wordLength = DEFAULT_WORD_LENGTH;

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);

            /* Use sortLetters on word and store the value on a new String so we don't need
            * to compute it several times */
            String modifiedWord = sortLetters(word);

            /* Check whether or not the modified word exists in the hashMap, if it does add
            * the value of word to the list, if it doesn't create a list and add it using
            * the modified word as key */
            if(lettersToWord.containsKey(modifiedWord)) {
                lettersToWord.get(modifiedWord).add(word);
            } else {
                ArrayList<String> tempList = new ArrayList<>();
                tempList.add(word);
                lettersToWord.put(modifiedWord, tempList);
            }

            /* Here we check the size of the word and store the size at a key in the hashmap, then
             * we store the word inside the arrayList mapped to that key */
            if(sizeToWords.containsKey(word.length())) {
                sizeToWords.get(word.length()).add(word);
            } else {
                ArrayList<String> tempList = new ArrayList<>();
                tempList.add(word);
                sizeToWords.put(word.length(), tempList);
            }
        }
    }

    public boolean isGoodWord(String word, String base) {
        /* This function will check first if the word is in the wordSet if it isn't then it returns
        * false, if it is then it checks for base existing as a substring in word by working as a
        * moving window */
        if (wordSet.contains(word)) {
            for (int i = 0; i + base.length() <= word.length(); i++) {
                if (word.substring(i, i + base.length()).equals(base)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>();
        targetWord = sortLetters(targetWord);
        for(String arrayListWord: wordList) {
            //check whether the length is the same first, if it isn't then it can't be an anagram
            if(targetWord.length() == arrayListWord.length()) {
                /* check the sorted version of all the words, if any matches the target word then we
                * add it to the list */
                if(targetWord.equals(sortLetters(arrayListWord))) {
                    result.add(arrayListWord);
                }
            }
        }
        return result;
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        //create an array of alphabet letters so we can append them easily to the word
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] secondAlphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (char alphabetChar: alphabet) {
            for (char secondChar: secondAlphabet) {
                String modifiedWord = sortLetters(word + alphabetChar + secondChar);
                if (lettersToWord.containsKey(modifiedWord)) {
                    /* The reason we add this for loop that checks if the anagrams we find are valid
                    * words is because otherwise the list of anagrams shown at the end will show
                    * invalid words that contain the base as a substring, despite the fact that it
                    * wouldn't be accepted as input, I'm not sure if this was the intended behavior
                    * but it didn't sit right with me so I decided to change it*/
                    for (String anagramElement: lettersToWord.get(modifiedWord)) {
                        if (isGoodWord(anagramElement, word)) {
                            result.add(anagramElement);
                        }
                    }
                }
            }
        }
        return result;
    }

    public String pickGoodStarterWord() {
        /* Get a random word from the wordList we use wordLength so that we only get words from our
        * set length and we modify random so that it only searches in ranges up to the existing
        * numbers in the list */
        String word = sizeToWords.get(wordLength).get(new Random().nextInt(sizeToWords.get(wordLength).size()));

        /* Use the getAnagramsWithOneMoreLetter function to check if at least 5 anagrams exist for
        the word */
        while (getAnagramsWithOneMoreLetter(word).size() < MIN_NUM_ANAGRAMS) {
            word = sizeToWords.get(wordLength).get(new Random().nextInt(sizeToWords.get(wordLength).size()));
        }

        //If our wordlength is less than the max then we increase it by one
        if (wordLength < MAX_WORD_LENGTH) {
            wordLength++;
        }

        return word;
    }

    public String sortLetters(String word) {
        /* Transform the string into an array of chars and then sort it, return the string of sorted
        * chars */
        char[] chars = word.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}
