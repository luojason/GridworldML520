package project2.utility;

public enum Sentiment {
    Free     (1), // calls constructor with value 1
    Unsure   (0), // calls constructor with value 0
    Blocked (-1); // calls constructor with value -1

    private final int sentiment;

    Sentiment(int levelCode) {
        this.sentiment = levelCode;
    }

    public int getSentiment() {
        return this.sentiment;
    }
}
