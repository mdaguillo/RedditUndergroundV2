package com.mikedaguillo.redditunderground2.data.database.model;

/**
 * Object representation of a reddit post in the database
 */
public class RedditPost {
    public RedditPost(String PostId, String SubredditDisplayName, String Author, String Title, int Score, float Created, String SelfText, String Url, String ThumbnailLocation, int NumComments, int isOver18, int isStickied, int isSelf)
    {
        this.PostId = PostId;
        this.SubredditDisplayName = SubredditDisplayName;
        this.Author = Author;
        this.Title = Title;
        this.Score = Score;
        this.Created = Created;
        this.SelfText = SelfText;
        this.Url = Url;
        this.ThumbnailLocation = ThumbnailLocation;
        this.NumComments = NumComments;
        this.isOver18 = isOver18;
        this.isStickied = isStickied;
        this.isSelf = isSelf;
    };

    private String PostId;
    public String GetPostId() { return PostId; }

    private String SubredditDisplayName;
    public String GetSubredditDisplayName() { return SubredditDisplayName; }

    private String Author;
    public String GetPostAuthor() { return Author; }

    private String Title;
    public String GetPostTitle() { return Title; }

    private int Score;
    public int GetPostScore() { return Score; }

    private float Created;
    public float GetCreated() { return Created; }

    private String SelfText;
    public String GetSelfText() { return SelfText; }

    private String Url;
    public String GetUrl() { return Url; }

    private String ThumbnailLocation;
    public String GetThumbnailLocation() { return ThumbnailLocation; }

    private int NumComments;
    public int GetNumComments() { return NumComments; }

    private int isOver18;
    public boolean IsOver18() { return isOver18 == 1; }

    private int isStickied;
    public boolean IsStickied() { return isStickied == 1; }

    private int isSelf;
    public boolean isSelf() { return isSelf == 1; }
}
