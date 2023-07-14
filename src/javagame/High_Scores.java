package javagame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class High_Scores {
    private final String highScoresFile = "highscores.txt";
    private final List<ScoreEntry> highScores;

    // Constructor
    public High_Scores() {
        highScores = new ArrayList<>();
        loadHighScores();
    }

    // Load high scores from the highscores file
    public void loadHighScores() {
        try (BufferedReader br = new BufferedReader(new FileReader(highScoresFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                // Check if the line has the expected format (playerName, score)
                if (parts.length >= 2) {
                    String playerName = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    highScores.add(new ScoreEntry(playerName, score));
                } else {
                    System.err.println("Error: Invalid line format in high scores file: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            saveHighScores();
        } catch (IOException e) {
            System.err.println("Error loading high scores from file: " + e.getMessage());
        }
    }


    // Add a new score to the high scores list and save it
    public void addScore(String playerName, int score) {
        if (score > 0) {
            ScoreEntry scoreEntry = new ScoreEntry(playerName, score);
            highScores.add(scoreEntry);
            saveHighScores();
        }
    }

    // Save high scores to the highscores file
    public void saveHighScores() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(highScoresFile))) {
            for (ScoreEntry entry : highScores) {
                bw.write(entry.getPlayerName() + "," + entry.getScore());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving high scores to file: " + e.getMessage());
        }
    }

    // ScoreEntry class for storing player name and score
    public static class ScoreEntry implements Comparable<ScoreEntry> {
        private final String playerName;
        private final int score;

        public ScoreEntry(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }

        // Compare ScoreEntry objects based on their scores
        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(this.score, other.score);
        }
    }
}



