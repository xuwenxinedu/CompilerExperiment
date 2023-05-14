public class LexAnalyzer {
    static int tokenIndex = 0;
    
    /**
     * process a sentence
     * @param s the sentence
     * @param line used to record the line number
     * @param col start from which column
     */
    public static void processSentence(String s, int line, int col, List<TokenTable> tokenTable) {
        int originCol = col;
        if (col >= s.length()) {
            return;
        }
        char ch = s.charAt(col);
        if (isLetter(ch)) {
            while (isLetter(ch)) {
                if (col >= s.length() - 1) {
                    break;
                }
                ch = s.charAt(++col);
            }
            // 此时s[col] is not a char, s[col - 1] is a char
            String word = s.substring(originCol, col);
            if (isKeyword(word)) {
//                System.out.println("Keyword: " + word);
                TokenTable token = new TokenTable(++tokenIndex, word, TokenValue.KEYWORD);
                tokenTable.add(token);
                if ((word.equals("int") || word.equals("float")) && ch == ' ') {
                    // analyze the next word
                    int nextCol = nextWord(s, col + 1);
                    if (nextCol != -1) {
                        tokenTable.add(new TokenTable(++tokenIndex, s.substring(col + 1, nextCol), TokenValue.ID));
                        col = nextCol;
                    }
                }
            } else {
//                System.out.println("id: " + word);
//                now word is an id
                TokenTable token = new TokenTable(++tokenIndex, word, TokenValue.ID);
                tokenTable.add(token);
            }
            processSentence(s, line, col, tokenTable);
        } else if (isDigit(ch)) {
            while (isDigit(ch)) {
                if (col >= s.length() - 1) {
                    break;
                }
                ch = s.charAt(++col);
            }
            String word = s.substring(originCol, col);
            tokenTable.add(new TokenTable(++tokenIndex, word, TokenValue.DIGIT));
            processSentence(s, line, col, tokenTable);
        } else if (isSingleOP(ch)) {
            if (col + 1 < s.length()) {
                if (ch == '=' && s.charAt(col + 1) == '=') {
                    tokenTable.add(new TokenTable(++tokenIndex, "==", TokenValue.OP));
                } else if (ch == '!' && s.charAt(col + 1) == '=') {
                    tokenTable.add(new TokenTable(++tokenIndex, "!=", TokenValue.OP));
                } else if (ch == '>' && s.charAt(col + 1) == '=') {
                    tokenTable.add(new TokenTable(++tokenIndex, ">=", TokenValue.OP));
                } else if (ch == '<' && s.charAt(col + 1) == '=') {
                    tokenTable.add(new TokenTable(++tokenIndex, "<=", TokenValue.OP));
                } else {
                    tokenTable.add(new TokenTable(++tokenIndex, String.valueOf(ch), TokenValue.OP));
                    col -= 1;
                }
                col += 2;
            } else {
                tokenTable.add(new TokenTable(++tokenIndex, String.valueOf(ch), TokenValue.OP));
                col += 1;
            }
            processSentence(s, line, col, tokenTable);
        } else {
            if (ch == ' ') {
                while (ch == ' ') {
                    if (col >= s.length() - 1) {
                        break;
                    }
                    ch = s.charAt(++col);
                }
                if (ch != ' ') {
                    processSentence(s, line, col, tokenTable);
                }
            } else {
                System.out.println("Error: " + ch + " is not a valid character in line " + line + ", column " + col);
                col += 1;
                processSentence(s, line, col, tokenTable);
            }
        }
    }

    public static void lexAnalyze(String code, List<TokenTable> tokenTable, boolean save_token) {
        String[] s = code.split("\n");
        int line = 1;
//        List<TokenTable> tokenTable = new ArrayList<>();
        for (String part : s) {
            part += " ";
            processSentence(part, line, 0, tokenTable);
            line += 1;
        }
//        System.out.println("=============================");
//        System.out.println("Token Table:");
//        System.out.println(tokenTable);
        /**
         * token table save
         */

        if (save_token) {
            saveTokenTable(tokenTable);
        }

    }


}
